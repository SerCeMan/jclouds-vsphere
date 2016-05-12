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

import com.vmware.vim25.FileFault;
import com.vmware.vim25.HostDiskDimensionsChs;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.VirtualDiskSpec;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Task;

import java.rmi.RemoteException;

public interface VirtualDiskManagerApi {
   Task copyVirtualDiskTask(String sourceName, Datacenter sourceDatacenter, String destName,
                            Datacenter destDatacenter, VirtualDiskSpec destSpec, Boolean force) throws FileFault, RuntimeFault, RemoteException;

   Task createVirtualDiskTask(String name, Datacenter datacenter, VirtualDiskSpec spec) throws FileFault, RuntimeFault, RemoteException;

   Task defragmentVirtualDiskTask(String name, Datacenter datacenter) throws FileFault, RuntimeFault, RemoteException;

   Task deleteVirtualDiskTask(String name, Datacenter datacenter) throws FileFault, RuntimeFault, RemoteException;

   /**
    * SDK2.5 signature for back compatibility
    */
   Task extendVirtualDiskTask(String name, Datacenter datacenter, long newCapacityKb) throws FileFault, RuntimeFault, RemoteException;

   /**
    * SDK4.0 signature
    */
   Task extendVirtualDiskTask(String name, Datacenter datacenter, long newCapacityKb, Boolean eagerZero) throws FileFault, RuntimeFault, RemoteException;

   /**
    * @since SDK4.0
    */
   Task eagerZeroVirtualDiskTask(String name, Datacenter datacenter) throws FileFault, RuntimeFault, RemoteException;

   Task inflateVirtualDiskTask(String name, Datacenter datacenter) throws FileFault, RuntimeFault, RemoteException;

   Task moveVirtualDiskTask(String sourceName, Datacenter sourceDatacenter, String destName, Datacenter destDatacenter, Boolean force) throws FileFault, RuntimeFault, RemoteException;

   int queryVirtualDiskFragmentation(String name, Datacenter datacenter) throws FileFault, RuntimeFault, RemoteException;

   HostDiskDimensionsChs queryVirtualDiskGeometry(String name, Datacenter datacenter) throws FileFault, RuntimeFault, RemoteException;

   String queryVirtualDiskUuid(String name, Datacenter datacenter) throws FileFault, RuntimeFault, RemoteException;

   void setVirtualDiskUuid(String name, Datacenter datacenter, String uuid) throws FileFault, RuntimeFault, RemoteException;

   Task shrinkVirtualDiskTask(String name, Datacenter datacenter, boolean copy) throws FileFault, RuntimeFault, RemoteException;

   Task zeroFillVirtualDiskTask(String name, Datacenter datacenter) throws FileFault, RuntimeFault, RemoteException;
}
