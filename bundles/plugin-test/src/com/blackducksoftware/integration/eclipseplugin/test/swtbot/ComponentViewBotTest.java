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

import java.io.IOException;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.junit.SWTBotJunit4ClassRunner;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.blackducksoftware.integration.eclipseplugin.common.constants.InspectionStatus;
import com.blackducksoftware.integration.eclipseplugin.common.constants.PluginIDs;
import com.blackducksoftware.integration.eclipseplugin.common.constants.ViewNames;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.BlackDuckBotUtils;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.HubPreferencesBotUtils;
import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.TestConstants;

@RunWith(SWTBotJunit4ClassRunner.class)
public class ComponentViewBotTest {
	private static final String COMMONS_FILEUPLOAD = "commons-fileupload";
	
    private final String[] testMavenComponents = { "commons-fileupload  1.0 ", "just-a-maven-project  0.0.1-SNAPSHOT ", "junit  3.8.1 " };

    private final String filterBoxMessage = "type filter text";

    private static BlackDuckBotUtils botUtils;

    @BeforeClass
    public static void setUpWorkspaceBot() throws IOException {
        botUtils = new BlackDuckBotUtils();
        botUtils.closeWelcomeView();
        botUtils.preferences().openBlackDuckPreferencesFromEclipseMenu();
        botUtils.preferences().hubSettings().enterValidCredentials();
        botUtils.preferences().pressApply();
        botUtils.preferences().inspectorSettings().openComponentInspectorPreferences();
        botUtils.preferences().inspectorSettings().setAnalyzeByDefaultTrue();
        botUtils.preferences().pressOK();
        botUtils.workbench().createProject().createMavenProject(TestConstants.TEST_MAVEN_GROUP, TestConstants.TEST_MAVEN_ARTIFACT);
        botUtils.workbench().closeProject(TestConstants.TEST_MAVEN_ARTIFACT);
        botUtils.addJarToProject(TestConstants.TEST_MAVEN_ARTIFACT_JAR, TestConstants.TEST_MAVEN_ARTIFACT);
        botUtils.workbench().createProject().createMavenProject(TestConstants.TEST_MAVEN_GROUP, TestConstants.TEST_MAVEN_EMPTY_ARTIFACT);
        botUtils.workbench().createProject().createMavenProject(TestConstants.TEST_MAVEN_GROUP, TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        botUtils.workbench().copyPomToProject(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT_POM_PATH, TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        botUtils.workbench().createProject().createGeneralProject(TestConstants.TEST_NON_JAVA_PROJECT_NAME);
    }

    @Test
    public void testThatVulnerabilityViewOpensFromWindowMenu() {
        botUtils.workbench().openComponentInspectorView();
        final SWTWorkbenchBot bot = botUtils.bot();
        assertNotNull(bot.viewByTitle(ViewNames.COMPONENT_INSPECTOR));
        assertNotNull(bot.viewById(PluginIDs.COMPONENT_INSPECTOR));
        bot.viewByTitle(ViewNames.COMPONENT_INSPECTOR).close();
    }

    @Test
    public void testInspectionResults() {
        botUtils.workbench().openComponentInspectorView();
        final SWTBotTreeItem projectNode = botUtils.workbench().getProject(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        projectNode.select();
        SWTBotTable table = botUtils.componentInspector().getInspectionResultsTable();
        for (final String componentName : testMavenComponents) {
            assertTrue(table.containsItem(componentName));
        }
    }

    @Test
    public void testConnectionOK() {
        botUtils.preferences().openBlackDuckPreferencesFromEclipseMenu();
        botUtils.preferences().hubSettings().enterValidCredentials();
        botUtils.preferences().pressOK();
        final SWTBotTreeItem projectNode = botUtils.workbench().getProject(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        projectNode.select();
        botUtils.workbench().openComponentInspectorView();
        botUtils.componentInspector().getInspectionResultsTable().setFocus();
        assertNotNull(botUtils.componentInspector().getInspectionStatus(InspectionStatus.CONNECTION_OK));
    }

    @Test
    public void testConnectionOKNoComponents() {
        botUtils.preferences().openBlackDuckPreferencesFromEclipseMenu();
        botUtils.preferences().hubSettings().enterValidCredentials();
        botUtils.preferences().pressOK();
        final SWTBotTreeItem projectNode = botUtils.workbench().getProject(TestConstants.TEST_MAVEN_EMPTY_ARTIFACT);
        projectNode.select();
        botUtils.workbench().openComponentInspectorView();
        botUtils.componentInspector().getInspectionResultsTable().setFocus();
        assertNotNull(botUtils.componentInspector().getInspectionStatus(InspectionStatus.CONNECTION_OK_NO_COMPONENTS));
    }

    @Test
    public void testConnectionDisconnected() {
        botUtils.preferences().openBlackDuckPreferencesFromEclipseMenu();
        botUtils.preferences().hubSettings().enterInvalidCredentials();
        botUtils.preferences().pressOK();
        final SWTBotTreeItem projectNode = botUtils.workbench().getProject(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        projectNode.select();
        botUtils.workbench().openComponentInspectorView();
        botUtils.componentInspector().getInspectionResultsTable().setFocus();
        assertNotNull(botUtils.componentInspector().getInspectionStatus(InspectionStatus.CONNECTION_DISCONNECTED));
    }

    @Test
    public void testProjectNotSupported() {
        final SWTBotTreeItem projectNode = botUtils.workbench().getProject(TestConstants.TEST_NON_JAVA_PROJECT_NAME);
        projectNode.select();
        botUtils.workbench().openComponentInspectorView();
        botUtils.componentInspector().getInspectionResultsTable().setFocus();
        assertNotNull(botUtils.componentInspector().getInspectionStatus(InspectionStatus.PROJECT_NOT_SUPPORTED));
    }

    @Test
    public void testInspectionDeactivated() {
        botUtils.preferences().openBlackDuckPreferencesFromEclipseMenu();
        botUtils.preferences().inspectorSettings().openComponentInspectorPreferences();
        botUtils.preferences().inspectorSettings().deactivateProject(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        botUtils.preferences().inspectorSettings().pressOK();
        try{
        	final SWTBotTreeItem projectNode = botUtils.workbench().getProject(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        	projectNode.select();
        	botUtils.workbench().openComponentInspectorView();
        	botUtils.componentInspector().getInspectionResultsTable().setFocus();
        	assertNotNull(botUtils.componentInspector().getInspectionStatus(InspectionStatus.PROJECT_INSPECTION_INACTIVE));
        }finally{
            botUtils.preferences().openBlackDuckPreferencesFromEclipseMenu();
            botUtils.preferences().inspectorSettings().openComponentInspectorPreferences();
            botUtils.preferences().inspectorSettings().activateProject(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
            botUtils.preferences().inspectorSettings().pressOK();
        }
    }

    @Test
    public void testFiltering() {
        botUtils.workbench().openComponentInspectorView();
        final SWTBotTreeItem projectNode = botUtils.workbench().getProject(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        projectNode.select();
        botUtils.componentInspector().getInspectionResultsTable().setFocus();
        final SWTBot viewBot = botUtils.componentInspector().getComponentInspectorView();
        final SWTBotText filterbox = viewBot.textWithMessage(filterBoxMessage);
        filterbox.typeText(COMMONS_FILEUPLOAD);
        try {
            final SWTBotTable table = botUtils.componentInspector().getInspectionResultsTable();
            for (final String componentName : testMavenComponents) {
                if (componentName.equals(testMavenComponents[0])) {
                    assertTrue(table.containsItem(componentName));
                } else {
                    assertFalse(table.containsItem(componentName));
                }
            }
        } finally {
            filterbox.setText("");
        }
    }

    @Test
    public void testSwitchHubInstanceFromValidInstanceToOtherValidInstance() {
        botUtils.preferences().openBlackDuckPreferencesFromEclipseMenu();
        botUtils.preferences().hubSettings().enterValidCredentials();
        botUtils.preferences().pressOK();
        final SWTBotTreeItem projectNode = botUtils.workbench().getProject(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        projectNode.select();
        assertNotNull(botUtils.componentInspector().getInspectionStatus(InspectionStatus.CONNECTION_OK));
        botUtils.preferences().openBlackDuckPreferencesFromEclipseMenu();
        botUtils.preferences().hubSettings().enterCredentials(HubPreferencesBotUtils.VALID_HUB_USERNAME, HubPreferencesBotUtils.VALID_HUB_PASSWORD,
                HubPreferencesBotUtils.ALT_VALID_HUB_URL, HubPreferencesBotUtils.VALID_HUB_TIMEOUT);
        botUtils.preferences().pressOK();
        assertNotNull(botUtils.componentInspector().getInspectionStatusIfCompleteOrInProgress());
    }

    @Test
    public void testSwitchHubInstanceFromValidInstanceToOtherInvalidInstance() {
        botUtils.preferences().openBlackDuckPreferencesFromEclipseMenu();
        botUtils.preferences().hubSettings().enterValidCredentials();
        botUtils.preferences().pressOK();
        final SWTBotTreeItem projectNode = botUtils.workbench().getProject(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        projectNode.select();
        assertNotNull(botUtils.componentInspector().getInspectionStatus(InspectionStatus.CONNECTION_OK));
        botUtils.preferences().openBlackDuckPreferencesFromEclipseMenu();
        botUtils.preferences().hubSettings().enterInvalidCredentials();
        botUtils.preferences().pressOK();
        assertNotNull(botUtils.componentInspector().getInspectionStatus(InspectionStatus.CONNECTION_DISCONNECTED));
    }

    @AfterClass
    public static void tearDownWorkspaceBot() {
        botUtils.workbench().deleteProjectFromDisk(TestConstants.TEST_MAVEN_ARTIFACT);
        botUtils.workbench().deleteProjectFromDisk(TestConstants.TEST_MAVEN_COMPONENTS_ARTIFACT);
        botUtils.workbench().deleteProjectFromDisk(TestConstants.TEST_MAVEN_EMPTY_ARTIFACT);
        botUtils.workbench().deleteProjectFromDisk(TestConstants.TEST_NON_JAVA_PROJECT_NAME);
        botUtils.bot().resetWorkbench();
    }

}
