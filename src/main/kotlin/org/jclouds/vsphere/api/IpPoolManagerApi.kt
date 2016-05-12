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

import com.vmware.vim25.IpPool
import com.vmware.vim25.IpPoolManagerIpAllocation
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.mo.Datacenter

import java.rmi.RemoteException

interface IpPoolManagerApi {
    @Throws(RuntimeFault::class, RemoteException::class)
    fun createIpPool(dc: Datacenter, pool: IpPool): Int

    @Throws(RuntimeFault::class, RemoteException::class)
    fun destroyIpPool(dc: Datacenter, id: Int, force: Boolean)

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryIpPools(dc: Datacenter): List<IpPool>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun updateIpPool(dc: Datacenter, pool: IpPool)

    /**
     * @since SDK5.1
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun allocateIpv4Address(dc: Datacenter, poolId: Int, allocationId: String): String

    /**
     * @since SDK5.1
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun allocateIpv6Address(dc: Datacenter, poolId: Int, allocationId: String): String

    /**
     * @since SDK5.1
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryIPAllocations(dc: Datacenter, poolId: Int, extensionKey: String): List<IpPoolManagerIpAllocation>

    /**
     * @since SDK5.1
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun releaseIpAllocation(dc: Datacenter, poolId: Int, allocationId: String)
}
