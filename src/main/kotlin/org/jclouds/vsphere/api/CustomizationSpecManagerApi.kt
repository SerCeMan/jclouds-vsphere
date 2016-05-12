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
import com.vmware.vim25.CustomizationFault
import com.vmware.vim25.CustomizationSpecInfo
import com.vmware.vim25.CustomizationSpecItem
import com.vmware.vim25.NotFound
import com.vmware.vim25.RuntimeFault

import java.rmi.RemoteException

interface CustomizationSpecManagerApi {
    val encryptionKey: ByteArray

    val info: List<CustomizationSpecInfo>

    @Throws(CustomizationFault::class, RuntimeFault::class, RemoteException::class)
    fun checkCustomizationResources(guestOs: String)

    @Throws(CustomizationFault::class, AlreadyExists::class, RuntimeFault::class, RemoteException::class)
    fun createCustomizationSpec(item: CustomizationSpecItem)

    @Throws(RuntimeFault::class, RemoteException::class)
    fun customizationSpecItemToXml(item: CustomizationSpecItem): String

    @Throws(NotFound::class, RuntimeFault::class, RemoteException::class)
    fun deleteCustomizationSpec(name: String)

    @Throws(RuntimeFault::class, RemoteException::class)
    fun doesCustomizationSpecExist(name: String): Boolean

    @Throws(AlreadyExists::class, NotFound::class, RuntimeFault::class, RemoteException::class)
    fun duplicateCustomizationSpec(name: String, newName: String)

    @Throws(NotFound::class, RuntimeFault::class, RemoteException::class)
    fun getCustomizationSpec(name: String): CustomizationSpecItem

    @Throws(NotFound::class, RuntimeFault::class, RemoteException::class)
    fun overwriteCustomizationSpec(item: CustomizationSpecItem)

    @Throws(AlreadyExists::class, NotFound::class, RuntimeFault::class, RemoteException::class)
    fun renameCustomizationSpec(name: String, newName: String)

    @Throws(CustomizationFault::class, RuntimeFault::class, RemoteException::class)
    fun xmlToCustomizationSpecItem(specItemXml: String): CustomizationSpecItem
}
