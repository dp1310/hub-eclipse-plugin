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
package com.blackducksoftware.integration.eclipseplugin.startup;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNames;
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
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.buildtool.FilePathMavenExternalIdExtractor;
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.phonehome.IntegrationInfo;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.phone.home.enums.ThirdPartyName;

public class Activator extends AbstractUIPlugin {
	public static final String PLUGIN_ID = "hub-eclipse-plugin";

	private final int COMPONENT_CACHE_CAPACITY = 10000;

	private static Activator plugin;

	private HubRestConnectionService connectionService;

	private ProjectDependencyInformation information;

	private ComponentCache componentCache;

	private InspectionQueueService inspectionQueueService;

	private NewJavaProjectListener newJavaProjectListener;

	private JavaProjectDeletedListener javaProjectDeletedListener;

	private DefaultPreferenceChangeListener defaultPrefChangeListener;

	private ProjectDependenciesChangedListener depsChangedListener;

	private SecurePreferencesService securePrefService;

	private PreferencesService defaultPreferencesService;

	private WorkspaceInformationService workspaceInformationService;

	private ProjectInformationService projService;

	@Override
	public void start(final BundleContext context) throws Exception {
		super.start(context);
		System.out.println("STARTING HUB ECLIPSE PLUGIN");
		plugin = this;
		final FilePathMavenExternalIdExtractor extractor = new FilePathMavenExternalIdExtractor();
		final DependencyInformationService depService = new DependencyInformationService(this);
		projService = new ProjectInformationService(depService, extractor);
		workspaceInformationService = new WorkspaceInformationService(projService);
		securePrefService = new SecurePreferencesService();
		connectionService = new HubRestConnectionService(getInitialHubConnection());
		componentCache = new ComponentCache(COMPONENT_CACHE_CAPACITY, depService);
		inspectionQueueService = new InspectionQueueService(this, projService);
		information = new ProjectDependencyInformation(this, componentCache);
		defaultPreferencesService = new PreferencesService(plugin);
		newJavaProjectListener = new NewJavaProjectListener(this);
		defaultPrefChangeListener = new DefaultPreferenceChangeListener(this);
		depsChangedListener = new ProjectDependenciesChangedListener(information, extractor, depService);
		javaProjectDeletedListener = new JavaProjectDeletedListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(newJavaProjectListener);
		ResourcesPlugin.getWorkspace().addResourceChangeListener(javaProjectDeletedListener,
				IResourceChangeEvent.PRE_DELETE);
		plugin.getPreferenceStore().addPropertyChangeListener(defaultPrefChangeListener);
		JavaCore.addElementChangedListener(depsChangedListener);
		defaultPreferencesService.setDefaultConfig();
		try{
			this.phoneHome();
		}catch(final Exception e){
			// Do nothing
		}
		inspectionQueueService.enqueueInspections(workspaceInformationService.getSupportedJavaProjectNames());
	}

	public ProjectDependencyInformation getProjectInformation() {
		return information;
	}

	public HubRestConnectionService updateConnection(final RestConnection restConnection) {
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
		final IPreferenceStore prefs = plugin.getPreferenceStore();
		final String hubURL = prefs.getString(PreferenceNames.HUB_URL);
		final String hubUsername = prefs.getString(PreferenceNames.HUB_USERNAME);
		final String hubPassword = securePrefService.getSecurePreference(SecurePreferenceNames.HUB_PASSWORD);
		final String hubTimeout = prefs.getString(PreferenceNames.HUB_TIMEOUT);
		final String proxyUsername = prefs.getString(PreferenceNames.PROXY_USERNAME);
		final String proxyPassword = securePrefService.getSecurePreference(SecurePreferenceNames.PROXY_PASSWORD);
		final String proxyPort = prefs.getString(PreferenceNames.PROXY_PORT);
		final String proxyHost = prefs.getString(PreferenceNames.PROXY_HOST);
		final HubServerConfigBuilder builder = new HubServerConfigBuilder();
		final HubRestConnectionService connectionService = new HubRestConnectionService();
		final AuthorizationValidator validator = new AuthorizationValidator(connectionService, builder);
		final AuthorizationResponse response = validator.validateCredentials(hubUsername, hubPassword, hubURL, proxyUsername, proxyPassword, proxyPort,
				proxyHost, hubTimeout);
		if (response.getConnection() != null) {
			return response.getConnection();
		} else {
			return null;
		}
	}

	public void updateHubConnection(final RestConnection connection) throws HubIntegrationException {
		information.updateCache(connection);
		inspectionQueueService.enqueueInspections(workspaceInformationService.getSupportedJavaProjectNames());
	}

	@Override
	public void stop(final BundleContext context) throws Exception {
		plugin.getPreferenceStore().removePropertyChangeListener(defaultPrefChangeListener);
		plugin = null;
		inspectionQueueService.shutDown();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(newJavaProjectListener);
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(javaProjectDeletedListener);
		JavaCore.removeElementChangedListener(depsChangedListener);
		super.stop(context);
	}

	/**
	 * Returns the plugin instance.
	 * Use this method sparingly, it should only be used when you can't pass this instance.
	 * If it is used, use it once per class to set ONE (preferably final) variable.
	 * The only exceptions are with the setter methods present in this instance.
	 *
	 * @return the singleton instance of the plugin
	 */
	public static Activator getPlugin() {
		return plugin;
	}

	public static ImageDescriptor getImageDescriptor(final String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	public static void reportError(final String dialogTitle, final String message, final IStatus status) {
		ErrorDialog.openError(null, dialogTitle, message, status);
	}

	public void phoneHome() throws IntegrationException {
		if (!plugin.getConnectionService().hasActiveHubConnection()) {
			return;
		}
		final PhoneHomeDataService phoneHomeService = this.connectionService.getPhoneHomeDataService();
		final IProduct eclipseProduct = Platform.getProduct();
		final String eclipseVersion = eclipseProduct.getDefiningBundle().getVersion().toString();
		final String pluginVersion = Platform.getBundle("hub-eclipse-plugin").getVersion().toString();
		final AuthorizationValidator authorizationValidator = new AuthorizationValidator(this.connectionService,
				new HubServerConfigBuilder());
		final SecurePreferencesService securePrefService = new SecurePreferencesService();
		final IPreferenceStore prefStore = this.getPreferenceStore();
		final String username = prefStore.getString(PreferenceNames.HUB_USERNAME);
		final String password = securePrefService.getSecurePreference(SecurePreferenceNames.HUB_PASSWORD);
		final String hubUrl = prefStore.getString(PreferenceNames.HUB_URL);
		final String proxyUsername = prefStore.getString(PreferenceNames.PROXY_USERNAME);
		final String proxyPassword = securePrefService.getSecurePreference(SecurePreferenceNames.PROXY_PASSWORD);
		final String proxyPort = prefStore.getString(PreferenceNames.PROXY_PORT);
		final String proxyHost = prefStore.getString(PreferenceNames.PROXY_HOST);
		final String timeout = prefStore.getString(PreferenceNames.HUB_TIMEOUT);
		authorizationValidator.setHubServerConfigBuilderFields(username, password, hubUrl,
				proxyUsername, proxyPassword, proxyPort,
				proxyHost, timeout);
		final HubServerConfig hubServerConfig = authorizationValidator.getHubServerConfigBuilder().build();
		final IntegrationInfo integrationInfo = new IntegrationInfo(ThirdPartyName.ECLIPSE, eclipseVersion, pluginVersion);
		phoneHomeService.phoneHome(hubServerConfig, integrationInfo);
	}

}
