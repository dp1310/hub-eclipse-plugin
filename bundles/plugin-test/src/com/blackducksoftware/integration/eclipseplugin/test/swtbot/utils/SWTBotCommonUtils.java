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
import org.eclipse.swtbot.swt.finder.utils.SWTBotPreferences;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotButton;

import com.blackducksoftware.integration.eclipseplugin.test.swtbot.utils.conditions.ButtonIsEnabledCondition;

public class SWTBotCommonUtils {
    protected final SWTWorkbenchBot bot;

    public SWTBotCommonUtils(final SWTWorkbenchBot bot) {
        this.bot = bot;
    }

    protected SWTBotButton pressButton(final String buttonTitle) {
        final SWTBotButton target = bot.button(buttonTitle);
        bot.waitUntil(new ButtonIsEnabledCondition(target));
        return target.click();
    }

    protected void setSWTBotTimeoutShort() {
        SWTBotPreferences.TIMEOUT = 500;
    }

    protected void setSWTBotTimeoutDefault() {
        SWTBotPreferences.TIMEOUT = 5000;
    }

}
