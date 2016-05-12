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