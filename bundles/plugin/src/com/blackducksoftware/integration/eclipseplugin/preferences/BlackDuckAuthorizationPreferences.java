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
package com.blackducksoftware.integration.eclipseplugin.preferences;

import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.jface.preference.FieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNodes;
import com.blackducksoftware.integration.eclipseplugin.common.services.HubRestConnectionService;
import com.blackducksoftware.integration.eclipseplugin.common.services.SecurePreferencesService;
import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationResponse;
import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationValidator;
import com.blackducksoftware.integration.eclipseplugin.preferences.listeners.TestHubCredentialsSelectionListener;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;

public class BlackDuckAuthorizationPreferences extends PreferencePage implements IWorkbenchPreferencePage {

    private SecurePreferencesService securePrefService;

    private StringFieldEditor hubUsername;

    private Text hubPassword;

    private StringFieldEditor hubURL;

    private IntegerFieldEditor hubTimeout;

    private StringFieldEditor proxyUsername;

    private Text proxyPassword;

    private StringFieldEditor proxyHost;

    private IntegerFieldEditor proxyPort;

    private StringFieldEditor ignoredProxyHosts;

    private Button testHubCredentials;

    private Text connectionMessageText;

    private AuthorizationValidator validator;

    private final String HUB_USERNAME_LABEL = "Hub Username";

    private final String HUB_PASSWORD_LABEL = "Hub Password";

    private final String HUB_URL_LABEL = "Hub Instance URL";

    private final String HUB_TIMEOUT_LABEL = "Hub Timeout in Seconds";

    private final String PROXY_USERNAME_LABEL = "Proxy Username";

    private final String PROXY_PASSWORD_LABEL = "Proxy Password";

    private final String PROXY_HOST_LABEL = "Proxy Host";

    private final String PROXY_PORT_LABEL = "Proxy Port";

    private final String IGNORED_PROXY_HOSTS_LABEL = "Ignored Proxy Hosts";

    private final String TEST_HUB_CREDENTIALS_TEXT = "Test Hub Credentials";

    private final int NUM_COLUMNS = 2;

    @Override
    public void init(final IWorkbench workbench) {
        securePrefService = new SecurePreferencesService(SecurePreferenceNodes.BLACK_DUCK,
                SecurePreferencesFactory.getDefault());
        validator = new AuthorizationValidator(new HubRestConnectionService(), new HubServerConfigBuilder());
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(final Composite parent) {

        final Composite authComposite = new Composite(parent, SWT.LEFT);
        GridLayout authCompositeLayout = new GridLayout();
        authCompositeLayout.numColumns = NUM_COLUMNS;
        authComposite.setLayout(authCompositeLayout);
        authComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_BEGINNING));

        hubUsername = new StringFieldEditor(PreferenceNames.HUB_USERNAME, HUB_USERNAME_LABEL, authComposite);
        performFieldEditorSetup(authComposite, hubUsername);

