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

import com.vmware.vim25.InvalidLocale
import com.vmware.vim25.InvalidLogin
import com.vmware.vim25.NoClientCertificate
import com.vmware.vim25.NoSubjectName
import com.vmware.vim25.NotFound
import com.vmware.vim25.RuntimeFault
import com.vmware.vim25.SSPIChallenge
import com.vmware.vim25.SessionManagerGenericServiceTicket
import com.vmware.vim25.SessionManagerLocalTicket
import com.vmware.vim25.SessionManagerServiceRequestSpec
import com.vmware.vim25.UserSession
import com.vmware.vim25.mo.ServiceInstance

import java.net.MalformedURLException
import java.rmi.RemoteException

interface SessionManagerApi {
    val currentSession: UserSession

    val defaultLocale: String

    val message: String

    val messageLocaleList: List<String>

    val sessionList: List<UserSession>

    val supportedLocaleList: List<String>

    @Throws(InvalidLogin::class, RuntimeFault::class, RemoteException::class)
    fun acquireLocalTicket(userName: String): SessionManagerLocalTicket

    /**
     * @since SDK5.0
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun acquireGenericServiceTicket(spec: SessionManagerServiceRequestSpec): SessionManagerGenericServiceTicket

    /**
     * @since SDK4.0
     * * You don't need to use this method. Instead, look at the other cloneSession method.
     */
    @Throws(InvalidLogin::class, RuntimeFault::class, RemoteException::class)
    fun cloneSession(cloneTicket: String): UserSession

    @Throws(InvalidLogin::class, RuntimeFault::class, RemoteException::class, MalformedURLException::class)
    fun cloneSession(ignoreCert: Boolean): ServiceInstance

    /**
     * @since SDK4.0
     * * This method is called in the cloneSession method. If you happen to use this method,
     * * please double check if it's really needed.
     */
    @Throws(RuntimeFault::class, RemoteException::class)
    fun acquireCloneTicket(): String

    /**
     * @since SDK4.0
     */
    @Throws(InvalidLogin::class, InvalidLocale::class, NotFound::class, NoClientCertificate::class, NoSubjectName::class, RuntimeFault::class, RemoteException::class)
    fun loginExtensionBySubjectName(extensionKey: String, locale: String): UserSession

    /**
     * @since SDK4.0
     */
    @Throws(InvalidLogin::class, InvalidLocale::class, NoClientCertificate::class, RuntimeFault::class, RemoteException::class)
    fun loginExtensionByCertificate(extensionKey: String, locale: String): UserSession

    @Throws(InvalidLogin::class, InvalidLocale::class, RuntimeFault::class, RemoteException::class)
    fun impersonateUser(userName: String, locale: String): UserSession

    @Throws(InvalidLogin::class, InvalidLocale::class, RuntimeFault::class, RemoteException::class)
    fun login(userName: String, password: String, locale: String): UserSession

    @Throws(InvalidLogin::class, InvalidLocale::class, SSPIChallenge::class, RuntimeFault::class, RemoteException::class)
    fun loginBySSPI(base64Token: String, locale: String): UserSession

    /**
     * @since SDK5.1
     */
    @Throws(InvalidLogin::class, InvalidLocale::class, RuntimeFault::class, RemoteException::class)
    fun loginByToken(locale: String): UserSession

    @Throws(RuntimeFault::class, RemoteException::class)
    fun logout()

    @Throws(RuntimeFault::class, RemoteException::class)
    fun sessionIsActive(sessionID: String, userName: String): Boolean

    @Throws(InvalidLocale::class, RuntimeFault::class, RemoteException::class)
    fun setLocale(locale: String)

    @Throws(NotFound::class, RuntimeFault::class, RemoteException::class)
    fun terminateSession(sessionIDs: List<String>)

    @Throws(RuntimeFault::class, RemoteException::class)
    fun updateServiceMessage(message: String)
}
