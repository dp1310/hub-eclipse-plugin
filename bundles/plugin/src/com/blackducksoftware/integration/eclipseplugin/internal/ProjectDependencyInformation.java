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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.jface.preference.IPreferenceStore;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNodes;
import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService;
import com.blackducksoftware.integration.eclipseplugin.common.services.SecurePreferencesService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.DependencyTableViewerComparator;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.nonpublic.HubVersionRequestService;
import com.blackducksoftware.integration.hub.builder.HubServerConfigBuilder;
import com.blackducksoftware.integration.hub.buildtool.Gav;
import com.blackducksoftware.integration.hub.dataservice.phonehome.PhoneHomeDataService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.global.HubServerConfig;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.phone.home.enums.ThirdPartyName;

public class ProjectDependencyInformation {
    public static final String JOB_INSPECT_ALL = "Black Duck Hub inspecting all projects";

    public static final String JOB_INSPECT_PROJECT_PREFACE = "Black Duck Hub inspecting ";

    public static final String INSPECTION_JOB = "Black Duck Hub Inspection";

    private final ComponentCache componentCache;

    private final Map<String, List<ComponentModel>> projectInfo = new HashMap<>();

    private VulnerabilityView componentView;

    public ProjectDependencyInformation(ComponentCache componentCache) {
        this.componentCache = componentCache;
    }

    public VulnerabilityView getComponentView() {
        return componentView;
    }

    public void setComponentView(final VulnerabilityView componentView) {
        this.componentView = componentView;
    }

    public void removeComponentView() {
        componentView = null;
    }

    public List<String> getRunningInspections() {
        IJobManager jobMan = Job.getJobManager();
        ArrayList<String> inspectionList = new ArrayList<>();
        Job[] inspections = jobMan.find(INSPECTION_JOB);
        for (Job inspection : inspections) {
            inspectionList.add(inspection.getName());
        }
        return inspectionList;
    }

    public void phoneHome() throws HubIntegrationException {
        if (!Activator.getPlugin().getConnectionService().hasActiveHubConnection()) {
            return;
        }
        final PhoneHomeDataService phoneHomeService = Activator.getPlugin().getConnectionService().getPhoneHomeDataService();
        final HubVersionRequestService hubVersionRequestService = Activator.getPlugin().getConnectionService().getHubVersionRequestService();
        final String hubVersion = hubVersionRequestService.getHubVersion();
        final IProduct eclipseProduct = Platform.getProduct();
        final String eclipseVersion = eclipseProduct.getDefiningBundle().getVersion().toString();
        final String pluginVersion = Platform.getBundle("hub-eclipse-plugin").getVersion().toString();
        final AuthorizationValidator authorizationValidator = new AuthorizationValidator(Activator.getPlugin().getConnectionService(),
                new HubServerConfigBuilder());
        final SecurePreferencesService securePrefService = new SecurePreferencesService(SecurePreferenceNodes.BLACK_DUCK,
                SecurePreferencesFactory.getDefault());
        final IPreferenceStore prefStore = Activator.getPlugin().getPreferenceStore();
        final String username = prefStore.getString(PreferenceNames.HUB_USERNAME);
        final String password = securePrefService.getSecurePreference(SecurePreferenceNames.HUB_PASSWORD);
        final String hubUrl = prefStore.getString(PreferenceNames.HUB_URL);
        final String proxyUsername = prefStore.getString(PreferenceNames.PROXY_USERNAME);
        final String proxyPassword = securePrefService.getSecurePreference(SecurePreferenceNames.PROXY_PASSWORD);
        final String proxyPort = prefStore.getString(PreferenceNames.PROXY_PORT);
        final String proxyHost = prefStore.getString(PreferenceNames.PROXY_HOST);
        final String ignoredProxyHosts = prefStore.getString(PreferenceNames.IGNORED_PROXY_HOSTS);
        final String timeout = prefStore.getString(PreferenceNames.HUB_TIMEOUT);
        authorizationValidator.setHubServerConfigBuilderFields(username, password, hubUrl,
                proxyUsername, proxyPassword, proxyPort,
                proxyHost, ignoredProxyHosts, timeout);
        HubServerConfig hubServerConfig = authorizationValidator.getHubServerConfigBuilder().build();
        phoneHomeService.phoneHome(hubServerConfig, ThirdPartyName.ECLIPSE, eclipseVersion,
                pluginVersion, hubVersion);
    }

    public List<ComponentModel> initializeProjectComponents(final String projectName) {
        return projectInfo.put(projectName, Collections.synchronizedList(new ArrayList<ComponentModel>()));
    }

    public List<ComponentModel> addProjectComponents(final String projectName, final List<ComponentModel> models) {
        return projectInfo.put(projectName, models);
    }

    public void addComponentToProject(final String projectName, final Gav gav) {
        final List<ComponentModel> models = projectInfo.get(projectName);
        if (models != null) {
            try {
                models.add(componentCache.get(gav));
                models.sort(new DependencyTableViewerComparator());
                projectInfo.put(projectName, models);
                if (componentView != null) {
                    componentView.resetInput();
                }
            } catch (IntegrationException e) {
                /*
                 * Thrown if exception occurs when accessing key gav from cache. If an exception is
                 * thrown, info associated with that gav is inaccessible, and so don't put any
                 * information related to said gav into hashmap associated with the project
                 */
            }
        }
    }

    public List<ComponentModel> getProjectComponents(final String projectName) {
        List<ComponentModel> models = projectInfo.get(projectName);
        if (models == null) {
            return new ArrayList<>();
        }
        return projectInfo.get(projectName);
    }

    public void removeProject(final String projectName) {
        projectInfo.remove(projectName);
    }

    public void removeComponentFromProject(final String projectName, final Gav gav) {
        final List<ComponentModel> models = projectInfo.get(projectName);
        if (models != null) {
            for (Iterator<ComponentModel> iterator = models.iterator(); iterator.hasNext();) {
                ComponentModel model = iterator.next();
                if (model.getGav().equals(gav)) {
                    iterator.remove();
                }
            }
            projectInfo.put(projectName, models);
            if (componentView != null) {
                componentView.resetInput();
            }
        }
    }

    public boolean containsComponentsFromProject(final String projectName) {
        return projectInfo.containsKey(projectName);
    }

    public void renameProject(final String oldName, final String newName) {
        List<ComponentModel> models = projectInfo.get(oldName);
        projectInfo.put(newName, models);
        projectInfo.remove(oldName);
    }

    public void updateCache(final RestConnection connection) throws HubIntegrationException {
        if (projectInfo.isEmpty() && Activator.getPlugin().updateConnection(connection).hasActiveHubConnection()) {
            final InspectionQueueService inspectionQueueService = Activator.getPlugin().getInspectionQueueService();
            final WorkspaceInformationService workspaceInformationService = Activator.getPlugin().getWorkspaceInformationService();
            List<String> supportedJavaProjects = workspaceInformationService.getSupportedJavaProjectNames();
            inspectionQueueService.enqueueInspections(supportedJavaProjects);
        }
    }

}
