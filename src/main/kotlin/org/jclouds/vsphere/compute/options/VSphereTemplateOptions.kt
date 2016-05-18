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
package org.jclouds.vsphere.compute.options

import org.jclouds.compute.options.TemplateOptions

/**
 * Contains options supported in the `ComputeService#runNode` operation on
 * the "vSphere" provider.
 * Usage The recommended way to instantiate a VSphereTemplateOptions object
 * is to statically import VSphereTemplateOptions.* and invoke a static creation
 * method followed by an instance mutator (if needed):
 *
 * ComputeService client = // get connection
 * templateBuilder.options(inboundPorts(22, 80, 8080, 443));
 * Set set = client.createNodesInGroup(tag, 2, templateBuilder.build());
 *
 */
enum class CloningStrategy { FULL, LINKED}

class VSphereTemplateOptions : TemplateOptions(), Cloneable {
    var domain: String? = null
    var datacenter = "default"
    var resourcePool: String? = null
    var folder: String? = null
    var cloning: CloningStrategy = CloningStrategy.FULL
    var extraConfig: Map<String, String>? = null

    override fun clone() = VSphereTemplateOptions().apply { copyTo(this) }

    override fun copyTo(eTo: TemplateOptions) {
        super.copyTo(eTo)
        if (eTo is VSphereTemplateOptions) {
            if (domain != null) {
                eTo.domain = domain
            }
            eTo.datacenter = datacenter
            if (folder != null) {
                eTo.folder = folder
            }
            if (resourcePool != null) {
                eTo.resourcePool = resourcePool
            }
            eTo.cloning = cloning
            if (extraConfig != null) {
                eTo.extraConfig = extraConfig
            }
        }
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        if (!super.equals(other)) return false
        other as VSphereTemplateOptions
        if (domain != other.domain) return false
        if (datacenter != other.datacenter) return false
        if (resourcePool != other.resourcePool) return false
        if (folder != other.folder) return false
        if (cloning != other.cloning) return false
        if (extraConfig != other.extraConfig) return false
        return true
    }

    override fun hashCode(): Int{
        var result = super.hashCode()
        result += 31 * result + (domain?.hashCode() ?: 0)
        result += 31 * result + datacenter.hashCode()
        result += 31 * result + (resourcePool?.hashCode() ?: 0)
        result += 31 * result + (folder?.hashCode() ?: 0)
        result += 31 * result + cloning.hashCode()
        result += 31 * result + (extraConfig?.hashCode() ?: 0)
        return result
    }
}
