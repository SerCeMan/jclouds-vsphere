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

package org.jclouds.vsphere.api

import com.vmware.vim25.BackupBlobWriteFailure
import com.vmware.vim25.DVSFeatureCapability
import com.vmware.vim25.DVSManagerDvsConfigTarget
import com.vmware.vim25.DistributedVirtualSwitchHostProductSpec
import com.vmware.vim25.DistributedVirtualSwitchManagerCompatibilityResult
import com.vmware.vim25.DistributedVirtualSwitchManagerDvsProductSpec
import com.vmware.vim25.DistributedVirtualSwitchManagerHostContainer
import com.vmware.vim25.DistributedVirtualSwitchManagerHostDvsFilterSpec
import com.vmware.vim25.DistributedVirtualSwitchProductSpec
import com.vmware.vim25.DvsFault
import com.vmware.vim25.EntityBackupConfig
import com.vmware.vim25.NotFound
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.SelectionSet
import com.vmware.vim25.mo.DistributedVirtualPortgroup
import com.vmware.vim25.mo.DistributedVirtualSwitch
import com.vmware.vim25.mo.HostSystem
import com.vmware.vim25.mo.ManagedEntity
import com.vmware.vim25.mo.Task

import java.rmi.RemoteException

interface DistributedVirtualSwitchManagerApi {
    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryAvailableDvsSpec(): List<DistributedVirtualSwitchProductSpec>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryCompatibleHostForExistingDvs(container: ManagedEntity, recursive: Boolean, dvs: DistributedVirtualSwitch): List<HostSystem>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryCompatibleHostForNewDvs(container: ManagedEntity, recursive: Boolean, switchProductSpec: DistributedVirtualSwitchProductSpec): List<HostSystem>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryDvsCompatibleHostSpec(switchProductSpec: DistributedVirtualSwitchProductSpec): List<DistributedVirtualSwitchHostProductSpec>

    /**
     * @since SDK4.1
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryDvsCheckCompatibility(
            hostContainer: DistributedVirtualSwitchManagerHostContainer, dvsProductSpec: DistributedVirtualSwitchManagerDvsProductSpec,
            hostFilterSpec: List<DistributedVirtualSwitchManagerHostDvsFilterSpec>): List<DistributedVirtualSwitchManagerCompatibilityResult>


    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryDvsConfigTarget(host: HostSystem, dvs: DistributedVirtualSwitch): DVSManagerDvsConfigTarget

    @Throws(NotFound::class, RuntimeFault::class, RemoteException::class)
    fun queryDvsByUuid(uuid: String): DistributedVirtualSwitch

    /**
     * @since SDK4.1
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryDvsFeatureCapability(switchProductSpec: DistributedVirtualSwitchProductSpec): DVSFeatureCapability

    /**
     * @since SDK5.0
     */
    @Throws(DvsFault::class, RuntimeFault::class, RemoteException::class)
    fun rectifyDvsOnHostTask(hosts: List<HostSystem>): Task

    /**
     * @since SDK5.1
     */
    @Throws(BackupBlobWriteFailure::class, NotFound::class, RuntimeFault::class, RemoteException::class)
    fun dVSManagerExportEntityTask(selectionSet: List<SelectionSet>): Task

    /**
     * @since SDK5.1
     */
    @Throws(DvsFault::class, NotFound::class, RuntimeFault::class, RemoteException::class)
    fun dVSManagerImportEntityTask(entityBackup: List<EntityBackupConfig>, importType: String): Task

    /**
     * @since SDK5.1
     */
    @Throws(NotFound::class, RuntimeFault::class, RemoteException::class)
    fun dVSManagerLookupDvPortGroup(switchUuid: String, portgroupKey: String): DistributedVirtualPortgroup
}
