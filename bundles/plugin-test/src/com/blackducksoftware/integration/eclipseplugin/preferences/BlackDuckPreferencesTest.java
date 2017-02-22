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
package com.blackducksoftware.integration.eclipseplugin.preferences;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.waits.DefaultCondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferencePageNames;
import com.blackducksoftware.integration.eclipseplugin.test.utils.swtbot.SWTBotPreferenceUtils;
import com.blackducksoftware.integration.eclipseplugin.test.utils.swtbot.SWTBotProjectUtils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class BlackDuckPreferencesTest {

	private static final String TEST_JAVA_PROJECT = "testJavaProject";

	public static SWTWorkbenchBot bot;
	public static SWTBotProjectUtils botProjectUtils;
	public static SWTBotPreferenceUtils botPrefUtils;

	@BeforeClass
	public static void setUpWorkspace() {
		bot = new SWTWorkbenchBot();
		botProjectUtils = new SWTBotProjectUtils(bot);
		botPrefUtils = new SWTBotPreferenceUtils(bot);
		try {
			bot.viewByTitle("Welcome").close();
		} catch (final RuntimeException e) {
		}
		botProjectUtils.createJavaProject(TEST_JAVA_PROJECT);
	}

	@Test
	public void testOpeningFromContextMenu() {
		botPrefUtils.openBlackDuckPreferencesFromContextMenu(TEST_JAVA_PROJECT);
		assertNotNull(bot.shell("Preferences (Filtered)"));
		assertTrue(bot.shell("Preferences (Filtered)").isActive());
		bot.shell("Preferences (Filtered)").close();
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
		botPrefUtils.openBlackDuckPreferencesFromContextMenu(TEST_JAVA_PROJECT);
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

	@AfterClass
	public static void tearDownWorkspace() {
		botProjectUtils.deleteProjectFromDisk(TEST_JAVA_PROJECT);
		bot.resetWorkbench();
	}
}
