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

import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ContainerView;
import com.vmware.vim25.mo.InventoryView;
import com.vmware.vim25.mo.ListView;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.ManagedObject;
import com.vmware.vim25.mo.View;

import java.rmi.RemoteException;
import java.util.List;

public interface ViewManagerApi {
   List<View> getViewList();

   ContainerView createContainerView(ManagedEntity container, List<String> type, boolean recursive) throws RuntimeFault, RemoteException;

   InventoryView createInventoryView() throws RuntimeFault, RemoteException;

   ListView createListView(List<ManagedObject> mos) throws RuntimeFault, RemoteException;

   ListView createListViewFromView(View view) throws RuntimeFault, RemoteException;
}
