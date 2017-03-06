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
package com.blackducksoftware.integration.eclipseplugin.internal.listeners;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;

import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService;
import com.blackducksoftware.integration.eclipseplugin.common.services.PreferencesService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;

public class NewJavaProjectListener implements IResourceChangeListener {

    private final PreferencesService service;

    public static final String DELAYED_INSPECTION_JOB_PREFIX = "Black Duck Component Inspector Delayed Inspection of ";

    public static final String DELAYED_INSPECTION_JOB = "Black Duck Hub Delayed Inspection";

    public NewJavaProjectListener(final PreferencesService service) {
        this.service = service;
    }

    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
        if (event.getSource() != null && event.getSource().equals(ResourcesPlugin.getWorkspace()) && event.getDelta() != null) {
            final IResourceDelta[] childrenDeltas = event.getDelta().getAffectedChildren();
            if (childrenDeltas == null) return;
            for (final IResourceDelta delta : childrenDeltas) {
                final IResource resource = delta.getResource();
                if (resource != null && ((delta.getKind() & IResourceDelta.ADDED & IResourceDelta.CHANGED) != 0)) {
                    try {
                        if (resource instanceof IProject
                                && ((IProject) resource).hasNature(JavaCore.NATURE_ID)) {
                            final String projectName = resource.getName();
                            service.setAllProjectSpecificDefaults(projectName);
                            InspectionQueueService inspectionQueueService = Activator.getPlugin().getInspectionQueueService();
                            if ((delta.getFlags() & IResourceDelta.MOVED_FROM) != 0 && delta.getMovedFromPath() != null) {
                                String oldProjectName = delta.getMovedFromPath().toFile().getName();
                                inspectionQueueService.enqueueInspection(projectName);
                                if (service.isActivated(oldProjectName)) {
                                    service.activateProject(projectName);
                                }
                            } else {
                                inspectionQueueService.enqueueInspection(projectName);
                            }
                        }
                    } catch (final CoreException e) {
                        /*
                         * If error is thrown when calling hasNature(), then assume it isn't a Java
                         * project and therefore don't do anything
                         */

                    }
                }
            }

        }
    }

}
