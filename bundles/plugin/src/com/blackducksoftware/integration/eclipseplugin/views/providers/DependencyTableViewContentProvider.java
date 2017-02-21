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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;

import com.blackducksoftware.integration.eclipseplugin.common.constants.InspectionStatus;
import com.blackducksoftware.integration.eclipseplugin.internal.DependencyInfo;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;
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
                view.setStatusMessage(InspectionStatus.NO_SELECTED_PROJECT);
                return NOTHING;
            }
            boolean isActivated = Activator.getPlugin().getPreferenceStore().getBoolean(projectName);
            if (isActivated) {
                if (Activator.getPlugin().getConnectionService().hasActiveHubConnection()) {
                    final Map<Gav, DependencyInfo> gavInfos = Activator.getPlugin().getProjectInformation().getDependencyInfoMap(projectName);
                    ArrayList<GavWithParentProject> gavsWithParents = new ArrayList<>();
                    for (Entry<Gav, DependencyInfo> gavInfo : gavInfos.entrySet()) {
                        gavsWithParents.add(new GavWithParentProject(gavInfo.getKey(), projectName, gavInfo.getValue().getLicenseIsKnown(),
                                gavInfo.getValue().getComponentIsKnown()));
                    }
                    List<String> runningInspections = Activator.getPlugin().getProjectInformation().getRunningInspections();
                    if (runningInspections.contains(ProjectDependencyInformation.JOB_INSPECT_PROJECT_PREFACE + projectName)) {
                        view.setStatusMessage(InspectionStatus.PROJECT_INSPECTION_ACTIVE);
                    } else if (gavsWithParents.size() == 0) {
                        view.setStatusMessage(
                                runningInspections.contains(ProjectDependencyInformation.JOB_INSPECT_ALL)
                                        ? InspectionStatus.PROJECT_NEEDS_INSPECTION
                                        : InspectionStatus.PROJECT_INSPECTION_SCHEDULED);
                    } else {
                        view.setStatusMessage(InspectionStatus.CONNECTION_OK);
                    }
                    return gavsWithParents.toArray();
                }
                view.setStatusMessage(InspectionStatus.CONNECTION_DISCONNECTED);
                return NOTHING;
            }
            view.setStatusMessage(InspectionStatus.PROJECT_INSPECTION_INACTIVE);
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
