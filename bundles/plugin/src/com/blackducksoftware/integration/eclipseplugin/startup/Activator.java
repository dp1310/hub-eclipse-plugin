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
package com.blackducksoftware.integration.eclipseplugin.startup;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNodes;
import com.blackducksoftware.integration.eclipseplugin.common.services.DependencyInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.HubRestConnectionService;
import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService;
import com.blackducksoftware.integration.eclipseplugin.common.services.PreferencesService;
import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.SecurePreferencesService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationResponse;
import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationValidator;
import com.blackducksoftware.integration.eclipseplugin.internal.ComponentCache;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.internal.listeners.JavaProjectDeletedListener;
import com.blackducksoftware.integration.eclipseplugin.internal.listeners.NewJavaProjectListener;
import com.blackducksoftware.integration.eclipseplugin.internal.listeners.ProjectDependenciesChangedListener;
import com.blackducksoftware.integration.eclipseplugin.preferences.listeners.DefaultPreferenceChangeListener;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.buildtool.FilePathGavExtractor;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class Activator extends AbstractUIPlugin {
    public static final String PLUGIN_ID = "hub-eclipse-plugin";

    private final int COMPONENT_CACHE_CAPACITY = 10000;

    private static Activator plugin;

    private HubRestConnectionService connectionService;

    private ProjectDependencyInformation information;

    private ComponentCache componentCache;

    private InspectionQueueService inspectionQueueService;

    private IResourceChangeListener newJavaProjectListener;

    private IResourceChangeListener javaProjectDeletedListener;

    private IPropertyChangeListener defaultPrefChangeListener;

    private IElementChangedListener depsChangedListener;

    private SecurePreferencesService securePrefService;

    private PreferencesService defaultPreferencesService;

    private WorkspaceInformationService workspaceInformationService;

    @Override
    public void start(final BundleContext context) throws Exception {
        super.start(context);
        System.out.println("STARTING HUB ECLIPSE PLUGIN");
        plugin = this;
        final FilePathGavExtractor extractor = new FilePathGavExtractor();
        final DependencyInformationService depService = new DependencyInformationService();
        final ProjectInformationService projService = new ProjectInformationService(depService, extractor);
        workspaceInformationService = new WorkspaceInformationService(projService);
        securePrefService = new SecurePreferencesService(SecurePreferenceNodes.BLACK_DUCK, SecurePreferencesFactory.getDefault());
        connectionService = new HubRestConnectionService(getInitialHubConnection());
        componentCache = new ComponentCache(COMPONENT_CACHE_CAPACITY, depService);
        inspectionQueueService = new InspectionQueueService(projService);
        information = new ProjectDependencyInformation(componentCache);
        defaultPreferencesService = new PreferencesService(getPreferenceStore());
        newJavaProjectListener = new NewJavaProjectListener();
        defaultPrefChangeListener = new DefaultPreferenceChangeListener(defaultPreferencesService);
        depsChangedListener = new ProjectDependenciesChangedListener(information, extractor, depService);
        javaProjectDeletedListener = new JavaProjectDeletedListener(information);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(newJavaProjectListener);
        ResourcesPlugin.getWorkspace().addResourceChangeListener(javaProjectDeletedListener,
                IResourceChangeEvent.PRE_DELETE);
        getPreferenceStore().addPropertyChangeListener(defaultPrefChangeListener);
        JavaCore.addElementChangedListener(depsChangedListener);
        defaultPreferencesService.setDefaultConfig();
        inspectionQueueService.enqueueInspections(workspaceInformationService.getSupportedJavaProjectNames());
    }

    public ProjectDependencyInformation getProjectInformation() {
        return information;
    }

    public HubRestConnectionService updateConnection(RestConnection restConnection) {
        connectionService = new HubRestConnectionService(restConnection);
        return connectionService;
    }

    public HubRestConnectionService getConnectionService() {
        return connectionService;
    }

    public InspectionQueueService getInspectionQueueService() {
        return inspectionQueueService;
    }

    public PreferencesService getDefaultPreferencesService() {
        return defaultPreferencesService;
    }

    public WorkspaceInformationService getWorkspaceInformationService() {
        return workspaceInformationService;
    }

    public RestConnection getInitialHubConnection() throws HubIntegrationException {
        final IPreferenceStore prefs = getPlugin().getPreferenceStore();
        final String hubURL = prefs.getString(PreferenceNames.HUB_URL);
        final String hubUsername = prefs.getString(PreferenceNames.HUB_USERNAME);
        final String hubPassword = securePrefService.getSecurePreference(SecurePreferenceNames.HUB_PASSWORD);
        final String hubTimeout = prefs.getString(PreferenceNames.HUB_TIMEOUT);
        final String proxyUsername = prefs.getString(PreferenceNames.PROXY_USERNAME);
        final String proxyPassword = securePrefService.getSecurePreference(SecurePreferenceNames.PROXY_PASSWORD);
        final String proxyPort = prefs.getString(PreferenceNames.PROXY_PORT);
        final String proxyHost = prefs.getString(PreferenceNames.PROXY_HOST);
        final String ignoredProxyHosts = prefs.getString(PreferenceNames.IGNORED_PROXY_HOSTS);
        final HubServerConfigBuilder builder = new HubServerConfigBuilder();
        final HubRestConnectionService connectionService = new HubRestConnectionService();
        final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
        final AuthorizationResponse response = validator.validateCredentials(hubUsername, hubPassword, hubURL, proxyUsername, proxyPassword, proxyPort,
                proxyHost,
                ignoredProxyHosts, hubTimeout);
        if (response.getConnection() != null) {
            return response.getConnection();
        } else {
            return null;
        }
    }

    public void updateHubConnection(final RestConnection connection) throws HubIntegrationException {
        information.updateCache(connection);
    }

    @Override
    public void stop(final BundleContext context) throws Exception {
        plugin = null;
        inspectionQueueService.shutDown();
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(newJavaProjectListener);
        ResourcesPlugin.getWorkspace().removeResourceChangeListener(javaProjectDeletedListener);
        getPreferenceStore().removePropertyChangeListener(defaultPrefChangeListener);
        JavaCore.removeElementChangedListener(depsChangedListener);
        super.stop(context);
    }

    public static Activator getPlugin() {
        return plugin;
    }

    public static ImageDescriptor getImageDescriptor(final String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
    }

    public static void reportError(final String dialogTitle, final String message, final IStatus status) {
        ErrorDialog.openError(null, dialogTitle, message, status);
    }

}
