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
package com.blackducksoftware.integration.eclipseplugin.common.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.eclipse.jdt.core.JavaCore;

import com.blackducksoftware.integration.eclipseplugin.common.constants.ClasspathVariables;
import com.blackducksoftware.integration.eclipseplugin.internal.exception.ComponentLookupNotFoundException;
import com.blackducksoftware.integration.eclipseplugin.internal.exception.LicenseLookupNotFoundException;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.component.version.ComplexLicenseItem;
import com.blackducksoftware.integration.hub.api.vulnerability.SeverityEnum;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;
import com.blackducksoftware.integration.hub.buildtool.Gav;
import com.blackducksoftware.integration.hub.dataservice.license.LicenseDataService;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityDataService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;

public class DependencyInformationService {

    public boolean isMavenDependency(final URL filePath) {
        URL m2Repo;
        try {
            m2Repo = JavaCore.getClasspathVariable(ClasspathVariables.MAVEN).toFile().toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return false;
        }
        final String[] m2RepoSegments = m2Repo.getFile().split("/");
        final String[] filePathSegments = filePath.getFile().split("/");
        if (filePathSegments.length < m2RepoSegments.length) {
            return false;
        }
        for (int i = 0; i < m2RepoSegments.length; i++) {
            if (!filePathSegments[i].equals(m2RepoSegments[i])) {
                return false;
            }
        }
        return true;
    }

    public boolean isGradleDependency(final URL filePath) {
        final String[] filePathSegments = filePath.getFile().split("/");
        if (filePathSegments.length < 3) {
            return false;
        }
        if (filePathSegments[filePathSegments.length - 3].equals("lib")
                || filePathSegments[filePathSegments.length - 2].equals("plugins")
                || filePathSegments[filePathSegments.length - 2].equals("lib")) {
            return false;
        }
        for (final String segment : filePathSegments) {
            if (segment.equals(".gradle")) {
                return true;
            }
        }
        return false;
    }

    public ComponentModel load(final Gav gav)
            throws ComponentLookupNotFoundException, IOException, URISyntaxException,
            LicenseLookupNotFoundException, IntegrationException {
        VulnerabilityDataService vulnService = Activator.getPlugin().getConnectionService().getVulnerabilityDataService();
        List<VulnerabilityItem> vulns = null;
        ComplexLicenseItem sLicense = null;
        try {
            if (vulnService != null) {
                vulns = vulnService.getVulnsFromComponentVersion(gav.getNamespace().toLowerCase(), gav.getGroupId(),
                        gav.getArtifactId(), gav.getVersion());
            } else {
                throw new ComponentLookupNotFoundException("Unable to look up component in Hub");
            }

            LicenseDataService licenseService = Activator.getPlugin().getConnectionService().getLicenseDataService();
            if (licenseService != null) {
                sLicense = licenseService.getComplexLicenseItemFromComponent(gav.getNamespace().toLowerCase(), gav.getGroupId(),
                        gav.getArtifactId(), gav.getVersion());
            } else {
                throw new LicenseLookupNotFoundException("Unable to look up license info in Hub");
            }
        } catch (HubIntegrationException e) {
            // Do nothing
        }
        return new ComponentModel(gav, sLicense, getVulnerabilitySeverityCount(vulns), vulns != null);
    }

    public int[] getVulnerabilitySeverityCount(List<VulnerabilityItem> vulnerabilities) {
        int high = 0;
        int medium = 0;
        int low = 0;
        if (vulnerabilities == null) {
            return new int[] { 0, 0, 0 };
        }
        for (VulnerabilityItem vuln : vulnerabilities) {
            switch (SeverityEnum.valueOf(vuln.getSeverity())) {
            case HIGH:
                high++;
                break;
            case MEDIUM:
                medium++;
                break;
            case LOW:
                low++;
                break;
            default:
                break;
            }
        }
        return new int[] { high, medium, low };
    }
}
