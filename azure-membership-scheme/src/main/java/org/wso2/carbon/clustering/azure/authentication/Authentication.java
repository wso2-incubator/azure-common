/*
 * Copyright (c) 2016, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.wso2.carbon.clustering.azure.authentication;

import com.microsoft.aad.adal4j.AuthenticationContext;
import com.microsoft.aad.adal4j.AuthenticationResult;
import com.microsoft.aad.adal4j.ClientCredential;
import org.wso2.carbon.clustering.azure.exceptions.AzureMembershipSchemeException;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Authentication {

    public static AuthenticationResult getAuthToken(String authorizationEndPoint, String username, String credentials,
            String tenantID, String clientID, boolean validateAuthority, String ARMEndPoint)
            throws AzureMembershipSchemeException {

        AuthenticationResult result = null;
        ExecutorService service = Executors.newFixedThreadPool(1);
        try {
            String url = authorizationEndPoint + "/" + tenantID + "/oauth2/authorize";
            AuthenticationContext context = new AuthenticationContext(url, validateAuthority, service);
            Future<AuthenticationResult> future;
            if (username == null) {
                ClientCredential cred = new ClientCredential(clientID, credentials);
                future = context.acquireToken(ARMEndPoint + "/", cred, null);
            } else {
                future = context.acquireToken(ARMEndPoint + "/", clientID, username, credentials, null);
            }
            result = future.get();
        } catch (Exception ex) {
            throw new AzureMembershipSchemeException("Could not connect to Azure API", ex);
        } finally {
            service.shutdown();
        }
        return result;
    }
}
