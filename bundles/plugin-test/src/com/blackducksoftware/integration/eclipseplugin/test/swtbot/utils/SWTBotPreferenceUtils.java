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
package com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRootMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import com.blackducksoftware.integration.eclipseplugin.common.constants.MenuLabels;
import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferencePageNames;
import com.blackducksoftware.integration.eclipseplugin.preferences.PreferenceDefaults;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.conditions.TreeItemIsExpandedCondition;

public class SWTBotPreferenceUtils extends SWTBotCommonUtils {
    public static final String PREFERENCES_WINDOW_TITLE = "Preferences (Filtered)";

    public SWTBotPreferenceUtils(final SWTWorkbenchBot bot) {
        super(bot);
    }

    public void openBlackDuckPreferencesFromContextMenu() {
        final SWTBotProjectUtils projUtils = new SWTBotProjectUtils(bot);
        final SWTBot viewBot = projUtils.getSupportedProjectView();
        final SWTBotTree tree = viewBot.tree();
        tree.setFocus();
        final SWTBotRootMenu rootMenu = tree.contextMenu();
        final SWTBotMenu blackDuckMenu = rootMenu.contextMenu(MenuLabels.BLACK_DUCK);
        final SWTBotMenu openHubSettings = blackDuckMenu.contextMenu(MenuLabels.HUB_SETTINGS);
        openHubSettings.click();
        bot.waitUntil(Conditions.shellIsActive("Preferences (Filtered)"));
    }

    public void openBlackDuckPreferencesFromEclipseMenu() {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable() {
            @Override
            public void run() {
                final IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                if (window != null) {
                    final Menu appMenu = workbench.getDisplay().getSystemMenu();
                    for (final MenuItem item : appMenu.getItems()) {
                        if (item.getText().startsWith("Preferences")) {
                            final Event event = new Event();
                            event.time = (int) System.currentTimeMillis();
                            event.widget = item;
                            event.display = workbench.getDisplay();
                            item.setSelection(true);
                            item.notifyListeners(SWT.Selection, event);
                            break;
                        }
                    }
                }
            }
        });
        try {
            bot.waitUntil(Conditions.shellIsActive("Preferences"));
        } catch (final TimeoutException e1) {
            try {
                bot.menu("Window").menu("Preferences").click();
                bot.waitUntil(Conditions.shellIsActive("Preferences"));
            } catch (final WidgetNotFoundException e2) {
                bot.activeShell().close();
                bot.menu("Window").menu("Preferences").click();
                bot.waitUntil(Conditions.shellIsActive("Preferences"));
            }
        }
    }

    public void setPrefsToActivateScanByDefault() {
        getDefaultSettingsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.radio(PreferenceDefaults.ACTIVATE_BY_DEFAULT).click();
        pageBot.button("OK").click();
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell("Preferences")));
        } catch (final WidgetNotFoundException e) {

        }
    }

    public void setPrefsToNotActivateScanByDefault() {
        getDefaultSettingsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.radio(PreferenceDefaults.DO_NOT_ACTIVATE_BY_DEFAULT).click();
        pageBot.button("OK").click();
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell("Preferences")));
        } catch (final WidgetNotFoundException e) {

        }
    }

    public void activateProject(final String projectName) {
        getActiveJavaProjectsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.checkBox(projectName).select();
        pageBot.button("OK").click();
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell("Preferences")));
        } catch (final WidgetNotFoundException e) {

        }
    }

    public void deactivateProject(final String projectName) {
        getActiveJavaProjectsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.checkBox(projectName).deselect();
        pageBot.button("OK").click();
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell("Preferences")));
        } catch (final WidgetNotFoundException e) {

        }
    }

    public void restoreAllBlackDuckDefaults() {
        getDefaultSettingsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.button("Restore Defaults").click();
        pageBot.button("OK").click();
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell("Preferences")));
        } catch (final WidgetNotFoundException e) {

        }
    }

    public void getActiveJavaProjectsPage() {
        openBlackDuckPreferencesFromEclipseMenu();
        final SWTBot prefBot = bot.activeShell().bot();
        final SWTBotTreeItem blackDuck = prefBot.tree().expandNode(PreferencePageNames.BLACK_DUCK);
        bot.waitUntil(new TreeItemIsExpandedCondition(blackDuck));
        blackDuck.getNode(PreferencePageNames.ACTIVE_JAVA_PROJECTS).click();
    }

    public void getDefaultSettingsPage() {
        openBlackDuckPreferencesFromEclipseMenu();
        final SWTBot pageBot = bot.activeShell().bot();
        final SWTBotTreeItem blackDuck = pageBot.tree().expandNode(PreferencePageNames.BLACK_DUCK);
        bot.waitUntil(new TreeItemIsExpandedCondition(blackDuck));
        blackDuck.getNode(PreferencePageNames.BLACK_DUCK_DEFAULTS).click();
    }

}
