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

public class BlackDuckBotUtils extends AbstractBotUtils {
    private final ComponentInspectorBotUtils componentInspectorBotUtils;

    private final PreferenceBotUtils preferenceBotUtils;

    private final WorkbenchBotUtils workbenchBotUtils;

    public static String WELCOME_VIEW_TITLE = "Welcome";

    public BlackDuckBotUtils() {
        super(null);
        this.componentInspectorBotUtils = new ComponentInspectorBotUtils(this);
        this.preferenceBotUtils = new PreferenceBotUtils(this);
        this.workbenchBotUtils = new WorkbenchBotUtils(this);
    }

    public SWTWorkbenchBot bot() {
        return bot;
    }

    public ComponentInspectorBotUtils componentInspector() {
        return componentInspectorBotUtils;
    }

    public PreferenceBotUtils preferences() {
        return preferenceBotUtils;
    }

    public WorkbenchBotUtils workbench() {
        return workbenchBotUtils;
    }

    public void closeWelcomeView() {
        this.setSWTBotTimeoutShort();
        try {
            bot.viewByTitle(WELCOME_VIEW_TITLE).close();
        } catch (final RuntimeException e) {
        }
        this.setSWTBotTimeoutDefault();
    }

    public SWTBot getSupportedProjectView() {
        for (final String viewTitle : Arrays.asList(WorkbenchBotUtils.PACKAGE_EXPLORER_VIEW, WorkbenchBotUtils.PROJECT_EXPLORER_VIEW)) {
            try {
                final SWTBotView view = bot.viewByTitle(viewTitle);
                return view.bot();
            } catch (WidgetNotFoundException e) {
            }
        }
        throw new WidgetNotFoundException(
                "Niether " + WorkbenchBotUtils.PACKAGE_EXPLORER_VIEW + " nor " + WorkbenchBotUtils.PROJECT_EXPLORER_VIEW + " was found");
    }

}
