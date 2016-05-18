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
package org.jclouds.vsphere

import org.jclouds.Constants.PROPERTY_SESSION_INTERVAL
import org.jclouds.compute.ComputeServiceContext
import org.jclouds.reflect.Reflection2.typeToken
import org.jclouds.rest.internal.BaseHttpApiMetadata
import org.jclouds.vsphere.api.VSphereApi
import org.jclouds.vsphere.compute.config.VSphereComputeServiceContextModule
import org.jclouds.vsphere.config.VSphereConstants
import java.net.URI

/**
 * Implementation of [BaseHttpApiMetadata] for vSphere 5.1 API
 */
class VSphereApiMetadata(builder: VBuilder) : BaseHttpApiMetadata<VSphereApi>(builder) {

    constructor() : this(VBuilder()) {
    }

    override fun toBuilder() = VBuilder().fromApiMetadata(this)

    class VBuilder() : BaseHttpApiMetadata.Builder<VSphereApi, VBuilder>() {
        init {
            id("vsphere")
                    .name("vSphere 5.1 API")
                    .identityName("user")
                    .credentialName("password")
                    .endpointName("ESXi endpoint or vCenter server")
                    .documentation(URI.create("http://www.vmware.com/support/pubs/vcd_pubs.html"))
                    .version("5.1")
                    .defaultProperties(defaultProps())
                    .view(typeToken(ComputeServiceContext::class.java))
                    .defaultModules(setOf(VSphereComputeServiceContextModule::class.java))
        }

        override fun build() = VSphereApiMetadata(this)
        override fun self() = this
    }

    companion object {
        fun defaultProps() = BaseHttpApiMetadata.defaultProperties().apply {
            setProperty("jclouds.dns_name_length_min", "1")
            setProperty("jclouds.dns_name_length_max", "80")
            setProperty(PROPERTY_SESSION_INTERVAL, "300")
            setProperty(VSphereConstants.JCLOUDS_VSPHERE_VM_PASSWORD, "master")
        }
    }
}
