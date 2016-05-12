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

package org.jclouds.vsphere.functions

import com.google.common.base.Function
import com.google.common.base.Strings
import com.google.common.base.Supplier
import com.google.inject.Inject
import com.vmware.vim25.mo.Folder
import com.vmware.vim25.mo.InventoryNavigator
import com.vmware.vim25.mo.VirtualMachine
import org.jclouds.vsphere.domain.VSphereServiceInstance
import javax.inject.Singleton


fun VirtualMachine.findFolder(serviceInstance: Supplier<VSphereServiceInstance>, folderName: String?): Folder {
    val master = this;
    if (Strings.isNullOrEmpty(folderName))
        return master.parent as Folder
    val instance = serviceInstance.get()
    val entity = InventoryNavigator(instance.instance.rootFolder).searchManagedEntity("Folder", folderName)
    return entity as Folder
}


@Singleton
class FolderNameToFolderManagedEntity
@Inject
constructor(val serviceInstance: Supplier<VSphereServiceInstance>, private val master: VirtualMachine) : Function<String, Folder> {

    override fun apply(folderName: String?): Folder? {
        if (Strings.isNullOrEmpty(folderName))
            return master.parent as Folder
        val instance = serviceInstance.get()
        val entity = InventoryNavigator(instance.instance.rootFolder).searchManagedEntity("Folder", folderName)
        return entity as Folder
    }
}
