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

package org.jclouds.vsphere.api;

import com.vmware.vim25.BackupBlobWriteFailure;
import com.vmware.vim25.DVSFeatureCapability;
import com.vmware.vim25.DVSManagerDvsConfigTarget;
import com.vmware.vim25.DistributedVirtualSwitchHostProductSpec;
import com.vmware.vim25.DistributedVirtualSwitchManagerCompatibilityResult;
import com.vmware.vim25.DistributedVirtualSwitchManagerDvsProductSpec;
import com.vmware.vim25.DistributedVirtualSwitchManagerHostContainer;
import com.vmware.vim25.DistributedVirtualSwitchManagerHostDvsFilterSpec;
import com.vmware.vim25.DistributedVirtualSwitchProductSpec;
import com.vmware.vim25.DvsFault;
import com.vmware.vim25.EntityBackupConfig;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SelectionSet;
import com.vmware.vim25.mo.DistributedVirtualPortgroup;
import com.vmware.vim25.mo.DistributedVirtualSwitch;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Task;

import java.rmi.RemoteException;
import java.util.List;

public interface DistributedVirtualSwitchManagerApi {
   List<DistributedVirtualSwitchProductSpec> queryAvailableDvsSpec() throws RuntimeFault, RemoteException;

   List<HostSystem> queryCompatibleHostForExistingDvs(ManagedEntity container, boolean recursive, DistributedVirtualSwitch dvs) throws RuntimeFault, RemoteException;

   List<HostSystem> queryCompatibleHostForNewDvs(ManagedEntity container, boolean recursive, DistributedVirtualSwitchProductSpec switchProductSpec) throws RuntimeFault, RemoteException;

   List<DistributedVirtualSwitchHostProductSpec> queryDvsCompatibleHostSpec(DistributedVirtualSwitchProductSpec switchProductSpec) throws RuntimeFault, RemoteException;

   /**
    * @since SDK4.1
    */
   List<DistributedVirtualSwitchManagerCompatibilityResult> queryDvsCheckCompatibility(
           DistributedVirtualSwitchManagerHostContainer hostContainer, DistributedVirtualSwitchManagerDvsProductSpec dvsProductSpec,
           List<DistributedVirtualSwitchManagerHostDvsFilterSpec> hostFilterSpec) throws RuntimeFault, RemoteException;


   DVSManagerDvsConfigTarget queryDvsConfigTarget(HostSystem host, DistributedVirtualSwitch dvs) throws RuntimeFault, RemoteException;

   DistributedVirtualSwitch queryDvsByUuid(String uuid) throws NotFound, RuntimeFault, RemoteException;

   /**
    * @since SDK4.1
    */
   DVSFeatureCapability queryDvsFeatureCapability(DistributedVirtualSwitchProductSpec switchProductSpec) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.0
    */
   Task rectifyDvsOnHostTask(List<HostSystem> hosts) throws DvsFault, RuntimeFault, RemoteException;

   /**
    * @since SDK5.1
    */
   Task dVSManagerExportEntityTask(List<SelectionSet> selectionSet) throws BackupBlobWriteFailure, NotFound, RuntimeFault, RemoteException;

   /**
    * @since SDK5.1
    */
   Task dVSManagerImportEntityTask(List<EntityBackupConfig> entityBackup, String importType) throws DvsFault, NotFound, RuntimeFault, RemoteException;

   /**
    * @since SDK5.1
    */
   DistributedVirtualPortgroup dVSManagerLookupDvPortGroup(String switchUuid, String portgroupKey) throws NotFound, RuntimeFault, RemoteException;
}
