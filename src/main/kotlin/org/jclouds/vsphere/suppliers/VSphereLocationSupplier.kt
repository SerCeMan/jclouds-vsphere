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
package org.jclouds.vsphere.suppliers

import com.google.common.base.Supplier
import com.google.common.base.Throwables
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Maps
import com.google.common.collect.Sets
import com.vmware.vim25.mo.InventoryNavigator
import com.vmware.vim25.mo.ManagedEntity
import org.jclouds.compute.reference.ComputeServiceConstants
import org.jclouds.domain.Location
import org.jclouds.domain.LocationScope
import org.jclouds.domain.internal.LocationImpl
import org.jclouds.location.suppliers.LocationsSupplier
import org.jclouds.logging.Logger
import org.jclouds.vsphere.domain.VSphereServiceInstance

import javax.annotation.Resource
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

import com.google.common.base.Preconditions.checkNotNull

@Singleton
class VSphereLocationSupplier
@Inject
constructor(val serviceInstance: Supplier<VSphereServiceInstance>) : LocationsSupplier {
    private val clusters: Set<Location>
        get() {
            val hosts = Sets.newHashSet<Location>()
            serviceInstance.get().use { instance ->
                val clusterEntities = InventoryNavigator(instance.instance.rootFolder).searchManagedEntities("ClusterComputeResource")
                for (cluster in clusterEntities) {
                    val location = LocationImpl(LocationScope.ZONE, cluster.name, cluster.name, null, ImmutableSet.of(""), Maps.newHashMap<String, Any>())
                    hosts.add(location)
                }
                hosts.add(LocationImpl(LocationScope.ZONE, "default", "default", null, ImmutableSet.of(""), Maps.newHashMap<String, Any>()))
                return hosts
            }
        }

    override fun get(): Set<Location> = ImmutableSet.copyOf(clusters)
}
