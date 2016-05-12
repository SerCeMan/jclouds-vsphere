package org.jclouds.vsphere.compute.config;

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

import com.vmware.vim25.VirtualMachinePowerState;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Injector;
import com.google.inject.Provider;
import com.google.inject.Provides;
import com.google.inject.TypeLiteral;
import com.vmware.vim25.VirtualMachinePowerState;
import com.vmware.vim25.mo.VirtualMachine;
import org.jclouds.compute.ComputeServiceAdapter;
import org.jclouds.compute.config.ComputeServiceAdapterContextModule;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.domain.Location;
import org.jclouds.functions.IdentityFunction;
import org.jclouds.location.suppliers.LocationsSupplier;
import org.jclouds.ssh.SshClient;
import org.jclouds.vsphere.compute.options.VSphereTemplateOptions;
import org.jclouds.vsphere.domain.VSphereHost;
import org.jclouds.vsphere.domain.VSphereServiceInstance;
import org.jclouds.vsphere.functions.*;
import org.jclouds.vsphere.suppliers.VSphereLocationSupplier;

import javax.inject.Singleton;
import java.util.Map;


/**
 */
public class VSphereComputeServiceContextModule extends
        ComputeServiceAdapterContextModule<VirtualMachine, Hardware, Image, Location> {

    @Singleton
    @Provides
    protected Map<VirtualMachinePowerState, Image.Status> toPortableImageStatus() {
        return toPortableImageStatus;
    }


    @Provides
    @Singleton
    protected Map<VirtualMachinePowerState, NodeMetadata.Status> toPortableNodeStatus() {
        return toPortableNodeStatus;
    }


    @Override
    protected void configure() {
        super.configure();

        bind(TemplateOptions.class).to(VSphereTemplateOptions.class);
        bind(LocationsSupplier.class).to(VSphereLocationSupplier.class);

        bind(new TypeLiteral<ComputeServiceAdapter<VirtualMachine, Hardware, Image, Location>>() {
        }).to(VSphereComputeServiceAdapter.class);

        bind(new TypeLiteral<Function<Location, Location>>() {
        }).to(Class.class.cast(IdentityFunction.class));

        bind(new TypeLiteral<Function<Image, Image>>() {
        }).to(Class.class.cast(IdentityFunction.class));

        bind(new TypeLiteral<Function<Hardware, Hardware>>() {
        }).to(Class.class.cast(IdentityFunction.class));

        bind(new TypeLiteral<Function<VirtualMachine, NodeMetadata>>() {
        }).to(VirtualMachineToNodeMetadata.class);

        bind(new TypeLiteral<Supplier<VSphereServiceInstance>>() {
        }).to(CreateAndConnectVSphereClient.class);

        bind(new TypeLiteral<Function<VirtualMachine, SshClient>>() {
        }).to(VirtualMachineToSshClient.class);

        bind(new TypeLiteral<Function<String, VSphereHost>>() {
        }).to(GetRecommendedVSphereHost.class);
    }

    private static final Map<VirtualMachinePowerState, NodeMetadata.Status> toPortableNodeStatus = ImmutableMap
            .<VirtualMachinePowerState, NodeMetadata.Status>builder()
            .put(VirtualMachinePowerState.poweredOff, NodeMetadata.Status.TERMINATED)
            .put(VirtualMachinePowerState.poweredOn, NodeMetadata.Status.RUNNING)
            .put(VirtualMachinePowerState.suspended, NodeMetadata.Status.SUSPENDED).build();


    private static final Map<VirtualMachinePowerState, Image.Status> toPortableImageStatus = ImmutableMap
            .<VirtualMachinePowerState, Image.Status>builder().put(VirtualMachinePowerState.poweredOn, Image.Status.PENDING)
            .put(VirtualMachinePowerState.poweredOff, Image.Status.AVAILABLE)
            .put(VirtualMachinePowerState.suspended, Image.Status.PENDING)
            .build();


    @Override
    protected TemplateOptions provideTemplateOptions(Injector injector, TemplateOptions options) {
        return options.as(VSphereTemplateOptions.class);
    }
}
