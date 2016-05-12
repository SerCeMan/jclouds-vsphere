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


import com.vmware.vim25.CannotAccessFile;
import com.vmware.vim25.DiagnosticManagerLogDescriptor;
import com.vmware.vim25.DiagnosticManagerLogHeader;
import com.vmware.vim25.LogBundlingFailed;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.Task;

import java.rmi.RemoteException;
import java.util.List;

public interface DiagnosticManagerApi {
   DiagnosticManagerLogHeader browseDiagnosticLog(HostSystem host, String key, int start, int lines) throws CannotAccessFile, RuntimeFault, RemoteException;

   Task generateLogBundlesTask(boolean includeDefault, List<HostSystem> hosts) throws LogBundlingFailed, RuntimeFault, RemoteException;

   List<DiagnosticManagerLogDescriptor> queryDescriptions(HostSystem host) throws RuntimeFault, RemoteException;
}
