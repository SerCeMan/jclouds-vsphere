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

import com.vmware.vim25.IpPool;
import com.vmware.vim25.IpPoolManagerIpAllocation;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Datacenter;

import java.rmi.RemoteException;
import java.util.List;

public interface IpPoolManagerApi {
   int createIpPool(Datacenter dc, IpPool pool) throws RuntimeFault, RemoteException;

   void destroyIpPool(Datacenter dc, int id, boolean force) throws RuntimeFault, RemoteException;

   List<IpPool> queryIpPools(Datacenter dc) throws RuntimeFault, RemoteException;

   void updateIpPool(Datacenter dc, IpPool pool) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.1
    */
   String allocateIpv4Address(Datacenter dc, int poolId, String allocationId) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.1
    */
   String allocateIpv6Address(Datacenter dc, int poolId, String allocationId) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.1
    */
   List<IpPoolManagerIpAllocation> queryIPAllocations(Datacenter dc, int poolId, String extensionKey) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.1
    */
   void releaseIpAllocation(Datacenter dc, int poolId, String allocationId) throws RuntimeFault, RemoteException;
}
