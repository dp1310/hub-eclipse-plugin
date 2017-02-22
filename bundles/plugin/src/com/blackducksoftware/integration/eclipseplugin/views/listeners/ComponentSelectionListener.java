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

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

public class ComponentSelectionListener implements ISelectionChangedListener {

    private final VulnerabilityView vulnerabilityView;

    public ComponentSelectionListener(VulnerabilityView vulnerabilityView) {
        this.vulnerabilityView = vulnerabilityView;
    }

    @Override
    public void selectionChanged(SelectionChangedEvent event) {
        // TODO Auto-generated method stub
        System.out.println("arg0: " + event.toString() + " : " + event.getSelection().toString());
        System.out.println(event.getSelection().getClass().toString());
        // Update secondary pane
        if (event.getSelection() instanceof StructuredSelection) {
            StructuredSelection structuredSel = ((StructuredSelection) event.getSelection());
            if (structuredSel.getFirstElement() instanceof GavWithParentProject) {
                GavWithParentProject gavWithParentProject = ((GavWithParentProject) structuredSel.getFirstElement());
                // vulnerabilityView.setDrilldownInput(gavWithParentProject);
            }
        }
    }

}
