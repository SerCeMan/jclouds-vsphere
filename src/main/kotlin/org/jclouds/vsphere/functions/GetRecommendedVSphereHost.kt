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
import com.google.common.base.Supplier
import com.google.common.collect.Iterables
import com.vmware.vim25.mo.*
import org.jclouds.compute.reference.ComputeServiceConstants
import org.jclouds.logging.Logger
import org.jclouds.vsphere.domain.VSphereHost
import org.jclouds.vsphere.domain.VSphereServiceInstance
import javax.annotation.Resource
import javax.inject.Inject
import javax.inject.Named
import java.util.Arrays

import com.google.common.base.Preconditions.checkNotNull

/**
 * Created by igreenfi on 26/10/2014.
 */
class GetRecommendedVSphereHost
@Inject
constructor(val serviceInstance: Supplier<VSphereServiceInstance>) : Function<String, VSphereHost> {
    override fun apply(dataCenter: String?): VSphereHost {
        serviceInstance.get().use { instance ->
            val clusterEntities = InventoryNavigator(instance.instance.rootFolder).searchManagedEntities("ComputeResource")
            val clusterComputeResources = Iterables.transform(Arrays.asList(*clusterEntities)) { input -> input as ComputeResource }

            var curHostSystem: HostSystem? = null
            for (cluster in clusterComputeResources) {
                if (cluster.name == dataCenter || dataCenter == "default") {
                    val hostSystems = cluster.hosts
                    var maxMemory = Integer.MIN_VALUE.toLong()
                    for (hostSystem in hostSystems) {
                        val currentMemory = hostSystem.summary.getQuickStats().getOverallMemoryUsage()!!
                        val currentTotalMemory = hostSystem.config.getSystemResources().getConfig().getMemoryAllocation().getLimit()!!
                        if (currentTotalMemory - currentMemory > maxMemory) {
                            curHostSystem = hostSystem
                            maxMemory = currentTotalMemory - currentMemory
                        }
                    }
                    break
                }
            }
            return VSphereHost(curHostSystem!!.name, serviceInstance.get())
        }
    }
}
