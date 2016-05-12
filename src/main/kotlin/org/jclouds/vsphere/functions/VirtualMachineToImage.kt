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
import com.vmware.vim25.VirtualMachinePowerState
import com.vmware.vim25.mo.VirtualMachine
import org.jclouds.compute.domain.Image
import org.jclouds.compute.domain.Image.Status
import org.jclouds.compute.domain.ImageBuilder
import org.jclouds.compute.domain.OperatingSystem
import org.jclouds.compute.domain.OsFamily
import org.jclouds.compute.util.ComputeServiceUtils.parseOsFamilyOrUnrecognized
import org.jclouds.compute.util.ComputeServiceUtils.parseVersionOrReturnEmptyString
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class VirtualMachineToImage
@Inject
constructor(val toPortableImageStatus: Provider<Map<VirtualMachinePowerState, Status>>,
            val osVersionMap: Provider<Map<OsFamily, Map<String, String>>>) : Function<VirtualMachine, Image> {

    override fun apply(from: VirtualMachine?): Image? {
        if (from == null || from.config == null) {
            val os = OperatingSystem.builder().description("null").family(OsFamily.UNRECOGNIZED).version("null").is64Bit(true).arch("null").build()
            return ImageBuilder().id("null").name("null").description("null").operatingSystem(os).status(toPortableImageStatus.get()[from!!.runtime.getPowerState()]).build()
        }
        val guestFamily = from.config.getGuestId()
        // TODO every template should contain this annotation ...
        val annotation = from.config.getAnnotation()
        val family = if (annotation == null) OsFamily.UNRECOGNIZED else parseOsFamilyOrUnrecognized(annotation)
        val description = annotation ?: guestFamily
        val version = parseVersionOrReturnEmptyString(family, annotation, osVersionMap.get())
        val os = OperatingSystem.builder().description(description).family(family).version(version).is64Bit(true).arch("x86_64").build()
        return ImageBuilder().id(from.name).name(from.name).description(from.name).operatingSystem(os).status(toPortableImageStatus.get()[from.runtime.getPowerState()]).build()
    }
}
