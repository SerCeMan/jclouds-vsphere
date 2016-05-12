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

import com.vmware.vim25.AlreadyExists;
import com.vmware.vim25.AuthMinimumAdminPermission;
import com.vmware.vim25.AuthorizationDescription;
import com.vmware.vim25.AuthorizationPrivilege;
import com.vmware.vim25.AuthorizationRole;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.Permission;
import com.vmware.vim25.RemoveFailed;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.UserNotFound;
import com.vmware.vim25.mo.ManagedEntity;

import java.rmi.RemoteException;
import java.util.List;

public interface AuthorizationManagerApi {
   AuthorizationDescription getDescription();

   List<AuthorizationPrivilege> getPrivilegeList();

   List<AuthorizationRole> getRoleList();

   int addAuthorizationRole(String name, List<String> privIds) throws InvalidName, AlreadyExists, RuntimeFault, RemoteException;

   /**
    * @since SDK5.0
    */
   List<Boolean> HasPrivilegeOnEntity(ManagedEntity entity, String sessionId, List<String> privId) throws RuntimeFault, RemoteException;

   void mergePermissions(int srcRoleId, int dstRoleId) throws AuthMinimumAdminPermission, NotFound, RuntimeFault, RemoteException;

   void removeAuthorizationRole(int roleId, boolean failIfUsed) throws RemoveFailed, NotFound, RuntimeFault, RemoteException;

   void removeEntityPermission(ManagedEntity entity, String user, boolean isGroup) throws AuthMinimumAdminPermission, NotFound, RuntimeFault, RemoteException;

   void resetEntityPermissions(ManagedEntity entity, List<Permission> permission) throws AuthMinimumAdminPermission, NotFound, UserNotFound, RuntimeFault, RemoteException;

   List<Permission> retrieveEntityPermissions(ManagedEntity entity, boolean inherited) throws RuntimeFault, RemoteException;

   List<Permission> retrieveAllPermissions() throws RuntimeFault, RemoteException;

   List<Permission> retrieveRolePermissions(int roleId) throws NotFound, RuntimeFault, RemoteException;

   void setEntityPermissions(ManagedEntity entity, List<Permission> permission) throws AuthMinimumAdminPermission, NotFound, UserNotFound, RuntimeFault, RemoteException;

   void updateAuthorizationRole(int roleId, String newName, List<String> privIds) throws InvalidName, AlreadyExists, NotFound, RuntimeFault, RemoteException;

}
