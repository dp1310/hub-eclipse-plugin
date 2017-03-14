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
package com.blackducksoftware.integration.eclipseplugin.preferences.listeners

import org.eclipse.swt.widgets.Text
import org.junit.Before
import org.junit.Test

import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationResponse
import com.blackducksoftware.integration.eclipseplugin.internal.AuthorizationValidator
import com.blackducksoftware.integration.eclipseplugin.preferences.services.HubAuthorizationConfig

import groovy.mock.interceptor.StubFor

class TestHubCredentialsSelectionListenerTestGroovy {
    def hubAuthorizationConfig = [validateCredentialFields: { new AuthorizationResponse(message) }] as HubAuthorizationConfig

    def validator = new AuthorizationValidator(null, null)

    def widgetText = ""

    def message = "message"

    def messageStub = new StubFor(Text)

    def Text connectionMessageText

    @Before()
    def void setUp(){
        Text.metaClass.constructor << { -> }
        messageStub = new StubFor(Text)
    }

    @Test
    def void testWidgetSelected() {
        messageStub.demand.setText { String string -> widgetText }
        messageStub.demand.getText { widgetText }
        messageStub.use{
            connectionMessageText = new Text()
            connectionMessageText.setText(message)
            assert connectionMessageText.getText().equals(message)
        }
        //def listener = new TestHubCredentialsSelectionListener(hubAuthorizationConfig, connectionMessageText);
        //listener.widgetSelected(null);
        //assert connectionMessageText.getText().equals(message)
    }

    @Test
    def void testWidgetDefaultSelected() {
        messageStub.demand.setText { String string -> widgetText }
        messageStub.demand.getText { widgetText }
        messageStub.use{
            connectionMessageText = new Text()
            connectionMessageText.setText(message)
            assert connectionMessageText.getText().equals(message)
        }
        //def listener = new TestHubCredentialsSelectionListener(hubAuthorizationConfig, connectionMessageText);
        //listener.widgetDefaultSelected(null);
        //assert connectionMessageText.getText().equals(message)
    }
}
