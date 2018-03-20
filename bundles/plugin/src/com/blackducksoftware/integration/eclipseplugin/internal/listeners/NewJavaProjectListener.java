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
package com.blackducksoftware.integration.eclipseplugin.internal.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;

import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService;
import com.blackducksoftware.integration.eclipseplugin.common.services.PreferencesService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;

public class NewJavaProjectListener implements IResourceChangeListener {
    private final Activator plugin;

    public NewJavaProjectListener(final Activator plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
        final IResourceDelta eventDelta = event.getDelta();
        if (eventDelta == null) return;
        final PreferencesService defaultPreferencesService = plugin.getDefaultPreferencesService();
        final WorkspaceInformationService workspaceInformationService = plugin.getWorkspaceInformationService();
        InspectionQueueService inspectionQueueService = plugin.getInspectionQueueService();
        final IResourceDelta[] childrenDeltas = eventDelta.getAffectedChildren();
        for (final IResourceDelta delta : childrenDeltas) {
            final String projectName = this.extractProjectNameIfMovedOrAdded(delta);
            if (projectName != null && workspaceInformationService.getIsSupportedProject(projectName)) {
                if (defaultPreferencesService.checkIfProjectNeedsInitialization(projectName)) {
                    // If the preferences page hasn't been set up yet, we need to do it manually
                    defaultPreferencesService.initializeProjectActivation(projectName);
                }
                if ((delta.getFlags() == IResourceDelta.MOVED_FROM) && delta.getMovedFromPath() != null) {
                    String oldProjectName = delta.getMovedFromPath().toFile().getName();
                    if (defaultPreferencesService.isActivated(oldProjectName)) {
                        defaultPreferencesService.setProjectActivation(projectName, true);
                    }
                }
                if (defaultPreferencesService.isActivated(projectName)) {
                    inspectionQueueService.enqueueInspection(projectName);
                }

            }
        }
    }

    private String extractProjectNameIfMovedOrAdded(IResourceDelta delta) {
        final IResource resource = delta.getResource();
        if (resource != null && resource instanceof IProject) {
            if (delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.CHANGED) {
                return resource.getName();
            }
        }
        return null;
    }
}
