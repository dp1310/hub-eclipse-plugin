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

import java.io.File;

import org.apache.commons.lang3.StringEscapeUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.JavaCore;

import com.blackducksoftware.integration.eclipseplugin.common.services.PreferencesService;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;

public class NewJavaProjectListener implements IResourceChangeListener {

    private final PreferencesService service;

    private final ProjectDependencyInformation information;

    public static final String DELAYED_INSPECTION_JOB_PREFIX = "Black Duck Component Inspector Delayed Inspection of ";

    public static final String DELAYED_INSPECTION_JOB = "Black Duck Hub Delayed Inspection";

    public NewJavaProjectListener(final PreferencesService service,
            final ProjectDependencyInformation information) {
        this.service = service;
        this.information = information;
    }

    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
        if (event.getSource() != null && event.getSource().equals(ResourcesPlugin.getWorkspace()) && event.getDelta() != null) {
            final IResourceDelta[] childrenDeltas = event.getDelta().getAffectedChildren();
            if (childrenDeltas != null) {
                for (final IResourceDelta delta : childrenDeltas) {
                    if (delta.getKind() == IResourceDelta.ADDED || delta.getKind() == IResourceDelta.CHANGED) {
                        if (delta.getResource() != null) {
                            final IResource resource = delta.getResource();
                            try {
                                if (resource instanceof IProject
                                        && ((IProject) resource).hasNature(JavaCore.NATURE_ID)) {
                                    final String projectName = resource.getName();
                                    service.setAllProjectSpecificDefaults(projectName);
                                    Job inspectionJob = null;
                                    if ((delta.getFlags() | IResourceDelta.MOVED_FROM) != 0 && delta.getMovedFromPath() != null) {
                                        String[] movedFromPath = delta.getMovedFromPath().toOSString()
                                                .split(StringEscapeUtils.escapeJava(File.separator));
                                        String oldProjectName = movedFromPath[movedFromPath.length - 1];
                                        inspectionJob = information.createInspection(projectName, true);
                                        if (service.isActivated(oldProjectName)) {
                                            service.activateProject(projectName);
                                        }
                                    } else {
                                        inspectionJob = information.createInspection(projectName, true);
                                    }
                                    if (inspectionJob != null) {
                                        final Job delayedInspection = inspectionJob;
                                        Job delayer = new Job(DELAYED_INSPECTION_JOB_PREFIX + projectName) {
                                            @Override
                                            public boolean belongsTo(Object family) {
                                                return family.equals(DELAYED_INSPECTION_JOB);
                                            }

                                            @Override
                                            protected IStatus run(IProgressMonitor monitor) {
                                                IJobManager jobMan = Job.getJobManager();
                                                Job[] delayedInspections = jobMan.find(DELAYED_INSPECTION_JOB);
                                                for (Job delayedInspection : delayedInspections) {
                                                    if (delayedInspection.getName().equals(this.getName())) {
                                                        // Kill stale jobs
                                                        delayedInspection.cancel();
                                                    }
                                                }
                                                Job[] inspections = jobMan.find(ProjectDependencyInformation.INSPECTION_JOB);
                                                monitor.setTaskName("Waiting for active inspection to finish");
                                                while (inspections.length > 0) {
                                                    try {
                                                        inspections[0].join();
                                                    } catch (InterruptedException e) {
                                                        // Do nothing, a missed scan is nbd
                                                    }
                                                    inspections = jobMan.find(ProjectDependencyInformation.INSPECTION_JOB);
                                                }
                                                delayedInspection.schedule();
                                                return Status.OK_STATUS;
                                            }
                                        };
                                        delayer.schedule();
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
    }

}
