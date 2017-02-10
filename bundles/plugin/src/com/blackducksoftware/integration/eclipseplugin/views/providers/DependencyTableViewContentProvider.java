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

import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;
import com.blackducksoftware.integration.hub.buildtool.Gav;

public class DependencyTableViewContentProvider implements IStructuredContentProvider {

    public static final String[] NO_SELECTED_PROJECT = new String[] { "No open project currently selected" };

    public static final String[] INSPECTION_ACTIVE = new String[] { "Project scheduled for inspection" };

    public static final String[] PROJECT_NOT_ACTIVATED = new String[] {
            "Black Duck inspection not activated for current project" };

    public static final String[] ERR_UNKNOWN_INPUT = new String[] { "Input is of unknown type" };

    public static final String[] PROJECT_NEEDS_INSPECTION = new String[] { "Project has not yet been inspected" };

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
                if (Activator.getPlugin().getConnectionService().hasActiveHubConnection()) {

                    final Gav[] gavs = Activator.getPlugin().getProjectInformation().getAllDependencyGavs(projectName);
                    GavWithParentProject[] gavsWithParents = new GavWithParentProject[gavs.length];
                    for (int i = 0; i < gavs.length; i++) {
                        Gav gav = gavs[i];
                        Map<Gav, List<VulnerabilityItem>> vulnMap = Activator.getPlugin().getProjectInformation().getVulnMap(projectName);
                        boolean hasVulns = vulnMap.get(gav) != null && vulnMap.get(gav).size() > 0;
                        gavsWithParents[i] = new GavWithParentProject(gav, projectName, hasVulns);
                    }
                    if (gavsWithParents.length == 0) {
                        List<String> runningInspections = Activator.getPlugin().getProjectInformation().getRunningInspections();
                        if (!runningInspections.isEmpty()) {
                            return INSPECTION_ACTIVE;
                        } else {
                            return PROJECT_NEEDS_INSPECTION;
                        }
                    }
                    return gavsWithParents;
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

}
