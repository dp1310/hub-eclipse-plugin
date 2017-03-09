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
import java.util.Set;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;

public class PreferencesService implements IPropertyChangeListener {
    private final Set<String> initializedProjects;

    private final IPreferenceStore prefStore;

    public PreferencesService(final IPreferenceStore prefStore) {
        this.prefStore = prefStore;
        this.initializedProjects = new HashSet<>();
    }

    public void setDefaultConfig() {
        prefStore.setDefault(PreferenceNames.ACTIVATE_SCAN_BY_DEFAULT, "true");
    }

    public void initializeProjectActivation(String projectName) {
        if (!initializedProjects.contains(projectName)) {
            String defaultBooleanAsString = prefStore.getString(PreferenceNames.ACTIVATE_SCAN_BY_DEFAULT);
            boolean defaultBoolean = Boolean.parseBoolean(defaultBooleanAsString);
            prefStore.setDefault(projectName, defaultBoolean);
            initializedProjects.add(projectName);
        }
    }

    public boolean checkIfProjectNeedsInitialization(String projectName) {
        return !(prefStore.contains(projectName));
    }

    public void setProjectActivation(String projectName, boolean value) {
        prefStore.setValue(projectName, value);
    }

    public boolean isActivated(String projectName) {
        return prefStore.getBoolean(projectName);
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getProperty().equals(PreferenceNames.ACTIVATE_SCAN_BY_DEFAULT) && event.getNewValue() != null) {
            String stringValue = (String) event.getNewValue();
            boolean newValue = Boolean.parseBoolean(stringValue);
            initializedProjects.forEach(initializedProject -> prefStore.setDefault(initializedProject, newValue));
        }
    }
}
