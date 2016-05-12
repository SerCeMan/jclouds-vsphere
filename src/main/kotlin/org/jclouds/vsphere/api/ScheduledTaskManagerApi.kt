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

import com.vmware.vim25.DuplicateName
import com.vmware.vim25.InvalidName
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.ScheduledTaskDescription
import com.vmware.vim25.ScheduledTaskSpec
import com.vmware.vim25.mo.ManagedEntity
import com.vmware.vim25.mo.ManagedObject
import com.vmware.vim25.mo.ScheduledTask

import java.rmi.RemoteException

interface ScheduledTaskManagerApi {
    val descriptioin: ScheduledTaskDescription

    val scheduledTasks: List<ScheduledTask>

    @Throws(InvalidName::class, DuplicateName::class, RuntimeFault::class, RemoteException::class)
    fun createScheduledTask(entity: ManagedEntity, spec: ScheduledTaskSpec): ScheduledTask

    /**
     * @since SDK4.0
     */
    @Throws(InvalidName::class, DuplicateName::class, RuntimeFault::class, RemoteException::class)
    fun createObjectScheduledTask(obj: ManagedObject, spec: ScheduledTaskSpec): ScheduledTask

    @Throws(RuntimeFault::class, RemoteException::class)
    fun retrieveEntityScheduledTask(entity: ManagedEntity): List<ScheduledTask>

    /**
     * @since SDK4.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun retrieveObjectScheduledTask(obj: ManagedObject): List<ScheduledTask>
}
