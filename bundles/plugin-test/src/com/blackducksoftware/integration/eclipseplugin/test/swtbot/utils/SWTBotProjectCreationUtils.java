/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.waits.Conditions;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTree;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.swtbot.swt.finder.widgets.TimeoutException;

import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.conditions.TreeItemIsExpandedCondition;

public class SWTBotProjectCreationUtils extends SWTBotCommonUtils {
    public static final String MENU_FILE = "File";

    public static final String MENU_FILE_NEW = "New";

    public static final String MENU_FILE_NEW_PROJECT = "Project...";

    public static final String NEW_PROJECT_WINDOW_TITLE = "New Project";

    public static final String FINISH_BUTTON = "Finish";

    public static final String NEXT_BUTTON = "Next >";

    public static final String PROJECT_NAME_FIELD = "Project name:";

    public static final String PROJECT_NAME_FIELD_GRADLE = "Project name";

    public static final String OPEN_ASSOCIATED_PERSPECTIVE_WINDOW_TITLE = "Open Associated Perspective?";

    public static final String OPEN_ASSOCIATED_PERSPECTIVE_NO_BUTTON = "No";

    public static final String PROJECT_TYPE_JAVA = "Java";

    public static final String PROJECT_TYPE_GRADLE = "Gradle";

    public static final String PROJECT_TYPE_MAVEN = "Maven";

    public static final String PROJECT_TYPE_GENERAL = "General";

    public static final String JAVA_PROJECT = "Java Project";

    public static final String GRADLE_PROJECT = "Gradle Project";

    public static final String MAVEN_PROJECT = "Maven Project";

    public static final String GENERAL_PROJECT = "Project";

    public SWTBotProjectCreationUtils(SWTWorkbenchBot bot) {
        super(bot);
    }

    public void createJavaProject(final String projectName) {
        this.openNewProjectWindow();
        final SWTBotTree optionTree = bot.tree();
        final SWTBotTreeItem javaNode = optionTree.expandNode(PROJECT_TYPE_JAVA);
        bot.waitUntil(new TreeItemIsExpandedCondition(javaNode));
        javaNode.expandNode(JAVA_PROJECT).select();
        this.pressButton(NEXT_BUTTON);
        bot.textWithLabel(PROJECT_NAME_FIELD).setText(projectName);
        this.finishAndOpenAssociatedPerspectiveIfNotOpen();
    }

    public void createGradleProject(final String projectName) {
        this.openNewProjectWindow();
        final SWTBotTree optionTree = bot.tree();
        final SWTBotTreeItem javaNode = optionTree.expandNode(PROJECT_TYPE_GRADLE);
        bot.waitUntil(new TreeItemIsExpandedCondition(javaNode));
        javaNode.expandNode(GRADLE_PROJECT).select();
        this.pressButton(NEXT_BUTTON);
        try {
            this.pressButton(NEXT_BUTTON);
        } catch (WidgetNotFoundException e) {
            // For the welcome screen
        }
        bot.textWithLabel(PROJECT_NAME_FIELD_GRADLE).setText(projectName);
        this.finishAndOpenAssociatedPerspectiveIfNotOpen();
    }

    public void createNonJavaProject(final String projectName) {
        this.openNewProjectWindow();
        final SWTBotTree optionTree = bot.tree();
        final SWTBotTreeItem generalNode = optionTree.expandNode(PROJECT_TYPE_GENERAL);
        bot.waitUntil(new TreeItemIsExpandedCondition(generalNode));
        generalNode.expandNode(GENERAL_PROJECT).select();
        this.pressButton(NEXT_BUTTON);
        bot.textWithLabel(PROJECT_NAME_FIELD).setText(projectName);
        this.finishAndOpenAssociatedPerspectiveIfNotOpen();
    }

    public void createMavenProject(final String groupId, final String artifactId) {
        this.openNewProjectWindow();
        final SWTBotTree optionTree = bot.tree();
        final SWTBotTreeItem mavenNode = optionTree.expandNode(PROJECT_TYPE_MAVEN);
        bot.waitUntil(new TreeItemIsExpandedCondition(mavenNode));
        mavenNode.expandNode(MAVEN_PROJECT).select();
        this.pressButton(NEXT_BUTTON);
        bot.checkBox("Create a simple project (skip archetype selection)").select();
        this.pressButton(NEXT_BUTTON);
        bot.comboBox(0).setText(groupId);
        bot.comboBox(1).setText(artifactId);
        this.finishAndOpenAssociatedPerspectiveIfNotOpen();
    }

    private void openNewProjectWindow() {
        final SWTBotMenu fileMenu = bot.menu(MENU_FILE);
        final SWTBotMenu projectMenu = fileMenu.menu(MENU_FILE_NEW);
        final SWTBotMenu newMenu = projectMenu.menu(MENU_FILE_NEW_PROJECT);
        newMenu.click();
        bot.waitUntil(Conditions.shellIsActive(NEW_PROJECT_WINDOW_TITLE));
    }

    private void finishAndOpenAssociatedPerspectiveIfNotOpen() {
        this.pressButton(FINISH_BUTTON);
        try {
            bot.waitUntil(Conditions.shellIsActive(OPEN_ASSOCIATED_PERSPECTIVE_WINDOW_TITLE));
            bot.button(OPEN_ASSOCIATED_PERSPECTIVE_NO_BUTTON).click();
        } catch (final TimeoutException e) {
        } finally {
            try {
                bot.waitUntil(Conditions.shellCloses(bot.activeShell()));
            } catch (final TimeoutException e) {

            }
        }
    }

}
