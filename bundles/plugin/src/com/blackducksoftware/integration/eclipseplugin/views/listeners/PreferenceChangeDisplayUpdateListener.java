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
package com.blackducksoftware.integration.eclipseplugin.views.listeners;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

public class PreferenceChangeDisplayUpdateListener implements IPropertyChangeListener {

    private final VulnerabilityView componentView;

    public final static String PREFERENCE_CHANGE_JOB_PREFIX = "Preferences changed; updating Component Inspector for ";

    public static final String PREFERENCE_CHANGE_JOB = "Black Duck Component Inspector preference change";

    public PreferenceChangeDisplayUpdateListener(final VulnerabilityView componentView) {
        this.componentView = componentView;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        Job job = new Job(PREFERENCE_CHANGE_JOB_PREFIX + event.getProperty()) {
            @Override
            public boolean belongsTo(Object family) {
                return family.equals(PREFERENCE_CHANGE_JOB);
            }

            @Override
            protected IStatus run(IProgressMonitor monitor) {
                IJobManager jobMan = Job.getJobManager();
                Job[] preferenceChanges = jobMan.find(PREFERENCE_CHANGE_JOB);
                for (Job preferenceChange : preferenceChanges) {
                    if (preferenceChange.getName().equals(this.getName())) {
                        // Cancel stale changes
                        preferenceChange.cancel();
                    }
                }
                Job[] inspections = jobMan.find(ProjectDependencyInformation.INSPECTION_JOB);
                monitor.setTaskName("Waiting for active inspection to finish");
                while (inspections.length > 0) {
                    try {
                        inspections[0].join();
                    } catch (InterruptedException e) {
                        if (componentView != null) {
                            componentView.openError("Black Duck Preference Change interrupted",
                                    "Preference change interrupted before it could reach completion.", e);
                        }
                    }
                    inspections = jobMan.find(ProjectDependencyInformation.INSPECTION_JOB);
                }
                monitor.setTaskName("Updating...");
                if (componentView.getDependencyTableViewer() != null) {
                    if (Activator.getPlugin().getProjectInformation().containsProject(event.getProperty())) {
                        Activator.getPlugin().getProjectInformation().removeProject(event.getProperty());
                    } else {
                        Job inspectionJob = Activator.getPlugin().getProjectInformation().createInspection(event.getProperty(), true);
                        inspectionJob.schedule();
                    }
                    componentView.resetInput();
                }
                return Status.OK_STATUS;
            }
        };
        job.schedule();
    }
}
