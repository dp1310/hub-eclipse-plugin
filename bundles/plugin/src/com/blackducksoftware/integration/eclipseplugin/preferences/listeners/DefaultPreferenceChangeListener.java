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
package com.blackducksoftware.integration.eclipseplugin.preferences.listeners;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.blackducksoftware.integration.eclipseplugin.common.services.InspectionQueueService;
import com.blackducksoftware.integration.eclipseplugin.common.services.PreferencesService;

public class DefaultPreferenceChangeListener implements IPropertyChangeListener {
    private final PreferencesService defaultPreferencesService;

    private final InspectionQueueService inspectionQueueService;

    public DefaultPreferenceChangeListener(final InspectionQueueService inspectionQueueService, final PreferencesService defaultPreferencesService) {
        super();
        this.defaultPreferencesService = defaultPreferencesService;
        this.inspectionQueueService = inspectionQueueService;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        final String projectName = event.getProperty();
        if (defaultPreferencesService.isActivated(projectName)) {
            inspectionQueueService.enqueueInspection(projectName);
        }
    }
}
