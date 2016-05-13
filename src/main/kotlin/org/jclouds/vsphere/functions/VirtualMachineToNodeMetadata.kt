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

import com.google.common.base.*
import com.google.common.base.Function
import com.google.common.base.Predicates.not
import com.google.common.collect.Iterables.filter
import com.google.common.collect.Lists
import com.google.common.collect.Sets.newHashSet
import com.google.common.net.InetAddresses
import com.google.inject.Inject
import com.google.inject.Singleton
import com.vmware.vim25.VirtualMachinePowerState
import com.vmware.vim25.VirtualMachineToolsStatus
import com.vmware.vim25.mo.InventoryNavigator
import com.vmware.vim25.mo.VirtualMachine
import org.jclouds.compute.domain.NodeMetadata
import org.jclouds.compute.domain.NodeMetadata.Status
import org.jclouds.compute.domain.NodeMetadataBuilder
import org.jclouds.compute.reference.ComputeServiceConstants
import org.jclouds.domain.LocationBuilder
import org.jclouds.domain.LocationScope
import org.jclouds.logging.Logger
import org.jclouds.util.InetAddresses2
import org.jclouds.util.Predicates2
import org.jclouds.vsphere.domain.VSphereServiceInstance
import org.jclouds.vsphere.predicates.VSpherePredicate
import java.net.Inet4Address
import java.net.Inet6Address
import java.net.URI
import java.net.URISyntaxException
import java.util.concurrent.TimeUnit
import javax.annotation.Resource
import javax.inject.Named
import javax.inject.Provider

