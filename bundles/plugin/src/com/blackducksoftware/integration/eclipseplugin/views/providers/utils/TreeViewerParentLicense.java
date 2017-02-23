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

import com.blackducksoftware.integration.hub.api.component.version.ComplexLicenseItem;

public class TreeViewerParentLicense extends TreeViewerParent {

    private final ComplexLicenseItem complexLicenseItem;

    public TreeViewerParentLicense(String dispName, ComponentModel gavWithParentProject, ComplexLicenseItem complexLicenseItem) {
        super(dispName, gavWithParentProject);
        this.complexLicenseItem = complexLicenseItem;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public Object[] getChildren() {
        ComplexLicenseItem parentLicense = complexLicenseItem;
        int numLicense = parentLicense.getLicenses().size();
        Object[] children = new Object[numLicense + 1]; // Add type of the license as well as a child

        children[0] = ("Type: " + complexLicenseItem.getType().toString());

        for (int i = 0; i < numLicense; i++) {
            children[i + 1] = new ComplexLicenseWithParentGav(gavWithParentProject.getGav(), complexLicenseItem.getLicenses().get(i));
        }

        return children;
    }

    public ComplexLicenseItem getComplexLicense() {
        return this.complexLicenseItem;
    }

}
