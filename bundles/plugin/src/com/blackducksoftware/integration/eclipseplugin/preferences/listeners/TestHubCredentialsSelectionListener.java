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
package com.blackducksoftware.integration.eclipseplugin.preferences.listeners;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;

import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationResponse;
import com.blackducksoftware.integration.eclipseplugin.preferences.services.HubAuthorizationConfig;

public class TestHubCredentialsSelectionListener implements SelectionListener {

    private final HubAuthorizationConfig hubAuthorizationConfig;

    private final Text connectionMessageText;

    public TestHubCredentialsSelectionListener(HubAuthorizationConfig hubAuthorizationConfig, final Text connectionMessageText) {
        this.hubAuthorizationConfig = hubAuthorizationConfig;
        this.connectionMessageText = connectionMessageText;
    }

    @Override
    public void widgetDefaultSelected(final SelectionEvent arg0) {
        final String message = attemptToConnect();
        connectionMessageText.setText(message);
    }

    @Override
    public void widgetSelected(final SelectionEvent arg0) {
        final String message = attemptToConnect();
        connectionMessageText.setText(message);
    }

    private String attemptToConnect() {
        final AuthorizationResponse hubAuthorizationResponse = hubAuthorizationConfig.validateCredentialFields();
        return hubAuthorizationResponse.getResponseMessage();
    }

}
