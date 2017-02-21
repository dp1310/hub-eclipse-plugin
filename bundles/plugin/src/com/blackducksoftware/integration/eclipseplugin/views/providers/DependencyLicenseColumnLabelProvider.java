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

import java.util.Map;

import com.blackducksoftware.integration.eclipseplugin.internal.DependencyInfo;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.hub.buildtool.Gav;
import com.blackducksoftware.integration.hub.dataservice.license.ComplexLicenseParser;

public class DependencyLicenseColumnLabelProvider extends DependencyTreeViewLabelProvider {

    private DependencyTableViewContentProvider dependencyTableViewCp;

    public DependencyLicenseColumnLabelProvider(DependencyTableViewContentProvider dependencyTableViewCp) {
        super();
        this.dependencyTableViewCp = dependencyTableViewCp;
    }

    public DependencyLicenseColumnLabelProvider(int width, int alignment, DependencyTableViewContentProvider dependencyTableViewCp) {
        super(width, alignment);
        this.dependencyTableViewCp = dependencyTableViewCp;
    }

    @Override
    public String getText(Object input) {
        if (input instanceof GavWithParentProject) {
            if (!((GavWithParentProject) input).getLicenseIsKnown()) {
                return "";
            }
            Map<Gav, DependencyInfo> dependencyInfoMap = dependencyTableViewCp.getProjectInformation()
                    .getDependencyInfoMap(dependencyTableViewCp.getInputProject());
            String text = ""
                    + new ComplexLicenseParser(dependencyInfoMap.get(((GavWithParentProject) input).getGav()).getSimpleLicense().getComplexLicenseItem());
            return text;
        }
        if (input instanceof String) {
            return (String) input;
        }
        return "";
    }

    @Override
    public String getTitle() {
        return "License";
    }

}
