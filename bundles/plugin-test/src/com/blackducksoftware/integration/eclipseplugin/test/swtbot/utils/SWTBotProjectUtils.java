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

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

public class SWTBotProjectUtils {

	private final SWTWorkbenchBot bot;

	public SWTBotProjectUtils(final SWTWorkbenchBot bot) {
		this.bot = bot;
	}

	public void createJavaProject(final String projectName) {
		final SWTBotMenu fileMenu = bot.menu("File");
		final SWTBotMenu projectMenu = fileMenu.menu("New");
		final SWTBotMenu newMenu = projectMenu.menu("Project...");
		newMenu.click();
		bot.waitUntil(Conditions.shellIsActive("New Project"));
		final SWTBotTree optionTree = bot.tree();
		final SWTBotTreeItem javaNode = optionTree.expandNode("Java");
		bot.waitUntil(new TreeItemIsExpandedCondition(javaNode));
		javaNode.expandNode("Java Project").select();
		final SWTBotButton nextButton = bot.button("Next >");
		bot.waitUntil(new ButtonIsEnabledCondition(nextButton));
		nextButton.click();
		bot.textWithLabel("Project name:").setText(projectName);
		bot.button("Finish").click();
		try {
			bot.waitUntil(Conditions.shellIsActive("Open Associated Perspective?"));
			bot.button("Yes").click();
		} catch (final TimeoutException e) {
		} finally {
			try {
				bot.waitUntil(Conditions.shellCloses(bot.activeShell()));
			} catch (final TimeoutException e) {

			}
		}
	}

	public void createNonJavaProject(final String projectName) {
		final SWTBotMenu fileMenu = bot.menu("File");
		final SWTBotMenu projectMenu = fileMenu.menu("New");
		final SWTBotMenu newMenu = projectMenu.menu("Project...");
		newMenu.click();
		bot.waitUntil(Conditions.shellIsActive("New Project"));
		final SWTBotTree optionTree = bot.tree();
		final SWTBotTreeItem generalNode = optionTree.expandNode("General");
		bot.waitUntil(new TreeItemIsExpandedCondition(generalNode));
		generalNode.expandNode("Project").select();
		final SWTBotButton nextButton = bot.button("Next >");
		bot.waitUntil(new ButtonIsEnabledCondition(nextButton));
		nextButton.click();
		bot.textWithLabel("Project name:").setText(projectName);
		bot.button("Finish").click();
		try {
			bot.waitUntil(Conditions.shellIsActive("Open Associated Perspective?"));
			bot.button("Yes").click();
		} catch (final TimeoutException e) {
		} finally {
			try {
				bot.waitUntil(Conditions.shellCloses(bot.activeShell()));
			} catch (final TimeoutException e) {

			}
		}
	}

	public void createMavenProject(final String groupId, final String artifactId) {
		final SWTBotMenu fileMenu = bot.menu("File");
		final SWTBotMenu projectMenu = fileMenu.menu("New");
		final SWTBotMenu newMenu = projectMenu.menu("Project...");
		newMenu.click();
		bot.waitUntil(Conditions.shellIsActive("New Project"));
		final SWTBotTree optionTree = bot.tree();
		final SWTBotTreeItem mavenNode = optionTree.expandNode("Maven");
		bot.waitUntil(new TreeItemIsExpandedCondition(mavenNode));
		mavenNode.expandNode("Maven Project").select();
		final SWTBotButton nextButton = bot.button("Next >");
		bot.waitUntil(new ButtonIsEnabledCondition(nextButton));
		nextButton.click();
		bot.checkBox("Create a simple project (skip archetype selection)").select();
		bot.button("Next >").click();
		bot.comboBox(0).setText(groupId);
		bot.comboBox(1).setText(artifactId);
		final SWTBotButton finishButton = bot.button("Finish");
		bot.waitUntil(new ButtonIsEnabledCondition(finishButton));
		finishButton.click();
		try {
			bot.waitUntil(Conditions.shellIsActive("Open Associated Perspective?"));
			bot.button("Yes").click();
		} catch (final TimeoutException e) {
		}
		bot.sleep(2000);
	}

	public void updateMavenProject(final String projectName) {
		final SWTBotTreeItem mavenProjectNode = bot.viewByTitle("Package Explorer").bot().tree()
				.getTreeItem(projectName);
		final SWTBotMenu mavenMenu = mavenProjectNode.contextMenu().menu("Maven");
		mavenMenu.menu("Update Project...").click();
		bot.waitUntil(Conditions.shellIsActive("Update Maven Project"));
		final SWTBotButton okButton = bot.button("OK");
		bot.waitUntil(new ButtonIsEnabledCondition(okButton));
		okButton.click();
	}

	public void addMavenDependency(final String projectName, final String groupId, final String artifactId,
			final String version) {
		final SWTBotTreeItem mavenProjectNode = bot.viewByTitle("Package Explorer").bot().tree()
				.getTreeItem(projectName);
		final SWTBotMenu mavenMenu = mavenProjectNode.contextMenu().menu("Maven");
		mavenMenu.menu("Add Dependency").click();
		bot.waitUntil(Conditions.shellIsActive("Add Dependency"));
		bot.text(0).setText(groupId);
		bot.text(1).setText(artifactId);
		bot.text(2).setText(version);
		final SWTBotButton okButton = bot.button("OK");
		bot.waitUntil(new ButtonIsEnabledCondition(okButton));
		okButton.click();
	}

	public void deleteProjectFromDisk(final String projectName) {
		final SWTBotTreeItem nonJavaProjectNode = bot.viewByTitle("Package Explorer").bot().tree()
				.getTreeItem(projectName);
		nonJavaProjectNode.contextMenu().menu("Delete").click();
		bot.waitUntil(Conditions.shellIsActive("Delete Resources"));
		bot.checkBox().select();
		bot.button("OK").click();
		try {
			bot.waitUntil(Conditions.shellCloses(bot.shell("Delete Resources")));
		} catch (final WidgetNotFoundException e) {
		}
	}

	public void deleteProjectFromWorkspace(final String projectName, final SWTWorkbenchBot bot) {
		final SWTBotTreeItem nonJavaProjectNode = bot.viewByTitle("Package Explorer").bot().tree()
				.getTreeItem(projectName);
		nonJavaProjectNode.contextMenu().menu("Delete").click();
		bot.waitUntil(Conditions.shellIsActive("Delete Resources"));
		bot.button("OK").click();
		try {
			bot.waitUntil(Conditions.shellCloses(bot.shell("Delete Resources")));
		} catch (final WidgetNotFoundException e) {
		}
	}

}
