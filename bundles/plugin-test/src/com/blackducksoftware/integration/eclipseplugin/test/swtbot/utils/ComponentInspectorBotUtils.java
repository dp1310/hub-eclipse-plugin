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

import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotLabel;

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

    public SWTBotLabel getInspectionStatusIfCompleteOrInProgress() {
        final SWTBot viewBot = this.getComponentInspectorView();
        this.setSWTBotTimeoutShort();
        for (String statusMessage : Arrays.asList(InspectionStatus.CONNECTION_OK, InspectionStatus.PROJECT_INSPECTION_ACTIVE,
                InspectionStatus.PROJECT_INSPECTION_SCHEDULED)) {
            try {
                final SWTBotLabel label = viewBot.label(statusMessage);
                this.setSWTBotTimeoutDefault();
                return label;
            } catch (final WidgetNotFoundException e) {
            }
        }
        this.setSWTBotTimeoutDefault();
        throw new WidgetNotFoundException(String.format("Inspection status widget not found with value '%s', '%s', or '%s'", InspectionStatus.CONNECTION_OK,
                InspectionStatus.PROJECT_INSPECTION_ACTIVE, InspectionStatus.PROJECT_INSPECTION_SCHEDULED));
    }

}
