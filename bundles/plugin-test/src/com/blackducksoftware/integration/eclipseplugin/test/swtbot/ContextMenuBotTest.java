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
package com.blackducksoftware.integration.eclipseplugin.test.swtbot;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotRootMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.blackducksoftware.integration.eclipseplugin.common.constants.MenuLabels;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.SWTBotComponentInspectorUtils;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.SWTBotPreferenceUtils;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.SWTBotProjectCreationUtils;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.SWTBotProjectUtils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ContextMenuBotTest {
    public static SWTWorkbenchBot bot;

    public static SWTBotProjectUtils botProjectUtils;

    public static SWTBotProjectCreationUtils botCreationUtils;

    public static SWTBotComponentInspectorUtils botComponentInspectorUtils;

    public static SWTBotPreferenceUtils botPreferenceUtils;

    private static final String TEST_MAVEN_GROUP = "com.blackducksoftware.eclipseplugin.test";

    private static final String TEST_MAVEN_ARTIFACT = "maven-project";

    private static final String TEST_GRADLE_PROJECT_NAME = "gradle-project";

    private static final String TEST_NON_JAVA_PROJECT_NAME = "non-java-project";

    @BeforeClass
    public static void setUpWorkspaceBot() {
        bot = new SWTWorkbenchBot();
        botProjectUtils = new SWTBotProjectUtils(bot);
        botCreationUtils = new SWTBotProjectCreationUtils(bot);
        botComponentInspectorUtils = new SWTBotComponentInspectorUtils(bot);
        botPreferenceUtils = new SWTBotPreferenceUtils(bot);
        try {
            bot.viewByTitle("Welcome").close();
        } catch (final RuntimeException e) {
        }
        botCreationUtils.createMavenProject(TEST_MAVEN_GROUP, TEST_MAVEN_ARTIFACT);
        botCreationUtils.createGradleProject(TEST_GRADLE_PROJECT_NAME);
        botCreationUtils.createNonJavaProject(TEST_NON_JAVA_PROJECT_NAME);
        botProjectUtils.openProjectsView();
        botProjectUtils.openPackageExplorerView();
        botProjectUtils.openProjectExplorerview();
    }

    @Before
    public void resetTimeout() {
        SWTBotPreferences.TIMEOUT = 5000;
    }

    @Test
    public void testContextMenuLabelsForMavenProject() {
        final SWTBot viewBot = botProjectUtils.getSupportedProjectView();
        final SWTBotTree tree = viewBot.tree();
        final SWTBotTreeItem node = tree.getTreeItem(TEST_MAVEN_ARTIFACT);
        node.setFocus();
        final SWTBotMenu blackDuckMenu = node.select().contextMenu(MenuLabels.BLACK_DUCK);
        assertNotNull(blackDuckMenu.contextMenu(MenuLabels.INSPECT_PROJECT));
        assertNotNull(blackDuckMenu.contextMenu(MenuLabels.HUB_SETTINGS));
        assertNotNull(blackDuckMenu.contextMenu(MenuLabels.OPEN_COMPONENT_INSPECTOR));
    }

    @Test
    public void testContextMenuLabelsForGradleProject() {
        final SWTBot viewBot = botProjectUtils.getSupportedProjectView();
        final SWTBotTree tree = viewBot.tree();
        final SWTBotTreeItem node = tree.getTreeItem(TEST_GRADLE_PROJECT_NAME);
        node.setFocus();
        final SWTBotMenu blackDuckMenu = node.select().contextMenu(MenuLabels.BLACK_DUCK);
        assertNotNull(blackDuckMenu.contextMenu(MenuLabels.INSPECT_PROJECT));
        assertNotNull(blackDuckMenu.contextMenu(MenuLabels.HUB_SETTINGS));
        assertNotNull(blackDuckMenu.contextMenu(MenuLabels.OPEN_COMPONENT_INSPECTOR));
    }

    @Test
    public void testContextMenuLabelsForUnspportedProject() {
        SWTBotPreferences.TIMEOUT = 500;
        final SWTBot viewBot = botProjectUtils.getSupportedProjectView();
        final SWTBotTree tree = viewBot.tree();
        final SWTBotTreeItem node = tree.getTreeItem(TEST_NON_JAVA_PROJECT_NAME);
        node.setFocus();
        final SWTBotMenu blackDuckMenu = node.select().contextMenu(MenuLabels.BLACK_DUCK);
        try {
            assertNull(blackDuckMenu.contextMenu(MenuLabels.INSPECT_PROJECT));
        } catch (WidgetNotFoundException e) {
        }
        assertNotNull(blackDuckMenu.contextMenu(MenuLabels.HUB_SETTINGS));
        assertNotNull(blackDuckMenu.contextMenu(MenuLabels.OPEN_COMPONENT_INSPECTOR));
    }

    @Test
    public void testVisibleInPackagExplorerView() {
        SWTBotPreferences.TIMEOUT = 500;
        final SWTBot viewBot = botProjectUtils.getPackageExplorerView();
        final SWTBotTree tree = viewBot.tree();
        tree.setFocus();
        final SWTBotRootMenu rootMenu = tree.contextMenu();
        assertNotNull(rootMenu.contextMenu(MenuLabels.BLACK_DUCK));
    }

    @Test
    public void testVisibleInProjectExplorerView() {
        final SWTBot viewBot = botProjectUtils.getProjectExplorerView();
        final SWTBotTree tree = viewBot.tree();
        tree.setFocus();
        final SWTBotRootMenu rootMenu = tree.contextMenu();
        assertNotNull(rootMenu.contextMenu(MenuLabels.BLACK_DUCK));
    }

    @Test
    public void testNotVisibleInNonProjectNonPackageView() {
        SWTBotPreferences.TIMEOUT = 500;
        final SWTBot viewBot = botProjectUtils.getProjectsView();
        final SWTBotTree tree = viewBot.tree();
        tree.setFocus();
        final SWTBotRootMenu rootMenu = tree.contextMenu();
        try {
            assertNull(rootMenu.contextMenu(MenuLabels.BLACK_DUCK));
        } catch (WidgetNotFoundException e) {
        }
    }

    // TODO: Update test once we have the utils to put in valid creds
    @Test
    public void testManualInspection() {
        final SWTBot viewBot = botProjectUtils.getSupportedProjectView();
        final SWTBotTree tree = viewBot.tree();
        final SWTBotTreeItem node = tree.getTreeItem(TEST_MAVEN_ARTIFACT);
        node.setFocus();
        final SWTBotRootMenu rootMenu = node.select().contextMenu();
        final SWTBotMenu blackDuckMenu = rootMenu.contextMenu(MenuLabels.BLACK_DUCK);
        final SWTBotMenu inspectProject = blackDuckMenu.contextMenu(MenuLabels.INSPECT_PROJECT);
        inspectProject.click();
        assertNotNull(botComponentInspectorUtils.getComponentInspectorView());
        assertNotNull(botComponentInspectorUtils.getInspectionStatusCompleteOrInProgress());
    }

    @Test
    public void testOpenComponentView() {
        final SWTBot viewBot = botProjectUtils.getSupportedProjectView();
        final SWTBotTree tree = viewBot.tree();
        tree.setFocus();
        final SWTBotRootMenu rootMenu = tree.contextMenu();
        final SWTBotMenu blackDuckMenu = rootMenu.contextMenu(MenuLabels.BLACK_DUCK);
        final SWTBotMenu openComponentInspector = blackDuckMenu.contextMenu(MenuLabels.OPEN_COMPONENT_INSPECTOR);
        openComponentInspector.click();
        assertNotNull(botComponentInspectorUtils.getComponentInspectorView());
    }

    @Test
    public void testOpenBlackDuckHubPreferences() {
        botPreferenceUtils.openBlackDuckPreferencesFromContextMenu();
        assertNotNull(bot.shell(SWTBotPreferenceUtils.PREFERENCES_WINDOW_TITLE));
        assertTrue(bot.shell(SWTBotPreferenceUtils.PREFERENCES_WINDOW_TITLE).isActive());
        bot.shell(SWTBotPreferenceUtils.PREFERENCES_WINDOW_TITLE).close();
    }

    @AfterClass
    public static void tearDownWorkspace() {
        botProjectUtils.deleteProjectFromDisk(TEST_MAVEN_ARTIFACT);
        botProjectUtils.deleteProjectFromDisk(TEST_GRADLE_PROJECT_NAME);
        botProjectUtils.deleteProjectFromDisk(TEST_NON_JAVA_PROJECT_NAME);
        bot.resetWorkbench();
    }

}
