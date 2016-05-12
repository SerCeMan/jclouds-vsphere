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
class VSphereTemplateOptions : TemplateOptions(), Cloneable {
    var domain: String? = null
    var description: String? = null
    var customizationScript: String? = null
    var addressType: String? = null
    var datacenterName = "default"
    var resourcePool: String? = null
    var isoFileName: String? = null
    var flpFileName: String? = null
    var postConfiguration = true
    var distributedVirtualSwitch = false
    var waitOnPort: Int? = null
    var vmFolder: String? = null
    var extraConfig: Map<String, String>? = null

    override fun clone(): VSphereTemplateOptions {
        val options = VSphereTemplateOptions()
        copyTo(options)
        return options
    }

    override fun copyTo(to: TemplateOptions) {
        super.copyTo(to)
        if (to is VSphereTemplateOptions) {
            val eTo = VSphereTemplateOptions::class.java.cast(to)
            if (customizationScript != null)
                eTo.customizationScript = customizationScript!!
            if (description != null)
                eTo.description = (description as String)
            if (addressType != null)
                eTo.addressType = (addressType as String)
            if (isoFileName != null)
                eTo.isoFileName = (isoFileName!!)
            if (flpFileName != null)
                eTo.flpFileName = (flpFileName!!)
            eTo.datacenterName = datacenterName
            if (waitOnPort != null)
                eTo.waitOnPort = (waitOnPort)
            if (vmFolder != null)
                eTo.vmFolder = vmFolder!!
            if (resourcePool != null) {
                eTo.resourcePool = resourcePool
            }
            if (extraConfig != null) {
                eTo.extraConfig = extraConfig
            }
            if(domain != null) {
                eTo.domain = domain
            }
            eTo.postConfiguration = (postConfiguration)
            eTo.distributedVirtualSwitch = (distributedVirtualSwitch)
        }
    }


    override fun hashCode(): Int{
        var result = super.hashCode()
        result += 31 * result + (description?.hashCode() ?: 0)
        result += 31 * result + (customizationScript?.hashCode() ?: 0)
        result += 31 * result + (addressType?.hashCode() ?: 0)
        result += 31 * result + datacenterName.hashCode()
        result += 31 * result + (resourcePool?.hashCode() ?: 0)
        result += 31 * result + (isoFileName?.hashCode() ?: 0)
        result += 31 * result + (flpFileName?.hashCode() ?: 0)
        result += 31 * result + postConfiguration.hashCode()
        result += 31 * result + distributedVirtualSwitch.hashCode()
        result += 31 * result + (waitOnPort ?: 0)
        result += 31 * result + (vmFolder?.hashCode() ?: 0)
        result += 31 * result + (extraConfig?.hashCode() ?: 0)
        return result
    }

    override fun equals(other: Any?): Boolean{
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        if (!super.equals(other)) return false
        other as VSphereTemplateOptions
        if (description != other.description) return false
        if (customizationScript != other.customizationScript) return false
        if (addressType != other.addressType) return false
        if (datacenterName != other.datacenterName) return false
        if (resourcePool != other.resourcePool) return false
        if (isoFileName != other.isoFileName) return false
        if (flpFileName != other.flpFileName) return false
        if (postConfiguration != other.postConfiguration) return false
        if (distributedVirtualSwitch != other.distributedVirtualSwitch) return false
        if (waitOnPort != other.waitOnPort) return false
        if (vmFolder != other.vmFolder) return false
        if (extraConfig != other.extraConfig) return false
        return true
    }
}
