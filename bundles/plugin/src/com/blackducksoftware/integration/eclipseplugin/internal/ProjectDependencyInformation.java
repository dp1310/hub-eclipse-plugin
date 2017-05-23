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
package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.eclipseplugin.common.services.HubRestConnectionService;
import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.DependencyTableViewerComparator;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.MavenExternalId;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.blackducksoftware.integration.hub.rest.RestConnection;

public class ProjectDependencyInformation {
	private final Activator plugin;

	private final ComponentCache componentCache;

	private final Map<String, List<ComponentModel>> projectInfo = new HashMap<>();

	private VulnerabilityView componentView;

	public ProjectDependencyInformation(final Activator plugin, final ComponentCache componentCache) {
		this.componentCache = componentCache;
		this.plugin = plugin;
	}

	public VulnerabilityView getComponentView() {
		return componentView;
	}

	public void setComponentView(final VulnerabilityView componentView) {
		this.componentView = componentView;
	}

	public void removeComponentView() {
		componentView = null;
	}

	public List<ComponentModel> initializeProjectComponents(final String projectName) {
		return projectInfo.put(projectName, Collections.synchronizedList(new ArrayList<ComponentModel>()));
	}

	public List<ComponentModel> addProjectComponents(final String projectName, final List<ComponentModel> models) {
		return projectInfo.put(projectName, models);
	}

	public void addComponentToProject(final String projectName, final MavenExternalId gav) {
		final List<ComponentModel> models = projectInfo.get(projectName);
		if (models != null) {
			try {
				final ComponentModel newModel = componentCache.get(gav);
				models.add(newModel);
				models.sort(new DependencyTableViewerComparator());
				projectInfo.put(projectName, models);
				if (componentView != null) {
					componentView.resetInput();
				}
			} catch (final IntegrationException e) {
				/*
				 * Thrown if exception occurs when accessing key gav from cache. If an exception is
				 * thrown, info associated with that gav is inaccessible, and so don't put any
				 * information related to said gav into hashmap associated with the project
				 */
			}
		}
	}

	public List<ComponentModel> getProjectComponents(final String projectName) {
		final List<ComponentModel> models = projectInfo.get(projectName);
		if (models == null) {
			return null;
		}
		return projectInfo.get(projectName);
	}

	public void removeProject(final String projectName) {
		projectInfo.remove(projectName);
		plugin.getDefaultPreferencesService().removeProject(projectName);
		if (componentView != null) {
			if (componentView.getLastSelectedProjectName().equals(projectName)) {
				componentView.setLastSelectedProjectName("");
			}
		}
	}

	public void removeComponentFromProject(final String projectName, final MavenExternalId gav) {
		final List<ComponentModel> models = projectInfo.get(projectName);
		if (models != null) {
			for (final Iterator<ComponentModel> iterator = models.iterator(); iterator.hasNext();) {
				final ComponentModel model = iterator.next();
				if (model.getGav().equals(gav)) {
					iterator.remove();
				}
			}
			projectInfo.put(projectName, models);
			if (componentView != null) {
				componentView.resetInput();
			}
		}
	}

	public boolean containsComponentsFromProject(final String projectName) {
		return projectInfo.containsKey(projectName);
	}

	public void renameProject(final String oldName, final String newName) {
		final List<ComponentModel> models = projectInfo.get(oldName);
		projectInfo.put(newName, models);
		projectInfo.remove(oldName);
	}

	public void updateCache(final RestConnection connection) throws HubIntegrationException {
		final HubRestConnectionService newConnectionService = Activator.getPlugin().updateConnection(connection);
		if (projectInfo.isEmpty() && newConnectionService.hasActiveHubConnection()) {
			final InspectionQueueService inspectionQueueService = plugin.getInspectionQueueService();
			final WorkspaceInformationService workspaceInformationService = plugin.getWorkspaceInformationService();
			final List<String> supportedJavaProjects = workspaceInformationService.getSupportedJavaProjectNames();
			inspectionQueueService.enqueueInspections(supportedJavaProjects);
		}
	}

}
