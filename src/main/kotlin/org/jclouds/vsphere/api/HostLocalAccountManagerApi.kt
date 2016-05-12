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
import com.vmware.vim25.HostAccountSpec
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.UserNotFound

import java.rmi.RemoteException

interface HostLocalAccountManagerApi {
    @Throws(AlreadyExists::class, UserNotFound::class, RuntimeFault::class, RemoteException::class)
    fun assignUserToGroup(user: String, group: String)

    @Throws(AlreadyExists::class, RuntimeFault::class, RemoteException::class)
    fun createGroup(group: HostAccountSpec)

    @Throws(AlreadyExists::class, RuntimeFault::class, RemoteException::class)
    fun createUser(user: HostAccountSpec)

    @Throws(UserNotFound::class, RuntimeFault::class, RemoteException::class)
    fun removeGroup(groupName: String)

    @Throws(UserNotFound::class, RuntimeFault::class, RemoteException::class)
    fun removeUser(userName: String)

    @Throws(UserNotFound::class, RuntimeFault::class, RemoteException::class)
    fun unassignUserFromGroup(user: String, group: String)

    @Throws(AlreadyExists::class, UserNotFound::class, RuntimeFault::class, RemoteException::class)
    fun updateUser(user: HostAccountSpec)
}
