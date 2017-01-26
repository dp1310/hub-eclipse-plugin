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
package com.blackducksoftware.integration.eclipseplugin.popupmenu;

import static org.junit.Assert.assertNotNull;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.blackducksoftware.integration.eclipseplugin.common.constants.MenuLabels;
import com.blackducksoftware.integration.eclipseplugin.test.utils.swtbot.SWTBotProjectUtils;

@RunWith(SWTBotJunit4ClassRunner.class)
public class PopupMenuTest {

	public static SWTWorkbenchBot bot;
	public static SWTBotProjectUtils botProjectUtils;

	private static final String TEST_JAVA_PROJECT_NAME = "popup-menu-test-java-project";
	private static final String TEST_NON_JAVA_PROJECT_NAME = "popup-menu-test-non-java-project";

	@BeforeClass
	public static void setUpWorkspaceBot() {
		bot = new SWTWorkbenchBot();
		botProjectUtils = new SWTBotProjectUtils(bot);
		try {
			bot.viewByTitle("Welcome").close();
		} catch (final RuntimeException e) {
		}
		botProjectUtils.createJavaProject(TEST_JAVA_PROJECT_NAME);
		botProjectUtils.createNonJavaProject(TEST_NON_JAVA_PROJECT_NAME);
	}

	@Test
	public void testContextMenuLabelsForJavaProject() {
		final SWTBotTreeItem node = bot.viewByTitle("Package Explorer").bot().tree()
				.getTreeItem(TEST_JAVA_PROJECT_NAME);
		node.setFocus();
		final SWTBotMenu blackDuckMenu = node.select().contextMenu(MenuLabels.BLACK_DUCK);
		assertNotNull(blackDuckMenu.contextMenu(MenuLabels.PROJECT_PREFERENCES));
		assertNotNull(blackDuckMenu.contextMenu(MenuLabels.BLACK_DUCK_SETTINGS));
		assertNotNull(blackDuckMenu.contextMenu(MenuLabels.VULNERABILITY_VIEW));
	}

	@Test
	public void testContextMenuLabelsForNonJavaProject() {
		final SWTBotTreeItem node = bot.viewByTitle("Package Explorer").bot().tree()
				.getTreeItem(TEST_NON_JAVA_PROJECT_NAME);
		node.setFocus();
		final SWTBotMenu blackDuckMenu = node.select().contextMenu(MenuLabels.BLACK_DUCK);
		assertNotNull(blackDuckMenu.contextMenu(MenuLabels.BLACK_DUCK_SETTINGS));
	}

	@AfterClass
	public static void tearDownWorkspace() {
		botProjectUtils.deleteProjectFromDisk(TEST_JAVA_PROJECT_NAME);
		botProjectUtils.deleteProjectFromDisk(TEST_NON_JAVA_PROJECT_NAME);
		bot.resetWorkbench();
	}

}
