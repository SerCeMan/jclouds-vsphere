/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.vsphere.compute.config

import com.github.rholder.retry.RetryerBuilder
import com.github.rholder.retry.StopStrategies
import com.github.rholder.retry.WaitStrategies
import com.google.common.base.Function
import com.google.common.base.Preconditions
import com.google.common.base.Predicates
import com.google.common.base.Supplier
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Lists
import com.vmware.vim25.*
import com.vmware.vim25.mo.*
import com.vmware.vim25.mox.VirtualMachineDeviceManager
import org.jclouds.compute.ComputeServiceAdapter
import org.jclouds.compute.ComputeServiceAdapter.NodeAndInitialCredentials
import org.jclouds.compute.domain.Hardware
import org.jclouds.compute.domain.Image
import org.jclouds.compute.domain.Template
import org.jclouds.compute.domain.Volume
import org.jclouds.compute.reference.ComputeServiceConstants
import org.jclouds.domain.Location
import org.jclouds.logging.Logger
import org.jclouds.predicates.validators.DnsNameValidator
import org.jclouds.vsphere.compute.options.VSphereTemplateOptions
import org.jclouds.vsphere.domain.HardwareProfiles.*
import org.jclouds.vsphere.domain.VSphereHost
import org.jclouds.vsphere.domain.VSphereServiceInstance
import org.jclouds.vsphere.functions.MasterToVirtualMachineCloneSpec
import org.jclouds.vsphere.functions.VirtualMachineToImage
import org.jclouds.vsphere.predicates.VSpherePredicate
import java.util.*
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 */
@Singleton
class VSphereComputeServiceAdapter
@Inject
constructor(val serviceInstance: Supplier<VSphereServiceInstance>,
            val virtualMachineToImage: VirtualMachineToImage,
            val hostFunction: Function<String, VSphereHost>) : ComputeServiceAdapter<VirtualMachine, Hardware, Image, Location> {

    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected var logger = Logger.NULL


    private val dnsValidator = DnsNameValidator(3, 30)

    override fun createNodeWithGroupEncodedIntoName(tag: String, name: String, template: Template): ComputeServiceAdapter.NodeAndInitialCredentials<VirtualMachine> {
        val vOptions = template.options as VSphereTemplateOptions
        val datacenterName = vOptions.datacenter
        try {
            serviceInstance.get().use { instance ->
                hostFunction.apply(datacenterName)!!.use({ sphereHost ->
                    val rootFolder = instance.instance.rootFolder

                    dnsValidator.validate(name)

                    val master = getVMwareTemplate(template.image.id, rootFolder)
                    val resourcePool = tryFindResourcePool(template, rootFolder)

                    logger.trace("<< trying to use ResourcePool: " + resourcePool.name)

                    val cloneSpec = MasterToVirtualMachineCloneSpec(
                            resourcePool,
                            logger,
                            sphereHost.datastore,
                            vOptions.cloning,
                            name,
                            vOptions).invoke(master)


                    cloneSpec.config = VirtualMachineConfigSpec().apply {
                        memoryMB = template.hardware.ram.toLong()
                        numCPUs = when {
                            template.hardware.processors.size > 0 -> template.hardware.processors[0].cores.toInt()
                            else -> 1
                        }

                        deviceChange = master.findVirtualDisk()?.let { virtualDisk ->
                            template.hardware.volumes.map { volume ->
                                VirtualDeviceConfigSpec().apply {
                                    operation = VirtualDeviceConfigSpecOperation.edit
                                    device = virtualDisk.apply {
                                        capacityInKB = volume.sizeInKilobytes()
                                    }
                                }
                            }.toTypedArray()
                        }

                        if (vOptions.cloudConfigFun == null) {
                            extraConfig = vOptions.extraConfig?.map { e ->
                                OptionValue().apply {
                                    key = e.key
                                    value = e.value
                                }
                            }?.toTypedArray()
                        }
                    }


                    var cloned: VirtualMachine
                    try {
                        cloned = cloneMaster(master, name, cloneSpec, vOptions.folder)
                        VSpherePredicate.WAIT_FOR_NIC(1000 * 60 * 60 * 2, TimeUnit.MILLISECONDS).apply(cloned)
                        cloudConfigReinit(cloned, vOptions)
                    } catch (e: Exception) {
                        logger.error("Can't clone vm " + master.name + ", Error message: " + e.toString(), e)
                        throw e;
                    }
                    return NodeAndInitialCredentials(cloned, cloned.name, null)
                })
            }
        } catch (t: Throwable) {
            logger.error("Got ERROR while create new VM : ${t.toString()}")
            throw t
        }
    }

    private fun cloudConfigReinit(vm: VirtualMachine, vOptions: VSphereTemplateOptions) {
        val cloudConf = "guestinfo.coreos.config.data"
        val cloudConfEncoding = "guestinfo.coreos.config.data.encoding"
        val extraConf = vOptions.extraConfig ?: hashMapOf()
        val net = vm.guest.net
        val configFn = vOptions.cloudConfigFun
        if (configFn != null) {
            try {
                val newConfig = when {
                    net.size > 0 -> extraConf
                            .plus(cloudConf to replaceIp(configFn, net))
                            .plus(cloudConfEncoding to "base64")
                    else -> extraConf
                }
                reconfigureNode(vm.name, VirtualMachineConfigSpec().apply {
                    extraConfig = newConfig.map { e ->
                        OptionValue().apply {
                            key = e.key
                            value = e.value
                        }
                    }.toTypedArray()
                })
            } catch(e: Exception) {
                logger.error("Can't reconfigure ${vm.name}")
                throw RuntimeException("Can't reconfigure vm", e)
            }
        }
    }

    private fun replaceIp(configFn: (String) -> String, guestNicInfo: Array<GuestNicInfo>): String {
        val ip = findIp(guestNicInfo)
        return Base64.getEncoder().encodeToString(configFn(ip).toByteArray())
    }

    private fun findIp(guestNicInfo: Array<GuestNicInfo>) = guestNicInfo.find { !it.ipAddress[0].startsWith("127.") }!!.ipAddress[0].toString()

    fun reconfigureNode(vmName: String, spec: VirtualMachineConfigSpec) = serviceInstance.get().use { instance ->
        val virtualMachine = findVM(vmName, instance.instance.rootFolder)
        if (virtualMachine == null) {
            throw IllegalArgumentException("VM $vmName can't be found")
        }
        val powerOff = virtualMachine.powerOffVM_Task().waitForTask()
        if (powerOff != Task.SUCCESS) {
            throw RuntimeException("Unable to power off machine $vmName")
        }
        val result = virtualMachine.reconfigVM_Task(spec).waitForTask()
        val powerOn = virtualMachine.powerOnVM_Task(null).waitForTask()
        if (powerOn != Task.SUCCESS) {
            throw RuntimeException("Unable to power on machine $vmName")
        }
        logger.debug(when (result) {
            Task.SUCCESS -> "VM $vmName reconfigured successfuly"
            else -> "Reconfiguration of $vmName failed"
        })
        VSpherePredicate.WAIT_FOR_NIC(1000 * 60 * 60 * 2, TimeUnit.MILLISECONDS).apply(virtualMachine)
    }

    private fun Volume.sizeInKilobytes() = size.toLong() * 1024 * 1024

    private fun VirtualMachine.findVirtualDisk() = (VirtualMachineDeviceManager(this).allVirtualDevices.filter { it is VirtualDisk }.firstOrNull() as VirtualDisk?)

    private fun listNodes(instance: VSphereServiceInstance): Collection<VirtualMachine> {
        var vms: Collection<VirtualMachine> = emptySet()
        try {
            val nodesFolder = instance.instance.rootFolder
            val managedEntities = InventoryNavigator(nodesFolder).searchManagedEntities("VirtualMachine")
            vms = managedEntities.map { it as VirtualMachine }
        } catch (e: Throwable) {
            logger.error("Can't find vm", e)
        }

        return vms
    }

    override fun listNodes(): Iterable<VirtualMachine> = try {
        serviceInstance.get().use { instance -> listNodes(instance) }
    } catch (e: Throwable) {
        logger.error("Can't find vm", e)
        throw e
    }

    override fun listNodesByIds(ids: Iterable<String>): Iterable<VirtualMachine> {
        var vms: Iterable<VirtualMachine> = ImmutableSet.of<VirtualMachine>()
        try {
            serviceInstance.get().use { instance ->
                val nodesFolder = instance.instance.rootFolder
                val list = ArrayList<List<String>>()

                for (id in ids) {
                    list.add(Lists.newArrayList("VirtualMachine", id))
                }

                val typeInfo = ListToArray(list)

                val managedEntities = InventoryNavigator(nodesFolder).searchManagedEntities(typeInfo, true)
                vms = managedEntities.map { it as VirtualMachine }
            }
        } catch (e: Throwable) {
            logger.error("Can't find vms ", e)
        }

        return vms
    }

    private fun ListToArray(values: List<List<String>>): Array<Array<String>> {
        val results = Array(values.size) { arrayOf("", "") }
        var index = 0
        for (value in values) {
            results[index][0] = value[0]
            results[index][1] = value[1]
            index++
        }
        return results
    }


    override fun listHardwareProfiles() = listOf(
            C1_M1_D10,
            C2_M2_D30,
            C2_M2_D50,
            C2_M4_D50,
            C2_M10_D80,
            C3_M10_D80,
            C4_M4_D20,
            C2_M6_D40,
            C8_M16_D30,
            C8_M16_D80
    ).map { it.hardware }

    override fun listImages() = serviceInstance.get().use {
        listNodes(it)
                .filter { VSpherePredicate.isTemplatePredicate(it) }
                .map { virtualMachineToImage.apply(it)!! }
    }

    override fun listLocations() = emptySet<Location>()

    override fun getNode(vmName: String) = serviceInstance.get().use { instance -> instance.findVm(vmName) }

    override fun destroyNode(vmName: String) {
        try {
            serviceInstance.get().use { instance ->
                val virtualMachine = findVM(vmName, instance.instance.rootFolder)!!
                val powerOffTask = virtualMachine.powerOffVM_Task()
                if (powerOffTask.waitForTask() == Task.SUCCESS)
                    logger.debug(String.format("VM %s powered off", vmName))
                else
                    logger.debug(String.format("VM %s could not be powered off", vmName))

                val destroyTask = virtualMachine.destroy_Task()
                if (destroyTask.waitForTask() == Task.SUCCESS)
                    logger.debug(String.format("VM %s destroyed", vmName))
                else
                    logger.debug(String.format("VM %s could not be destroyed", vmName))
            }
        } catch (e: Exception) {
            logger.error("Can't destroy vm " + vmName, e)
            throw e;
        }
    }

    override fun rebootNode(vmName: String) = serviceInstance.get().use { instance ->
        val virtualMachine = instance.findVm(vmName)
        if (virtualMachine == null) {
            logger.info("No node $vmName found")
            return
        }
        try {
            virtualMachine.rebootGuest()
        } catch (e: Exception) {
            logger.error("Can't reboot vm $vmName", e)
            throw e
        }
        logger.debug("$vmName rebooted")
    }

    override fun resumeNode(vmName: String) = serviceInstance.get().use { instance ->
        val virtualMachine = instance.findVm(vmName)
        if (virtualMachine == null) {
            logger.info("No node $vmName found")
            return
        }
        if (virtualMachine.runtime.getPowerState() == VirtualMachinePowerState.poweredOff) {
            try {
                val task = virtualMachine.powerOnVM_Task(null)
                if (task.waitForTask() == Task.SUCCESS) {
                    logger.debug("${virtualMachine.name} resumed")
                }
            } catch (e: Exception) {
                logger.error("Can't resume vm $vmName", e)
                throw e
            }
        } else {
            logger.debug("$vmName can't be resumed")
        }
    }

    override fun suspendNode(vmName: String) = serviceInstance.get().use { instance ->
        val virtualMachine = instance.findVm(vmName)
        if (virtualMachine == null) {
            logger.info("No node $vmName found")
            return
        }
        try {
            val task = virtualMachine.suspendVM_Task()
            logger.debug("$vmName" + if (task.waitForTask() == Task.SUCCESS) "suspended" else "can't be suspended")
        } catch (e: Exception) {
            logger.error("Can't suspend vm " + vmName, e)
            throw e;
        }
    }

    override fun getImage(imageName: String): Image? =
            try {
                serviceInstance.get().use { instance ->
                    virtualMachineToImage.apply(getVMwareTemplate(imageName, instance.instance.rootFolder))
                }
            } catch (e: Exception) {
                logger.error("Can't get image", e)
                throw e;
            }

    fun VirtualMachine.findFolder(serviceInstance: Supplier<VSphereServiceInstance>, folderName: String?) =
            when {
                folderName.isNullOrEmpty() -> this.parent as Folder
                else -> serviceInstance.get().instance.rootFolder.let { rootFolder ->
                    InventoryNavigator(rootFolder).searchManagedEntity("Folder", folderName) as Folder
                }
            }

    private fun VSphereServiceInstance.findVm(vmName: String) = findVM(vmName, instance.rootFolder)

    private fun cloneMaster(master: VirtualMachine, name: String, cloneSpec: VirtualMachineCloneSpec, folderName: String?): VirtualMachine {
        var cloned: VirtualMachine? = null
        try {
            val folder = master.findFolder(serviceInstance, folderName)
            val task = master.cloneVM_Task(folder, name, cloneSpec)
            val result = task.waitForTask()
            if (result == Task.SUCCESS) {
                logger.trace("<< after clone search for VM with name: $name")
                val retryer = RetryerBuilder.newBuilder<VirtualMachine>()
                        .retryIfResult(Predicates.isNull())
                        .withStopStrategy(StopStrategies.stopAfterAttempt(5))
                        .retryIfException()
                        .withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS))
                        .build()
                cloned = retryer.call {
                    findVM(name, folder) ?: findVM(name, serviceInstance.get().instance.rootFolder)
                }
            } else {
                logger.error(task.taskInfo.getError().getLocalizedMessage())
            }
        } catch (e: Exception) {
            if (e is NoPermission) {
                logger.error("NoPermission: ${e.getPrivilegeId()}")
            }
            logger.error("Can't clone vm: ${e.toString()}", e)
            throw e
        }

        if (cloned == null)
            throw Exception("<< Failed to get cloned VM. $name")
        return cloned
    }

    private fun tryFindResourcePool(template: Template, folder: Folder): ResourcePool {
        val options = template.options as VSphereTemplateOptions
        val resourcePools: Iterable<ResourcePool>
        try {
            Preconditions.checkNotNull<String>(options.resourcePool)
            val resourcePoolEntities = InventoryNavigator(folder).searchManagedEntities("ResourcePool")
            resourcePools = resourcePoolEntities.map { it as ResourcePool }
            return resourcePools.find { p -> options.resourcePool == p.name }!!
        } catch (e: Exception) {
            logger.error("Problem in finding a valid resource pool", e)
            throw e
        }
    }

    private fun findVM(vmName: String, nodesFolder: Folder): VirtualMachine? {
        logger.trace(">> search for vm with name : " + vmName)
        var vm: VirtualMachine? = null
        try {
            vm = InventoryNavigator(nodesFolder).searchManagedEntity("VirtualMachine", vmName) as VirtualMachine
        } catch (e: Exception) {
            // It might be expected behaviour
            logger.debug("Can't find vm", e)
        }
        return vm
    }

    private fun getVMwareTemplate(imageName: String, rootFolder: Folder): VirtualMachine {
        var image: VirtualMachine? = null
        try {
            val node = findVM(imageName, rootFolder)
            if (VSpherePredicate.isTemplatePredicate(node)) {
                image = node
            }
        } catch (e: Exception) {
            logger.error("cannot find an image called $imageName", e)
            throw e
        }
        if (image == null) {
            throw Exception("cannot find an image called $imageName")
        }
        return image
    }
}

