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
package com.blackducksoftware.integration.eclipseplugin.views.utils;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;

import com.blackducksoftware.integration.eclipseplugin.views.providers.DependencyTableViewContentProvider;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;

public class DependencyTableViewerComparator extends ViewerComparator {

    private DependencyTableViewContentProvider contentProvider;

    public DependencyTableViewerComparator(DependencyTableViewContentProvider contentProvider) {
        super();
        this.contentProvider = contentProvider;
    }

    @Override
    public int compare(Viewer viewer, Object e1, Object e2) {
        GavWithParentProject gav1 = (GavWithParentProject) e1;
        GavWithParentProject gav2 = (GavWithParentProject) e2;
        int[] gav1vulns = contentProvider.getProjectInformation().getVulnMapSeverityCount(gav1.getParentProject(), gav1.getGav());
        int[] gav2vulns = contentProvider.getProjectInformation().getVulnMapSeverityCount(gav2.getParentProject(), gav2.getGav());
        for (int i = 0; i < gav1vulns.length; i++) {
            int compareVal = gav2vulns[i] - gav1vulns[i];
            if (compareVal != 0) {
                return compareVal;
            }
        }
        if (!gav1.getComponentIsKnown()) {
            return -1;
        } else if (!gav2.getComponentIsKnown()) {
            return 1;
        }
        return (gav1.getGav().getArtifactId() + gav1.getGav().getVersion()).compareTo(
                (gav2.getGav().getArtifactId() + gav2.getGav().getVersion()));
    }
}
