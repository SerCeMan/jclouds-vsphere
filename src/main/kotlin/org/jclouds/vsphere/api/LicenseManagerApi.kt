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

import com.vmware.vim25.CannotAccessLocalSource
import com.vmware.vim25.InvalidLicense
import com.vmware.vim25.InvalidState
import com.vmware.vim25.KeyValue
import com.vmware.vim25.LicenseAvailabilityInfo
import com.vmware.vim25.LicenseDiagnostics
import com.vmware.vim25.LicenseFeatureInfo
import com.vmware.vim25.LicenseManagerEvaluationInfo
import com.vmware.vim25.LicenseManagerLicenseInfo
import com.vmware.vim25.LicenseServerUnavailable
import com.vmware.vim25.LicenseSource
import com.vmware.vim25.LicenseUsageInfo
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.mo.HostSystem
import com.vmware.vim25.mo.LicenseAssignmentManager

import java.rmi.RemoteException

interface LicenseManagerApi {

    val diagnostics: LicenseDiagnostics

    val evaluation: LicenseManagerEvaluationInfo

    val licenseAssignmentManager: LicenseAssignmentManager

    val licenses: Array<LicenseManagerLicenseInfo>


    val featureInfo: Array<LicenseFeatureInfo>


    val licensedEdition: String


    val source: LicenseSource


    val sourceAvailable: Boolean

    /**
     * @since SDK4.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun addLicense(licenseKey: String, labels: Array<KeyValue>): LicenseManagerLicenseInfo

    /**
     * @since SDK4.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun decodeLicense(licenseKey: String): LicenseManagerLicenseInfo

    @Throws(InvalidState::class, RuntimeFault::class, RemoteException::class)
    fun checkLicenseFeature(host: HostSystem, featureKey: String): Boolean

    @Throws(CannotAccessLocalSource::class, LicenseServerUnavailable::class, InvalidLicense::class, RuntimeFault::class, RemoteException::class)
    fun configureLicenseSource(host: HostSystem, licenseSource: LicenseSource)


    @Deprecated("in SDK4.0")
    @Throws(LicenseServerUnavailable::class, InvalidState::class, RuntimeFault::class, RemoteException::class)
    fun disableFeature(host: HostSystem, featureKey: String)

    @Throws(LicenseServerUnavailable::class, InvalidState::class, RuntimeFault::class, RemoteException::class)
    fun enableFeature(host: HostSystem, featureKey: String)

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryLicenseSourceAvailability(host: HostSystem): Array<LicenseAvailabilityInfo>

    @Throws(RuntimeFault::class, RemoteException::class)
    fun queryLicenseUsage(host: HostSystem): LicenseUsageInfo

    @Throws(RuntimeFault::class, RemoteException::class)
    fun querySupportedFeatures(host: HostSystem): Array<LicenseFeatureInfo>

    /**
     * @since SDK4.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun removeLicense(licenseKey: String)

    /**
     * @since SDK4.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun removeLicenseLabel(licenseKey: String, labelKey: String)

    /**
     * @since SDK4.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun updateLicense(licenseKey: String, labels: Array<KeyValue>)

    /**
     * @since SDK4.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun updateLicenseLabel(licenseKey: String, labelKey: String, labelValue: String)

    @Throws(LicenseServerUnavailable::class, InvalidState::class, RuntimeFault::class, RemoteException::class)
    fun setLicenseEdition(host: HostSystem, featureKey: String)

}
