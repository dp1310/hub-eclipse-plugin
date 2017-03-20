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
package com.blackducksoftware.integration.eclipseplugin.common;

import java.net.URL;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubMonitor;
import org.eclipse.core.runtime.jobs.Job;

import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.hub.buildtool.Gav;

public class InspectionJob extends Job {
    public static final String FAMILY = "Black Duck Component Inspection";

    public static final String JOB_INSPECT_PROJECT_PREFACE = "Black Duck Component Inspector inspecting ";

    private static final int ONE_HUNDRED_PERCENT = 100000;

    private static final int THIRTY_PERCENT = 30000;

    private static final int SEVENTY_PERCENT = 70000;

    private final ProjectInformationService projectInformationService;

    private final String projectName;

    private final Activator plugin;

    public InspectionJob(final Activator plugin, final String projectName, final ProjectInformationService projectInformationService) {
        super(JOB_INSPECT_PROJECT_PREFACE + projectName);
        this.projectName = projectName;
        this.projectInformationService = projectInformationService;
        this.plugin = plugin;
        this.setPriority(Job.BUILD);
    }

    public String getProjectName() {
        return projectName;
    }

    @Override
    public boolean belongsTo(Object family) {
        return family.equals(FAMILY);
    }

    @Override
    protected IStatus run(IProgressMonitor monitor) {
        try {
            if (!plugin.getConnectionService().hasActiveHubConnection()
                    || !plugin.getPreferenceStore().getBoolean(projectName)) {
                System.err.println("Job failed at hub connection or activation");
                return Status.OK_STATUS;
            }
            Activator.getPlugin().getProjectInformation().initializeProjectComponents(projectName);
            SubMonitor subMonitor = SubMonitor.convert(monitor, ONE_HUNDRED_PERCENT);
            subMonitor.setTaskName("Gathering dependencies");
            final List<URL> dependencyFilepaths = projectInformationService.getProjectDependencyFilePaths(projectName);
            subMonitor.split(THIRTY_PERCENT).done();
            for (URL filePath : dependencyFilepaths) {
                subMonitor.setTaskName(String.format("Inspecting %s", filePath));
                Gav gav = projectInformationService.getGavFromFilepath(filePath);
                if (gav != null) {
                    Activator.getPlugin().getProjectInformation().addComponentToProject(projectName, gav);
                    System.err.println("Gav added to project: " + gav);
                    if (dependencyFilepaths.size() < SEVENTY_PERCENT) {
                        subMonitor.split(SEVENTY_PERCENT / dependencyFilepaths.size()).done();
                    } else {
                        subMonitor.split(SEVENTY_PERCENT).done();
                    }
                }
            }
            return Status.OK_STATUS;
        } catch (Exception e) {
            return Status.CANCEL_STATUS;
        }
    }

}
