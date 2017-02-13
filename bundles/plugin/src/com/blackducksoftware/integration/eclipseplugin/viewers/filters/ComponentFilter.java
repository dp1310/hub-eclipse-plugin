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
