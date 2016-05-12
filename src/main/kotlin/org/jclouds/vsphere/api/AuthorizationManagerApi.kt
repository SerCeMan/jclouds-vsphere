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

import com.vmware.vim25.AlreadyExists
import com.vmware.vim25.AuthMinimumAdminPermission
import com.vmware.vim25.AuthorizationDescription
import com.vmware.vim25.AuthorizationPrivilege
import com.vmware.vim25.AuthorizationRole
import com.vmware.vim25.InvalidName
import com.vmware.vim25.NotFound
import com.vmware.vim25.Permission
import com.vmware.vim25.RemoveFailed
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.UserNotFound
import com.vmware.vim25.mo.ManagedEntity

import java.rmi.RemoteException

interface AuthorizationManagerApi {
    val description: AuthorizationDescription

    val privilegeList: List<AuthorizationPrivilege>

    val roleList: List<AuthorizationRole>

    @Throws(InvalidName::class, AlreadyExists::class, RuntimeFault::class, RemoteException::class)
    fun addAuthorizationRole(name: String, privIds: List<String>): Int

    /**
     * @since SDK5.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun HasPrivilegeOnEntity(entity: ManagedEntity, sessionId: String, privId: List<String>): List<Boolean>

    @Throws(AuthMinimumAdminPermission::class, NotFound::class, RuntimeFault::class, RemoteException::class)
    fun mergePermissions(srcRoleId: Int, dstRoleId: Int)

    @Throws(RemoveFailed::class, NotFound::class, RuntimeFault::class, RemoteException::class)
    fun removeAuthorizationRole(roleId: Int, failIfUsed: Boolean)

    @Throws(AuthMinimumAdminPermission::class, NotFound::class, RuntimeFault::class, RemoteException::class)
    fun removeEntityPermission(entity: ManagedEntity, user: String, isGroup: Boolean)

    @Throws(AuthMinimumAdminPermission::class, NotFound::class, UserNotFound::class, RuntimeFault::class, RemoteException::class)
    fun resetEntityPermissions(entity: ManagedEntity, permission: List<Permission>)

    @Throws(RuntimeFault::class, RemoteException::class)
    fun retrieveEntityPermissions(entity: ManagedEntity, inherited: Boolean): List<Permission>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun retrieveAllPermissions(): List<Permission>

    @Throws(NotFound::class, RuntimeFault::class, RemoteException::class)
    fun retrieveRolePermissions(roleId: Int): List<Permission>

    @Throws(AuthMinimumAdminPermission::class, NotFound::class, UserNotFound::class, RuntimeFault::class, RemoteException::class)
    fun setEntityPermissions(entity: ManagedEntity, permission: List<Permission>)

    @Throws(InvalidName::class, AlreadyExists::class, NotFound::class, RuntimeFault::class, RemoteException::class)
    fun updateAuthorizationRole(roleId: Int, newName: String, privIds: List<String>)

}
