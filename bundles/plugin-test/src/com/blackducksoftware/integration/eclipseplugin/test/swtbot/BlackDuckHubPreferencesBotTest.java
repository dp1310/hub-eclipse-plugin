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
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferencePageNames;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.SWTBotPreferenceUtils;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.SWTBotProjectCreationUtils;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.SWTBotProjectUtils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class BlackDuckHubPreferencesBotTest {
    private static final String TEST_JAVA_PROJECT = "testJavaProject";

    public static SWTWorkbenchBot bot;

    public static SWTBotProjectUtils botProjectUtils;

    public static SWTBotPreferenceUtils botPrefUtils;

    public static SWTBotProjectCreationUtils botCreationUtils;

    @BeforeClass
    public static void setUpWorkspace() {
        bot = new SWTWorkbenchBot();
        botProjectUtils = new SWTBotProjectUtils(bot);
        botPrefUtils = new SWTBotPreferenceUtils(bot);
        botCreationUtils = new SWTBotProjectCreationUtils(bot);
        try {
            bot.viewByTitle("Welcome").close();
        } catch (final RuntimeException e) {
        }
        botCreationUtils.createJavaProject(TEST_JAVA_PROJECT);
    }

    @Test
    public void testOpeningFromEclipseMenu() {
        botPrefUtils.openBlackDuckPreferencesFromEclipseMenu();
        final SWTBotTreeItem blackDuck = bot.activeShell().bot().tree().getTreeItem(PreferencePageNames.BLACK_DUCK);
        assertNotNull(blackDuck);
        bot.activeShell().bot().tree().expandNode(PreferencePageNames.BLACK_DUCK);
        assertNotNull(blackDuck.getNode(PreferencePageNames.ACTIVE_JAVA_PROJECTS));
        assertNotNull(blackDuck.getNode(PreferencePageNames.BLACK_DUCK_DEFAULTS));
    }

    @Test
    public void testContentsOfActiveJavaProjectsPage() {
        botPrefUtils.openBlackDuckPreferencesFromContextMenu();
        final SWTBotTreeItem blackDuck = bot.activeShell().bot().tree().expandNode(PreferencePageNames.BLACK_DUCK);
        bot.waitUntil(new DefaultCondition() {

            @Override
            public boolean test() throws Exception {
                return (blackDuck.isExpanded());
            }

            @Override
            public String getFailureMessage() {
                return "Could not expand Black Duck preference node";
            }

        });
    }

    // @Test
    public void testSwitchHubInstanceToOtherValidInstance() {
        // TODO: Test stub
    }

    // @Test
    public void testSwitchHubInstanceToOtherInvalidInstance() {
        // TODO: Test stub
    }

    // @Test
    public void testValidHubConfiguration() {
        // TODO: Test stub
    }

    // @Test
    public void testInvalidHubURL() {
        // TODO: Test stub
    }

    // @Test
    public void testInvalidHubUsername() {
        // TODO: Test stub
    }

    // @Test
    public void testInvalidHubPassword() {
        // TODO: Test stub
    }

    // @Test
    public void testInvalidHubTimeout() {
        // TODO: Test stub
    }

    // @Test
    public void testApplyChanges() {
        // TODO: Test stub
    }

    // @Test
    public void testOK() {
        // TODO: Test stub
    }

    // @Test
    public void testCancel() {
        // TODO: Test stub
    }

    // @Test
    public void testRestoreDefaults() {
        // TODO: Test stub
    }

    @AfterClass
    public static void tearDownWorkspace() {
        botProjectUtils.deleteProjectFromDisk(TEST_JAVA_PROJECT);
        bot.resetWorkbench();
    }
}
