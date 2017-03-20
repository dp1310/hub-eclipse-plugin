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
import org.eclipse.swtbot.eclipse.finder.waits.Conditions;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
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

public class PreferenceBotUtils extends AbstractPreferenceBotUtils {
    public static final String PREFERENCES_WINDOW_TITLE = "Preferences";

    public static final String PREFERENCES_FILTERED_WINDOW_TITLE = PREFERENCES_WINDOW_TITLE + " (Filtered)";

    public static final String OK_BUTTON_TEXT = "OK";

    public static final String APPLY_BUTTON_TEXT = "Apply";

    public static final String DEFAULTS_BUTTON_TEXT = "Restore Defaults";

    public static final String CANCEL_BUTTON_TEXT = "Cancel";

    private final HubPreferencesBotUtils hubPreferencesBotUtils;

    public PreferenceBotUtils(final BlackDuckBotUtils botUtils) {
        super(botUtils);
        this.hubPreferencesBotUtils = new HubPreferencesBotUtils(botUtils);
    }

    public HubPreferencesBotUtils hubSettings() {
        return hubPreferencesBotUtils;
    }

    public void openBlackDuckPreferencesFromContextMenu() {
        final SWTBotView view = botUtils.getSupportedProjectView();
        view.setFocus();
        final SWTBot viewBot = view.bot();
        final SWTBotTree tree = viewBot.tree();
        tree.setFocus();
        this.selectFromMenu(tree.contextMenu(), MenuLabels.BLACK_DUCK, MenuLabels.HUB_SETTINGS);
        bot.waitUntil(Conditions.shellIsActive(PREFERENCES_FILTERED_WINDOW_TITLE));
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
                        if (item.getText().startsWith(PREFERENCES_WINDOW_TITLE)) {
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
            this.setSWTBotTimeoutShort();
            bot.waitUntil(Conditions.shellIsActive(PREFERENCES_WINDOW_TITLE));
            this.setSWTBotTimeoutDefault();
        } catch (final TimeoutException e1) {
            try {
                this.setSWTBotTimeoutDefault();
                bot.menu("Window").menu(PREFERENCES_WINDOW_TITLE).click();
                bot.waitUntil(Conditions.shellIsActive(PREFERENCES_WINDOW_TITLE));
            } catch (final WidgetNotFoundException e2) {
                this.setSWTBotTimeoutDefault();
                bot.activeShell().close();
                bot.menu("Window").menu(PREFERENCES_WINDOW_TITLE).click();
                bot.waitUntil(Conditions.shellIsActive(PREFERENCES_WINDOW_TITLE));
            }
        }
        SWTBotTree tree = bot.tree();
        SWTBotTreeItem blackDuckNode = tree.getTreeItem(PreferencePageNames.BLACK_DUCK_HUB);
        blackDuckNode.click();
    }

    public void setPrefsToActivateScanByDefault() {
        getDefaultSettingsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.radio(PreferenceDefaults.ACTIVATE_BY_DEFAULT).click();
        this.pressButton(OK_BUTTON_TEXT);
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell(PREFERENCES_WINDOW_TITLE)));
        } catch (final WidgetNotFoundException e) {

        }
    }

    public void setPrefsToNotActivateScanByDefault() {
        getDefaultSettingsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.radio(PreferenceDefaults.DO_NOT_ACTIVATE_BY_DEFAULT).click();
        this.pressButton(OK_BUTTON_TEXT);
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell(PREFERENCES_WINDOW_TITLE)));
        } catch (final WidgetNotFoundException e) {

        }
    }

    public void activateProject(final String projectName) {
        getDefaultSettingsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.checkBox(projectName).select();
        this.pressButton(OK_BUTTON_TEXT);
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell(PREFERENCES_WINDOW_TITLE)));
        } catch (final WidgetNotFoundException e) {

        }
    }

    public void deactivateProject(final String projectName) {
        getDefaultSettingsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.checkBox(projectName).deselect();
        this.pressButton(OK_BUTTON_TEXT);
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell(PREFERENCES_WINDOW_TITLE)));
        } catch (final WidgetNotFoundException e) {

        }
    }

    public void restoreAllBlackDuckDefaults() {
        getDefaultSettingsPage();
        this.pressButton(DEFAULTS_BUTTON_TEXT);
        this.pressButton(OK_BUTTON_TEXT);
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell(PREFERENCES_WINDOW_TITLE)));
        } catch (final WidgetNotFoundException e) {

        }
    }

    public void getDefaultSettingsPage() {
        openBlackDuckPreferencesFromEclipseMenu();
        final SWTBot pageBot = bot.activeShell().bot();
        final SWTBotTreeItem blackDuck = pageBot.tree().expandNode(PreferencePageNames.BLACK_DUCK_HUB);
        bot.waitUntil(new TreeItemIsExpandedCondition(blackDuck));
        blackDuck.getNode(PreferencePageNames.COMPONENT_INSPECTOR_SETTINGS).click();
    }

}
