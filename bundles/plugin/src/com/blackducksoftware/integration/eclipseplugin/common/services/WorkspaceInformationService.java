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
package com.blackducksoftware.integration.eclipseplugin.common.services;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

public class WorkspaceInformationService {

    private final ProjectInformationService projectInformationService;

    public WorkspaceInformationService(final ProjectInformationService projectInformationService) {
        this.projectInformationService = projectInformationService;
    }

    public IProject[] getAllProjects() {
        return ResourcesPlugin.getWorkspace().getRoot().getProjects();
    }

    public int getNumJavaProjects() {
        final IProject[] projects = getAllProjects();
        int numJava = 0;
        for (final IProject project : projects) {
            if (projectInformationService.isJavaProject(project)) {
                numJava++;
            }
        }
        return numJava;
    }

    public String[] getJavaProjectNames() {
        final IProject[] projects = getAllProjects();
        final int numJavaProjects = getNumJavaProjects();
        final String[] names = new String[numJavaProjects];
        int javaIndex = 0;
        for (final IProject project : projects) {
            if (projectInformationService.isJavaProject(project)) {
                try {
                    final IProjectDescription projectDescription = project.getDescription();
                    if (projectDescription != null) {
                        final String projectName = projectDescription.getName();
                        names[javaIndex] = projectName;
                        javaIndex++;
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
        return names;
    }

    public String getSelectedProject() {
        final IWorkbenchWindow activeWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (activeWindow != null) {
            final ISelectionService selectionService = activeWindow.getSelectionService();
            if (selectionService != null) {
                final IStructuredSelection selection = (IStructuredSelection) selectionService.getSelection();
                if (selection != null && selection.getFirstElement() != null) {
                    final Object selected = selection.getFirstElement();
                    if (selected instanceof IAdaptable) {
                        final IProject project = ((IAdaptable) selected).getAdapter(IProject.class);
                        try {
                            if (project != null && project.getDescription() != null) {
                                return project.getDescription().getName();
                            } else {
                                return "";
                            }
                        } catch (final CoreException e) {
                            return "";
                        }

                    } else {
                        return "";
                    }
                } else {
                    return "";
                }

            } else {
                return "";
            }

        } else {
            return "";
        }
    }

}
