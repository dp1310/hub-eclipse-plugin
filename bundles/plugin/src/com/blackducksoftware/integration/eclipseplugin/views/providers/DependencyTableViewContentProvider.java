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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.blackducksoftware.integration.eclipseplugin.common.constants.ConnectionStatus;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;
import com.blackducksoftware.integration.hub.buildtool.Gav;

public class DependencyTableViewContentProvider implements IStructuredContentProvider {

    private static final String[] NOTHING = new String[] {};

    private String inputProject;

    private VulnerabilityView view;

    public DependencyTableViewContentProvider(VulnerabilityView view) {
        this.view = view;
    }

    @Override
    public Object[] getElements(Object inputElement) {
        if (inputElement instanceof String) {
            String projectName = (String) inputElement;
            inputProject = projectName;
            if (projectName.equals("")) {
                view.setStatusMessage(ConnectionStatus.NO_SELECTED_PROJECT);
                return NOTHING;
            }
            boolean isActivated = Activator.getPlugin().getPreferenceStore().getBoolean(projectName);
            if (isActivated) {
                if (Activator.getPlugin().getConnectionService().hasActiveHubConnection()) {

                    final Gav[] gavs = Activator.getPlugin().getProjectInformation().getAllDependencyGavs(projectName);
                    GavWithParentProject[] gavsWithParents = new GavWithParentProject[gavs.length];
                    for (int i = 0; i < gavs.length; i++) {
                        Gav gav = gavs[i];
                        Map<Gav, List<VulnerabilityItem>> vulnMap = Activator.getPlugin().getProjectInformation().getVulnMap(projectName);
                        boolean hasVulns = vulnMap.get(gav) != null && vulnMap.get(gav).size() > 0;
                        gavsWithParents[i] = new GavWithParentProject(gav, projectName, hasVulns);
                    }
                    if (gavsWithParents.length != 0) {
                        view.setStatusMessage(ConnectionStatus.CONNECTION_OK);
                        return gavsWithParents;
                    }
                    List<String> runningInspections = Activator.getPlugin().getProjectInformation().getRunningInspections();
                    if (!runningInspections.isEmpty()) {
                        view.setStatusMessage(ConnectionStatus.PROJECT_INSPECTION_ACTIVE);
                    } else {
                        view.setStatusMessage(ConnectionStatus.PROJECT_NEEDS_INSPECTION);
                    }
                    return NOTHING;
                }
                view.setStatusMessage(ConnectionStatus.CONNECTION_DISCONNECTED);
                return NOTHING;
            }
            view.setStatusMessage(ConnectionStatus.PROJECT_INSPECTION_INACTIVE);
            return NOTHING;
        }
        view.setStatusMessage("Error: Unknown Input");
        return NOTHING;
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

}
