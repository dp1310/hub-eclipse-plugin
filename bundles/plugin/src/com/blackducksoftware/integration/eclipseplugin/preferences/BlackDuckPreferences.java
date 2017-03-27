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

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
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

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceFieldLabels;
import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.services.SecurePreferencesService;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.preferences.listeners.TestHubCredentialsSelectionListener;
import com.blackducksoftware.integration.eclipseplugin.preferences.services.HubAuthorizationConfig;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;

public class BlackDuckPreferences extends PreferencePage implements IWorkbenchPreferencePage {
    public static final String TEST_HUB_CREDENTIALS_TEXT = "Test Connection";

    private Activator plugin;

    private SecurePreferencesService securePrefService;

    private Button testHubCredentials;

    private Text connectionMessageText;

    private HubAuthorizationConfig hubAuthorizationConfig;

    private final int NUM_COLUMNS = 2;

    private final String INTEGER_FIELD_EDITOR_ERROR_STRING = "IntegerFieldEditor.errorMessage";

    @Override
    public void init(final IWorkbench workbench) {
        this.plugin = Activator.getPlugin();
        securePrefService = new SecurePreferencesService();
        setPreferenceStore(plugin.getPreferenceStore());
        hubAuthorizationConfig = new HubAuthorizationConfig();
        this.noDefaultButton();
    }

    @Override
    protected Control createContents(final Composite parent) {
        final Composite authComposite = new Composite(parent, SWT.LEFT);
        final GridLayout authCompositeLayout = new GridLayout();
        authCompositeLayout.numColumns = NUM_COLUMNS;
        authComposite.setLayout(authCompositeLayout);
        authComposite.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.HORIZONTAL_ALIGN_BEGINNING));
        hubAuthorizationConfig
                .setHubUsernameField(createStringField(PreferenceNames.HUB_USERNAME, PreferenceFieldLabels.HUB_USERNAME_LABEL, authComposite, false));
        hubAuthorizationConfig
                .setHubPasswordField(createPasswordField(parent, authComposite, PreferenceFieldLabels.HUB_PASSWORD_LABEL, SecurePreferenceNames.HUB_PASSWORD));
        hubAuthorizationConfig.setHubURLField(createStringField(PreferenceNames.HUB_URL, PreferenceFieldLabels.HUB_URL_LABEL, authComposite, false));
        hubAuthorizationConfig
                .setHubTimeoutField(createStringField(PreferenceNames.HUB_TIMEOUT, PreferenceFieldLabels.HUB_TIMEOUT_LABEL, authComposite, true));
        hubAuthorizationConfig
                .setProxyUsernameField(createStringField(PreferenceNames.PROXY_USERNAME, PreferenceFieldLabels.PROXY_USERNAME_LABEL, authComposite, false));
        hubAuthorizationConfig.setProxyPasswordField(
                createPasswordField(parent, authComposite, PreferenceFieldLabels.PROXY_PASSWORD_LABEL, SecurePreferenceNames.PROXY_PASSWORD));
        hubAuthorizationConfig.setProxyHostField(createStringField(PreferenceNames.PROXY_HOST, PreferenceFieldLabels.PROXY_HOST_LABEL, authComposite, false));
        hubAuthorizationConfig.setProxyPortField(createStringField(PreferenceNames.PROXY_PORT, PreferenceFieldLabels.PROXY_PORT_LABEL, authComposite, true));
        final Composite connectionMessageComposite = new Composite(parent, SWT.LEFT);
        final GridLayout connectionMessageCompositeLayout = new GridLayout();
        connectionMessageCompositeLayout.numColumns = 1;
        connectionMessageComposite.setLayout(connectionMessageCompositeLayout);
        connectionMessageComposite
                .setLayoutData(new GridData(GridData.FILL_BOTH | GridData.BEGINNING));
        GridData textData = new GridData(GridData.FILL_BOTH);
        connectionMessageText = new Text(connectionMessageComposite, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
        connectionMessageText
                .setBackground(connectionMessageText.getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
        connectionMessageText.setLayoutData(textData);
        return parent;
    }

    @Override
    protected void contributeButtons(Composite parent) {
        ((GridLayout) parent.getLayout()).numColumns++;
        testHubCredentials = new Button(parent, SWT.PUSH);
        testHubCredentials.setText(TEST_HUB_CREDENTIALS_TEXT);
        testHubCredentials.addSelectionListener(
                new TestHubCredentialsSelectionListener(hubAuthorizationConfig, connectionMessageText));
    }

    private StringFieldEditor createStringField(final String preferenceName, final String label, final Composite composite, final boolean integerValidation) {
        final StringFieldEditor editor;
        if (integerValidation) {
            // String field editor w/ integer validation, we can make this a separate class if we need to.
            editor = new StringFieldEditor(preferenceName, label, composite) {
                @Override
                protected boolean checkState() {
                    setErrorMessage(JFaceResources.getString(INTEGER_FIELD_EDITOR_ERROR_STRING));
                    Text text = getTextControl();
                    if (text == null) {
                        return false;
                    }
                    String intString = text.getText();
                    if (intString.isEmpty()) {
                        clearErrorMessage();
                        return true;
                    }
                    try {
                        Integer.valueOf(intString).intValue();
                    } catch (NumberFormatException nfe) {
                        showErrorMessage();
                    }
                    return false;
                }
            };
        } else {
            editor = new StringFieldEditor(preferenceName, label, composite);
        }
        editor.setPage(this);
        editor.setPreferenceStore(plugin.getPreferenceStore());
        editor.fillIntoGrid(composite, NUM_COLUMNS);
        editor.load();
        return editor;
    }

    private Text createPasswordField(final Composite parent, final Composite composite,
            final String labelText, final String passwordKey) {
        final Label label = new Label(composite, SWT.WRAP);
        label.setText(labelText);
        label.setFont(parent.getFont());
        Text passwordField = new Text(composite, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
        passwordField.setText(securePrefService.getSecurePreference(passwordKey));
        passwordField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        return passwordField;
    }

    private void storeValues() throws HubIntegrationException {
        final StringFieldEditor[] editors = hubAuthorizationConfig.getEditors();
        for (final StringFieldEditor editor : editors) {
            plugin.getPreferenceStore().setValue(editor.getPreferenceName(), editor.getStringValue());
        }
        securePrefService.saveSecurePreference(SecurePreferenceNames.HUB_PASSWORD, hubAuthorizationConfig.getHubPasswordField().getText(), true);
        securePrefService.saveSecurePreference(SecurePreferenceNames.PROXY_PASSWORD, hubAuthorizationConfig.getProxyPasswordField().getText(), true);
        Activator.getPlugin().updateHubConnection(hubAuthorizationConfig.validateCredentialFields().getConnection());
        final ProjectDependencyInformation projectInfo = Activator.getPlugin().getProjectInformation();
        final VulnerabilityView componentView = projectInfo.getComponentView();
        if (componentView != null) {
            componentView.resetInput();
        }
    }

    @Override
    public void performApply() {
        try {
            this.storeValues();
        } catch (HubIntegrationException e) {
            // e.printStackTrace();
        }
    }

    @Override
    public boolean performOk() {
        try {
            this.storeValues();
        } catch (HubIntegrationException e) {
            // e.printStackTrace();
        }
        return super.performOk();
    }

}
