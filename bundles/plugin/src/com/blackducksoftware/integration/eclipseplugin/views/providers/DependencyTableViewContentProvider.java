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

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.blackducksoftware.integration.eclipseplugin.common.constants.InspectionStatus;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

public class DependencyTableViewContentProvider implements ILazyContentProvider {
    private static final ComponentModel[] NOTHING = new ComponentModel[] {};

    private final VulnerabilityView view;

    private final TableViewer viewer;

    private String inputProject;

    private ComponentModel[] parsedElements;

    public DependencyTableViewContentProvider(VulnerabilityView view, TableViewer viewer) {
        this.view = view;
        this.viewer = viewer;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        this.parsedElements = (ComponentModel[]) newInput;
    }

    public ComponentModel[] parseElements(Object inputElement) {
        String projectName = (String) inputElement;
        inputProject = projectName;
        if (projectName.equals("")) {
            view.setStatusMessage(InspectionStatus.NO_SELECTED_PROJECT);
            return NOTHING;
        }
        boolean isActivated = Activator.getPlugin().getPreferenceStore().getBoolean(projectName);
        if (isActivated) {
            if (Activator.getPlugin().getConnectionService().hasActiveHubConnection()) {
                final List<ComponentModel> componentModels = Activator.getPlugin().getProjectInformation().getProjectComponents(projectName);
                List<String> runningInspections = Activator.getPlugin().getProjectInformation().getRunningInspections();
                if (runningInspections.contains(ProjectDependencyInformation.JOB_INSPECT_PROJECT_PREFACE + projectName)) {
                    view.setStatusMessage(InspectionStatus.PROJECT_INSPECTION_ACTIVE);
                } else if (componentModels.size() == 0) {
                    view.setStatusMessage(
                            runningInspections.contains(ProjectDependencyInformation.JOB_INSPECT_ALL)
                                    ? InspectionStatus.PROJECT_INSPECTION_SCHEDULED
                                    : InspectionStatus.PROJECT_NEEDS_INSPECTION);
                } else {
                    view.setStatusMessage(InspectionStatus.CONNECTION_OK);
                }
                return componentModels.toArray(new ComponentModel[componentModels.size()]);
            }
            view.setStatusMessage(InspectionStatus.CONNECTION_DISCONNECTED);
            return NOTHING;
        }
        view.setStatusMessage(InspectionStatus.PROJECT_INSPECTION_INACTIVE);
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

    @Override
    public void updateElement(int index) {
        viewer.replace(parsedElements[index], index);
    }

}
