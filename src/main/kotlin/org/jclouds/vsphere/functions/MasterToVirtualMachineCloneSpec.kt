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

import com.google.common.base.Function
import com.google.common.collect.Lists
import com.google.inject.Inject
import com.vmware.vim25.*
import com.vmware.vim25.mo.Datastore
import com.vmware.vim25.mo.ResourcePool
import com.vmware.vim25.mo.Task
import com.vmware.vim25.mo.VirtualMachine
import com.vmware.vim25.mo.VirtualMachineSnapshot
import org.jclouds.compute.reference.ComputeServiceConstants
import org.jclouds.logging.Logger

import javax.annotation.Resource
import javax.inject.Named
import javax.inject.Singleton
import java.rmi.RemoteException
import java.util.ArrayList

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Throwables.propagate
import org.jclouds.vsphere.compute.options.VSphereTemplateOptions


class MasterToVirtualMachineCloneSpec(private val resourcePool: ResourcePool,
                                      private val datastore: Datastore,
                                      private val cloningStrategy: String,
                                      private val linuxName: String,
                                      private val vOptions: VSphereTemplateOptions) : Function<VirtualMachine, VirtualMachineCloneSpec> {

    override fun apply(master: VirtualMachine?) = prepareCloneSpec(master!!, resourcePool, datastore, linuxName, vOptions.postConfiguration)

    private fun prepareCloneSpec(master: VirtualMachine, resourcePool: ResourcePool, datastore: Datastore, linuxName: String, postConfiguration: Boolean): VirtualMachineCloneSpec {
        var relocateSpec = configureRelocateSpec(resourcePool, datastore, master)
        return configureVirtualMachineCloneSpec(relocateSpec, linuxName, postConfiguration)
    }

    private fun configureVirtualMachineCloneSpec(rSpec: VirtualMachineRelocateSpec, linuxName: String, postConfiguration: Boolean)
            = VirtualMachineCloneSpec()
            .apply {
                isPowerOn = true
                template = false
                location = rSpec
                if (postConfiguration) {
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
                }
            }

    private fun configureRelocateSpec(resourcePool: ResourcePool, datastore: Datastore, master: VirtualMachine): VirtualMachineRelocateSpec {
        val rSpec = VirtualMachineRelocateSpec()
        if (cloningStrategy == "linked") {
            val diskKeys = getIndependentVirtualDiskKeys(master)
            if (diskKeys.size > 0) {
                val dss = master.datastores
                rSpec.setDiskMoveType(VirtualMachineRelocateDiskMoveOptions.createNewChildDiskBacking.toString())
                rSpec.setDisk(diskKeys.map { key ->
                    VirtualMachineRelocateSpecDiskLocator().apply {
                        this.datastore = dss[0].mor
                        diskMoveType = VirtualMachineRelocateDiskMoveOptions.moveAllDiskBackingsAndDisallowSharing.toString()
                        diskId = key
                    }
                }.toTypedArray())
            } else {
                rSpec.setDiskMoveType(VirtualMachineRelocateDiskMoveOptions.createNewChildDiskBacking.toString())
            }
        } else if (cloningStrategy == "full") {
            rSpec.setDatastore(datastore.mor)
            rSpec.setPool(resourcePool.mor)
        } else
            throw Exception(String.format("Cloning strategy %s not supported", cloningStrategy))
        return rSpec
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
