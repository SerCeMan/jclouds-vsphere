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
import com.google.common.base.*
import com.google.common.base.Function
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Throwables.propagate
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Lists
import com.google.common.collect.Sets
import com.vmware.vim25.*
import com.vmware.vim25.mo.*
import org.jclouds.compute.ComputeServiceAdapter
import org.jclouds.compute.domain.Hardware
import org.jclouds.compute.domain.Image
import org.jclouds.compute.domain.Template
import org.jclouds.compute.reference.ComputeServiceConstants
import org.jclouds.domain.Location
import org.jclouds.domain.LoginCredentials
import org.jclouds.logging.Logger
import org.jclouds.predicates.validators.DnsNameValidator
import org.jclouds.vsphere.VSphereApiMetadata
import org.jclouds.vsphere.compute.options.VSphereTemplateOptions
import org.jclouds.vsphere.config.VSphereConstants
import org.jclouds.vsphere.config.VSphereConstants.CLONING
import org.jclouds.vsphere.domain.HardwareProfiles
import org.jclouds.vsphere.domain.NetworkConfig
import org.jclouds.vsphere.domain.VSphereHost
import org.jclouds.vsphere.domain.VSphereServiceInstance
import org.jclouds.vsphere.functions.FolderNameToFolderManagedEntity
import org.jclouds.vsphere.functions.MasterToVirtualMachineCloneSpec
import org.jclouds.vsphere.functions.VirtualMachineToImage
import org.jclouds.vsphere.predicates.VSpherePredicate
import java.util.*
import java.util.concurrent.Callable
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
            val hostFunction: Function<String, VSphereHost>,
            @Named(VSphereConstants.JCLOUDS_VSPHERE_VM_PASSWORD) val vmInitPassword: String) : ComputeServiceAdapter<VirtualMachine, Hardware, Image, Location> {


    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected var logger = Logger.NULL


    private val dnsValidator = DnsNameValidator(3, 30)

    override fun createNodeWithGroupEncodedIntoName(tag: String, name: String, template: Template): ComputeServiceAdapter.NodeAndInitialCredentials<VirtualMachine>? {

        val vOptions = template.options as VSphereTemplateOptions

        val datacenterName = vOptions.datacenterName
        try {
            this.serviceInstance.get().use { instance ->
                hostFunction.apply(datacenterName)!!.use({ sphereHost ->
                    val rootFolder = instance.instance.rootFolder

                    dnsValidator.validate(name)

                    val master = getVMwareTemplate(template.image.id, rootFolder)
                    val resourcePool = checkNotNull<ResourcePool>(tryFindResourcePool(template, rootFolder), "resourcePool")

                    logger.trace("<< trying to use ResourcePool: " + resourcePool.name)
                    // VSphereTemplateOptions vOptions = VSphereTemplateOptions.class.cast(template.getOptions());


                    val cloneSpec = MasterToVirtualMachineCloneSpec(
                            resourcePool,
                            sphereHost.datastore,
                            VSphereApiMetadata.defaultProps().getProperty(CLONING),
                            name,
                            vOptions).apply(master)


                    val virtualMachineConfigSpec = VirtualMachineConfigSpec()
                    virtualMachineConfigSpec.setMemoryMB(template.hardware.ram.toLong())
                    if (template.hardware.processors.size > 0)
                        virtualMachineConfigSpec.setNumCPUs(template.hardware.processors[0].cores.toInt())
                    else
                        virtualMachineConfigSpec.setNumCPUs(1)

                    val extraConf = vOptions.extraConfig
                    if (extraConf != null) {
                        val optionsValue = arrayOfNulls<OptionValue>(extraConf.size)
                        var i = 0
                        for (entry in extraConf.entries) {
                            val op = OptionValue()
                            op.setKey(entry.key)
                            op.setValue(entry.value)
                            optionsValue[i++] = op
                        }
                        virtualMachineConfigSpec.setExtraConfig(optionsValue)
                    }


                    val networkConfigs = Sets.newHashSet<NetworkConfig>()
                    if (networkConfigs.isEmpty()) {
                        networkConfigs.add(NetworkConfig("QA Lan 30"))
                    }


                    cloneSpec.setConfig(virtualMachineConfigSpec)

                    vOptions.publicKey

                    var cloned: VirtualMachine? = null
                    try {
                        cloned = cloneMaster(master, name, cloneSpec, vOptions.vmFolder)
                        VSpherePredicate.WAIT_FOR_NIC(1000 * 60 * 60 * 2, TimeUnit.MILLISECONDS).apply(cloned)
                    } catch (e: Exception) {
                        logger.error("Can't clone vm " + master.name + ", Error message: " + e.toString(), e)
                        propagate(e)
                    }

                    val nodeAndInitialCredentials = ComputeServiceAdapter.NodeAndInitialCredentials<VirtualMachine>(cloned, cloned!!.name,
                            LoginCredentials.builder().user("core").password(vmInitPassword).build())
                    return nodeAndInitialCredentials
                })
            }
        } catch (t: Throwable) {
            logger.error("Got ERROR while create new VM : " + t.toString())
            Throwables.propagateIfPossible(t)
        }

        return null
    }

    private fun listNodes(instance: VSphereServiceInstance): Collection<VirtualMachine> {
        var vms: Collection<VirtualMachine> = ImmutableSet.of<VirtualMachine>()
        try {
            val nodesFolder = instance.instance.rootFolder
            val managedEntities = InventoryNavigator(nodesFolder).searchManagedEntities("VirtualMachine")
            vms = managedEntities.map { it as VirtualMachine }
        } catch (e: Throwable) {
            logger.error("Can't find vm", e)
        }

        return vms
    }

    override fun listNodes(): Iterable<VirtualMachine> {
        try {
            serviceInstance.get().use { instance -> return listNodes(instance) }
        } catch (e: Throwable) {
            logger.error("Can't find vm", e)
            Throwables.propagateIfPossible(e)
            return ImmutableSet.of<VirtualMachine>()
        }

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


    override fun listHardwareProfiles(): Iterable<Hardware> {
        val hardware = Sets.newLinkedHashSet<org.jclouds.compute.domain.Hardware>()
        hardware.add(HardwareProfiles.C1_M1_D10.hardware)
        hardware.add(HardwareProfiles.C2_M2_D30.hardware)
        hardware.add(HardwareProfiles.C2_M2_D50.hardware)
        hardware.add(HardwareProfiles.C2_M4_D50.hardware)
        hardware.add(HardwareProfiles.C2_M10_D80.hardware)
        hardware.add(HardwareProfiles.C3_M10_D80.hardware)
        hardware.add(HardwareProfiles.C4_M4_D20.hardware)
        hardware.add(HardwareProfiles.C2_M6_D40.hardware)
        hardware.add(HardwareProfiles.C8_M16_D30.hardware)
        hardware.add(HardwareProfiles.C8_M16_D80.hardware)

        return hardware
    }

    override fun listImages() = serviceInstance.get().use {
        listNodes(it)
                .filter { VSpherePredicate.isTemplatePredicate(it) }
                .map { virtualMachineToImage.apply(it)!! }
    }

    override fun listLocations() = ImmutableSet.of<Location>()

    override fun getNode(vmName: String) = serviceInstance.get().use { instance -> getVM(vmName, instance.instance.rootFolder) }!!

    override fun destroyNode(vmName: String) {
        try {
            serviceInstance.get().use { instance ->
                val virtualMachine = getVM(vmName, instance.instance.rootFolder)!!
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
            Throwables.propagateIfPossible(e)
        }

    }

    override fun rebootNode(vmName: String) {
        val virtualMachine = getNode(vmName)
        try {
            virtualMachine.rebootGuest()
        } catch (e: Exception) {
            logger.error("Can't reboot vm " + vmName, e)
            propagate(e)
        }
        logger.debug(vmName + " rebooted")
    }

    override fun resumeNode(vmName: String) {
        val virtualMachine = getNode(vmName)

        if (virtualMachine.getRuntime().getPowerState() == VirtualMachinePowerState.poweredOff) {
            try {
                val task = virtualMachine.powerOnVM_Task(null)
                if (task.waitForTask() == Task.SUCCESS)
                    logger.debug(virtualMachine.getName() + " resumed")
            } catch (e: Exception) {
                logger.error("Can't resume vm " + vmName, e)
                propagate(e)
            }

        } else
            logger.debug(vmName + " can't be resumed")
    }

    override fun suspendNode(vmName: String) {
        val virtualMachine = getNode(vmName)

        try {
            val task = virtualMachine.suspendVM_Task()
            if (task.waitForTask() == Task.SUCCESS)
                logger.debug(vmName + " suspended")
            else
                logger.debug(vmName + " can't be suspended")
        } catch (e: Exception) {
            logger.error("Can't suspend vm " + vmName, e)
            propagate(e)
        }

    }

    override fun getImage(imageName: String): Image? {
        try {
            serviceInstance.get().use { instance -> return virtualMachineToImage.apply(getVMwareTemplate(imageName, instance.instance.rootFolder)) }
        } catch (t: Throwable) {
            Throwables.propagateIfPossible(t)
            return null
        }

    }

    private fun cloneMaster(master: VirtualMachine, name: String, cloneSpec: VirtualMachineCloneSpec, folderName: String?): VirtualMachine {

        var cloned: VirtualMachine? = null
        try {
            val toFolderManagedEntity = FolderNameToFolderManagedEntity(serviceInstance, master)
            val folder = toFolderManagedEntity.apply(folderName)!!
            val task = master.cloneVM_Task(folder, name, cloneSpec)
            val result = task.waitForTask()
            if (result == Task.SUCCESS) {
                logger.trace("<< after clone search for VM with name: " + name)
                val retryer = RetryerBuilder.newBuilder<VirtualMachine>().retryIfResult(Predicates.isNull<VirtualMachine>()).withStopStrategy(StopStrategies.stopAfterAttempt(5)).retryIfException().withWaitStrategy(WaitStrategies.fixedWait(1, TimeUnit.SECONDS)).build()
                cloned = retryer.call(GetVirtualMachineCallable(name, folder, serviceInstance.get().instance.rootFolder))
            } else {
                val errorMessage = task.taskInfo.getError().getLocalizedMessage()
                logger.error(errorMessage)
            }
        } catch (e: Exception) {
            if (e is NoPermission) {
                logger.error("NoPermission: " + e.getPrivilegeId())
            }
            logger.error("Can't clone vm: " + e.toString(), e)
            propagate(e)
        }

        if (cloned == null)
            logger.error("<< Failed to get cloned VM. " + name)
        return checkNotNull<VirtualMachine>(cloned, "cloned")
    }

    inner class GetVirtualMachineCallable(val vmName: String,
                                          val folder: Folder,
                                          val rootFolder: Folder) : Callable<VirtualMachine> {
        override fun call(): VirtualMachine {
            var cloned: VirtualMachine?
            cloned = getVM(vmName, folder)
            if (cloned == null)
                cloned = getVM(vmName, rootFolder)
            return cloned!!
        }
    }

    private fun tryFindResourcePool(template: Template, folder: Folder): ResourcePool? {
        val options = template.options as VSphereTemplateOptions
        val resourcePools: Iterable<ResourcePool>
        try {
            Preconditions.checkNotNull<String>(options.resourcePool)
            val resourcePoolEntities = InventoryNavigator(folder).searchManagedEntities("ResourcePool")
            resourcePools = resourcePoolEntities.map { it as ResourcePool }
            return resourcePools.find { p -> options.resourcePool == p.name }
        } catch (e: Exception) {
            logger.error("Problem in finding a valid resource pool", e)
        }
        return null
    }

    private fun getVM(vmName: String, nodesFolder: Folder): VirtualMachine? {
        logger.trace(">> search for vm with name : " + vmName)
        var vm: VirtualMachine? = null
        try {
            vm = InventoryNavigator(nodesFolder).searchManagedEntity("VirtualMachine", vmName) as VirtualMachine
        } catch (e: Exception) {
            logger.error("Can't find vm", e)
        }
        return vm
    }

    private fun getVMwareTemplate(imageName: String, rootFolder: Folder): VirtualMachine {
        var image: VirtualMachine? = null
        try {
            val node = getVM(imageName, rootFolder)
            if (VSpherePredicate.isTemplatePredicate(node))
                image = node
        } catch (e: Exception) {
            logger.error("cannot find an image called " + imageName, e)
            throw e
        }
        return image!!
    }
}
