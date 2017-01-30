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

import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;

import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationValidator;

public class TestHubCredentialsSelectionListener implements SelectionListener {

    private final StringFieldEditor hubUsername;

    private final Text hubPassword;

    private final StringFieldEditor hubURL;

    private final IntegerFieldEditor hubTimeout;

    private final StringFieldEditor proxyUsername;

    private final Text proxyPassword;

    private final StringFieldEditor proxyHost;

    private final StringFieldEditor proxyPort;

    private final StringFieldEditor ignoredProxyHosts;

    private final Text connectionMessageText;

    private final AuthorizationValidator validator;

    public TestHubCredentialsSelectionListener(final StringFieldEditor hubUsername, final Text hubPassword,
            final StringFieldEditor hubURL, final IntegerFieldEditor hubTimeout, final StringFieldEditor proxyUsername,
            final Text proxyPassword, final StringFieldEditor proxyHost, final StringFieldEditor proxyPort,
            final StringFieldEditor ignoredProxyHosts, final Text connectionMessageText,
            final AuthorizationValidator validator) {
        this.hubUsername = hubUsername;
        this.hubPassword = hubPassword;
        this.hubURL = hubURL;
        this.hubTimeout = hubTimeout;
        this.proxyUsername = proxyUsername;
        this.proxyPassword = proxyPassword;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        this.ignoredProxyHosts = ignoredProxyHosts;
        this.connectionMessageText = connectionMessageText;
        this.validator = validator;
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
        return validator.validateCredentials(hubUsername.getStringValue(), hubPassword.getText(),
                hubURL.getStringValue(), proxyUsername.getStringValue(), proxyPassword.getText(),
                proxyPort.getStringValue(), proxyHost.getStringValue(), ignoredProxyHosts.getStringValue(),
                hubTimeout.getStringValue()).getResponseMessage();
    }

}
