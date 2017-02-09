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
package com.blackducksoftware.integration.eclipseplugin.internal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.buildtool.Gav;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDependencyInformationTest {

    private final String proj = "proj";

    @Mock
    Gav gav1, gav2, gav3;

    @Mock
    DependencyInfo vulnerabilities1, vulnerabilities2, vulnerabilities3;

    @Mock
    ComponentCache componentCache;

    @Mock
    ProjectInformationService projService;

    @Mock
    WorkspaceInformationService workspaceService;

    private void prepareCache() throws IntegrationException {
        Mockito.when(componentCache.get(gav1)).thenReturn(vulnerabilities1);
        Mockito.when(componentCache.get(gav2)).thenReturn(vulnerabilities2);
        Mockito.when(componentCache.get(gav3)).thenReturn(vulnerabilities3);
    }

    private boolean containsGav(final Gav[] gavs, final Gav gav) {
        for (final Gav curGav : gavs) {
            if (curGav.equals(gav)) {
                return true;
            }
        }
        return false;
    }

    @Test
    public void testAddingProject() throws IntegrationException {
        prepareCache();
        Mockito.when(projService.getGavsFromFilepaths(projService.getProjectDependencyFilePaths(proj))).thenReturn(Arrays.asList(gav1, gav2, gav3));
        final ProjectDependencyInformation projInfo = new ProjectDependencyInformation(projService, workspaceService, componentCache);
        projInfo.inspectProject(proj, true);
        final Gav[] gavs = projInfo.getAllDependencyGavs(proj);
        assertTrue(containsGav(gavs, gav1));
        assertTrue(containsGav(gavs, gav2));
        assertTrue(containsGav(gavs, gav3));
    }

    @Test
    public void testAddingDependency() throws IntegrationException {
        prepareCache();
        Mockito.when(projService.getGavsFromFilepaths(projService.getProjectDependencyFilePaths(proj))).thenReturn(Arrays.asList(gav1, gav2));
        final ProjectDependencyInformation projInfo = new ProjectDependencyInformation(projService, workspaceService, componentCache);
        projInfo.inspectProject(proj, true);
        assertTrue(projInfo.containsProject(proj));
        final Gav[] gavsBefore = projInfo.getAllDependencyGavs(proj);
        assertFalse(containsGav(gavsBefore, gav3));
        projInfo.addWarningToProject(proj, gav3);
        final Gav[] gavsAfter = projInfo.getAllDependencyGavs(proj);
        assertTrue(containsGav(gavsAfter, gav3));
    }

    @Test
    public void testRemovingDependency() throws IntegrationException {
        prepareCache();
        Mockito.when(projService.getGavsFromFilepaths(projService.getProjectDependencyFilePaths(proj))).thenReturn(Arrays.asList(gav1, gav2, gav3));
        final ProjectDependencyInformation projInfo = new ProjectDependencyInformation(projService, workspaceService, componentCache);
        projInfo.inspectProject(proj, true);
        final Gav[] gavsBefore = projInfo.getAllDependencyGavs(proj);
        assertTrue(containsGav(gavsBefore, gav3));
        projInfo.removeWarningFromProject(proj, gav3);
        final Gav[] gavsAfter = projInfo.getAllDependencyGavs(proj);
        assertFalse(containsGav(gavsAfter, gav3));
    }

    @Test
    public void testRemovingProject() throws IntegrationException {
        prepareCache();
        Mockito.when(projService.getGavsFromFilepaths(projService.getProjectDependencyFilePaths(proj))).thenReturn(Arrays.asList(gav1, gav2));
        final ProjectDependencyInformation projInfo = new ProjectDependencyInformation(projService, workspaceService, componentCache);
        projInfo.inspectProject(proj, true);
        assertTrue(projInfo.containsProject(proj));
        projInfo.removeProject(proj);
        assertFalse(projInfo.containsProject(proj));
    }
}
