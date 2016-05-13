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
package org.jclouds.vsphere.domain

import com.vmware.vim25.mo.Datastore
import com.vmware.vim25.mo.HostSystem
import com.vmware.vim25.mo.InventoryNavigator
import com.vmware.vim25.mo.ServiceInstance
import java.io.Closeable

class NetworkConfig(val networkName: String, val nicName: String? = null, val addressType: String? = null)


class VSphereServiceInstance(val instance: ServiceInstance) : Closeable {
    override fun close() = instance.serverConnection.logout()
}


class VSphereHost(hostName: String, val serviceInstance: VSphereServiceInstance) : Closeable by serviceInstance {
    private var host: HostSystem =
            InventoryNavigator(serviceInstance.instance.rootFolder).searchManagedEntity("HostSystem", hostName) as HostSystem

    val datastore: Datastore
        get() {
            var datastore: Datastore? = null
            var freeSpace: Long = 0
            for (d in host.datastores) {
                if (d.summary.getFreeSpace() > freeSpace) {
                    freeSpace = d.summary.getFreeSpace()
                    datastore = d
                }
            }
            return datastore!!
        }
}