/**
 * hub-eclipse-plugin-test
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
package com.blackducksoftware.integration.eclipseplugin.views.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.ui.IWorkbenchPart;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

@RunWith(MockitoJUnitRunner.class)
public class ProjectSelectionListenerTest {

    @Mock
    VulnerabilityView view;

    @Mock
    IWorkbenchPart part;

    @Mock
    ISelection sel;

    @Mock
    IStructuredSelection structuredSel;

    @Mock
    IAdaptable adaptable;

    @Mock
    IProject project;

    @Mock
    TableViewer table;

    @Mock
    IProjectDescription description;

    @Mock
    CoreException e;

    private final String PROJECT_NAME = "name";

    private final String SELECTED_PROJECT_NAME = "name";

    private void setUpRelationships() throws CoreException {
        Mockito.when(structuredSel.getFirstElement()).thenReturn(adaptable);
        Mockito.when(adaptable.getAdapter(IProject.class)).thenReturn(project);
        Mockito.when(project.getDescription()).thenReturn(description);
        Mockito.when(description.getName()).thenReturn(PROJECT_NAME);
        Mockito.when(view.getDependencyTableViewer()).thenReturn(table);
    }

    @Test
    public void testNotStructuredSelection() {
        final ProjectSelectionListener listener = new ProjectSelectionListener(view);
        listener.selectionChanged(part, sel);
        Mockito.verify(view, Mockito.times(0)).setLastSelectedProjectName(SELECTED_PROJECT_NAME);
    }

    @Test
    public void testStructuredSelection() throws CoreException {
        setUpRelationships();
        final ProjectSelectionListener listener = new ProjectSelectionListener(view);
        listener.selectionChanged(part, structuredSel);
        Mockito.verify(view, Mockito.times(1)).setLastSelectedProjectName(PROJECT_NAME);
        Mockito.verify(view, Mockito.times(1)).setTableInput(PROJECT_NAME);
    }

    @Test
    public void testWhenCoreExceptionThrown() throws CoreException {
        setUpRelationships();
        Mockito.when(project.getDescription()).thenThrow(e);
        final ProjectSelectionListener listener = new ProjectSelectionListener(view);
        listener.selectionChanged(part, structuredSel);
        Mockito.verify(view, Mockito.times(1)).setLastSelectedProjectName("");
        Mockito.verify(view, Mockito.times(1)).setTableInput("");
    }

}
