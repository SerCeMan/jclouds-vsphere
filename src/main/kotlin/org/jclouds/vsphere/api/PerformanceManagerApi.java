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

import com.vmware.vim25.PerfCompositeMetric;
import com.vmware.vim25.PerfCounterInfo;
import com.vmware.vim25.PerfEntityMetricBase;
import com.vmware.vim25.PerfInterval;
import com.vmware.vim25.PerfMetricId;
import com.vmware.vim25.PerfProviderSummary;
import com.vmware.vim25.PerfQuerySpec;
import com.vmware.vim25.PerformanceDescription;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ManagedEntity;

import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.List;

public interface PerformanceManagerApi {
   PerformanceDescription getDescription();

   List<PerfInterval> getHistoricalInterval();

   List<PerfCounterInfo> getPerfCounter();

   /**
    * @throws RemoteException
    * @throws com.vmware.vim25.RuntimeFault
    * @deprecated use UpdatePerfInterval instead
    */
   @Deprecated
   void createPerfInterval(PerfInterval intervalId) throws RuntimeFault, RemoteException;

   List<PerfMetricId> queryAvailablePerfMetric(ManagedEntity entity, Calendar beginTime, Calendar endTime, Integer intervalId) throws RuntimeFault, RemoteException;

   List<PerfEntityMetricBase> queryPerf(List<PerfQuerySpec> querySpec) throws RuntimeFault, RemoteException;

   PerfCompositeMetric queryPerfComposite(PerfQuerySpec querySpec) throws RuntimeFault, RemoteException;

   List<PerfCounterInfo> queryPerfCounter(List<Integer> counterIds) throws RuntimeFault, RemoteException;

   List<PerfCounterInfo> queryPerfCounterByLevel(int level) throws RuntimeFault, RemoteException;

   PerfProviderSummary queryPerfProviderSummary(ManagedEntity entity) throws RuntimeFault, RemoteException;

   void removePerfInterval(int samplePeriod) throws RuntimeFault, RemoteException;

   void updatePerfInterval(PerfInterval interval) throws RuntimeFault, RemoteException;
}
