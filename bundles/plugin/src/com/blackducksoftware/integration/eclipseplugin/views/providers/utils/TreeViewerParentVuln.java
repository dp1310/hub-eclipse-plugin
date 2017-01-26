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

import java.util.Iterator;
import java.util.List;

import com.blackducksoftware.integration.eclipseplugin.views.providers.DependencyTableViewContentProvider;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityItemPlusMeta;

public class TreeViewerParentVuln extends TreeViewerParent {

    private final List<VulnerabilityItemPlusMeta> vulns;

    public TreeViewerParentVuln(String dispName, GavWithParentProject gavWithParentProj, List<VulnerabilityItemPlusMeta> vulns) {
        super(dispName, gavWithParentProj);
        this.vulns = vulns;
    }

    @Override
    public boolean hasChildren() {
        return true;
    }

    @Override
    public Object[] getChildren() {
        if (vulns == null || vulns.size() == 0) {
            return DependencyTableViewContentProvider.NO_VULNERABILITIES_TO_SHOW;
        }

        List<VulnerabilityItemPlusMeta> vulnList = vulns;
        Iterator<VulnerabilityItemPlusMeta> vulnIt = vulnList.iterator();
        VulnerabilityWithParentGav[] vulnsWithGavs = new VulnerabilityWithParentGav[vulnList.size()];
        int i = 0;
        while (vulnIt.hasNext()) {
            VulnerabilityWithParentGav vulnWithGav = new VulnerabilityWithParentGav(gavWithParentProject.getGav(), vulnIt.next());
            vulnsWithGavs[i] = vulnWithGav;
            i++;
        }
        return vulnsWithGavs;

    }

}
