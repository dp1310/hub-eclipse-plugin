/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
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

    private StringFieldEditor ignoredProxyHostsField;

    public final String HUB_USERNAME_LABEL = "Hub Username";

    public final String HUB_PASSWORD_LABEL = "Hub Password";

    public final String HUB_URL_LABEL = "Hub Instance URL";

    public final String HUB_TIMEOUT_LABEL = "Hub Timeout in Seconds";

    public final String PROXY_USERNAME_LABEL = "Proxy Username";

    public final String PROXY_PASSWORD_LABEL = "Proxy Password";

    public final String PROXY_HOST_LABEL = "Proxy Host";

    public final String PROXY_PORT_LABEL = "Proxy Port";

    public final String IGNORED_PROXY_HOSTS_LABEL = "Ignored Proxy Hosts";

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

    public StringFieldEditor getIgnoredProxyHostsField() {
        return ignoredProxyHostsField;
    }

    public void setIgnoredProxyHostsField(StringFieldEditor ignoredProxyHostsField) {
        this.ignoredProxyHostsField = ignoredProxyHostsField;
    }

    public StringFieldEditor[] getEditors() {
        return new StringFieldEditor[] { hubUsernameField, hubURLField, hubTimeoutField, proxyUsernameField,
                proxyHostField, proxyPortField, ignoredProxyHostsField };
    }

    public AuthorizationResponse validateCredentialFields() {
        return validator.validateCredentials(hubUsernameField.getStringValue(), hubPasswordField.getText(), hubURLField.getStringValue(),
                proxyUsernameField.getStringValue(), proxyPasswordField.getText(), proxyPortField.getStringValue(), proxyHostField.getStringValue(),
                ignoredProxyHostsField.getStringValue(), hubTimeoutField.getStringValue());
    }

}
