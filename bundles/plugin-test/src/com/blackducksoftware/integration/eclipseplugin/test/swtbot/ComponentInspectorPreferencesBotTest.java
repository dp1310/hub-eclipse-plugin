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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.SWTBotPreferenceUtils;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.SWTBotProjectCreationUtils;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.SWTBotProjectUtils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ComponentInspectorPreferencesBotTest {
    private static SWTWorkbenchBot bot;

    private static SWTBotPreferenceUtils botPrefUtils;

    private static SWTBotProjectUtils botProjectUtils;

    private static SWTBotProjectCreationUtils botCreationUtils;

    private static final String TEST_JAVA_PROJECT_NAME = "pref-test-java-project";

    private static final String TEST_NON_JAVA_PROJECT_NAME = "pref-test-non-java-project";

    private static final String TEST_MAVEN_PROJECT_ARTIFACT_ID = "test.artifact.id";

    private static final String TEST_MAVEN_PROJECT_GROUP_ID = "test.group.id";

    @BeforeClass
    public static void setUpWorkspace() {
        bot = new SWTWorkbenchBot();
        botPrefUtils = new SWTBotPreferenceUtils(bot);
        botProjectUtils = new SWTBotProjectUtils(bot);
        botCreationUtils = new SWTBotProjectCreationUtils(bot);
        try {
            bot.viewByTitle("Welcome").close();
        } catch (final RuntimeException e) {
        }
        botCreationUtils.createJavaProject(TEST_JAVA_PROJECT_NAME);
        botCreationUtils.createNonJavaProject(TEST_NON_JAVA_PROJECT_NAME);
        botCreationUtils.createMavenProject(TEST_MAVEN_PROJECT_GROUP_ID, TEST_MAVEN_PROJECT_ARTIFACT_ID);
    }

    @Ignore
    public void testThatAllJavaProjectsShow() {
        botPrefUtils.getActiveJavaProjectsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        assertNotNull(pageBot.checkBox(TEST_JAVA_PROJECT_NAME));
        assertNotNull(pageBot.checkBox(TEST_MAVEN_PROJECT_ARTIFACT_ID));
        try {
            pageBot.checkBox(TEST_NON_JAVA_PROJECT_NAME);
            fail();
        } catch (final WidgetNotFoundException e) {
        }
        bot.shell("Preferences").close();
    }

    // TODO: Fix broken test
    @Ignore
    public void testAnalyzeByDefault() {
        botPrefUtils.setPrefsToActivateScanByDefault();
        botPrefUtils.getActiveJavaProjectsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.button("Restore Defaults").click();
        assertTrue(pageBot.checkBox(TEST_JAVA_PROJECT_NAME).isChecked());
        assertTrue(pageBot.checkBox(TEST_MAVEN_PROJECT_ARTIFACT_ID).isChecked());
        bot.shell("Preferences").close();
    }

    // TODO: Fix broken test
    @Ignore
    public void testDoNotAnalyzeByDefault() {
        botPrefUtils.setPrefsToNotActivateScanByDefault();
        botPrefUtils.getActiveJavaProjectsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.button("Restore Defaults").click();
        assertFalse(pageBot.checkBox(TEST_JAVA_PROJECT_NAME).isChecked());
        assertFalse(pageBot.checkBox(TEST_MAVEN_PROJECT_ARTIFACT_ID).isChecked());
        bot.shell("Preferences").close();
    }

    @Test
    public void testThatBlackDuckDefaultWorks() {
        botPrefUtils.restoreAllBlackDuckDefaults();
        botPrefUtils.getActiveJavaProjectsPage();
        final SWTBot pageBot = bot.activeShell().bot();
        pageBot.button("Restore Defaults").click();
        assertTrue(pageBot.checkBox(TEST_JAVA_PROJECT_NAME).isChecked());
        assertTrue(pageBot.checkBox(TEST_MAVEN_PROJECT_ARTIFACT_ID).isChecked());
        bot.shell("Preferences").close();
    }

    // @Test
    public void testActivateProject() {
        // TODO: Test stub
    }

    // @Test
    public void testDeactivateProject() {
        // TODO: Test stub
    }

    // @Test
    public void testRestoreDefaults() {
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
    public void testApply() {
        // TODO: Test stub
    }

    @AfterClass
    public static void tearDownWorkspace() {
        botProjectUtils.deleteProjectFromDisk(TEST_JAVA_PROJECT_NAME);
        botProjectUtils.deleteProjectFromDisk(TEST_MAVEN_PROJECT_ARTIFACT_ID);
        botProjectUtils.deleteProjectFromDisk(TEST_NON_JAVA_PROJECT_NAME);
        bot.resetWorkbench();
    }

}
