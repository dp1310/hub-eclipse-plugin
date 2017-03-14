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
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService;
import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.buildtool.Gav;

@RunWith(MockitoJUnitRunner.class)
@Ignore
public class ProjectDependencyInformationTest {

    private final String proj = "proj";

    @Mock
    Gav gav1, gav2, gav3;

    @Mock
    ComponentModel vulnerabilities1, vulnerabilities2, vulnerabilities3;

    @Mock
    ComponentCache componentCache;

    @Mock
    ProjectInformationService projService;

    @Mock
    InspectionQueueService inspectionQueueService;

    @Test
    public void testAddingProject() throws IntegrationException {
        Mockito.when(projService.getGavsFromFilepaths(projService.getProjectDependencyFilePaths(proj))).thenReturn(Arrays.asList(gav1, gav2, gav3));
        final ProjectDependencyInformation projInfo = new ProjectDependencyInformation(Activator.getPlugin(), componentCache);
        inspectionQueueService.enqueueInspection(proj);
        final List<ComponentModel> componentModels = projInfo.getProjectComponents(proj);
        assertTrue(componentModels.contains(vulnerabilities1));
        assertTrue(componentModels.contains(vulnerabilities2));
        assertTrue(componentModels.contains(vulnerabilities3));
    }

    @Test
    public void testAddingDependency() throws IntegrationException {
        Mockito.when(projService.getGavsFromFilepaths(projService.getProjectDependencyFilePaths(proj))).thenReturn(Arrays.asList(gav1, gav2));
        final ProjectDependencyInformation projInfo = new ProjectDependencyInformation(Activator.getPlugin(), componentCache);
        inspectionQueueService.enqueueInspection(proj);
        assertTrue(projInfo.containsComponentsFromProject(proj));
        final List<ComponentModel> oldComponentModels = projInfo.getProjectComponents(proj);
        assertFalse(oldComponentModels.contains(vulnerabilities3));
        projInfo.addComponentToProject(proj, gav3);
        final List<ComponentModel> newComponentModels = projInfo.getProjectComponents(proj);
        assertTrue(newComponentModels.contains(vulnerabilities3));
    }

    @Test
    public void testRemovingDependency() throws IntegrationException {
        Mockito.when(projService.getGavsFromFilepaths(projService.getProjectDependencyFilePaths(proj))).thenReturn(Arrays.asList(gav1, gav2, gav3));
        final ProjectDependencyInformation projInfo = new ProjectDependencyInformation(Activator.getPlugin(), componentCache);
        inspectionQueueService.enqueueInspection(proj);
        final List<ComponentModel> oldComponentModels = projInfo.getProjectComponents(proj);
        assertTrue(oldComponentModels.contains(vulnerabilities3));
        projInfo.removeComponentFromProject(proj, gav3);
        final List<ComponentModel> newComponentModels = projInfo.getProjectComponents(proj);
        assertFalse(newComponentModels.contains(vulnerabilities3));
    }

    @Test
    public void testRemovingProject() throws IntegrationException {
        Mockito.when(projService.getGavsFromFilepaths(projService.getProjectDependencyFilePaths(proj))).thenReturn(Arrays.asList(gav1, gav2));
        final ProjectDependencyInformation projInfo = new ProjectDependencyInformation(Activator.getPlugin(), componentCache);
        inspectionQueueService.enqueueInspection(proj);
        assertTrue(projInfo.containsComponentsFromProject(proj));
        projInfo.removeProject(proj);
        assertFalse(projInfo.containsComponentsFromProject(proj));
    }
}
