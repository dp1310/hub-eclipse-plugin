/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.eclipseplugin.popupmenu.handlers;

import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import com.blackducksoftware.integration.eclipseplugin.common.services.DependencyInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService;
import com.blackducksoftware.integration.eclipseplugin.common.services.PreferencesService;
import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.hub.buildtool.FilePathGavExtractor;

public class InspectSelectedProject extends AbstractHandler {
    @Override
    public Object execute(final ExecutionEvent event) throws ExecutionException {
        final DependencyInformationService depService = new DependencyInformationService();
        final FilePathGavExtractor extractor = new FilePathGavExtractor();
        final ProjectInformationService projService = new ProjectInformationService(depService, extractor);
        final WorkspaceInformationService workspaceService = new WorkspaceInformationService(projService);
        final List<String> selectedProjects = workspaceService.getAllSelectedProjects();
        PreferencesService preferencesService = Activator.getPlugin().getDefaultPreferencesService();
        InspectionQueueService inspectionQueueService = Activator.getPlugin().getInspectionQueueService();
        for (String selectedProject : selectedProjects) {
            if (!preferencesService.isActivated(selectedProject)) {
                preferencesService.activateProject(selectedProject);
            }
            inspectionQueueService.enqueueInspection(selectedProject);
        }
        return null;
    }

}
