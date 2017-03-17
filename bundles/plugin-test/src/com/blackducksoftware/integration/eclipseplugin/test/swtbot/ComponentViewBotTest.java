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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.blackducksoftware.integration.eclipseplugin.common.constants.MenuLabels;
import com.blackducksoftware.integration.eclipseplugin.common.constants.ViewIds;
import com.blackducksoftware.integration.eclipseplugin.common.constants.ViewNames;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.BlackDuckBotUtils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ComponentViewBotTest {
    private static BlackDuckBotUtils botUtils;

    private static final String TEST_JAVA_PROJECT_NAME = "warning-view-test-java-project";

    private static final String TEST_NON_JAVA_PROJECT_NAME = "warning-view-test-non-java-project";

    @BeforeClass
    public static void setUpWorkspaceBot() {
        botUtils = new BlackDuckBotUtils();
        botUtils.closeWelcomeView();
        botUtils.workbench().createProject().createJavaProject(TEST_JAVA_PROJECT_NAME);
        botUtils.workbench().createProject().createGeneralProject(TEST_NON_JAVA_PROJECT_NAME);
    }

    private void openVulnerabilityViewFromContextMenu(final String projectName) {
        final SWTBot viewBot = botUtils.workbench().getPackageExplorerView();
        final SWTBotTree tree = viewBot.tree();
        tree.setFocus();
        final SWTBotMenu blackDuckMenu = tree.contextMenu(MenuLabels.BLACK_DUCK);
        final SWTBotMenu openComponentInspector = blackDuckMenu.contextMenu(MenuLabels.OPEN_COMPONENT_INSPECTOR);
        openComponentInspector.click();
    }

    @Test
    public void testThatWarningViewOpensFromContextMenu() {
        final SWTWorkbenchBot bot = botUtils.bot();
        openVulnerabilityViewFromContextMenu(TEST_JAVA_PROJECT_NAME);
        assertNotNull(bot.viewByTitle(ViewNames.COMPONENT_INSPECTOR));
        assertNotNull(bot.viewById(ViewIds.VULNERABILITIES));
        bot.viewByTitle(ViewNames.COMPONENT_INSPECTOR).close();
    }

    @Test
    public void testThatVulnerabilityViewOpensFromWindowMenu() {
        final SWTWorkbenchBot bot = botUtils.bot();
        botUtils.workbench().openComponentInspectorView();
        assertNotNull(bot.viewByTitle(ViewNames.COMPONENT_INSPECTOR));
        assertNotNull(bot.viewById(ViewIds.VULNERABILITIES));
        bot.viewByTitle(ViewNames.COMPONENT_INSPECTOR).close();
    }

    // @Test
    public void testInspectionResults() {
        // TODO: Test stub
    }

    // @Test
    public void testStatusMessages() {
        // TODO: Test stub
        // This might get broken down into additional tests
    }

    // @Test
    public void testFiltering() {
        // TODO: Test stub
    }

    // @Test
    public void testAttemptToOpenValidComponentWithValidHubCredentials() {
        // TODO: Test stub
    }

    // @Test
    public void testAttemptToOpenValidComponentWithInvalidHubCredentials() {
        // TODO: Test stub
    }

    // @Test
    public void testAttemptToOpenUnknownComponent() {
        // TODO: Test stub
    }

    // @Test
    public void testSwitchHubInstanceFromValidInstanceToOtherValidInstance() {
        // TODO: Test stub
    }

    // @Test
    public void testSwitchHubInstanceFromValidInstanceToOtherInvalidInstance() {
        // TODO: Test stub
    }

    @AfterClass
    public static void tearDownWorkspaceBot() {
        botUtils.workbench().deleteProjectFromDisk(TEST_JAVA_PROJECT_NAME);
        botUtils.workbench().deleteProjectFromDisk(TEST_NON_JAVA_PROJECT_NAME);
        botUtils.bot().resetWorkbench();
    }

}
