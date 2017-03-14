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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.blackducksoftware.integration.eclipseplugin.common.services.HubRestConnectionService;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;
import com.blackducksoftware.integration.hub.api.component.version.ComponentVersion;
import com.blackducksoftware.integration.hub.buildtool.Gav;
import com.blackducksoftware.integration.hub.dataservice.component.ComponentDataService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;

public class TableDoubleClickListener implements IDoubleClickListener {
    public static final String JOB_GENERATE_URL = "Opening component in the Hub...";

    private final Activator plugin;

    private VulnerabilityView vulnerabilityView;

    public TableDoubleClickListener(final Activator plugin, final VulnerabilityView vulnerabilityView) {
        this.vulnerabilityView = vulnerabilityView;
        this.plugin = plugin;
    }

    @Override
    public void doubleClick(final DoubleClickEvent event) {
        IStructuredSelection selection = (IStructuredSelection) event.getSelection();

        if (selection.getFirstElement() instanceof ComponentModel) {
            ComponentModel selectedObject = (ComponentModel) selection.getFirstElement();
            if (!selectedObject.getComponentIsKnown()) {
                return;
            }
            Job job = new Job(JOB_GENERATE_URL) {
                @Override
                protected IStatus run(IProgressMonitor arg0) {
                    Gav selectedGav = selectedObject.getGav();
                    String link;
                    try {
                        final HubRestConnectionService connectionService = plugin.getConnectionService();
                        final ComponentDataService componentDataService = connectionService.getComponentDataService();
                        final ComponentVersion selectedComponentVersion = componentDataService.getExactComponentVersionFromComponent(
                                selectedGav.getNamespace(), selectedGav.getGroupId(), selectedGav.getArtifactId(), selectedGav.getVersion());
                        // Final solution, will work once the redirect is set up
                        link = plugin.getConnectionService().getMetaService().getHref(selectedComponentVersion);

                        // But for now...
                        String versionID = link.substring(link.lastIndexOf("/") + 1);
                        link = plugin.getConnectionService().getRestConnection().getBaseUrl().toString();
                        link = link + "/#versions/id:" + versionID + "/view:overview";
                        IWebBrowser browser;
                        boolean createInternalBrowser = true;
                        if (createInternalBrowser) {
                            browser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser("Hub-Eclipse-Browser");
                        } else {
                            browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
                        }
                        browser.openURL(new URL(link));
                    } catch (PartInitException | MalformedURLException | HubIntegrationException e) {
                        vulnerabilityView.openError("Could not open Component in Hub instance",
                                String.format("Problem opening %1$s %2$s in %3$s, are you connected to your hub instance?",
                                        selectedGav.getArtifactId(),
                                        selectedGav.getVersion(),
                                        plugin.getConnectionService().getRestConnection().getBaseUrl()),
                                e);
                        return Status.CANCEL_STATUS;
                    }
                    return Status.OK_STATUS;
                }

            };
            job.schedule();
        }
    }

}
