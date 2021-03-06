/**
 * hub-eclipse-plugin
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.preference.RadioGroupFieldEditor;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceNames;
import com.blackducksoftware.integration.eclipseplugin.common.services.PreferencesService;
import com.blackducksoftware.integration.eclipseplugin.common.services.WorkspaceInformationService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;

public class PreferenceDefaults extends PreferencePage implements IWorkbenchPreferencePage, IPropertyChangeListener {
    public static final String ACTIVATE_BY_DEFAULT_LABEL = "When supported projects are added to workspace...";

    public static final String ACTIVATE_BY_DEFAULT = "Inspect them automatically";

    public static final String DO_NOT_ACTIVATE_BY_DEFAULT = "Do not inspect them automatically";

    public static final String ACTIVE_PROJECTS_LABEL = "Analyzed Projects";

    public static final String SELECT_ALL_BUTTON_LABEL = "Select All";

    private final String[][] DEFAULT_ACTIVATION_LABELS_AND_VALUES = new String[][] {
            new String[] { ACTIVATE_BY_DEFAULT, "true" },
            new String[] { DO_NOT_ACTIVATE_BY_DEFAULT, "false" }
    };

    private Activator plugin;

    private List<BooleanFieldEditor> activeProjectPreferences;

    private RadioGroupFieldEditor activateByDefault;

    private Composite activeComposite;

    private Composite defaultsComposite;

    @Override
    public void init(final IWorkbench workbench) {
        this.plugin = Activator.getPlugin();
        final IPreferenceStore pluginPreferenceStore = plugin.getPreferenceStore();
        this.setPreferenceStore(pluginPreferenceStore);
        pluginPreferenceStore.addPropertyChangeListener(this);
        this.noDefaultButton();
    }

    @Override
    protected Control createContents(final Composite parent) {
        final PreferencesService preferencesService = plugin.getDefaultPreferencesService();
        defaultsComposite = new Composite(parent, SWT.LEFT);
        defaultsComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        defaultsComposite.setLayout(new GridLayout());
        activateByDefault = new RadioGroupFieldEditor(PreferenceNames.ACTIVATE_SCAN_BY_DEFAULT,
                ACTIVATE_BY_DEFAULT_LABEL, 1, DEFAULT_ACTIVATION_LABELS_AND_VALUES, defaultsComposite);
        activateByDefault.setPreferenceStore(plugin.getPreferenceStore());
        activateByDefault.setPropertyChangeListener(preferencesService);
        activateByDefault.load();
        final Label spacer = new Label(defaultsComposite, SWT.HORIZONTAL);
        spacer.setVisible(false); // Not visible, but takes up a grid slot
        final Label activeProjectsLabel = new Label(defaultsComposite, SWT.HORIZONTAL);
        activeProjectsLabel.setText(ACTIVE_PROJECTS_LABEL);
        activeProjectsLabel.setFont(activateByDefault.getLabelControl(defaultsComposite).getFont());
        final GridData indentGrid = new GridData(SWT.FILL, SWT.FILL, true, true, 2, 2);
        indentGrid.horizontalIndent = ((GridData) activateByDefault.getRadioBoxControl(defaultsComposite).getLayoutData()).horizontalIndent;
        activeComposite = new Composite(defaultsComposite, SWT.LEFT);
        activeComposite.setLayoutData(indentGrid);
        activeComposite.setLayout(new GridLayout());
        reloadActiveProjects();
        return defaultsComposite;
    }

    @Override
    public void performApply() {
        this.storeValues();
        this.updateApplyButton();
    }

    @Override
    public boolean performOk() {
        this.storeValues();
        return super.performOk();
    }

    private void storeValues() {
        activateByDefault.store();
        activeProjectPreferences.forEach(isActiveProject -> isActiveProject.store());
    }

    public void reloadActiveProjects(final String... newProjects) {
        final WorkspaceInformationService workspaceInformationService = plugin.getWorkspaceInformationService();
        final List<String> names = workspaceInformationService.getSupportedJavaProjectNames();
        if (activeProjectPreferences != null) {
            for (Iterator<BooleanFieldEditor> iterator = activeProjectPreferences.iterator(); iterator.hasNext();) {
                final BooleanFieldEditor currentField = iterator.next();
                currentField.dispose();
                iterator.remove();
            }
        } else {
            activeProjectPreferences = new ArrayList<>();
        }
        for (final String name : names) {
            final BooleanFieldEditor isActive = addProject(name);
            for (final String newProjectName : newProjects) {
                if (name.equals(newProjectName)) {
                    isActive.loadDefault();
                }
            }
        }
    }

    private BooleanFieldEditor addProject(final String projectName) {
        PreferencesService defaultPreferencesService = plugin.getDefaultPreferencesService();
        defaultPreferencesService.initializeProjectActivation(projectName);
        final BooleanFieldEditor isActive = new BooleanFieldEditor(projectName, projectName, activeComposite);
        isActive.setPage(this);
        isActive.setPreferenceStore(plugin.getPreferenceStore());
        isActive.load();
        activeProjectPreferences.add(isActive);
        return isActive;
    }

    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getNewValue() == null) {
            for (final BooleanFieldEditor fieldEditor : activeProjectPreferences) {
                if (fieldEditor.getPreferenceName().equals(event.getProperty())) {
                    this.removeProject(event.getProperty());
                    return;
                }
            }
        }
        if (event.getOldValue() == null) {
            final WorkspaceInformationService workspaceInformationService = plugin.getWorkspaceInformationService();
            final List<String> supportedProjectNames = workspaceInformationService.getSupportedJavaProjectNames();
            if (supportedProjectNames.contains(event.getProperty())) {
                this.reloadActiveProjects(event.getProperty());
            }
        }

    }

    private void removeProject(final String projectName) {
        for (Iterator<BooleanFieldEditor> iterator = activeProjectPreferences.iterator(); iterator.hasNext();) {
            final BooleanFieldEditor currentField = iterator.next();
            if (currentField.getPreferenceName().equals(projectName)) {
                plugin.getPreferenceStore().setToDefault(projectName);
                currentField.dispose();
                iterator.remove();
            }
        }
    }

}
