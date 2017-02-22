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
package com.blackducksoftware.integration.eclipseplugin.preferences.listeners;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.blackducksoftware.integration.eclipseplugin.common.services.PreferencesService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;

@RunWith(MockitoJUnitRunner.class)
public class DefaultPreferenceChangeListenerTest {

	@Mock
	WorkspaceInformationService workspaceService;
	@Mock
	PreferencesService prefService;
	@Mock
	PropertyChangeEvent e;

	private final String[] PROJECT_NAMES = { "project 1", "project 2", "project 3", "project 4" };

	@Test
	public void testThatProjectDefaultsSet() {
		Mockito.when(workspaceService.getJavaProjectNames()).thenReturn(PROJECT_NAMES);
		final DefaultPreferenceChangeListener listener = new DefaultPreferenceChangeListener(prefService,
				workspaceService);
		listener.propertyChange(e);
		Mockito.verify(prefService, Mockito.times(1)).setAllProjectSpecificDefaults(PROJECT_NAMES[0]);
		Mockito.verify(prefService, Mockito.times(1)).setAllProjectSpecificDefaults(PROJECT_NAMES[1]);
		Mockito.verify(prefService, Mockito.times(1)).setAllProjectSpecificDefaults(PROJECT_NAMES[2]);
		Mockito.verify(prefService, Mockito.times(1)).setAllProjectSpecificDefaults(PROJECT_NAMES[3]);
	}
}