        createLabel(parent, authComposite, HUB_PASSWORD_LABEL);
        hubPassword = new Text(authComposite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        hubPassword.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        hubPassword.setText(securePrefService.getSecurePreference(SecurePreferenceNames.HUB_PASSWORD));
        hubURL = new StringFieldEditor(PreferenceNames.HUB_URL, HUB_URL_LABEL, authComposite);
        performFieldEditorSetup(authComposite, hubURL);
        hubTimeout = new IntegerFieldEditor(PreferenceNames.HUB_TIMEOUT, HUB_TIMEOUT_LABEL, authComposite);
        performFieldEditorSetup(authComposite, hubTimeout);
        proxyUsername = new StringFieldEditor(PreferenceNames.PROXY_USERNAME, PROXY_USERNAME_LABEL, authComposite);
        performFieldEditorSetup(authComposite, proxyUsername);

        createLabel(parent, authComposite, PROXY_PASSWORD_LABEL);
        proxyPassword = new Text(authComposite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        proxyPassword.setText(securePrefService.getSecurePreference(SecurePreferenceNames.PROXY_PASSWORD));
        proxyPassword.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        proxyHost = new StringFieldEditor(PreferenceNames.PROXY_HOST, PROXY_HOST_LABEL, authComposite);
        performFieldEditorSetup(authComposite, proxyHost);
        proxyPort = new IntegerFieldEditor(PreferenceNames.PROXY_PORT, PROXY_PORT_LABEL, authComposite);
        performFieldEditorSetup(authComposite, proxyPort);
        ignoredProxyHosts = new StringFieldEditor(PreferenceNames.IGNORED_PROXY_HOSTS, IGNORED_PROXY_HOSTS_LABEL,
                authComposite);
        performFieldEditorSetup(authComposite, ignoredProxyHosts);

        Composite connectionMessageComposite = new Composite(parent, SWT.LEFT);
        GridLayout connectionMessageCompositeLayout = new GridLayout();
        connectionMessageCompositeLayout.numColumns = 1;
        connectionMessageComposite.setLayout(connectionMessageCompositeLayout);
        connectionMessageComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_BEGINNING));
        GridData textData = new GridData(GridData.FILL_HORIZONTAL | GridData.FILL_VERTICAL);
        connectionMessageText = new Text(connectionMessageComposite, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
        connectionMessageText
                .setBackground(connectionMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        connectionMessageText.setLayoutData(textData);
        connectionMessageText.setText("\n");

        return parent;
    }

    @Override
    protected void contributeButtons(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns++;
        testHubCredentials = new Button(parent, SWT.PUSH);
        testHubCredentials.setText(TEST_HUB_CREDENTIALS_TEXT);
        testHubCredentials.addSelectionListener(
                new TestHubCredentialsSelectionListener(hubUsername, hubPassword, hubURL, hubTimeout, proxyUsername,
                        proxyPassword, proxyHost, proxyPort, ignoredProxyHosts, connectionMessageText,
                        validator));
    }

    private void storeValues() throws HubIntegrationException {
        final IPreferenceStore prefStore = getPreferenceStore();
        final StringFieldEditor[] editors = new StringFieldEditor[] { hubUsername, hubURL, hubTimeout, proxyUsername,
                proxyHost, proxyPort, ignoredProxyHosts };
        for (final StringFieldEditor editor : editors) {
            prefStore.setValue(editor.getPreferenceName(), editor.getStringValue());
        }
        securePrefService.saveSecurePreference(SecurePreferenceNames.HUB_PASSWORD, hubPassword.getText(), true);
        securePrefService.saveSecurePreference(SecurePreferenceNames.PROXY_PASSWORD, proxyPassword.getText(), true);
        AuthorizationResponse authResponse = validator.validateCredentials(hubUsername.getStringValue(), hubPassword.getText(), hubURL.getStringValue(),
                proxyUsername.getStringValue(), proxyPassword.getText(), proxyPort.getStringValue(), proxyHost.getStringValue(),
                ignoredProxyHosts.getStringValue(), hubTimeout.getStringValue());
        Activator.getDefault().updateHubConnection(authResponse.getConnection());
    }

    private void createLabel(final Composite parent, final Composite composite,
            final String labelText) {
        final Label label = new Label(composite, SWT.WRAP);
        label.setText(labelText);
        label.setFont(parent.getFont());
    }

    private void performFieldEditorSetup(Composite parent, final FieldEditor editor) {
        editor.setPage(this);
        editor.setPreferenceStore(getPreferenceStore());
        editor.fillIntoGrid(parent, NUM_COLUMNS);
        editor.load();
    }

    @Override
    public void performApply() {
        try {
			storeValues();
		} catch (HubIntegrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    @Override
    public boolean performOk() {
        try {
			storeValues();
		} catch (HubIntegrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        if (super.performOk()) {
            return true;
        } else {
            return false;
        }
    }

}
