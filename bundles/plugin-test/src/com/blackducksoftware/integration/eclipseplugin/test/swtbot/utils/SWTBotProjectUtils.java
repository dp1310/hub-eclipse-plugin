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

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;

import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.conditions.TreeItemIsExpandedCondition;

public class SWTBotProjectUtils extends SWTBotCommonUtils {
    public static final String PACKAGE_EXPLORER_VIEW = "Package Explorer";

    public static final String PROJECT_EXPLORER_VIEW = "Project Explorer";

    public static final String PROJECTS_VIEW = "Projects";

    public static final String MENU_WINDOW = "Window";

    public static final String MENU_WINDOW_SHOW_VIEW = "Show View";

    public static final String MENU_WINDOW_SHOW_VIEW_OTHER = "Other...";

    public static final String SHOW_VIEW_WINDOW_TITLE = "Show View";

    public static final String VIEW_TYPE_JAVA = "Java";

    public static final String VIEW_TYPE_JAVA_BROWSING = "Java Browsing";

    public static final String VIEW_TYPE_GENERAL = "General";

    public SWTBotProjectUtils(SWTWorkbenchBot bot) {
        super(bot);
    }

    public SWTBot getPackageExplorerView() {
        final SWTBotView view = bot.viewByTitle(PACKAGE_EXPLORER_VIEW);
        return view.bot();
    }

    public SWTBot getProjectExplorerView() {
        final SWTBotView view = bot.viewByTitle(PROJECT_EXPLORER_VIEW);
        return view.bot();
    }

    public SWTBot getProjectsView() {
        final SWTBotView view = bot.viewByTitle(PROJECTS_VIEW);
        return view.bot();
    }

    public SWTBot getSupportedProjectView() {
        for (final String viewTitle : Arrays.asList(PACKAGE_EXPLORER_VIEW, PROJECT_EXPLORER_VIEW)) {
            try {
                final SWTBotView view = bot.viewByTitle(viewTitle);
                return view.bot();
            } catch (WidgetNotFoundException e) {
            }
        }
        throw new WidgetNotFoundException("Niether " + PACKAGE_EXPLORER_VIEW + " nor " + PROJECT_EXPLORER_VIEW + " was found");
    }

    public void openPackageExplorerView() {
        this.openShowViewDialog();
        final SWTBotTree optionTree = bot.tree();
        final SWTBotTreeItem javaNode = optionTree.expandNode(VIEW_TYPE_JAVA);
        bot.waitUntil(new TreeItemIsExpandedCondition(javaNode));
        javaNode.expandNode(PACKAGE_EXPLORER_VIEW).select();
        this.pressButton("OK");
    }

    public void openProjectExplorerview() {
        this.openShowViewDialog();
        final SWTBotTree optionTree = bot.tree();
        final SWTBotTreeItem generalNode = optionTree.expandNode(VIEW_TYPE_GENERAL);
        bot.waitUntil(new TreeItemIsExpandedCondition(generalNode));
        generalNode.expandNode(PROJECT_EXPLORER_VIEW).select();
        this.pressButton("OK");
    }

    public void openProjectsView() {
        this.openShowViewDialog();
        final SWTBotTree optionTree = bot.tree();
        final SWTBotTreeItem javaNode = optionTree.expandNode(VIEW_TYPE_JAVA_BROWSING);
        bot.waitUntil(new TreeItemIsExpandedCondition(javaNode));
        javaNode.expandNode(PROJECTS_VIEW).select();
        this.pressButton("OK");
    }

    private void openShowViewDialog() {
        final SWTBotMenu windowMenu = bot.menu(MENU_WINDOW);
        final SWTBotMenu showViewMenu = windowMenu.menu(MENU_WINDOW_SHOW_VIEW);
        final SWTBotMenu allViewsMenu = showViewMenu.menu(MENU_WINDOW_SHOW_VIEW_OTHER);
        allViewsMenu.click();
        bot.waitUntil(Conditions.shellIsActive(SHOW_VIEW_WINDOW_TITLE));
    }

    public void updateMavenProject(final String projectName) {
        final SWTBotTreeItem mavenProjectNode = this.getProjectNodeByName(projectName);
        final SWTBotMenu mavenMenu = mavenProjectNode.contextMenu().menu("Maven");
        mavenMenu.menu("Update Project...").click();
        bot.waitUntil(Conditions.shellIsActive("Update Maven Project"));
        this.pressButton("OK");
    }

    public void addMavenDependency(final String projectName, final String groupId, final String artifactId,
            final String version) {
        final SWTBotTreeItem mavenProjectNode = this.getProjectNodeByName(projectName);
        final SWTBotMenu mavenMenu = mavenProjectNode.contextMenu().menu("Maven");
        mavenMenu.menu("Add Dependency").click();
        bot.waitUntil(Conditions.shellIsActive("Add Dependency"));
        bot.text(0).setText(groupId);
        bot.text(1).setText(artifactId);
        bot.text(2).setText(version);
        this.pressButton("OK");
    }

    public void deleteProjectFromDisk(final String projectName) {
        final SWTBotTreeItem projectNode = this.getProjectNodeByName(projectName);
        projectNode.contextMenu().menu("Delete").click();
        bot.waitUntil(Conditions.shellIsActive("Delete Resources"));
        bot.checkBox().select();
        this.pressButton("OK");
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell("Delete Resources")));
        } catch (final WidgetNotFoundException e) {
        }
    }

    public void deleteProjectFromWorkspace(final String projectName, final SWTWorkbenchBot bot) {
        final SWTBotTreeItem projectNode = this.getProjectNodeByName(projectName);
        projectNode.contextMenu().menu("Delete").click();
        bot.waitUntil(Conditions.shellIsActive("Delete Resources"));
        this.pressButton("OK");
        try {
            bot.waitUntil(Conditions.shellCloses(bot.shell("Delete Resources")));
        } catch (final WidgetNotFoundException e) {
        }
    }

    private SWTBotTreeItem getProjectNodeByName(final String projectName) {
        final SWTBot viewBot = this.getSupportedProjectView();
        final SWTBotTree tree = viewBot.tree();
        return tree.getTreeItem(projectName);
    }

}
