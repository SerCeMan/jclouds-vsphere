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

import com.google.common.base.Function
import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Strings
import com.google.common.net.HostAndPort
import com.google.inject.Inject
import com.vmware.vim25.VirtualMachinePowerState
import com.vmware.vim25.VirtualMachineToolsStatus
import com.vmware.vim25.mo.VirtualMachine
import org.jclouds.domain.LoginCredentials
import org.jclouds.ssh.SshClient
import org.jclouds.util.Predicates2
import org.jclouds.vsphere.config.VSphereConstants
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class VirtualMachineToSshClient
@Inject
constructor(val sshClientFactory: SshClient.Factory) : Function<VirtualMachine, SshClient> {
    @Named(VSphereConstants.JCLOUDS_VSPHERE_VM_PASSWORD)
    protected var password: String? = null

    override fun apply(vm: VirtualMachine?): SshClient? {
        val client: SshClient
        var clientIpAddress = vm!!.guest.getIpAddress()
        val sshPort = "22"
        while (vm.guest.getToolsStatus() != VirtualMachineToolsStatus.toolsOk || clientIpAddress.isEmpty()) {
            val timeoutValue = 1000
            val timeoutUnits = 500
            val tester = Predicates2.retry<String>(
                    { input: String? -> !input!!.isEmpty() },
                    timeoutValue.toLong(),
                    timeoutUnits.toLong(),
                    TimeUnit.MILLISECONDS)
            var passed = false
            while (vm.runtime.getPowerState() == VirtualMachinePowerState.poweredOn && !passed) {
                clientIpAddress = Strings.nullToEmpty(vm.guest.getIpAddress())
                passed = tester.apply(clientIpAddress)
            }
        }
        val loginCredentials = LoginCredentials.builder().user("root").password(password).build()
        checkNotNull(clientIpAddress, "clientIpAddress")
        client = sshClientFactory.create(
                HostAndPort.fromParts(clientIpAddress, Integer.parseInt(sshPort)),
                loginCredentials)
        checkNotNull(client)
        return client
    }
}
