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

import com.vmware.vim25.InvalidState
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.TaskDescription
import com.vmware.vim25.TaskFilterSpec
import com.vmware.vim25.TaskInfo
import com.vmware.vim25.mo.ManagedObject
import com.vmware.vim25.mo.Task
import com.vmware.vim25.mo.TaskHistoryCollector

import java.rmi.RemoteException

interface TaskManagerApi {

    val description: TaskDescription

    val maxCollector: Int

    val recentTasks: List<Task>

    @Throws(InvalidState::class, RuntimeFault::class, RemoteException::class)
    fun createCollectorForTasks(filter: TaskFilterSpec): TaskHistoryCollector

    /**
     * SDK2.5 signature for back compatibility
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun createTask(obj: ManagedObject, taskTypeId: String, initiatedBy: String, cancelable: Boolean): TaskInfo

    /**
     * SDK4.0 signature
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun createTask(obj: ManagedObject, taskTypeId: String, initiatedBy: String, cancelable: Boolean, parentTaskKey: String): TaskInfo
}
