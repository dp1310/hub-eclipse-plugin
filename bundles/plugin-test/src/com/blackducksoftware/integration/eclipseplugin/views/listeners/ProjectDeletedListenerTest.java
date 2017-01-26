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
package com.blackducksoftware.integration.eclipseplugin.views.listeners;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

@RunWith(MockitoJUnitRunner.class)
public class ProjectDeletedListenerTest {

    @Mock
    IResourceChangeEvent e;

    @Mock
    VulnerabilityView view;

    @Mock
    IResource resource;

    @Test
    public void testWhenProjectDeleted() {
        Mockito.when(e.getResource()).thenReturn(resource);
        final ProjectDeletedListener listener = new ProjectDeletedListener(view);
        listener.resourceChanged(e);
        Mockito.verify(view, Mockito.times(1)).setLastSelectedProjectName("");
        Mockito.verify(view, Mockito.times(1)).resetInput();
    }

}
