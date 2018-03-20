/**
 * hub-eclipse-plugin-test
 *
 * Copyright (C) 2018 Black Duck Software, Inc.
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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotCLabel;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTable;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTableItem;

import com.blackducksoftware.integration.eclipseplugin.common.constants.InspectionStatus;

public class ComponentInspectorBotUtils extends AbstractBotUtils {
	public ComponentInspectorBotUtils(final BlackDuckBotUtils parent) {
		super(parent);
	}

	public static final String COMPONENT_INSPECTOR_NAME = "Component Inspector";

	public SWTBot getComponentInspectorView() {
		final SWTBotView view = bot.viewByTitle(COMPONENT_INSPECTOR_NAME);
		return view.bot();
	}

	public SWTBotCLabel getInspectionStatus(final String status) {
		final SWTBot viewBot = this.getComponentInspectorView();
		this.setSWTBotTimeoutShort();
		final SWTBotCLabel clabel = viewBot.clabel(status);
		this.setSWTBotTimeoutDefault();
		return clabel;
	}

	public SWTBotCLabel getInspectionStatusIfCompleteOrInProgress() {
		final SWTBot viewBot = this.getComponentInspectorView();
		this.setSWTBotTimeoutShort();
		for (final String statusMessage : Arrays.asList(InspectionStatus.PROJECT_INSPECTION_ACTIVE,
				InspectionStatus.PROJECT_INSPECTION_SCHEDULED, InspectionStatus.CONNECTION_OK_NO_COMPONENTS,
				InspectionStatus.CONNECTION_OK)) {
			try {
				final SWTBotCLabel clabel = viewBot.clabel(statusMessage);
				this.setSWTBotTimeoutDefault();
				return clabel;
			} catch (final WidgetNotFoundException e) {
			}
		}
		this.setSWTBotTimeoutDefault();
		throw new WidgetNotFoundException(
				String.format("Inspection status widget not found with value '%s', '%s', '%s', or '%s'", InspectionStatus.CONNECTION_OK,
						InspectionStatus.PROJECT_INSPECTION_ACTIVE, InspectionStatus.PROJECT_INSPECTION_SCHEDULED,
						InspectionStatus.CONNECTION_OK_NO_COMPONENTS));
	}

	public SWTBotTable getInspectionResultsTable() {
		final SWTBot viewBot = this.getComponentInspectorView();
		return viewBot.table();
	}

	public String[][] getInspectionResults() {
		final SWTBotTable inspectorTable = this.getInspectionResultsTable();
		final String[][] inspectionResults = new String[inspectorTable.rowCount()][inspectorTable.columnCount()];
		for (int i = 0; i < inspectorTable.rowCount(); i++) {
			for (int j = 0; j < inspectorTable.columnCount(); j++) {
				inspectionResults[i][j] = inspectorTable.cell(i, j);
			}
		}
		return inspectionResults;
	}

	public void openComponent(final String componentText) {
		final SWTBotTable table = this.getInspectionResultsTable();
		final SWTBotTableItem tableItem = table.getTableItem(componentText);
		tableItem.doubleClick();
	}

}
