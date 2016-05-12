package org.jclouds.vsphere.compute.config

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

import com.vmware.vim25.VirtualMachinePowerState

import com.google.common.base.Function
import com.google.common.base.Supplier
import com.google.common.collect.ImmutableMap
import com.google.inject.Injector
import com.google.inject.Provides
import com.google.inject.TypeLiteral
import com.vmware.vim25.mo.VirtualMachine
import org.jclouds.compute.ComputeServiceAdapter
import org.jclouds.compute.config.ComputeServiceAdapterContextModule
import org.jclouds.compute.domain.Hardware
import org.jclouds.compute.domain.Image
import org.jclouds.compute.domain.NodeMetadata
import org.jclouds.compute.options.TemplateOptions
import org.jclouds.domain.Location
import org.jclouds.functions.IdentityFunction
import org.jclouds.location.suppliers.LocationsSupplier
import org.jclouds.ssh.SshClient
import org.jclouds.vsphere.compute.options.VSphereTemplateOptions
import org.jclouds.vsphere.domain.VSphereHost
import org.jclouds.vsphere.domain.VSphereServiceInstance
import org.jclouds.vsphere.functions.*
import org.jclouds.vsphere.suppliers.VSphereLocationSupplier

import javax.inject.Singleton


/**
 */
class VSphereComputeServiceContextModule : ComputeServiceAdapterContextModule<VirtualMachine, Hardware, Image, Location>() {

    @Singleton
    @Provides
    protected fun toPortableImageStatus(): Map<VirtualMachinePowerState, Image.Status> {
        return toPortableImageStatus
    }


    @Provides
    @Singleton
    protected fun toPortableNodeStatus(): Map<VirtualMachinePowerState, NodeMetadata.Status> {
        return toPortableNodeStatus
    }


    @Suppress("UNCHECKED_CAST")
    override fun configure() {
        super.configure()

        bind(TemplateOptions::class.java).to(VSphereTemplateOptions::class.java)
        bind(LocationsSupplier::class.java).to(VSphereLocationSupplier::class.java)

        bind(object : TypeLiteral<ComputeServiceAdapter<VirtualMachine, Hardware, Image, Location>>() {
        }).to(VSphereComputeServiceAdapter::class.java)

        bind(object : TypeLiteral<Function<Location, Location>>() {
        }).toInstance(IdentityFunction.INSTANCE as Function<Location, Location>)

        bind(object : TypeLiteral<Function<Image, Image>>() {
        }).toInstance(IdentityFunction.INSTANCE as Function<Image, Image>)

        bind(object : TypeLiteral<Function<Hardware, Hardware>>() {
        }).toInstance(IdentityFunction.INSTANCE as Function<Hardware, Hardware>)

        bind(object : TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
        }).to(VirtualMachineToNodeMetadata::class.java)

        bind(object : TypeLiteral<Supplier<VSphereServiceInstance>>() {

        }).to(CreateAndConnectVSphereClient::class.java)

        bind(object : TypeLiteral<Function<VirtualMachine, SshClient>>() {

        }).to(VirtualMachineToSshClient::class.java)

        bind(object : TypeLiteral<Function<String, VSphereHost>>() {

        }).to(GetRecommendedVSphereHost::class.java)
    }


    override fun provideTemplateOptions(injector: Injector?, options: TemplateOptions): TemplateOptions {
        return options.`as`(VSphereTemplateOptions::class.java)
    }

    companion object {

        private val toPortableNodeStatus = ImmutableMap.builder<VirtualMachinePowerState, NodeMetadata.Status>().put(VirtualMachinePowerState.poweredOff, NodeMetadata.Status.TERMINATED).put(VirtualMachinePowerState.poweredOn, NodeMetadata.Status.RUNNING).put(VirtualMachinePowerState.suspended, NodeMetadata.Status.SUSPENDED).build()


        private val toPortableImageStatus = ImmutableMap.builder<VirtualMachinePowerState, Image.Status>().put(VirtualMachinePowerState.poweredOn, Image.Status.PENDING).put(VirtualMachinePowerState.poweredOff, Image.Status.AVAILABLE).put(VirtualMachinePowerState.suspended, Image.Status.PENDING).build()
    }
}
