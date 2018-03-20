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
package com.blackducksoftware.integration.eclipseplugin.views.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

public class ProjectSelectionListener implements ISelectionListener {
    private final VulnerabilityView componentView;

    public ProjectSelectionListener(final VulnerabilityView componentView) {
        this.componentView = componentView;
    }

    @Override
    public void selectionChanged(final IWorkbenchPart part, final ISelection sel) {
        if (!(sel instanceof IStructuredSelection)) {
            return;
        }
        final IStructuredSelection ss = (IStructuredSelection) sel;
        final Object element = ss.getFirstElement();
        IProject project = null;
        IResource resource = null;
        if (element instanceof IProject) {
            project = (IProject) element;
        } else {
            if (element instanceof IResource) {
                resource = ((IResource) element);
            } else if (element instanceof IAdaptable) {
                IAdaptable probableProject = ((IAdaptable) element);
                resource = probableProject.getAdapter(IResource.class);
            } else {
                resource = extractResourceFromEditor();
            }
            if (resource != null) {
                project = resource.getProject();
            }
        }
        String projectName = "";
        try {
            if (project != null && project.getDescription() != null && componentView.getDependencyTableViewerExists()) {
                projectName = project.getDescription().getName();
                componentView.setLastSelectedProjectName(projectName);
                componentView.setTableInput(projectName);
            }
        } catch (final CoreException e) {
            // Do nothing, we just don't set the project name
        }
    }

    public IResource extractResourceFromEditor() {
        IWorkbench iworkbench = PlatformUI.getWorkbench();
        if (iworkbench == null) return null;
        IWorkbenchWindow iworkbenchwindow = iworkbench.getActiveWorkbenchWindow();
        if (iworkbenchwindow == null) return null;
        IWorkbenchPage iworkbenchpage = iworkbenchwindow.getActivePage();
        if (iworkbenchpage == null) return null;
        IEditorPart ieditorpart = iworkbenchpage.getActiveEditor();
        if (ieditorpart == null) return null;
        IEditorInput input = ieditorpart.getEditorInput();
        if (!(input instanceof IFileEditorInput))
            return null;
        return ((IFileEditorInput) input).getFile();
    }
}
