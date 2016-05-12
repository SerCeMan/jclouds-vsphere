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

import com.vmware.vim25.InvalidLocale;
import com.vmware.vim25.InvalidLogin;
import com.vmware.vim25.NoClientCertificate;
import com.vmware.vim25.NoSubjectName;
import com.vmware.vim25.NotFound;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.SSPIChallenge;
import com.vmware.vim25.SessionManagerGenericServiceTicket;
import com.vmware.vim25.SessionManagerLocalTicket;
import com.vmware.vim25.SessionManagerServiceRequestSpec;
import com.vmware.vim25.UserSession;
import com.vmware.vim25.mo.ServiceInstance;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.List;

public interface SessionManagerApi {
   UserSession getCurrentSession();

   String getDefaultLocale();

   String getMessage();

   List<String> getMessageLocaleList();

   List<UserSession> getSessionList();

   List<String> getSupportedLocaleList();

   SessionManagerLocalTicket acquireLocalTicket(String userName) throws InvalidLogin, RuntimeFault, RemoteException;

   /**
    * @since SDK5.0
    */
   SessionManagerGenericServiceTicket acquireGenericServiceTicket(SessionManagerServiceRequestSpec spec) throws RuntimeFault, RemoteException;

   /**
    * @since SDK4.0
    * You don't need to use this method. Instead, look at the other cloneSession method.
    */
   UserSession cloneSession(String cloneTicket) throws InvalidLogin, RuntimeFault, RemoteException;

   ServiceInstance cloneSession(boolean ignoreCert) throws InvalidLogin, RuntimeFault, RemoteException, MalformedURLException;

   /**
    * @since SDK4.0
    * This method is called in the cloneSession method. If you happen to use this method,
    * please double check if it's really needed.
    */
   String acquireCloneTicket() throws RuntimeFault, RemoteException;

   /**
    * @since SDK4.0
    */
   UserSession loginExtensionBySubjectName(String extensionKey, String locale) throws InvalidLogin, InvalidLocale, NotFound, NoClientCertificate, NoSubjectName, RuntimeFault, RemoteException;

   /**
    * @since SDK4.0
    */
   UserSession loginExtensionByCertificate(String extensionKey, String locale) throws InvalidLogin, InvalidLocale, NoClientCertificate, RuntimeFault, RemoteException;

   UserSession impersonateUser(String userName, String locale) throws InvalidLogin, InvalidLocale, RuntimeFault, RemoteException;

   UserSession login(String userName, String password, String locale) throws InvalidLogin, InvalidLocale, RuntimeFault, RemoteException;

   UserSession loginBySSPI(String base64Token, String locale) throws InvalidLogin, InvalidLocale, SSPIChallenge, RuntimeFault, RemoteException;

   /**
    * @since SDK5.1
    */
   UserSession loginByToken(String locale) throws InvalidLogin, InvalidLocale, RuntimeFault, RemoteException;

   void logout() throws RuntimeFault, RemoteException;

   boolean sessionIsActive(String sessionID, String userName) throws RuntimeFault, RemoteException;

   void setLocale(String locale) throws InvalidLocale, RuntimeFault, RemoteException;

   void terminateSession(List<String> sessionIDs) throws NotFound, RuntimeFault, RemoteException;

   void updateServiceMessage(String message) throws RuntimeFault, RemoteException;
}
