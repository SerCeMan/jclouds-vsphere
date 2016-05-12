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

import org.jclouds.rest.annotations.Delegate

import java.io.Closeable

/**
 * Provides access to vSphere resources via their REST API.
 *
 *

 * @see [](https://communities.vmware.com/community/vmtn/developer/forums/vspherewebsdk)
 */
interface VSphereApi : Closeable {
    val clusterProfileManagerApi: ClusterProfileManagerApi

    val alarmManagerApi: AlarmManagerApi

    val authorizationManagerApi: AuthorizationManagerApi

    val customFieldsManagerApi: CustomFieldsManagerApi

    val customizationSpecManagerApi: CustomizationSpecManagerApi

    val eventManagerApi: EventManagerApi

    val diagnosticManagerApi: DiagnosticManagerApi

    val distributedVirtualSwitchManagerApi: DistributedVirtualSwitchManagerApi

    val extensionManagerApi: ExtensionManagerApi

    val guestOperationsManagerApi: GuestOperationsManagerApi

    val accountManagerApi: HostLocalAccountManagerApi

    val licenseManagerApi: LicenseManagerApi

    val localizationManagerApi: LocalizationManagerApi

    val performanceManagerApi: PerformanceManagerApi

    val profileComplianceManagerApi: ProfileComplianceManagerApi

    val scheduledTaskManagerApi: ScheduledTaskManagerApi

    val sessionManagerApi: SessionManagerApi

    val hostProfileManagerApi: HostProfileManagerApi

    val ipPoolManagerApi: IpPoolManagerApi

    val taskManagerApi: TaskManagerApi

    val viewManagerApi: ViewManagerApi

    val virtualDiskManagerApi: VirtualDiskManagerApi

    val optionManagerApi: OptionManagerApi
}
