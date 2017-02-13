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
package com.blackducksoftware.integration.eclipseplugin.viewers.filters;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.widgets.Text;

import com.blackducksoftware.integration.eclipseplugin.views.providers.DependencyTableViewContentProvider;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.hub.buildtool.Gav;
import com.blackducksoftware.integration.hub.dataservice.license.ComplexLicenseParser;

public class ComponentFilter extends ViewerFilter {

    private Text filterBox;

    private DependencyTableViewContentProvider dependencyTableViewContentProvider;

    public ComponentFilter(Text filterBox, DependencyTableViewContentProvider dependencyTableViewContentProvider) {
        this.filterBox = filterBox;
        this.dependencyTableViewContentProvider = dependencyTableViewContentProvider;
    }

    @Override
    public boolean select(Viewer viewer, Object parentElement, Object element) {
        if (filterBox == null || filterBox.getText().length() == 0) {
            return true;
        }
        Gav gav = ((GavWithParentProject) element).getGav();
        if (gav.toString().contains(filterBox.getText())) {
            return true;
        }
        String license = new ComplexLicenseParser(
                dependencyTableViewContentProvider.getProjectInformation().getDependencyInfoMap(((GavWithParentProject) element).getParentProject())
                        .get(gav).getSimpleLicense().getComplexLicenseItem()).toString();
        if (license.contains(filterBox.getText())) {
            return true;
        }

        return false;
    }

}
