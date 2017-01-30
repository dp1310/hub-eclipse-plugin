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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.buildtool.Gav;
import com.blackducksoftware.integration.hub.dataservice.license.LicenseDataService;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityDataService;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityItemPlusMeta;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.rest.RestConnection;
import com.blackducksoftware.integration.hub.service.HubServicesFactory;
import com.blackducksoftware.integration.log.IntBufferedLogger;

public class ProjectDependencyInformation {

    private final ComponentCache componentCache;

    private final Map<String, Map<Gav, DependencyInfo>> projectInfo = new HashMap<>();

    private final ProjectInformationService projService;

    private final WorkspaceInformationService workspaceService;

    private VulnerabilityView componentView;

    private RestConnection hubConnection;

    public ProjectDependencyInformation(final ProjectInformationService projService, WorkspaceInformationService workspaceService,
            ComponentCache componentCache, RestConnection hubConnection) {
        this.projService = projService;
        this.workspaceService = workspaceService;
        this.componentCache = componentCache;
        this.hubConnection = hubConnection;
    }

    public void setComponentView(final VulnerabilityView componentView) {
        this.componentView = componentView;
    }

    public void removeComponentView() {
        componentView = null;
    }

    public void addNewProject(final String projectName) {
        if (!projectInfo.containsKey(projectName)) {
            addProject(projectName);
        }
    }

    public void addAllProjects() {
        String[] projects = workspaceService.getJavaProjectNames();
        for (String project : projects) {
            addProject(project);
        }
    }

    public void addProject(String projectName) {
        final Gav[] gavs = projService.getMavenAndGradleDependencies(projectName);
        final Map<Gav, DependencyInfo> deps = new ConcurrentHashMap<>();
        for (final Gav gav : gavs) {
            try {
                deps.put(gav, componentCache.get(gav));
            } catch (final IntegrationException e) {
                /*
                 * Thrown if exception occurs when accessing key gav from cache. If an exception is
                 * thrown, info associated with that gav is inaccessible, and so don't put any
                 * information related to said gav into hashmap associated with the project
                 */
                // e.printStackTrace();
            }
        }
        projectInfo.put(projectName, deps);
    }

    public void addWarningToProject(final String projectName, final Gav gav) {
        final Map<Gav, DependencyInfo> deps = projectInfo.get(projectName);
        if (deps != null) {
            try {
                deps.put(gav, componentCache.get(gav));
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

    public void removeProject(final String projectName) {
        projectInfo.remove(projectName);
    }

    public void removeWarningFromProject(final String projectName, final Gav gav) {
        final Map<Gav, DependencyInfo> dependencies = projectInfo.get(projectName);
        if (dependencies != null) {
            dependencies.remove(gav);
            if (componentView != null) {
                componentView.resetInput();
            }
        }
    }

    public boolean containsProject(final String projectName) {
        return projectInfo.containsKey(projectName);
    }

    public Gav[] getAllDependencyGavs(final String projectName) {
        final Map<Gav, DependencyInfo> dependencyInfo = projectInfo.get(projectName);
        if (dependencyInfo != null) {
            return dependencyInfo.keySet().toArray(new Gav[dependencyInfo.keySet().size()]);
        } else {
            return new Gav[0];
        }
    }

    // TODO deprecate
    public Map<Gav, List<VulnerabilityItemPlusMeta>> getVulnMap(String projectName) {
        Map<Gav, List<VulnerabilityItemPlusMeta>> vulnMap = new HashMap<>();

        Map<Gav, DependencyInfo> projDepInfo = projectInfo.get(projectName);
        for (Map.Entry<Gav, DependencyInfo> entry : projDepInfo.entrySet()) {
            vulnMap.put(entry.getKey(), entry.getValue().getVulnList());
        }

        return vulnMap;
    }

    public Map<Gav, DependencyInfo> getDependencyInfoMap(String projectName) {
        return projectInfo.get(projectName);
    }

    public void renameProject(String oldName, String newName) {
        Map<Gav, DependencyInfo> info = projectInfo.get(oldName);
        projectInfo.put(newName, info);
        projectInfo.remove(oldName);
    }

    public void updateCache(RestConnection connection) throws HubIntegrationException {
        if (connection != null) {
            HubServicesFactory servicesFactory = new HubServicesFactory(connection);
            // TODO logging
            VulnerabilityDataService vulnService = servicesFactory.createVulnerabilityDataService(new IntBufferedLogger());
            LicenseDataService licenseService = servicesFactory.createLicenseDataService(new IntBufferedLogger());
            componentCache.setVulnService(vulnService, licenseService);
            addAllProjects();
        } else {
            componentCache.setVulnService(null, null);
        }
    }

    public RestConnection getHubConnection() {
        return this.hubConnection;
    }

}
