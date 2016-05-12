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

package org.jclouds.vsphere.api

import com.vmware.vim25.AnswerFile
import com.vmware.vim25.AnswerFileCreateSpec
import com.vmware.vim25.AnswerFileStatusResult
import com.vmware.vim25.AnswerFileUpdateFailed
import com.vmware.vim25.ApplyProfile
import com.vmware.vim25.HostApplyProfile
import com.vmware.vim25.HostConfigFailed
import com.vmware.vim25.HostConfigSpec
import com.vmware.vim25.HostProfileManagerConfigTaskList
import com.vmware.vim25.InvalidState
import com.vmware.vim25.ProfileDeferredPolicyOptionParameter
import com.vmware.vim25.ProfileMetadata
import com.vmware.vim25.ProfileProfileStructure
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.mo.HostSystem
import com.vmware.vim25.mo.Profile
import com.vmware.vim25.mo.Task

import java.rmi.RemoteException

interface HostProfileManagerApi {
    /**
     * SDK4.1 signature for back compatibility
     */
    @Throws(HostConfigFailed::class, InvalidState::class, RuntimeFault::class, RemoteException::class)
    fun applyHostConfigTask(host: HostSystem, configSpec: HostConfigSpec): Task

    /**
     * SDK5.0 signature
     */
    @Throws(HostConfigFailed::class, InvalidState::class, RuntimeFault::class, RemoteException::class)
    fun applyHostConfigTask(host: HostSystem, configSpec: HostConfigSpec, userInputs: List<ProfileDeferredPolicyOptionParameter>): Task

    /**
     * @since SDK5.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun checkAnswerFileStatusTask(hosts: List<HostSystem>): Task

    /**
     * SDK4.1 signature for back compatibility
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun createDefaultProfile(profileType: String): ApplyProfile

    /**
     * SDK5.0 signature
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun createDefaultProfile(profileType: String, profileTypeName: String, profile: Profile): ApplyProfile

    /**
     * @since SDK5.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun exportAnswerFileTask(host: HostSystem): Task

    @Throws(RuntimeFault::class, RemoteException::class)
    fun generateConfigTaskList(configSpec: HostConfigSpec, host: HostSystem): HostProfileManagerConfigTaskList

    /**
     * @since SDK5.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryAnswerFileStatus(hosts: List<HostSystem>): List<AnswerFileStatusResult>

    /**
     * SDK4.1 signature for back compatibility
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryHostProfileMetadata(profileName: List<String>): List<ProfileMetadata>

    /**
     * SDK5.0 signature
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryHostProfileMetadata(profileNames: List<String>, profile: Profile): List<ProfileMetadata>

    /**
     * @since SDK5.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryProfileStructure(profile: Profile): ProfileProfileStructure

    /**
     * @since SDK5.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun retrieveAnswerFile(host: HostSystem): AnswerFile

    /**
     * @since SDK5.1
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun retrieveAnswerFileForProfile(host: HostSystem, applyProfile: HostApplyProfile): AnswerFile

    /**
     * @since SDK5.0
     */
    @Throws(AnswerFileUpdateFailed::class, RuntimeFault::class, RemoteException::class)
    fun updateAnswerFileTask(host: HostSystem, configSpec: AnswerFileCreateSpec): Task
}
