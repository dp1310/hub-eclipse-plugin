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
package com.blackducksoftware.integration.eclipseplugin.views.providers;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ILazyContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.blackducksoftware.integration.eclipseplugin.common.constants.InspectionStatus;
import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.viewers.filters.ComponentFilter;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

public class DependencyTableViewContentProvider implements ILazyContentProvider {
    private static final ComponentModel[] NOTHING = new ComponentModel[] {};

    private final VulnerabilityView view;

    private final TableViewer viewer;

    private final Activator plugin;

    private String inputProject;

    private ComponentModel[] parsedElements;

    private ComponentFilter componentFilter = null;

    public DependencyTableViewContentProvider(final Activator plugin, final VulnerabilityView view, final TableViewer viewer) {
        this.view = view;
        this.viewer = viewer;
        this.plugin = plugin;
    }

    @Override
    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        if (newInput == null) {
            this.parsedElements = new ComponentModel[] {};
        } else {
            this.parsedElements = (ComponentModel[]) newInput;
            if (componentFilter != null) {
                Stream<ComponentModel> componentStream = Arrays.stream(parsedElements);
                componentStream = componentStream.filter(model -> componentFilter.filter(model));
                this.parsedElements = componentStream.toArray(ComponentModel[]::new);
            }
        }
    }

    public ComponentModel[] parseElements(final Object inputElement) {
        final String projectName = (String) inputElement;
        inputProject = projectName;
        if (projectName.equals("")) {
            view.setStatusMessage(InspectionStatus.NO_SELECTED_PROJECT);
            return NOTHING;
        }
        final InspectionQueueService inspectionQueueService = plugin.getInspectionQueueService();
        final boolean isActivated = plugin.getDefaultPreferencesService().isActivated(projectName);
        if (isActivated) {
            if (plugin.getConnectionService().hasActiveHubConnection()) {
                final List<ComponentModel> componentModels = plugin.getProjectInformation().getProjectComponents(projectName);
                if (componentModels != null) {
                    if (inspectionQueueService.getInspectionIsRunning(projectName)) {
                        view.setStatusMessage(InspectionStatus.PROJECT_INSPECTION_ACTIVE);
                    } else {
                        if (inspectionQueueService.getInspectionIsScheduled(projectName)) {
                            view.setStatusMessage(InspectionStatus.PROJECT_INSPECTION_SCHEDULED);
                        } else if (componentModels.size() == 0) {
                            view.setStatusMessage(InspectionStatus.CONNECTION_OK_NO_COMPONENTS);
                        } else {
                            view.setStatusMessage(InspectionStatus.CONNECTION_OK);
                        }
                    }
                    return componentModels.toArray(new ComponentModel[componentModels.size()]);
                }
                view.setStatusMessage(InspectionStatus.PROJECT_NEEDS_INSPECTION);
                return NOTHING;
            }
            view.setStatusMessage(InspectionStatus.CONNECTION_DISCONNECTED);
            return NOTHING;
        }
        final WorkspaceInformationService workspaceInformationService = plugin.getWorkspaceInformationService();
        if (workspaceInformationService.getIsSupportedProject(projectName)) {
            view.setStatusMessage(InspectionStatus.PROJECT_INSPECTION_INACTIVE);
        } else {
            view.setStatusMessage(InspectionStatus.PROJECT_NOT_SUPPORTED);
        }
        return NOTHING;
    }

    public String getInputProject() {
        return inputProject;
    }

    public IPreferenceStore getPreferenceStore() {
        return plugin.getPreferenceStore();
    }

    public ProjectDependencyInformation getProjectInformation() {
        return plugin.getProjectInformation();
    }

    @Override
    public void updateElement(int index) {
        // TODO: See if there's a graceful way around "Ignored reentrant call while viewer is busy"
        if (parsedElements.length > 0) {
            viewer.setItemCount(parsedElements.length);
            viewer.replace(parsedElements[index], index);
        }
    }

    public void addFilter(ComponentFilter filter) {
        this.componentFilter = filter;
    }

}
