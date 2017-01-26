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
package com.blackducksoftware.integration.eclipseplugin.preferences;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.blackducksoftware.integration.eclipseplugin.common.services.DependencyInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.ProjectInformationService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.hub.buildtool.FilePathGavExtractor;

public class ActiveJavaProjects extends PreferencePage implements IWorkbenchPreferencePage {

    private BooleanFieldEditor[] activeProjectPreferences;

    @Override
    public void init(final IWorkbench workbench) {
        setPreferenceStore(Activator.getDefault().getPreferenceStore());
    }

    @Override
    protected Control createContents(final Composite parent) {
        final Composite activeProjectsComposite = new Composite(parent, SWT.LEFT);
        activeProjectsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        activeProjectsComposite.setLayout(new GridLayout());

        final Label activeProjectsLabel = new Label(activeProjectsComposite, SWT.HORIZONTAL);
        activeProjectsLabel.setText("Active Java Projects");

        final DependencyInformationService depService = new DependencyInformationService();
        final FilePathGavExtractor extractor = new FilePathGavExtractor();
        final ProjectInformationService projService = new ProjectInformationService(depService, extractor);
        final WorkspaceInformationService workspaceService = new WorkspaceInformationService(projService);
        final String[] names = workspaceService.getJavaProjectNames();
        activeProjectPreferences = new BooleanFieldEditor[names.length];
        for (int i = 0; i < names.length; i++) {
            final BooleanFieldEditor isActive = new BooleanFieldEditor(names[i], names[i], activeProjectsComposite);
            isActive.setPage(this);
            isActive.setPreferenceStore(getPreferenceStore());
            isActive.load();
            activeProjectPreferences[i] = isActive;
        }
        return activeProjectsComposite;
    }

    @Override
    public void performApply() {
        storeValues();
    }

    @Override
    public boolean performOk() {
        storeValues();
        if (super.performOk()) {
            return true;
        } else {
            return false;
        }
    }

    private void storeValues() {
        final IPreferenceStore prefStore = getPreferenceStore();
        for (final BooleanFieldEditor isActive : activeProjectPreferences) {
            prefStore.setValue(isActive.getPreferenceName(), isActive.getBooleanValue());
        }
    }

    @Override
    protected void performDefaults() {
        for (final BooleanFieldEditor isActive : activeProjectPreferences) {
            isActive.loadDefault();
        }
        super.performDefaults();
    }

}
