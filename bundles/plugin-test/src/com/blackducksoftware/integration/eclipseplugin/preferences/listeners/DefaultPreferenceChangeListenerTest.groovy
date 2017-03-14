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
package com.blackducksoftware.integration.eclipseplugin.preferences.listeners

import org.eclipse.jface.util.PropertyChangeEvent
import org.junit.Test

import com.blackducksoftware.integration.eclipseplugin.common.InspectionJob
import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService
import com.blackducksoftware.integration.eclipseplugin.common.services.PreferencesService
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService
import com.blackducksoftware.integration.eclipseplugin.startup.Activator

class DefaultPreferenceChangeListenerTest {
    def activeProject = "activeProject"

    def activeProjectInspection = new InspectionJob(null, activeProject, null)

    def inactiveProject = "inactiveProject"

    def inactiveProjectInspection = new InspectionJob(null, inactiveProject, null)

    def plugin = [getWorkspaceInformationService: { workspaceInformationService },
        getInspectionQueueService: { inspectionQueueService }] as Activator

    def preferencesService = new PreferencesService(null){
        @Override
        def boolean isActivated(String projectName) {
            projectName.equals(activeProject)
        }
    }

    def workspaceInformationService = new WorkspaceInformationService(null){
        @Override
        def List<String> getSupportedJavaProjectNames(){
            [
                activeProject,
                inactiveProject
            ]
        }
    }

    def inspectionQueueService = new InspectionQueueService(null, null){
        @Override
        def boolean enqueueInspection(String projectName){
            if(projectName.equals(activeProject)){
                inspectionQueue.add(activeProjectInspection)
            }else{
                inspectionQueue.add(inactiveProjectInspection)
            }
        }
    }


    @Test
    def testActivatedProject() {
        def e = new PropertyChangeEvent(new Object(), activeProject, null, null)
        def listener = new DefaultPreferenceChangeListener(plugin, preferencesService);
        listener.propertyChange(e);
        assert inspectionQueueService.inspectionQueue.contains(activeProjectInspection)
    }

    @Test
    def testDeactivatedProject() {
        def e = new PropertyChangeEvent(new Object(), inactiveProject, null, null)
        def listener = new DefaultPreferenceChangeListener(plugin, preferencesService);
        listener.propertyChange(e);
        assert !inspectionQueueService.inspectionQueue.contains(inactiveProjectInspection)
    }
}
