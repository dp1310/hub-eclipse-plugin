/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.eclipseplugin.views.listeners

import org.eclipse.core.resources.IProject
import org.eclipse.core.resources.IProjectDescription
import org.eclipse.core.resources.IResource
import org.eclipse.core.runtime.CoreException
import org.eclipse.core.runtime.IAdaptable
import org.eclipse.core.runtime.IStatus
import org.eclipse.jface.viewers.IStructuredSelection
import org.eclipse.jface.viewers.TreeSelection
import org.junit.Test

import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView

class ProjectSelectionListenerTest {
    def view = [resetInput: { },
        getDependencyTableViewerExists: { true },
        setTableInput: { }] as VulnerabilityView

    def structuredSelection = [getFirstElement: { project }] as IStructuredSelection

    def project = [getDescription: { description }] as IProject

    def description = [getName: { projectName }] as IProjectDescription

    def e = new CoreException( [getMessage: { "Test Core Exception" }] as IStatus )

    def notAStructuredSelection = [] as TreeSelection

    def projectName = "someProject"

    def noProjectSelected = ""

    def resource = [getProject: { project }] as IResource

    def adaptable = [getAdapter: { resource }] as IAdaptable

    @Test
    def void testNotStructuredSelection() {
        final ProjectSelectionListener listener = new ProjectSelectionListener(view);
        listener.selectionChanged(null, notAStructuredSelection);
        assert view.getLastSelectedProjectName().equals(noProjectSelected)
    }

    @Test
    def void testStructuredProjectSelection() throws CoreException {
        structuredSelection = [getFirstElement: { project }] as IStructuredSelection
        final ProjectSelectionListener listener = new ProjectSelectionListener(view);
        listener.selectionChanged(null, structuredSelection);
        assert view.lastSelectedProjectName.contentEquals(projectName)
    }

    @Test
    def void testStructuredResourceSelection() throws CoreException {
        structuredSelection = [getFirstElement: { resource }] as IStructuredSelection
        final ProjectSelectionListener listener = new ProjectSelectionListener(view);
        listener.selectionChanged(null, structuredSelection);
        assert view.lastSelectedProjectName.contentEquals(projectName)
    }

    @Test
    def void testStructuredAdaptableSelection() throws CoreException {
        structuredSelection = [getFirstElement: { adaptable }] as IStructuredSelection
        final ProjectSelectionListener listener = new ProjectSelectionListener(view);
        listener.selectionChanged(null, structuredSelection);
        assert view.lastSelectedProjectName.contentEquals(projectName)
    }

    @Test
    def void testWhenCoreExceptionThrown() throws CoreException {
        project = [getDescription: { throw e }] as IProject
        final ProjectSelectionListener listener = new ProjectSelectionListener(view);
        listener.selectionChanged(null, structuredSelection);
        assert view.lastSelectedProjectName.contentEquals(noProjectSelected)
    }
}
