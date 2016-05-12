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
package org.jclouds.vsphere.predicates

import com.google.common.base.Predicate
import com.google.common.net.InetAddresses
import com.vmware.vim25.GuestNicInfo
import com.vmware.vim25.VirtualMachineToolsStatus
import com.vmware.vim25.mo.VirtualMachine
import org.jclouds.util.Predicates2
import java.net.Inet4Address
import java.net.Inet6Address
import java.util.concurrent.TimeUnit

/**
 */
object VSpherePredicate {

    val isTemplatePredicate = { virtualMachine: VirtualMachine? ->
        if (virtualMachine == null) {
            true
        } else try {
            virtualMachine.config.isTemplate
        } catch (e: Exception) {
            true
        }
    }

    val isNicConnected = { input: VirtualMachine? ->
        if (input == null)
            false
        else {
            val nics = input.guest.getNet()
            var nicConnected = false
            if (null != nics) {
                for (nic in nics) {
                    nicConnected = nicConnected || nic.connected
                }
            }
            nicConnected
        }
    }

    fun WAIT_FOR_NIC(timeout: Int?, timeUnit: TimeUnit): Predicate<VirtualMachine> {
        return WaitForNic(timeout, timeUnit)
    }

    fun IsToolsStatusEquals(status: VirtualMachineToolsStatus): Predicate<VirtualMachine> = Predicate { input ->
        try {
            if (input!!.guest.getToolsStatus() == status)
                return@Predicate true
        } catch (e: Exception) {
            return@Predicate false
        }
        false
    }

    fun IsToolsStatusIsIn(statuses: List<VirtualMachineToolsStatus>): Predicate<VirtualMachine> = Predicate { input ->
        try {
            if (statuses.contains(input!!.guest.getToolsStatus()))
                return@Predicate true
        } catch (e: Exception) {
            return@Predicate false
        }
        false
    }
}

private class WaitForNic(timeout: Int?, timeUnit: TimeUnit) : Predicate<VirtualMachine> {
    private val delegate: Predicate<VirtualMachine>

    init {
        delegate = Predicates2.retry(Predicate<com.vmware.vim25.mo.VirtualMachine> { vm ->
            try {
                return@Predicate vm!!.guest.getNet() != null
            } catch (e: Exception) {
                return@Predicate false
            }
        }, timeout!!.toLong(), 1000, timeUnit)

    }

    override fun apply(vm: VirtualMachine?): Boolean {
        try {
            return delegate.apply(vm)
        } catch (e: Exception) {
            return false
        }

    }
}
