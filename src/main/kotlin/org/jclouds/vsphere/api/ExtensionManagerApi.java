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


import com.vmware.vim25.Extension;
import com.vmware.vim25.ExtensionManagerIpAllocationUsage;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ManagedEntity;

import java.rmi.RemoteException;
import java.util.List;

public interface ExtensionManagerApi {
   /**
    * Retrieve all the registered plugins objects
    *
    * @return List of extension objects. If no extension found, an empty array is returned.
    */
   List<Extension> getExtensionList();

   /**
    * @since SDK5.1
    */
   List<ExtensionManagerIpAllocationUsage> queryExtensionIpAllocationUsage(List<String> extensionKeys) throws RuntimeFault, RemoteException;

   /**
    * @since SDK5.0
    */
   List<ManagedEntity> queryManagedBy(String extensionKey) throws RuntimeFault, RemoteException;

   void setPublicKey(String extensionKey, String publicKey) throws RuntimeFault, RemoteException;

   /**
    * Un-register an existing plugin
    * If <code>keyStr</code> is null then a <code>NullPointerException</code>
    * is thrown.
    *
    * @param keyStr The unique key of the plugin
    * @throws RemoteException
    * @throws RuntimeFault
    * @throws com.vmware.vim25.NotFound either because of the web service itself, or because of the service
    *                                   provider unable to handle the request.
    */
   void unregisterExtension(String keyStr) throws NotFound, RuntimeFault, RemoteException;

   /**
    * Update an existing plugin with modified information
    * If <code>extension</code> is null then a <code>NullPointerException</code> is thrown.
    *
    * @param extension The extension object with updated information
    * @throws RemoteException
    * @throws RuntimeFault
    * @throws NotFound        either because of the web service itself, or because of the service
    *                         provider unable to handle the request.
    */
   void updateExtension(Extension extension) throws NotFound, RuntimeFault, RemoteException;

   /**
    * Register a new plugin
    * If <code>extension</code> is null then a <code>NullPointerException</code> is thrown.
    *
    * @param extension The extension object to be registered
    * @throws RemoteException
    * @throws RuntimeFault    either because of the web service itself, or because of the service
    *                         provider unable to handle the request.
    */
   void registerExtension(Extension extension) throws RuntimeFault, RemoteException;

   /**
    * Find the extension based on the unique key of the plugin
    * If <code>keyStr</code> is null then a <code>NullPointerException</code>
    *
    * @param keyStr The unique key for the plugin
    * @return The extension object found with the unique key
    * @throws RemoteException
    * @throws RuntimeFault
    * @throws RemoteException if something is wrong with web service call,
    *                         either because of the web service itself, or because of the service
    *                         provider unable to handle the request.
    */
   Extension findExtension(String keyStr) throws RuntimeFault, RemoteException;
}
