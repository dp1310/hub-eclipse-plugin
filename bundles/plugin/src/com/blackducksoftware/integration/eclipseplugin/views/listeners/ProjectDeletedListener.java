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

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;

import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

public class ProjectDeletedListener implements IResourceChangeListener {
    private final Activator plugin;

    private final VulnerabilityView componentView;

    public ProjectDeletedListener(final Activator plugin, final VulnerabilityView componentView) {
        this.plugin = plugin;
        this.componentView = componentView;
    }

    @Override
    public void resourceChanged(final IResourceChangeEvent event) {
        if (event != null && event.getResource() != null) {
            IResource resource = event.getResource();
            if (resource instanceof IProject) {
                plugin.getProjectInformation().removeProject(((IProject) resource).getName());
                if (componentView != null) {
                    if (componentView.getLastSelectedProjectName().equals(((IProject) resource).getName())) {
                        componentView.setLastSelectedProjectName("");
                        componentView.resetInput();
                    }
                }
            }
        }
    }

}
