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
import com.vmware.vim25.CustomizationFault;
import com.vmware.vim25.CustomizationSpecInfo;
import com.vmware.vim25.CustomizationSpecItem;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.RuntimeFault;

import java.rmi.RemoteException;
import java.util.List;

public interface CustomizationSpecManagerApi {
   byte[] getEncryptionKey();

   List<CustomizationSpecInfo> getInfo();

   void checkCustomizationResources(String guestOs) throws CustomizationFault, RuntimeFault, RemoteException;

   void createCustomizationSpec(CustomizationSpecItem item) throws CustomizationFault, AlreadyExists, RuntimeFault, RemoteException;

   String customizationSpecItemToXml(CustomizationSpecItem item) throws RuntimeFault, RemoteException;

   void deleteCustomizationSpec(String name) throws NotFound, RuntimeFault, RemoteException;

   boolean doesCustomizationSpecExist(String name) throws RuntimeFault, RemoteException;

   void duplicateCustomizationSpec(String name, String newName) throws AlreadyExists, NotFound, RuntimeFault, RemoteException;

   CustomizationSpecItem getCustomizationSpec(String name) throws NotFound, RuntimeFault, RemoteException;

   void overwriteCustomizationSpec(CustomizationSpecItem item) throws NotFound, RuntimeFault, RemoteException;

   void renameCustomizationSpec(String name, String newName) throws AlreadyExists, NotFound, RuntimeFault, RemoteException;

   CustomizationSpecItem xmlToCustomizationSpecItem(String specItemXml) throws CustomizationFault, RuntimeFault, RemoteException;
}
