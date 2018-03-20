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
package com.blackducksoftware.integration.eclipseplugin.common.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.blackducksoftware.integration.eclipseplugin.common.constants.NatureIDs;

public class WorkspaceInformationService {
    private final ProjectInformationService projectInformationService;

    public WorkspaceInformationService(final ProjectInformationService projectInformationService) {
        this.projectInformationService = projectInformationService;
    }

    public List<IProject> getAllSupportedProjects() {
        IProject[] allProjects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
        ArrayList<IProject> supportedProjects = new ArrayList<>();
        for (IProject project : allProjects) {
            for (String nature : NatureIDs.SUPPORTED_NATURES)
                try {
                    if (project.hasNature(nature) && !supportedProjects.contains(project)) {
                        supportedProjects.add(project);
                    }
                } catch (CoreException e) {
                    // do nothing
                }
        }
        return supportedProjects;
    }

    public boolean getIsSupportedProject(final String projectName) {
        for (final String supportedProjectName : getSupportedJavaProjectNames()) {
            if (projectName.equals(supportedProjectName)) {
                return true;
            }
        }
        return false;
    }

    public int getNumSupportedJavaProjects() {
        final List<IProject> projects = getAllSupportedProjects();
        int numJava = 0;
        for (final IProject project : projects) {
            if (projectInformationService.isJavaProject(project)) {
                numJava++;
            }
        }
        return numJava;
    }

    public List<String> getSupportedJavaProjectNames() {
        final List<IProject> projects = getAllSupportedProjects();
        final List<String> names = new ArrayList<>();
        for (final IProject project : projects) {
            if (projectInformationService.isJavaProject(project)) {
                try {
                    final IProjectDescription projectDescription = project.getDescription();
                    if (projectDescription != null) {
                        final String projectName = projectDescription.getName();
                        names.add(projectName);
                    }
                } catch (final CoreException e) {
                    /*
                     * If unsuccessful getting project description, means that project doesn't
                     * exist or is closed. In that case, do not add project name to list of
                     * java project names
                     */
                }
            }
        }
        Collections.sort(names, String.CASE_INSENSITIVE_ORDER);
        return names;
    }

    public String getSelectedProject() {
        final IStructuredSelection selection = getWorkspaceSelection();
        if (selection != null && selection.getFirstElement() != null) {
            final Object selected = selection.getFirstElement();
            if (selected instanceof IAdaptable) {
                return getSelectedProjectName((IAdaptable) selected);
            }
        }
        return "";
    }

    public List<String> getAllSelectedProjects() {
        ArrayList<String> projectNames = new ArrayList<>();
        final IStructuredSelection selection = getWorkspaceSelection();
        if (selection != null && !selection.toList().isEmpty()) {
            List<?> selectedObjects = selection.toList();
            for (Object selected : selectedObjects) {
                if (selected instanceof IAdaptable) {
                    String projectName = getSelectedProjectName((IAdaptable) selected);
                    if (!projectName.equals("")) {
                        projectNames.add(projectName);
                    }
                }
            }

        }
        return projectNames;
    }

    private IStructuredSelection getWorkspaceSelection() {
        final IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWindow != null) {
            final ISelectionService selectionService = activeWindow.getSelectionService();
            if (selectionService != null) {
                return (IStructuredSelection) selectionService.getSelection();
            }
        }
        return null;
    }

    private String getSelectedProjectName(IAdaptable selected) {
        final IProject project = selected.getAdapter(IProject.class);
        try {
            if (project != null && project.getDescription() != null) {
                return project.getDescription().getName();
            }
        } catch (final CoreException e) {
            // do nothing
        }
        return "";
    }

}
