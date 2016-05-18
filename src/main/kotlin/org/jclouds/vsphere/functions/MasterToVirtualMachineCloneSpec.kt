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

package org.jclouds.vsphere.functions

import com.google.common.collect.Lists
import com.vmware.vim25.*
import com.vmware.vim25.VirtualMachineRelocateDiskMoveOptions.createNewChildDiskBacking
import com.vmware.vim25.mo.*
import org.jclouds.logging.Logger
import org.jclouds.vsphere.compute.options.CloningStrategy
import org.jclouds.vsphere.compute.options.VSphereTemplateOptions
import java.util.*


class MasterToVirtualMachineCloneSpec(private val resourcePool: ResourcePool,
                                      private val logger: Logger,
                                      private val datastore: Datastore,
                                      private val cloningStrategy: CloningStrategy,
                                      private val linuxName: String,
                                      private val vOptions: VSphereTemplateOptions) {

    fun invoke(master: VirtualMachine?) = prepareCloneSpec(master!!, resourcePool, datastore, linuxName)

    private fun prepareCloneSpec(master: VirtualMachine, resourcePool: ResourcePool, datastore: Datastore, linuxName: String): VirtualMachineCloneSpec {
        var relocateSpec = configureRelocateSpec(resourcePool, datastore, master)
        return configureVirtualMachineCloneSpec(master, relocateSpec, linuxName)
    }

    private fun configureVirtualMachineCloneSpec(master: VirtualMachine, rSpec: VirtualMachineRelocateSpec, linuxName: String)
            = VirtualMachineCloneSpec()
            .apply {
                isPowerOn = true
                template = false
                location = rSpec
                customization = CustomizationSpec().apply {
                    identity = CustomizationLinuxPrep().apply {
                        hostName = CustomizationFixedName().apply {
                            name = linuxName
                        }
                        domain = vOptions.domain
                    }
                    globalIPSettings = CustomizationGlobalIPSettings()
                    nicSettingMap = arrayOf(CustomizationAdapterMapping().apply {
                        adapter = CustomizationIPSettings().apply {
                            ip = CustomizationDhcpIpGenerator()
                        }
                    })
                }
                if (vOptions.cloning == CloningStrategy.LINKED) {
                    snapshot = getCurrentSnapshot(master, "clone_snapshot").mor
                }
            }

    private fun getCurrentSnapshot(master: VirtualMachine, snapshotName: String): VirtualMachineSnapshot {
        if (master.snapshot == null) {
            val task = master.createSnapshot_Task(master.name, snapshotName, false, false);
            try {
                if (task.waitForTask() == Task.SUCCESS) {
                    logger.debug("snapshot taken for '${master.name}'");
                }
            } catch (e: Exception) {
                logger.debug("Can't take snapshot for '${master.name}'", e);
                throw e;
            }
        } else {
            logger.debug("snapshot already available for '${master.name}'");
        }
        val snapshot = master.currentSnapShot
        if (snapshot == null) {
            throw Exception("Unable to find snapshot for '${master.name}'")
        }
        return snapshot;
    }


    private fun configureRelocateSpec(resourcePool: ResourcePool, ds: Datastore, master: VirtualMachine): VirtualMachineRelocateSpec {
        return VirtualMachineRelocateSpec().apply {
            pool = resourcePool.mor
            when (cloningStrategy) {
                CloningStrategy.LINKED -> {
                    diskMoveType = createNewChildDiskBacking.toString()
                    disk = buildDiskKeys(master)
                }
                CloningStrategy.FULL -> {
                    datastore = ds.mor
                }
                else -> throw Exception(String.format("Cloning strategy %s not supported", cloningStrategy))
            }
        }
    }

    private fun buildDiskKeys(master: VirtualMachine): Array<VirtualMachineRelocateSpecDiskLocator> {
        val diskKeys = getIndependentVirtualDiskKeys(master)
        val dss = master.datastores
        return diskKeys.map { key ->
            VirtualMachineRelocateSpecDiskLocator().apply {
                datastore = dss[0].mor
                diskMoveType = VirtualMachineRelocateDiskMoveOptions.moveAllDiskBackingsAndDisallowSharing.toString()
                diskId = key
            }
        }.toTypedArray()
    }

    private fun getIndependentVirtualDiskKeys(vm: VirtualMachine): ArrayList<Int> {
        val diskKeys = Lists.newArrayList<Int>()
        val devices = vm.getPropertyByPath("config.hardware.device") as Array<*>
        for (i in devices.indices) {
            if (devices[i] is VirtualDisk) {
                val vDisk = devices[i] as VirtualDisk
                var diskMode = ""
                val vdbi = vDisk.getBacking()
                when (vdbi) {
                    is VirtualDiskFlatVer1BackingInfo -> diskMode = vdbi.getDiskMode()
                    is VirtualDiskFlatVer2BackingInfo -> diskMode = vdbi.getDiskMode()
                    is VirtualDiskRawDiskMappingVer1BackingInfo -> diskMode = vdbi.getDiskMode()
                    is VirtualDiskSparseVer1BackingInfo -> diskMode = vdbi.getDiskMode()
                    is VirtualDiskSparseVer2BackingInfo -> diskMode = vdbi.getDiskMode()
                }
                if (diskMode.indexOf("independent") != -1) {
                    diskKeys.add(vDisk.getKey())
                }
            }
        }
        return diskKeys
    }

}
