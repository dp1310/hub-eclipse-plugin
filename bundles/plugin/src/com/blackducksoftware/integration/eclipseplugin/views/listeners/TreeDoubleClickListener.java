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
package com.blackducksoftware.integration.eclipseplugin.views.listeners;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.VulnerabilityWithParentGav;

public class TreeDoubleClickListener implements IDoubleClickListener {

    @Override
    public void doubleClick(DoubleClickEvent event) {
        // TreeViewer viewer = (TreeViewer) event.getViewer();
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selectedObject = selection.getFirstElement();
        if (selectedObject instanceof VulnerabilityWithParentGav) {
            VulnerabilityWithParentGav vulnWithGav = (VulnerabilityWithParentGav) selectedObject;
            System.out.println("link activated");
            String link = vulnWithGav.getVuln().getLink();
            IWebBrowser browser;

            // Authenticate first
            /*
             * Currently, the hub will "redirect" calls only if authenticated, proper redirection will come in hub 3.5
             * (maybe)
             */

            try {
                // browser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(SWT.NONE, null, null, null);
                browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
                browser.openURL(new URL(link));
            } catch (PartInitException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            return;

        }

        // TODO: Figure out if this is important
        // if (selectedObject instanceof ComplexLicenseWithParentGav) {
        // ComplexLicenseWithParentGav cLicenseWithGav = (ComplexLicenseWithParentGav) selectedObject;
        // // String link = cLicenseWithGav.getComplexLicense();
        // }
    }

}
