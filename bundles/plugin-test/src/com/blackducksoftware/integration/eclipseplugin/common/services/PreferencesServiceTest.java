/**
 * hub-eclipse-plugin-test
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

import static org.junit.Assert.assertEquals;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Test;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;

public class PreferencesServiceTest {

    private final String testProject = "testProject";

    @Test
    public void testSetDefaultConfig() {
        final IPreferenceStore mockPrefStore = new PreferenceStoreMock();
        final PreferencesService service = new PreferencesService(mockPrefStore);
        service.setDefaultConfig();
        assertEquals("Default behavior is not to activate Black Duck scan by default", mockPrefStore.getString(PreferenceNames.ACTIVATE_SCAN_BY_DEFAULT),
                "true");
    }

    @Test
    public void testIndividualProjectDefaultSettings() {
        final IPreferenceStore mockPrefStore = new PreferenceStoreMock();
        final PreferencesService service = new PreferencesService(mockPrefStore);
        service.setDefaultConfig();
        service.setAllProjectSpecificDefaults(testProject);
        assertEquals("Black Duck scan not activated for new Java project by default", mockPrefStore.getBoolean(testProject), true);
    }

    @Test
    public void testIndividualProjectDefaultSettingsWhenActivateByDefaultFalse() {
        final IPreferenceStore mockPrefStore = new PreferenceStoreMock();
        final PreferencesService service = new PreferencesService(mockPrefStore);
        service.setDefaultConfig();
        mockPrefStore.setValue(PreferenceNames.ACTIVATE_SCAN_BY_DEFAULT, "false");
        service.setAllProjectSpecificDefaults(testProject);
        assertEquals("Scan automatically activated for new Java project even though default behavior is not to activate scan",
                mockPrefStore.getBoolean(testProject), false);
    }
}
