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

import com.google.common.base.Supplier
import com.google.common.base.Throwables
import com.vmware.vim25.mo.ServiceInstance
import org.jclouds.compute.reference.ComputeServiceConstants
import org.jclouds.domain.Credentials
import org.jclouds.location.Provider
import org.jclouds.logging.Logger
import org.jclouds.vsphere.domain.VSphereServiceInstance

import javax.annotation.Resource
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import java.net.MalformedURLException
import java.net.URI
import java.net.URL
import java.rmi.RemoteException

import com.google.common.base.Preconditions.checkNotNull
import com.google.common.base.Preconditions.checkState

@Singleton
class CreateAndConnectVSphereClient
@Inject
constructor(@Provider val providerSupplier: Supplier<URI>,
            @Provider val credentials: Supplier<Credentials>) : Supplier<VSphereServiceInstance> {

    @Synchronized fun start(): ServiceInstance {
        val provider = providerSupplier.get()
        return ServiceInstance(URL(provider.toASCIIString()), credentials.get().identity, credentials.get().credential, true)
    }

    override fun get() = VSphereServiceInstance(start())
}
