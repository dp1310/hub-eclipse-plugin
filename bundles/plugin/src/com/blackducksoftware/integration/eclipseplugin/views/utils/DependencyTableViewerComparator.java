/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
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
