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

package org.jclouds.vsphere.api;

import com.vmware.vim25.AnswerFile;
import com.vmware.vim25.AnswerFileCreateSpec;
import com.vmware.vim25.AnswerFileStatusResult;
import com.vmware.vim25.AnswerFileUpdateFailed;
import com.vmware.vim25.ApplyProfile;
import com.vmware.vim25.HostApplyProfile;
import com.vmware.vim25.HostConfigFailed;
import com.vmware.vim25.HostConfigSpec;
import com.vmware.vim25.HostProfileManagerConfigTaskList;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.ProfileDeferredPolicyOptionParameter;
import com.vmware.vim25.ProfileMetadata;
import com.vmware.vim25.ProfileProfileStructure;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.Profile;
import com.vmware.vim25.mo.Task;

import java.rmi.RemoteException;
import java.util.List;

public interface HostProfileManagerApi {
   /**
    * SDK4.1 signature for back compatibility
    */
   Task applyHostConfigTask(HostSystem host, HostConfigSpec configSpec) throws HostConfigFailed, InvalidState, RuntimeFault, RemoteException;

   /**
    * SDK5.0 signature
    */
   Task applyHostConfigTask(HostSystem host, HostConfigSpec configSpec, List<ProfileDeferredPolicyOptionParameter> userInputs) throws HostConfigFailed, InvalidState, RuntimeFault, RemoteException;

   /**
    * @since SDK5.0
    */
   Task checkAnswerFileStatusTask(List<HostSystem> hosts) throws RuntimeFault, RemoteException;

   /**
    * SDK4.1 signature for back compatibility
    */
   ApplyProfile createDefaultProfile(String profileType) throws RuntimeFault, RemoteException;

   /**
    * SDK5.0 signature
    */
   ApplyProfile createDefaultProfile(String profileType, String profileTypeName, Profile profile) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.0
    */
   Task exportAnswerFileTask(HostSystem host) throws RuntimeFault, RemoteException;

   HostProfileManagerConfigTaskList generateConfigTaskList(HostConfigSpec configSpec, HostSystem host) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.0
    */
   List<AnswerFileStatusResult> queryAnswerFileStatus(List<HostSystem> hosts) throws RuntimeFault, RemoteException;

   /**
    * SDK4.1 signature for back compatibility
    */
   List<ProfileMetadata> queryHostProfileMetadata(List<String> profileName) throws RuntimeFault, RemoteException;

   /**
    * SDK5.0 signature
    */
   List<ProfileMetadata> queryHostProfileMetadata(List<String> profileNames, Profile profile) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.0
    */
   ProfileProfileStructure queryProfileStructure(Profile profile) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.0
    */
   AnswerFile retrieveAnswerFile(HostSystem host) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.1
    */
   AnswerFile retrieveAnswerFileForProfile(HostSystem host, HostApplyProfile applyProfile) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.0
    */
   Task updateAnswerFileTask(HostSystem host, AnswerFileCreateSpec configSpec) throws AnswerFileUpdateFailed, RuntimeFault, RemoteException;
}
