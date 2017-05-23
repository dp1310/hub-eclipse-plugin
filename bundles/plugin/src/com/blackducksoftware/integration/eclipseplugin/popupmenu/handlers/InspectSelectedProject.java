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
import com.blackducksoftware.integration.hub.buildtool.FilePathMavenExternalIdExtractor;

public class InspectSelectedProject extends AbstractHandler {
	@Override
	public Object execute(final ExecutionEvent event) throws ExecutionException {
		final Activator plugin = Activator.getPlugin();
		final DependencyInformationService depService = new DependencyInformationService(plugin);
		final FilePathMavenExternalIdExtractor extractor = new FilePathMavenExternalIdExtractor();
		final ProjectInformationService projService = new ProjectInformationService(depService, extractor);
		final WorkspaceInformationService workspaceService = new WorkspaceInformationService(projService);
		final List<String> selectedProjects = workspaceService.getAllSelectedProjects();
		final PreferencesService preferencesService = plugin.getDefaultPreferencesService();
		final InspectionQueueService inspectionQueueService = plugin.getInspectionQueueService();
		for (final String selectedProject : selectedProjects) {
			if (!preferencesService.isActivated(selectedProject)) {
				preferencesService.setProjectActivation(selectedProject, true);
			}
			inspectionQueueService.enqueueInspection(selectedProject);
		}
		return null;
	}

}
