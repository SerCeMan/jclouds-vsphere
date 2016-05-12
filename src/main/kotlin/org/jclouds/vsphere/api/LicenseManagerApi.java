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

import com.vmware.vim25.CannotAccessLocalSource;
import com.vmware.vim25.InvalidLicense;
import com.vmware.vim25.InvalidState;
import com.vmware.vim25.KeyValue;
import com.vmware.vim25.LicenseAvailabilityInfo;
import com.vmware.vim25.LicenseDiagnostics;
import com.vmware.vim25.LicenseFeatureInfo;
import com.vmware.vim25.LicenseManagerEvaluationInfo;
import com.vmware.vim25.LicenseManagerLicenseInfo;
import com.vmware.vim25.LicenseServerUnavailable;
import com.vmware.vim25.LicenseSource;
import com.vmware.vim25.LicenseUsageInfo;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.LicenseAssignmentManager;

import java.rmi.RemoteException;

public interface LicenseManagerApi {
   /**
    * @deprecated in SDK4.0
    */
   @Deprecated
   LicenseDiagnostics getDiagnostics();

   LicenseManagerEvaluationInfo getEvaluation();

   LicenseAssignmentManager getLicenseAssignmentManager();

   LicenseManagerLicenseInfo[] getLicenses();

   /**
    * @deprecated in SDK4.0
    */
   @Deprecated
   LicenseFeatureInfo[] getFeatureInfo();

   /**
    * @deprecated in SDK4.0
    */
   @Deprecated
   String getLicensedEdition();

   /**
    * @deprecated in SDK4.0
    */
   @Deprecated
   LicenseSource getSource();

   /**
    * @deprecated in SDK4.0
    */
   @Deprecated
   boolean getSourceAvailable();

   /**
    * @since SDK4.0
    */
   LicenseManagerLicenseInfo addLicense(String licenseKey, KeyValue[] labels) throws RuntimeFault, RemoteException;

   /**
    * @since SDK4.0
    */
   LicenseManagerLicenseInfo decodeLicense(String licenseKey) throws RuntimeFault, RemoteException;

   boolean checkLicenseFeature(HostSystem host, String featureKey) throws InvalidState, RuntimeFault, RemoteException;

   void configureLicenseSource(HostSystem host, LicenseSource licenseSource) throws CannotAccessLocalSource, LicenseServerUnavailable, InvalidLicense, RuntimeFault, RemoteException;

   /**
    * @deprecated in SDK4.0
    */
   @Deprecated
   void disableFeature(HostSystem host, String featureKey) throws LicenseServerUnavailable, InvalidState, RuntimeFault, RemoteException;

   void enableFeature(HostSystem host, String featureKey) throws LicenseServerUnavailable, InvalidState, RuntimeFault, RemoteException;

   LicenseAvailabilityInfo[] queryLicenseSourceAvailability(HostSystem host) throws RuntimeFault, RemoteException;

   LicenseUsageInfo queryLicenseUsage(HostSystem host) throws RuntimeFault, RemoteException;

   LicenseFeatureInfo[] querySupportedFeatures(HostSystem host) throws RuntimeFault, RemoteException;

   /**
    * @since SDK4.0
    */
   void removeLicense(String licenseKey) throws RuntimeFault, RemoteException;

   /**
    * @since SDK4.0
    */
   void removeLicenseLabel(String licenseKey, String labelKey) throws RuntimeFault, RemoteException;

   /**
    * @since SDK4.0
    */
   void updateLicense(String licenseKey, KeyValue[] labels) throws RuntimeFault, RemoteException;

   /**
    * @since SDK4.0
    */
   void updateLicenseLabel(String licenseKey, String labelKey, String labelValue) throws RuntimeFault, RemoteException;

   void setLicenseEdition(HostSystem host, String featureKey) throws LicenseServerUnavailable, InvalidState, RuntimeFault, RemoteException;

}
