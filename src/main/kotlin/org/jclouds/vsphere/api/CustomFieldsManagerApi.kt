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

import com.vmware.vim25.CustomFieldDef
import com.vmware.vim25.DuplicateName
import com.vmware.vim25.InvalidPrivilege
import com.vmware.vim25.PrivilegePolicyDef
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.mo.ManagedEntity

import java.rmi.RemoteException


interface CustomFieldsManagerApi {
    val field: List<CustomFieldDef>

    @Throws(DuplicateName::class, InvalidPrivilege::class, RuntimeFault::class, RemoteException::class)
    fun addCustomFieldDef(name: String, moType: String, fieldDefPolicy: PrivilegePolicyDef,
                          fieldPolicy: PrivilegePolicyDef): CustomFieldDef

    @Throws(RuntimeFault::class, RemoteException::class)
    fun removeCustomFieldDef(key: Int)

    @Throws(DuplicateName::class, RuntimeFault::class, RemoteException::class)
    fun renameCustomFieldDef(key: Int, name: String)

    @Throws(RuntimeFault::class, RemoteException::class)
    fun setField(entity: ManagedEntity, key: Int, value: String)
}
