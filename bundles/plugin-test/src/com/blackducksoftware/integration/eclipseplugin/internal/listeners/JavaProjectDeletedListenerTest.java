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
package com.blackducksoftware.integration.eclipseplugin.internal.listeners;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;

@RunWith(MockitoJUnitRunner.class)
public class JavaProjectDeletedListenerTest {

	@Mock
	ProjectDependencyInformation information;
	@Mock
	IResourceChangeEvent event;
	@Mock
	IResource resource;

	private final String NAME = "name";

	@Test
	public void testResourceChanged() {
		Mockito.when(event.getResource()).thenReturn(resource);
		Mockito.when(resource.getName()).thenReturn(NAME);
		final JavaProjectDeletedListener listener = new JavaProjectDeletedListener(information);
		listener.resourceChanged(event);
		Mockito.verify(information, Mockito.times(1)).removeProject(NAME);
	}

}
