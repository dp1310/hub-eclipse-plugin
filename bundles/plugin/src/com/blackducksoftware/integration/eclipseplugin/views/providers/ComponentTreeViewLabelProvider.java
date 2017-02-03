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
package com.blackducksoftware.integration.eclipseplugin.views.providers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PathsToIconFiles;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComplexLicenseWithParentGav;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.InformationItemWithParentComplexLicense;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.InformationItemWithParentVulnerability;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.TreeViewerParent;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.VulnerabilityWithParentGav;

public class ComponentTreeViewLabelProvider extends LabelProvider implements IStyledLabelProvider {

    @Override
    public String getText(Object input) {
        // if (input instanceof GavWithParentProject) {
        // String text = "Component: " + ((GavWithParentProject) input).getGav().toString();
        // return text;
        // }
        if (input instanceof TreeViewerParent) {
            return ((TreeViewerParent) input).getDispName();
        }

        if (input instanceof VulnerabilityWithParentGav) {
            // TODO hperlink impl
            String text = "Name: " + ((VulnerabilityWithParentGav) input).getVuln().getVulnerabilityName();
            return text;
        }
        if (input instanceof InformationItemWithParentVulnerability) {
            return ((InformationItemWithParentVulnerability) input).getInformationItem();
        }
        if (input instanceof ComplexLicenseWithParentGav) {
            return ((ComplexLicenseWithParentGav) input).getComplexLicenseModel().getComplexLicenseItem().getName();
        }
        if (input instanceof InformationItemWithParentComplexLicense) {
            return ((InformationItemWithParentComplexLicense) input).getInformationItem();
        }
        if (input instanceof String) {
            return (String) input;
        }

        return "";
    }

    @Override
    public Image getImage(Object input) {
        if (input instanceof GavWithParentProject) {
            ImageDescriptor descriptor;
            if (!((GavWithParentProject) input).hasVulns()) {
                descriptor = Activator.getImageDescriptor(PathsToIconFiles.GREEN_CHECK);
            } else {
                descriptor = Activator.getImageDescriptor(PathsToIconFiles.RED_X);
            }
            return descriptor == null ? null : descriptor.createImage();
        }
        return null;
    }

    @Override
    public StyledString getStyledText(Object element) {
        String text = getText(element);
        return new StyledString(text);
    }

}
