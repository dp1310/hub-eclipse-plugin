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
package com.blackducksoftware.integration.eclipseplugin.views.listeners

import org.eclipse.jface.util.PropertyChangeEvent
import org.junit.Test

import com.blackducksoftware.integration.eclipseplugin.common.InspectionJob
import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService
import com.blackducksoftware.integration.eclipseplugin.startup.Activator

class PreferenceChangeDisplayUpdateListenerTest {
    def supportedProject = "supportedProject"

    def unsupportedProject = "unsupportedProject"

    def e

    def inspection

    def workspaceInformationService = new WorkspaceInformationService(null){
        @Override
        public boolean getIsSupportedProject(String projectName){
            return projectName.equals(supportedProject)
        }
    }

    def inspectionQueueService = new InspectionQueueService(null, null){
        @Override
        public boolean enqueueInspection(String projectName){
            inspectionQueue.add(inspection);
        }
    }

    def plugin = [
        getInspectionQueueService: { inspectionQueueService },
        getWorkspaceInformationService: { workspaceInformationService }
    ] as Activator

    @Test
    def void testIfSupportedProject() {
        this.setUpTestFor(supportedProject)
        def listener = new PreferenceChangeDisplayUpdateListener(plugin)
        listener.propertyChange(e)
        assert inspectionQueueService.inspectionQueue.contains(inspection)
    }

    @Test
    def void testIfUnsupportedProject() {
        this.setUpTestFor(unsupportedProject)
        def listener = new PreferenceChangeDisplayUpdateListener(plugin)
        listener.propertyChange(e);
        assert !inspectionQueueService.inspectionQueue.contains(inspection)
    }

    def void setUpTestFor(projectName){
        e = new PropertyChangeEvent(new Object(), projectName, null, null)
        inspection = new InspectionJob(null, projectName, null)
    }
}