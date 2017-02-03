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
package com.blackducksoftware.integration.eclipseplugin.views.providers;

import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNodes;
import com.blackducksoftware.integration.eclipseplugin.common.services.SecurePreferencesService;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.hub.api.nonpublic.HubVersionRequestService;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.buildtool.Gav;
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntBufferedLogger;
import com.blackducksoftware.integration.phone.home.enums.ThirdPartyName;

public class DependencyTableViewContentProvider implements IStructuredContentProvider {

    public static final String[] NO_SELECTED_PROJECT = new String[] { "No open project currently selected" };

    public static final String[] PROJECT_NOT_ACTIVATED = new String[] {
            "Black Duck scan not activated for current project" };

    public static final String[] ERR_UNKNOWN_INPUT = new String[] { "Input is of unknown type" };

    public static final String[] NO_VULNERABILITIES_TO_SHOW = new String[] { "No vulnerabilities to show!" };

    public static final String[] NO_HUB_CONNECTION = new String[] { "Cannot display vulnerabilities because you are not currently connected to the Hub" };

    private String inputProject;

    public DependencyTableViewContentProvider() {
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof String) {
            String projectName = (String) inputElement;
            inputProject = projectName;
            if (projectName.equals("")) {
                return NO_SELECTED_PROJECT;
            }
            boolean isActivated = Activator.getPlugin().getPreferenceStore().getBoolean(projectName);
            if (isActivated) {
                // Phone Home
                try {
                    if (Activator.getPlugin().getProjectInformation().hasActiveHubConnection()) {
                        // RestConnection hubConnection = projectInformation.getHubConnection();
                        // System.out.println("Hub Connection: " + hubConnection.toString());
                        HubServicesFactory factory = new HubServicesFactory(Activator.getPlugin().getProjectInformation().getHubConnection());
                        PhoneHomeDataService phoneHomeService = factory.createPhoneHomeDataService(new IntBufferedLogger());
                        // get version
                        HubVersionRequestService hubVersionRequestService = factory.createHubVersionRequestService();
                        String hubVersion = hubVersionRequestService.getHubVersion();
                        // get hubServerConfig

                        // create hubScanConfig
                        IProduct eclipseProduct = Platform.getProduct();
                        String eclipseVersion = eclipseProduct.getDefiningBundle().getVersion().toString();
                        String pluginVersion = Platform.getBundle("hub-eclipse-plugin").getVersion().toString();
                        // System.out.println("version: " + eclipseProduct.getId() + " - " + eclipseProduct.getName() +
                        // " - " + eclipseProduct.getApplication() + " - " + eclipseProduct.getDescription() + " - " +
                        // eclipseVersion);

                        HubServerConfigBuilder hubServerConfigBuilder = new HubServerConfigBuilder();

                        SecurePreferencesService securePrefService = new SecurePreferencesService(SecurePreferenceNodes.BLACK_DUCK,
                                SecurePreferencesFactory.getDefault());
                        IPreferenceStore prefStore = Activator.getPlugin().getPreferenceStore();
                        String username = prefStore.getString(PreferenceNames.HUB_USERNAME);
                        System.out.println("Username: " + username);
                        String password = securePrefService.getSecurePreference(SecurePreferenceNames.HUB_PASSWORD);
                        String hubUrl = prefStore.getString(PreferenceNames.HUB_URL);
                        String proxyUsername = prefStore.getString(PreferenceNames.PROXY_USERNAME);
                        String proxyPassword = securePrefService.getSecurePreference(SecurePreferenceNames.PROXY_PASSWORD);
                        String proxyPort = prefStore.getString(PreferenceNames.PROXY_PORT);
                        String proxyHost = prefStore.getString(PreferenceNames.PROXY_HOST);
                        String ignoredProxyHosts = prefStore.getString(PreferenceNames.IGNORED_PROXY_HOSTS);
                        String timeout = prefStore.getString(PreferenceNames.HUB_TIMEOUT);
                        this.setHubServerConfigBuilderFields(hubServerConfigBuilder, username, password, hubUrl, proxyUsername, proxyPassword, proxyPort,
                                proxyHost, ignoredProxyHosts, timeout);
                        HubServerConfig hubServerConfig = hubServerConfigBuilder.build();

                        System.out.println("Proxy Host: " + hubServerConfig.getProxyInfo().getHost());
                        System.out.println("Proxy Port: " + hubServerConfig.getProxyInfo().getPort());
                        System.out.println("Username:   " + hubServerConfig.getGlobalCredentials().getUsername());
                        phoneHomeService.phoneHome(hubServerConfig, ThirdPartyName.ECLIPSE, eclipseVersion, pluginVersion, hubVersion);

                        final Gav[] gavs = Activator.getPlugin().getProjectInformation().getAllDependencyGavs(projectName);
                        GavWithParentProject[] gavsWithParents = new GavWithParentProject[gavs.length];
                        for (int i = 0; i < gavs.length; i++) {
                            Gav gav = gavs[i];
                            Map<Gav, List<VulnerabilityItem>> vulnMap = Activator.getPlugin().getProjectInformation().getVulnMap(projectName);
                            boolean hasVulns = vulnMap.get(gav) != null && vulnMap.get(gav).size() > 0;
                            gavsWithParents[i] = new GavWithParentProject(gav, projectName, hasVulns);
                        }
                        return gavsWithParents;
                    }
                } catch (HubIntegrationException e) {
                    throw new RuntimeException(e);
                }

                return NO_HUB_CONNECTION;
            }
            return PROJECT_NOT_ACTIVATED;
        }
        return ERR_UNKNOWN_INPUT;
    }

    public String getInputProject() {
        return inputProject;
    }

    public IPreferenceStore getPreferenceStore() {
        return Activator.getPlugin().getPreferenceStore();
    }

    public ProjectDependencyInformation getProjectInformation() {
        return Activator.getPlugin().getProjectInformation();
    }

    // TODO figure out a better place to put (also in AuthorizationValidator), WET AF
    private void setHubServerConfigBuilderFields(final HubServerConfigBuilder builder, final String username,
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
