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
package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.List;

import com.blackducksoftware.integration.hub.api.component.version.ComplexLicenseItem;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;

public class DependencyInfo {

    private final List<VulnerabilityItem> vulnList;

    private final ComplexLicenseItem simpleLicense;

    public DependencyInfo(final List<VulnerabilityItem> vulnList, final ComplexLicenseItem simpleLicense) {
        this.vulnList = vulnList;
        this.simpleLicense = simpleLicense;
    }

    public List<VulnerabilityItem> getVulnList() {
        return vulnList;
    }

    public ComplexLicenseItem getComplexLicenseItem() {
        return simpleLicense;
    }

    public boolean getComponentIsKnown() {
        return vulnList != null;
    }

    public boolean getLicenseIsKnown() {
        return simpleLicense != null;
    }
}
