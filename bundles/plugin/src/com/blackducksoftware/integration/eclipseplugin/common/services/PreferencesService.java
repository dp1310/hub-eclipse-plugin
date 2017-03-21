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

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;

public class PreferencesService implements IPropertyChangeListener {
    private final Set<String> initializedProjects;

    private final ScopedPreferenceStore prefStore;

    private final Activator plugin;

    public PreferencesService(final Activator plugin, final IPreferenceStore prefStore) {
        this.prefStore = (ScopedPreferenceStore) prefStore;
        this.prefStore.setSearchContexts(null);
        this.plugin = plugin;
        this.initializedProjects = this.getInitializedProjects();
    }

    private Set<String> getInitializedProjects() {
        final WorkspaceInformationService workspaceInformationService = plugin.getWorkspaceInformationService();
        final List<String> existingProjects = workspaceInformationService.getSupportedJavaProjectNames();
        if (existingProjects != null) {
            return new HashSet<>(existingProjects);
        } else {
            return new HashSet<>();
        }
    }

    public void setDefaultConfig() {
        prefStore.setDefault(PreferenceNames.ACTIVATE_SCAN_BY_DEFAULT, "true");
    }

    public void initializeProjectActivation(final String projectName) {
        if (!initializedProjects.contains(projectName)) {
            final String defaultBooleanAsString = prefStore.getString(PreferenceNames.ACTIVATE_SCAN_BY_DEFAULT);
            final boolean defaultBoolean = Boolean.parseBoolean(defaultBooleanAsString);
            prefStore.setDefault(projectName, defaultBoolean);
            prefStore.setToDefault(projectName);
            initializedProjects.add(projectName);
        }
    }

    public boolean checkIfProjectNeedsInitialization(final String projectName) {
        return !initializedProjects.contains(projectName);
    }

    public void setProjectActivation(final String projectName, final boolean value) {
        prefStore.setValue(projectName, value);
    }

    public boolean isActivated(final String projectName) {
        return prefStore.getBoolean(projectName);
    }

    public void removeProject(final String projectName) {
        initializedProjects.remove(projectName);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(PreferenceNames.ACTIVATE_SCAN_BY_DEFAULT) && event.getNewValue() != null) {
            final String stringValue = (String) event.getNewValue();
            final boolean newValue = Boolean.parseBoolean(stringValue);
            initializedProjects.forEach(initializedProject -> prefStore.setDefault(initializedProject, newValue));
        }
    }
}
