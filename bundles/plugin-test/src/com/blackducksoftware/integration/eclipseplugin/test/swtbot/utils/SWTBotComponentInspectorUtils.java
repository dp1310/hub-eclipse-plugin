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

import java.util.Arrays;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.exceptions.WidgetNotFoundException;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

import com.blackducksoftware.integration.eclipseplugin.common.constants.InspectionStatus;

public class SWTBotComponentInspectorUtils extends SWTBotCommonUtils {
    public static final String COMPONENT_INSPECTOR_NAME = "Component Inspector";

    public SWTBotComponentInspectorUtils(final SWTWorkbenchBot bot) {
        super(bot);
    }

    public SWTBot getComponentInspectorView() {
        final SWTBotView view = bot.viewByTitle(COMPONENT_INSPECTOR_NAME);
        return view.bot();
    }

    public SWTBotText getInspectionStatusCompleteOrInProgress() {
        final SWTBot viewBot = this.getComponentInspectorView();
        this.setSWTBotTimeoutShort();
        for (String statusMessage : Arrays.asList(InspectionStatus.CONNECTION_OK, InspectionStatus.PROJECT_INSPECTION_ACTIVE,
                InspectionStatus.PROJECT_INSPECTION_SCHEDULED)) {
            try {
                final SWTBotText text = viewBot.text(statusMessage);
                this.setSWTBotTimeoutDefault();
                return text;
            } catch (final WidgetNotFoundException e) {
            }
        }
        this.setSWTBotTimeoutDefault();
        throw new WidgetNotFoundException(String.format("Inspection status widget not found with value '%s', '%s', or '%s'", InspectionStatus.CONNECTION_OK,
                InspectionStatus.PROJECT_INSPECTION_ACTIVE, InspectionStatus.PROJECT_INSPECTION_SCHEDULED));
    }

}
