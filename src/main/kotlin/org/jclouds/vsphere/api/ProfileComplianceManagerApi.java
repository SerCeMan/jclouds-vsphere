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

import com.vmware.vim25.ComplianceResult;
import com.vmware.vim25.ProfileExpressionMetadata;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Profile;
import com.vmware.vim25.mo.Task;

import java.rmi.RemoteException;
import java.util.List;

public interface ProfileComplianceManagerApi {
   Task checkComplianceTask(List<Profile> profile, List<ManagedEntity> entity) throws RuntimeFault, RemoteException;

   void clearComplianceStatus(List<Profile> profile, List<ManagedEntity> entity) throws RuntimeFault, RemoteException;

   List<ComplianceResult> queryComplianceStatus(List<Profile> profile, List<ManagedEntity> entity) throws RuntimeFault, RemoteException;

   /**
    * SDK4.1 signature for back compatibility
    */
   List<ProfileExpressionMetadata> queryExpressionMetadata(List<String> expressionName) throws RuntimeFault, RemoteException;

   /**
    * SDK5.0 signature
    */
   List<ProfileExpressionMetadata> queryExpressionMetadata(List<String> expressionName, Profile profile) throws RuntimeFault, RemoteException;
}
