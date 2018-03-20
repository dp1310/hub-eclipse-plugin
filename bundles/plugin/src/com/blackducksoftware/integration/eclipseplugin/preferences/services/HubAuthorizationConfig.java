/**
 * hub-eclipse-plugin
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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
package com.blackducksoftware.integration.eclipseplugin.preferences.services;

import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Text;

import com.blackducksoftware.integration.eclipseplugin.common.services.HubRestConnectionService;
import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationResponse;
import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationValidator;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;

public class HubAuthorizationConfig {

    private StringFieldEditor hubUsernameField;

    private AuthorizationValidator validator;

    private Text hubPasswordField;

    private StringFieldEditor hubURLField;

    private StringFieldEditor hubTimeoutField;

    private StringFieldEditor proxyUsernameField;

    private Text proxyPasswordField;

    private StringFieldEditor proxyHostField;

    private StringFieldEditor proxyPortField;

    public HubAuthorizationConfig() {
        this.validator = (new AuthorizationValidator(new HubRestConnectionService(), new HubServerConfigBuilder()));
    }

    public StringFieldEditor getHubUsernameField() {
        return hubUsernameField;
    }

    public void setHubUsernameField(StringFieldEditor hubUsernameField) {
        this.hubUsernameField = hubUsernameField;
    }

    public Text getHubPasswordField() {
        return hubPasswordField;
    }

    public void setHubPasswordField(Text hubPasswordField) {
        this.hubPasswordField = hubPasswordField;
    }

    public StringFieldEditor getHubURLField() {
        return hubURLField;
    }

    public void setHubURLField(StringFieldEditor hubURLField) {
        this.hubURLField = hubURLField;
    }

    public StringFieldEditor getHubTimeoutField() {
        return hubTimeoutField;
    }

    public void setHubTimeoutField(StringFieldEditor hubTimeoutField) {
        this.hubTimeoutField = hubTimeoutField;
    }

    public StringFieldEditor getProxyUsernameField() {
        return proxyUsernameField;
    }

    public void setProxyUsernameField(StringFieldEditor proxyUsernameField) {
        this.proxyUsernameField = proxyUsernameField;
    }

    public Text getProxyPasswordField() {
        return proxyPasswordField;
    }

    public void setProxyPasswordField(Text proxyPasswordField) {
        this.proxyPasswordField = proxyPasswordField;
    }

    public StringFieldEditor getProxyHostField() {
        return proxyHostField;
    }

    public void setProxyHostField(StringFieldEditor proxyHostField) {
        this.proxyHostField = proxyHostField;
    }

    public StringFieldEditor getProxyPortField() {
        return proxyPortField;
    }

    public void setProxyPortField(StringFieldEditor proxyPortField) {
        this.proxyPortField = proxyPortField;
    }

    public StringFieldEditor[] getEditors() {
        return new StringFieldEditor[] { hubUsernameField, hubURLField, hubTimeoutField, proxyUsernameField,
                proxyHostField, proxyPortField };
    }

    public AuthorizationResponse validateCredentialFields() {
        return validator.validateCredentials(hubUsernameField.getStringValue(), hubPasswordField.getText(), hubURLField.getStringValue(),
                proxyUsernameField.getStringValue(), proxyPasswordField.getText(), proxyPortField.getStringValue(), proxyHostField.getStringValue(),
                hubTimeoutField.getStringValue());
    }

}
