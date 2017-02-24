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

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.jface.preference.IPreferenceStore;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.constants.SecurePreferenceNodes;
import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService;
import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.SecurePreferencesService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;
import com.blackducksoftware.integration.eclipseplugin.views.utils.DependencyTableViewerComparator;
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

    private final ProjectInformationService projService;

    private final WorkspaceInformationService workspaceService;

    private final InspectionQueueService inspectionQueueService;

    private VulnerabilityView componentView;

    public ProjectDependencyInformation(final ProjectInformationService projService, WorkspaceInformationService workspaceService,
            ComponentCache componentCache, final InspectionQueueService inspectionQueueService) {
        this.projService = projService;
        this.workspaceService = workspaceService;
        this.componentCache = componentCache;
        this.inspectionQueueService = inspectionQueueService;
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
        PhoneHomeDataService phoneHomeService = Activator.getPlugin().getConnectionService().getPhoneHomeDataService();
        HubVersionRequestService hubVersionRequestService = Activator.getPlugin().getConnectionService().getHubVersionRequestService();
        String hubVersion = hubVersionRequestService.getHubVersion();
        IProduct eclipseProduct = Platform.getProduct();
        String eclipseVersion = eclipseProduct.getDefiningBundle().getVersion().toString();
        String pluginVersion = Platform.getBundle("hub-eclipse-plugin").getVersion().toString();
        AuthorizationValidator authorizationValidator = new AuthorizationValidator(Activator.getPlugin().getConnectionService(), new HubServerConfigBuilder());
        SecurePreferencesService securePrefService = new SecurePreferencesService(SecurePreferenceNodes.BLACK_DUCK,
                SecurePreferencesFactory.getDefault());
        IPreferenceStore prefStore = Activator.getPlugin().getPreferenceStore();
        String username = prefStore.getString(PreferenceNames.HUB_USERNAME);
        String password = securePrefService.getSecurePreference(SecurePreferenceNames.HUB_PASSWORD);
        String hubUrl = prefStore.getString(PreferenceNames.HUB_URL);
        String proxyUsername = prefStore.getString(PreferenceNames.PROXY_USERNAME);
        String proxyPassword = securePrefService.getSecurePreference(SecurePreferenceNames.PROXY_PASSWORD);
        String proxyPort = prefStore.getString(PreferenceNames.PROXY_PORT);
        String proxyHost = prefStore.getString(PreferenceNames.PROXY_HOST);
        String ignoredProxyHosts = prefStore.getString(PreferenceNames.IGNORED_PROXY_HOSTS);
        String timeout = prefStore.getString(PreferenceNames.HUB_TIMEOUT);
        authorizationValidator.setHubServerConfigBuilderFields(username, password, hubUrl,
                proxyUsername, proxyPassword, proxyPort,
                proxyHost, ignoredProxyHosts, timeout);
        HubServerConfig hubServerConfig = authorizationValidator.getHubServerConfigBuilder().build();
        phoneHomeService.phoneHome(hubServerConfig, ThirdPartyName.ECLIPSE, eclipseVersion,
                pluginVersion, hubVersion);
    }

    public void enqueueAllProjectInspections() {
        String[] projects = workspaceService.getJavaProjectNames();
        if (projects != null) {
            for (String projectName : projects) {
                inspectionQueueService.enqueueInspection(projectName);
            }
        }
    }

    public void enqueueProjectInspection(String projectName) {
        inspectionQueueService.enqueueInspection(projectName);
    }

    @Deprecated
    public void inspectAllProjects() {
        Job job = new Job(JOB_INSPECT_ALL) {
            @Override
            public boolean belongsTo(Object family) {
                return family.equals(INSPECTION_JOB);
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IJobManager jobMan = Job.getJobManager();
                Job[] jobList = jobMan.find(INSPECTION_JOB);
                if (jobList.length > 0) {
                    for (Job staleJob : jobList) {
                        if (!staleJob.getName().equals(this.getName())) {
                            staleJob.cancel();
                        }
                    }
                }
                try {
                    phoneHome();
                } catch (HubIntegrationException e1) {
                    // Do nothing
                }
                String[] projects = workspaceService.getJavaProjectNames();
                if (projects != null) {
                    SubMonitor subMonitor = SubMonitor.convert(monitor, projects.length);
                    int i = 1;
                    subMonitor.setTaskName("Inspecting projects");
                    subMonitor.split(1).done();
                    for (String project : projects) {
                        subMonitor.setTaskName(String.format("Inspecting project %1$d/%2$d", i, projects.length));
                        Job subJob = createInspection(project, false);
                        subJob.schedule();
                        try {
                            subJob.join();
                            if (componentView != null && componentView.getLastSelectedProjectName().equals(project)) {
                                componentView.resetInput();
                            }
                        } catch (InterruptedException e) {
                            if (componentView != null) {
                                componentView.openError("Black Duck Inspection interrupted",
                                        "Inspection interrupted before it could reach completion.", e);
                            }
                        }
                        i++;
                        subMonitor.split(1).done();
                    }
                }

                return Status.OK_STATUS;
            }
        };
        job.setPriority(Job.LONG);
        job.schedule();
    }

    @Deprecated
    public Job createInspection(String projectName, final boolean inspectIfNew) {
        Job job = new Job(JOB_INSPECT_PROJECT_PREFACE + projectName) {
            @Override
            public boolean belongsTo(Object family) {
                return family.equals(INSPECTION_JOB);
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                if (!Activator.getPlugin().getConnectionService().hasActiveHubConnection() || (inspectIfNew && projectInfo.containsKey(projectName))
                        || !Activator.getPlugin().getPreferenceStore().getBoolean(projectName)) {
                    return Status.OK_STATUS;
                }
                final List<ComponentModel> models = Collections.synchronizedList(new ArrayList<>());
                projectInfo.put(projectName, models);
                SubMonitor subMonitor = SubMonitor.convert(monitor, 100000);
                subMonitor.setTaskName("Gathering dependencies");
                final List<URL> dependencyFilepaths = projService.getProjectDependencyFilePaths(projectName);
                subMonitor.split(30000).done();
                for (URL filePath : dependencyFilepaths) {
                    subMonitor.setTaskName(String.format("Inspecting %1$s", filePath));
                    Gav gav = projService.getGavFromFilepath(filePath);
                    if (gav != null) {
                        addComponentToProject(projectName, gav);
                        if (dependencyFilepaths.size() < 70000) {
                            subMonitor.split(70000 / dependencyFilepaths.size()).done();
                        } else {
                            subMonitor.split(70000).done();
                        }
                    }
                }
                return Status.OK_STATUS;
            }
        };
        job.setPriority(Job.LONG);
        return job;
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
            models.remove(gav);
            projectInfo.put(projectName, models);
            if (componentView != null) {
                componentView.resetInput();
            }
        }
    }

    public boolean containsComponentsFromProject(final String projectName) {
        return projectInfo.containsKey(projectName);
    }

    public void renameProject(String oldName, String newName) {
        List<ComponentModel> models = projectInfo.get(oldName);
        projectInfo.put(newName, models);
        projectInfo.remove(oldName);
    }

    public void updateCache(RestConnection connection) throws HubIntegrationException {
        if (projectInfo.isEmpty() && Activator.getPlugin().updateConnection(connection).hasActiveHubConnection()) {
            inspectAllProjects();
        }
    }

}
