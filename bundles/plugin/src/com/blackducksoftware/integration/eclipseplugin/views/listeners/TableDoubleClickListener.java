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

import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.hub.api.component.version.ComponentVersion;
import com.blackducksoftware.integration.hub.buildtool.Gav;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;

public class TableDoubleClickListener implements IDoubleClickListener {

    @Override
    public void doubleClick(DoubleClickEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();
        Object selectedObject = selection.getFirstElement();
        if (selectedObject instanceof GavWithParentProject) {
            Gav selectedGav = ((GavWithParentProject) selectedObject).getGav();
            String link;
            try {
                ComponentVersion selectedComponentVersion = Activator.getPlugin().getConnectionService().getComponentDataService()
                        .getExactComponentVersionFromComponent(selectedGav.getNamespace(), selectedGav.getGroupId(),
                                selectedGav.getArtifactId(),
                                selectedGav.getVersion());
                // Final solution, will work once the redirect is set up
                link = Activator.getPlugin().getConnectionService().getMetaService().getHref(selectedComponentVersion);

                // But for now...
                String versionID = link.substring(link.lastIndexOf("/") + 1);
                link = Activator.getPlugin().getConnectionService().getRestConnection().getBaseUrl().toString();
                link = link + "/#versions/id:" + versionID + "/view:overview";
            } catch (HubIntegrationException e) {
                throw new RuntimeException(e);
            }
            IWebBrowser browser;

            // Authenticate first

            try {
                browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
                browser.openURL(new URL(link));
            } catch (PartInitException e1) {
                e1.printStackTrace();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            }

            return;

        }
    }

}
