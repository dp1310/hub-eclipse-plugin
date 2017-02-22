/**
 * hub-eclipse-plugin
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
package com.blackducksoftware.integration.eclipseplugin.views.providers.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.widgets.Hyperlink;

public class HubHyperlink extends Hyperlink {
    public HubHyperlink(Composite parent, int style) {
        super(parent, style);
        this.setUnderlined(true);
    }

    @Override
    public void paintText(GC gc, Rectangle bounds) {
        super.paintText(gc, bounds);
    }

    @Override
    protected void handleActivate(Event e) {
        super.handleActivate(e);
        System.out.println("link activated");
        IWebBrowser brower;
        try {
            brower = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(SWT.NONE, null, null, null);
            brower.openURL(new URL(this.getHref().toString()));
        } catch (PartInitException e1) {
            e1.printStackTrace();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        }
    }
}
