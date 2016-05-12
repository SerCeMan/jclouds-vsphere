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

import com.vmware.vim25.AlarmDescription;
import com.vmware.vim25.AlarmExpression;
import com.vmware.vim25.AlarmSpec;
import com.vmware.vim25.AlarmState;
import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.InvalidName;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.Alarm;
import com.vmware.vim25.mo.ManagedEntity;

import java.rmi.RemoteException;
import java.util.List;

/**
 */
public interface AlarmManagerApi {
   List<AlarmExpression> getDefaultExpression();

   AlarmDescription getDescription();

   /**
    * @since 4.0
    */
   void acknowledgeAlarm(Alarm alarm, ManagedEntity entity) throws RuntimeFault, RemoteException;

   /**
    * @since 4.0
    */
   boolean areAlarmActionsEnabled(ManagedEntity entity) throws RuntimeFault, RemoteException;

   /**
    * @since 4.0
    */
   void enableAlarmActions(ManagedEntity entity, boolean enabled) throws RuntimeFault, RemoteException;

   Alarm createAlarm(ManagedEntity me, AlarmSpec as) throws InvalidName, DuplicateName, RuntimeFault, RemoteException;

   List<Alarm> getAlarm(ManagedEntity me) throws RuntimeFault, RemoteException;

   List<AlarmState> getAlarmState(ManagedEntity me) throws RuntimeFault, RemoteException;
}
