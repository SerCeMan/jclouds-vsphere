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

import com.vmware.vim25.FileFault
import com.vmware.vim25.HostDiskDimensionsChs
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.VirtualDiskSpec
import com.vmware.vim25.mo.Datacenter
import com.vmware.vim25.mo.Task

import java.rmi.RemoteException

interface VirtualDiskManagerApi {
    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun copyVirtualDiskTask(sourceName: String, sourceDatacenter: Datacenter, destName: String,
                            destDatacenter: Datacenter, destSpec: VirtualDiskSpec, force: Boolean?): Task

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun createVirtualDiskTask(name: String, datacenter: Datacenter, spec: VirtualDiskSpec): Task

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun defragmentVirtualDiskTask(name: String, datacenter: Datacenter): Task

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun deleteVirtualDiskTask(name: String, datacenter: Datacenter): Task

    /**
     * SDK2.5 signature for back compatibility
     */
    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun extendVirtualDiskTask(name: String, datacenter: Datacenter, newCapacityKb: Long): Task

    /**
     * SDK4.0 signature
     */
    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun extendVirtualDiskTask(name: String, datacenter: Datacenter, newCapacityKb: Long, eagerZero: Boolean?): Task

    /**
     * @since SDK4.0
     */
    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun eagerZeroVirtualDiskTask(name: String, datacenter: Datacenter): Task

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun inflateVirtualDiskTask(name: String, datacenter: Datacenter): Task

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun moveVirtualDiskTask(sourceName: String, sourceDatacenter: Datacenter, destName: String, destDatacenter: Datacenter, force: Boolean?): Task

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun queryVirtualDiskFragmentation(name: String, datacenter: Datacenter): Int

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun queryVirtualDiskGeometry(name: String, datacenter: Datacenter): HostDiskDimensionsChs

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun queryVirtualDiskUuid(name: String, datacenter: Datacenter): String

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun setVirtualDiskUuid(name: String, datacenter: Datacenter, uuid: String)

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun shrinkVirtualDiskTask(name: String, datacenter: Datacenter, copy: Boolean): Task

    @Throws(FileFault::class, RuntimeFault::class, RemoteException::class)
    fun zeroFillVirtualDiskTask(name: String, datacenter: Datacenter): Task
}
