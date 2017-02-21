/**
 * hub-eclipse-plugin
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.eclipseplugin.internal;

import com.blackducksoftware.integration.eclipseplugin.common.services.HubRestConnectionService;
import com.blackducksoftware.integration.exception.EncryptionException;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.validator.ValidationResults;

public class AuthorizationValidator {
    private final HubRestConnectionService connectionService;

    private final HubServerConfigBuilder builder;

    public static final String LOGIN_SUCCESS_MESSAGE = "Successful login!";

    public AuthorizationValidator(final HubRestConnectionService connectionService,
            final HubServerConfigBuilder builder) {
        this.connectionService = connectionService;
        this.builder = builder;
    }

    public AuthorizationResponse validateCredentials(final String username, final String password, final String hubUrl,
            final String proxyUsername, final String proxyPassword, final String proxyPort, final String proxyHost,
            final String ignoredProxyHosts, final String timeout) {
        setHubServerConfigBuilderFields(username, password, hubUrl, proxyUsername, proxyPassword, proxyPort,
                proxyHost, ignoredProxyHosts, timeout);

        final ValidationResults results = builder.createValidator().assertValid();
        if (results.isSuccess()) {
            try {
                HubServerConfig config = builder.build();
                RestConnection connection = connectionService.getCredentialsRestConnection(config);
                connection.connect();
                return new AuthorizationResponse(connection, LOGIN_SUCCESS_MESSAGE);
            } catch (IllegalArgumentException | EncryptionException e) {
                return new AuthorizationResponse(e.getMessage());
            } catch (HubIntegrationException e) {
                return new AuthorizationResponse(e.getMessage());
            }
        }

        return new AuthorizationResponse(results.getAllResultString());
    }

    public HubServerConfigBuilder getHubServerConfigBuilder() {
        return builder;
    }

    public void setHubServerConfigBuilderFields(final String username,
            final String password, final String hubUrl, final String proxyUsername, final String proxyPassword,
            final String proxyPort, final String proxyHost, final String ignoredProxyHosts, final String timeout) {
        builder.setUsername(username);
        builder.setPassword(password);
        builder.setHubUrl(hubUrl);
        builder.setTimeout(timeout);
        builder.setProxyUsername(proxyUsername);
        builder.setProxyPassword(proxyPassword);
        builder.setProxyHost(proxyHost);
        builder.setProxyPort(proxyPort);
        builder.setIgnoredProxyHosts(ignoredProxyHosts);
    }
}
