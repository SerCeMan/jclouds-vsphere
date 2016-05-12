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

import com.vmware.vim25.AlarmDescription
import com.vmware.vim25.AlarmExpression
import com.vmware.vim25.AlarmSpec
import com.vmware.vim25.AlarmState
import com.vmware.vim25.DuplicateName
import com.vmware.vim25.InvalidName
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.mo.Alarm
import com.vmware.vim25.mo.ManagedEntity

import java.rmi.RemoteException

/**
 */
interface AlarmManagerApi {
    val defaultExpression: List<AlarmExpression>

    val description: AlarmDescription

    /**
     * @since 4.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun acknowledgeAlarm(alarm: Alarm, entity: ManagedEntity)

    /**
     * @since 4.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun areAlarmActionsEnabled(entity: ManagedEntity): Boolean

    /**
     * @since 4.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun enableAlarmActions(entity: ManagedEntity, enabled: Boolean)

    @Throws(InvalidName::class, DuplicateName::class, RuntimeFault::class, RemoteException::class)
    fun createAlarm(me: ManagedEntity, `as`: AlarmSpec): Alarm

    @Throws(RuntimeFault::class, RemoteException::class)
    fun getAlarm(me: ManagedEntity): List<Alarm>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun getAlarmState(me: ManagedEntity): List<AlarmState>
}
