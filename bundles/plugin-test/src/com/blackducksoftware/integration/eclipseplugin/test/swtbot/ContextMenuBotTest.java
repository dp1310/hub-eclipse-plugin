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

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
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
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.BlackDuckBotUtils;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.PreferenceBotUtils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ContextMenuBotTest {
    public static BlackDuckBotUtils botUtils;

    private static final String TEST_MAVEN_GROUP = "com.blackducksoftware.eclipseplugin.test";

    private static final String TEST_MAVEN_ARTIFACT = "maven-project";

    private static final String TEST_GRADLE_PROJECT_NAME = "gradle-project";

    private static final String TEST_NON_JAVA_PROJECT_NAME = "non-java-project";

    @BeforeClass
    public static void setUpWorkspaceBot() {
        botUtils = new BlackDuckBotUtils();
        botUtils.closeWelcomeView();
        botUtils.workbench().createProject().createMavenProject(TEST_MAVEN_GROUP, TEST_MAVEN_ARTIFACT);
        botUtils.workbench().createProject().createGradleProject(TEST_GRADLE_PROJECT_NAME);
        botUtils.workbench().createProject().createGeneralProject(TEST_NON_JAVA_PROJECT_NAME);
        botUtils.workbench().openProjectsView();
        botUtils.workbench().openPackageExplorerView();
        botUtils.workbench().openProjectExplorerview();
    }

    @Before
    public void resetTimeout() {
        botUtils.setSWTBotTimeoutDefault();
    }

    @Test
    public void testContextMenuLabelsForMavenProject() {
        final SWTBot viewBot = botUtils.getSupportedProjectView();
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
        final SWTBot viewBot = botUtils.getSupportedProjectView();
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
        botUtils.setSWTBotTimeoutShort();
        final SWTBot viewBot = botUtils.getSupportedProjectView();
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
        botUtils.setSWTBotTimeoutShort();
        final SWTBot viewBot = botUtils.workbench().getPackageExplorerView();
        final SWTBotTree tree = viewBot.tree();
        tree.setFocus();
        final SWTBotRootMenu rootMenu = tree.contextMenu();
        assertNotNull(rootMenu.contextMenu(MenuLabels.BLACK_DUCK));
    }

    @Test
    public void testVisibleInProjectExplorerView() {
        final SWTBot viewBot = botUtils.workbench().getProjectExplorerView();
        final SWTBotTree tree = viewBot.tree();
        tree.setFocus();
        final SWTBotRootMenu rootMenu = tree.contextMenu();
        assertNotNull(rootMenu.contextMenu(MenuLabels.BLACK_DUCK));
    }

    @Test
    public void testNotVisibleInNonProjectNonPackageView() {
        botUtils.setSWTBotTimeoutShort();
        final SWTBot viewBot = botUtils.workbench().getProjectsView();
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
        final SWTBot viewBot = botUtils.getSupportedProjectView();
        final SWTBotTree tree = viewBot.tree();
        final SWTBotTreeItem node = tree.getTreeItem(TEST_MAVEN_ARTIFACT);
        node.setFocus();
        final SWTBotRootMenu rootMenu = node.select().contextMenu();
        final SWTBotMenu blackDuckMenu = rootMenu.contextMenu(MenuLabels.BLACK_DUCK);
        final SWTBotMenu inspectProject = blackDuckMenu.contextMenu(MenuLabels.INSPECT_PROJECT);
        inspectProject.click();
        assertNotNull(botUtils.componentInspector().getComponentInspectorView());
        assertNotNull(botUtils.componentInspector().getInspectionStatusCompleteOrInProgress());
    }

    @Test
    public void testOpenComponentView() {
        final SWTBot viewBot = botUtils.getSupportedProjectView();
        final SWTBotTree tree = viewBot.tree();
        tree.setFocus();
        final SWTBotRootMenu rootMenu = tree.contextMenu();
        final SWTBotMenu blackDuckMenu = rootMenu.contextMenu(MenuLabels.BLACK_DUCK);
        final SWTBotMenu openComponentInspector = blackDuckMenu.contextMenu(MenuLabels.OPEN_COMPONENT_INSPECTOR);
        openComponentInspector.click();
        assertNotNull(botUtils.componentInspector().getComponentInspectorView());
    }

    @Test
    public void testOpenBlackDuckHubPreferences() {
        botUtils.preferences().openBlackDuckPreferencesFromContextMenu();
        assertNotNull(botUtils.bot().shell(PreferenceBotUtils.PREFERENCES_WINDOW_TITLE));
        assertTrue(botUtils.bot().shell(PreferenceBotUtils.PREFERENCES_WINDOW_TITLE).isActive());
        botUtils.bot().shell(PreferenceBotUtils.PREFERENCES_WINDOW_TITLE).close();
    }

    @AfterClass
    public static void tearDownWorkspace() {
        botUtils.workbench().deleteProjectFromDisk(TEST_MAVEN_ARTIFACT);
        botUtils.workbench().deleteProjectFromDisk(TEST_GRADLE_PROJECT_NAME);
        botUtils.workbench().deleteProjectFromDisk(TEST_NON_JAVA_PROJECT_NAME);
        botUtils.bot().resetWorkbench();
    }

}