@Singleton
class VirtualMachineToNodeMetadata
@Inject
constructor(val serviceInstanceSupplier: Supplier<VSphereServiceInstance>,
            var toPortableNodeStatus: Provider<Map<VirtualMachinePowerState, Status>>) : Function<VirtualMachine, NodeMetadata> {
    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected var logger = Logger.NULL

    override fun apply(vm: VirtualMachine?): NodeMetadata? {

        var freshVm: VirtualMachine? = null
        var virtualMachineName = ""
        val nodeMetadataBuilder = NodeMetadataBuilder()
        try {
            serviceInstanceSupplier.get().use { instance ->
                val vmMORId = vm!!.mor._value
                val vms = InventoryNavigator(instance.instance.rootFolder).searchManagedEntities("VirtualMachine")
                for (machine in vms) {

                    if (machine.mor.getVal() == vmMORId) {
                        freshVm = machine as VirtualMachine
                        break
                    }
                }
                val locationBuilder = LocationBuilder()
                locationBuilder.description("")
                locationBuilder.id("")
                locationBuilder.scope(LocationScope.HOST)

                if (freshVm == null) {
                    nodeMetadataBuilder.status(Status.ERROR).id("")
                    return nodeMetadataBuilder.build()
                }
                virtualMachineName = freshVm!!.name

                logger.trace("<< converting vm ($virtualMachineName) to NodeMetadata")

                val vmState = freshVm!!.runtime.getPowerState()
                var nodeState: Status? = toPortableNodeStatus.get()[vmState]
                if (nodeState == null)
                    nodeState = Status.UNRECOGNIZED


                nodeMetadataBuilder.name(virtualMachineName).ids(virtualMachineName).location(locationBuilder.build()).hostname(virtualMachineName)

                val host = freshVm!!.serverConnection.url.host

                try {
                    nodeMetadataBuilder.uri(URI("https://" + host + ":9443/vsphere-client/vmrc/vmrc.jsp?vm=urn:vmomi:VirtualMachine:" + vmMORId + ":" + freshVm!!.summary.getConfig().getUuid()))
                } catch (e: URISyntaxException) {
                }


                val ipv4Addresses = newHashSet<String>()
                val ipv6Addresses = newHashSet<String>()

                if (nodeState == Status.RUNNING && !freshVm!!.config.isTemplate &&
                        VSpherePredicate.IsToolsStatusEquals(VirtualMachineToolsStatus.toolsOk).apply(freshVm) &&
                        VSpherePredicate.isNicConnected(freshVm)) {
                    Predicates2.retry(Predicate<com.vmware.vim25.mo.VirtualMachine> { vm ->
                        try {
                            return@Predicate !Strings.isNullOrEmpty(vm!!.guest.getIpAddress())
                        } catch (e: Exception) {
                            return@Predicate false
                        }
                    }, 60 * 1000 * 10.toLong(), (10 * 1000).toLong(), TimeUnit.MILLISECONDS).apply(freshVm)
                }


                if (VSpherePredicate.IsToolsStatusIsIn(Lists.newArrayList(VirtualMachineToolsStatus.toolsNotInstalled, VirtualMachineToolsStatus.toolsNotRunning)).apply(freshVm))
                    logger.trace("<< No VMware tools installed or not running ( $virtualMachineName )")
                else if (nodeState == Status.RUNNING && not(VSpherePredicate.isTemplatePredicate).apply(freshVm)) {
                    var retries = 0
                    while (ipv4Addresses.size < 1) {
                        ipv4Addresses.clear()
                        ipv6Addresses.clear()
                        val nics = freshVm!!.guest.getNet()
                        var nicConnected = false
                        if (null != nics) {
                            for (nic in nics) {
                                nicConnected = nicConnected || nic.connected

                                val addresses = nic.getIpAddress()
                                if (null != addresses) {
                                    for (address in addresses) {
                                        if (logger.isTraceEnabled)
                                            logger.trace("<< find IP addresses $address for $virtualMachineName")
                                        if (isInet4Address(address)) {
                                            ipv4Addresses.add(address)
                                        } else if (isInet6Address(address)) {
                                            ipv6Addresses.add(address)
                                        }
                                    }
                                }
                            }
                        }

                        if (toPortableNodeStatus.get()[freshVm!!.runtime.getPowerState()] != Status.RUNNING) {
                            logger.trace(">> Node is not running. EXIT IP search.")
                            break
                        }

                        if (freshVm!!.guest.getToolsVersionStatus2() == "guestToolsUnmanaged" && nics == null) {
                            val ip = freshVm!!.guest.getIpAddress()
                            if (!Strings.isNullOrEmpty(ip)) {
                                if (isInet4Address(ip)) {
                                    ipv4Addresses.add(ip)
                                } else if (isInet6Address(ip)) {
                                    ipv6Addresses.add(ip)
                                }
                            }
                            break
                        }

                        if (!nicConnected && retries == 5) {
                            logger.trace("<< VM does NOT have any NIC connected.")
                            break
                        }

                        if (ipv4Addresses.size < 1 && null != nics) {
                            logger.warn("<< can't find IPv4 address for vm: " + virtualMachineName)
                            retries++
                            Thread.sleep(6000)
                        }
                        if (ipv4Addresses.size < 1 && retries == 15) {
                            logger.error("<< can't find IPv4 address after $retries retries for vm: $virtualMachineName")
                            break
                        }
                    }
                    nodeMetadataBuilder.publicAddresses(filter(ipv4Addresses, not<String>(isPrivateAddress)))
                    nodeMetadataBuilder.privateAddresses(filter(ipv4Addresses, isPrivateAddress))
                }
                nodeMetadataBuilder.status(nodeState)
                return nodeMetadataBuilder.build()
            }
        } catch (t: Throwable) {
            logger.error("Got an exception for virtual machine name : " + virtualMachineName)
            logger.error("The exception is : " + t.toString())
            Throwables.propagate(t)
            return nodeMetadataBuilder.build()
        }

    }

    companion object {
        private val isPrivateAddress = { addr: String? -> InetAddresses2.IsPrivateIPAddress.INSTANCE.apply(addr) }

        private val isInet4Address = { input: String? ->
            try {
                // Note we can do this, as InetAddress is now on the white list
                InetAddresses.forString(input) is Inet4Address
            } catch (e: IllegalArgumentException) {
                // could be a hostname
                false
            }
        }
        private val isInet6Address = { input: String? ->
            try {
                // Note we can do this, as InetAddress is now on the white list
                InetAddresses.forString(input) is Inet6Address
            } catch (e: IllegalArgumentException) {
                // could be a hostname
                false
            }
        }
    }
}
