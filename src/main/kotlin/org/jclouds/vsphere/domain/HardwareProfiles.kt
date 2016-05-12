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

package org.jclouds.vsphere.domain

import org.jclouds.compute.domain.*

private fun buildVolume(size: Float) = VolumeBuilder().size(size).type(Volume.Type.LOCAL).build()

enum class HardwareProfiles(ids: String, hypervisor: String, name: String, processor: Processor, ram: Int, volume: Volume) {
    C1_M1_D10(InstanceType.C1_M1_D10, "vSphere", InstanceType.C1_M1_D10, Processor(1.0, 1.0), 1024, buildVolume(20f)),
    C2_M2_D30(InstanceType.C2_M2_D30, "vSphere", InstanceType.C2_M2_D30, Processor(2.0, 1.0), 2 * 1024, buildVolume(30f)),
    C2_M2_D50(InstanceType.C2_M2_D50, "vSphere", InstanceType.C2_M2_D50, Processor(2.0, 1.0), 2 * 1024, buildVolume(50f)),
    C2_M4_D50(InstanceType.C2_M4_D50, "vSphere", InstanceType.C2_M4_D50, Processor(2.0, 2.0), 4 * 1024, buildVolume(50f)),
    C2_M10_D80(InstanceType.C2_M10_D80, "vSphere", InstanceType.C2_M10_D80, Processor(2.0, 2.0), 10 * 1024, buildVolume(80f)),
    C3_M10_D80(InstanceType.C3_M10_D80, "vSphere", InstanceType.C3_M10_D80, Processor(3.0, 2.0), 10 * 1024, buildVolume(80f)),
    C4_M4_D20(InstanceType.C4_M4_D20, "vSphere", InstanceType.C4_M4_D20, Processor(4.0, 2.0), 4 * 1024, buildVolume(20f)),
    C2_M6_D40(InstanceType.C2_M6_D40, "vSphere", InstanceType.C2_M6_D40, Processor(2.0, 2.0), 6 * 1024, buildVolume(40f)),
    C8_M16_D30(InstanceType.C8_M16_D30, "vSphere", InstanceType.C8_M16_D30, Processor(8.0, 2.0), 16 * 1024, buildVolume(30f)),
    C8_M16_D80(InstanceType.C8_M16_D80, "vSphere", InstanceType.C8_M16_D80, Processor(8.0, 2.0), 16 * 1024, buildVolume(80f));

    val hardware: Hardware = HardwareBuilder().ids(ids).hypervisor(hypervisor).name(name).processor(processor).ram(ram).volume(volume).build()

    object InstanceType {
        val C1_M1_D10 = "C1_M1_D10"
        val C2_M2_D30 = "C2_M2_D30"
        val C2_M2_D50 = "C2_M2_D50"
        val C2_M4_D50 = "C2_M4_D50"
        val C2_M10_D80 = "C2_M10_D80"
        val C3_M10_D80 = "C3_M10_D80"
        val C4_M4_D20 = "C4.M4.D20"
        val C2_M6_D40 = "C2.M6.D40"
        val C8_M16_D30 = "C8_M16_D30"
        val C8_M16_D80 = "C8_M16_D80"
    }
}
