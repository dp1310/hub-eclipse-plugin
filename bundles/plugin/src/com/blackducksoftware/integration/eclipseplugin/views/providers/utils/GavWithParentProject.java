/*
 * Copyright (C) 2016 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.eclipseplugin.views.providers.utils;

import com.blackducksoftware.integration.hub.buildtool.Gav;

public class GavWithParentProject {

    private Gav gav;

    private String parentProject;

    private boolean hasVulns;

    public GavWithParentProject(Gav gav, String parentProject, boolean hasVulns) {
        this.gav = gav;
        this.parentProject = parentProject;
        this.hasVulns = hasVulns;
    }

    public Gav getGav() {
        return gav;
    }

    public String getParentProject() {
        return parentProject;
    }

    public boolean hasVulns() {
        return hasVulns;
    }

}
