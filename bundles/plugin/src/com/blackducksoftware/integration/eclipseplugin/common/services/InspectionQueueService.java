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
package com.blackducksoftware.integration.eclipseplugin.common.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.IJobManager;
import org.eclipse.core.runtime.jobs.Job;

import com.blackducksoftware.integration.eclipseplugin.common.InspectionJob;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

public class InspectionQueueService implements IJobChangeListener {

    public final ConcurrentLinkedQueue<InspectionJob> inspectionQueue;

    private final ProjectInformationService projService;

    private InspectionJob currentInspection = null;

    public InspectionQueueService(ProjectInformationService projService) {
        inspectionQueue = new ConcurrentLinkedQueue<>();
        this.projService = projService;
    }

    public boolean enqueueInspection(String projectName) {
        if (currentInspection.getName().equals(projectName)) {
            return false;
        }
        for (InspectionJob queuedInspection : inspectionQueue) {
            if (queuedInspection.getName().equals(projectName)) {
                return false;
            }
        }
        InspectionJob inspection = new InspectionJob(projectName, projService);
        inspection.addJobChangeListener(this);
        if (currentInspection == null) {
            currentInspection = inspection;
            inspection.schedule();
            return true;
        }
        return inspectionQueue.add(inspection);
    }

    public boolean enqueueInspections(String... projectNames) {
        boolean success = true;
        for (String projectName : projectNames) {
            success = enqueueInspection(projectName);
        }
        return success;
    }

    public List<String> getScheduledInspectionsNames() {
        ArrayList<String> scheduledInspectionList = new ArrayList<>();
        inspectionQueue.forEach(inspection -> scheduledInspectionList.add(inspection.getName()));
        return scheduledInspectionList;
    }

    public List<String> getRunningInspectionsNames() {
        IJobManager jobMan = Job.getJobManager();
        ArrayList<String> inspectionList = new ArrayList<>();
        Job[] inspections = jobMan.find(InspectionJob.FAMILY);
        for (Job inspection : inspections) {
            inspectionList.add(inspection.getName());
        }
        return inspectionList;
    }

    @Override
    public void aboutToRun(IJobChangeEvent event) {
        // Do nothing
    }

    @Override
    public void awake(IJobChangeEvent event) {
        // Do nothing
    }

    @Override
    public void done(IJobChangeEvent event) {
        VulnerabilityView componentView = Activator.getPlugin().getProjectInformation().getComponentView();
        if (componentView != null && componentView.getLastSelectedProjectName().equals(currentInspection.getProjectName())) {
            componentView.resetInput();
        }
        currentInspection = null;
        if (!inspectionQueue.isEmpty()) {
            currentInspection = inspectionQueue.poll();
            currentInspection.schedule();
        }
    }

    @Override
    public void running(IJobChangeEvent event) {
        // Do nothing
    }

    @Override
    public void scheduled(IJobChangeEvent event) {
        // Do nothing
    }

    @Override
    public void sleeping(IJobChangeEvent event) {
        // Do nothing
    }

}
