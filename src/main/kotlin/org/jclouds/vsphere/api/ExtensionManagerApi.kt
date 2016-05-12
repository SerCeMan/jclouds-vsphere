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


import com.vmware.vim25.Extension
import com.vmware.vim25.ExtensionManagerIpAllocationUsage
import com.vmware.vim25.NotFound
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.mo.ManagedEntity

import java.rmi.RemoteException

interface ExtensionManagerApi {
    /**
     * Retrieve all the registered plugins objects

     * @return List of extension objects. If no extension found, an empty array is returned.
     */
    val extensionList: List<Extension>

    /**
     * @since SDK5.1
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryExtensionIpAllocationUsage(extensionKeys: List<String>): List<ExtensionManagerIpAllocationUsage>

    /**
     * @since SDK5.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryManagedBy(extensionKey: String): List<ManagedEntity>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun setPublicKey(extensionKey: String, publicKey: String)

    /**
     * Un-register an existing plugin
     * If `keyStr` is null then a `NullPointerException`
     * is thrown.

     * @param keyStr The unique key of the plugin
     * *
     * @throws RemoteException
     * *
     * @throws RuntimeFault
     * *
     * @throws com.vmware.vim25.NotFound either because of the web service itself, or because of the service
     * *                                   provider unable to handle the request.
     */
    @Throws(NotFound::class, RuntimeFault::class, RemoteException::class)
    fun unregisterExtension(keyStr: String)

    /**
     * Update an existing plugin with modified information
     * If `extension` is null then a `NullPointerException` is thrown.

     * @param extension The extension object with updated information
     * *
     * @throws RemoteException
     * *
     * @throws RuntimeFault
     * *
     * @throws NotFound        either because of the web service itself, or because of the service
     * *                         provider unable to handle the request.
     */
    @Throws(NotFound::class, RuntimeFault::class, RemoteException::class)
    fun updateExtension(extension: Extension)

    /**
     * Register a new plugin
     * If `extension` is null then a `NullPointerException` is thrown.

     * @param extension The extension object to be registered
     * *
     * @throws RemoteException
     * *
     * @throws RuntimeFault    either because of the web service itself, or because of the service
     * *                         provider unable to handle the request.
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun registerExtension(extension: Extension)

    /**
     * Find the extension based on the unique key of the plugin
     * If `keyStr` is null then a `NullPointerException`

     * @param keyStr The unique key for the plugin
     * *
     * @return The extension object found with the unique key
     * *
     * @throws RemoteException
     * *
     * @throws RuntimeFault
     * *
     * @throws RemoteException if something is wrong with web service call,
     * *                         either because of the web service itself, or because of the service
     * *                         provider unable to handle the request.
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun findExtension(keyStr: String): Extension
}
