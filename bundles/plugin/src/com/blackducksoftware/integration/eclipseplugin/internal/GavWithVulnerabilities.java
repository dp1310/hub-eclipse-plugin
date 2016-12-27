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
package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.List;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;

public class GavWithVulnerabilities {
    private final Gav gav;

    private final List<VulnerabilityItem> vulns;

    public GavWithVulnerabilities(Gav gav, List<VulnerabilityItem> vulns) {
        this.gav = gav;
        this.vulns = vulns;
    }

    public Gav getGav() {
        return gav;
    }

    public List<VulnerabilityItem> getVulns() {
        return vulns;
    }
}
