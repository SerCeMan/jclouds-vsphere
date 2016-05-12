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

import com.vmware.vim25.PerfCompositeMetric
import com.vmware.vim25.PerfCounterInfo
import com.vmware.vim25.PerfEntityMetricBase
import com.vmware.vim25.PerfInterval
import com.vmware.vim25.PerfMetricId
import com.vmware.vim25.PerfProviderSummary
import com.vmware.vim25.PerfQuerySpec
import com.vmware.vim25.PerformanceDescription
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.mo.ManagedEntity

import java.rmi.RemoteException
import java.util.Calendar

interface PerformanceManagerApi {
    val description: PerformanceDescription

    val historicalInterval: List<PerfInterval>

    val perfCounter: List<PerfCounterInfo>

    /**
     * @throws RemoteException
     * *
     * @throws com.vmware.vim25.RuntimeFault
     * *
     */
    @Deprecated("use UpdatePerfInterval instead")
    @Throws(RuntimeFault::class, RemoteException::class)
    fun createPerfInterval(intervalId: PerfInterval)

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryAvailablePerfMetric(entity: ManagedEntity, beginTime: Calendar, endTime: Calendar, intervalId: Int?): List<PerfMetricId>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryPerf(querySpec: List<PerfQuerySpec>): List<PerfEntityMetricBase>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryPerfComposite(querySpec: PerfQuerySpec): PerfCompositeMetric

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryPerfCounter(counterIds: List<Int>): List<PerfCounterInfo>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryPerfCounterByLevel(level: Int): List<PerfCounterInfo>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryPerfProviderSummary(entity: ManagedEntity): PerfProviderSummary

    @Throws(RuntimeFault::class, RemoteException::class)
    fun removePerfInterval(samplePeriod: Int)

    @Throws(RuntimeFault::class, RemoteException::class)
    fun updatePerfInterval(interval: PerfInterval)
}
