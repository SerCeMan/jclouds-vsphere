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


import com.vmware.vim25.Event
import com.vmware.vim25.EventArgDesc
import com.vmware.vim25.EventDescription
import com.vmware.vim25.EventFilterSpec
import com.vmware.vim25.InvalidEvent
import com.vmware.vim25.InvalidState
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.TaskInfo
import com.vmware.vim25.mo.EventHistoryCollector
import com.vmware.vim25.mo.ManagedEntity

import java.rmi.RemoteException

interface EventManagerApi {
    val description: EventDescription

    val latestEvent: Event

    val maxCollector: Int

    @Throws(InvalidState::class, RuntimeFault::class, RemoteException::class)
    fun createCollectorForEvents(filter: EventFilterSpec): EventHistoryCollector

    @Throws(RuntimeFault::class, RemoteException::class)
    fun logUserEvent(entity: ManagedEntity, msg: String)

    @Throws(InvalidEvent::class, RuntimeFault::class, RemoteException::class)
    fun postEvent(eventToPost: Event, taskInfo: TaskInfo)

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryEvents(filter: EventFilterSpec): List<Event>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun retrieveArgumentDescription(eventTypeId: String): List<EventArgDesc>
}
