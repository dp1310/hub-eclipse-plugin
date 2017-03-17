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

import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PreferenceFieldLabels;
import com.blackducksoftware.integration.eclipseplugin.preferences.BlackDuckPreferences;

public class HubPreferencesBotUtils extends AbstractPreferenceBotUtils {
    public HubPreferencesBotUtils(final BlackDuckBotUtils botUtils) {
        super(botUtils);
    }

    public void enterCredentials(final String hubUsername, final String hubPassword, final String hubUrl, final String hubTimeout) {
        this.enterCredentials(hubUsername, hubPassword, hubUrl, hubTimeout, "", "", "", "");
    }

    public void enterCredentials(final String hubUsername, final String hubPassword, final String hubUrl, final String hubTimeout,
            final String proxyUsername, final String proxyPassword, final String proxyHost, final String proxyPort) {
        final SWTBotText usernameField = bot.textWithLabel(PreferenceFieldLabels.HUB_USERNAME_LABEL);
        usernameField.typeText(hubUsername);
        final SWTBotText passwordField = bot.textWithLabel(PreferenceFieldLabels.HUB_PASSWORD_LABEL);
        passwordField.typeText(hubPassword);
        final SWTBotText urlField = bot.textWithLabel(PreferenceFieldLabels.HUB_URL_LABEL);
        urlField.typeText(hubUrl);
        final SWTBotText timeoutField = bot.textWithLabel(PreferenceFieldLabels.HUB_TIMEOUT_LABEL);
        timeoutField.typeText(hubTimeout);
        final SWTBotText proxyUsernameField = bot.textWithLabel(PreferenceFieldLabels.PROXY_USERNAME_LABEL);
        proxyUsernameField.typeText(proxyUsername);
        final SWTBotText proxyPasswordField = bot.textWithLabel(PreferenceFieldLabels.PROXY_PASSWORD_LABEL);
        proxyPasswordField.typeText(proxyPassword);
        final SWTBotText proxyHostField = bot.textWithLabel(PreferenceFieldLabels.PROXY_HOST_LABEL);
        proxyHostField.typeText(proxyHost);
        final SWTBotText proxyPortField = bot.textWithLabel(PreferenceFieldLabels.PROXY_PORT_LABEL);
        proxyPortField.typeText(proxyPort);
    }

    public void resetCredentials() {
        this.enterCredentials("", "", "", "", "", "", "", "");
        this.pressOK();
    }

    public void testCurrentCredentials() {
        this.pressButton(BlackDuckPreferences.TEST_HUB_CREDENTIALS_TEXT);
    }
}
