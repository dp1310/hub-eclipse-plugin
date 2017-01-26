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

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.blackducksoftware.integration.eclipseplugin.internal.exception.ComponentLookupNotFoundException;
import com.blackducksoftware.integration.eclipseplugin.internal.exception.LicenseLookupNotFoundException;
import com.blackducksoftware.integration.hub.api.component.version.ComplexLicensePlusMeta;
import com.blackducksoftware.integration.hub.buildtool.Gav;
import com.blackducksoftware.integration.hub.dataservice.license.LicenseDataService;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityDataService;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityItemPlusMeta;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ComponentCache {

    private LoadingCache<Gav, DependencyInfo> cache;

    private int cacheCapacity;

    public ComponentCache(final VulnerabilityDataService vulnService, final LicenseDataService licenseService, final int cacheCapacity) {
        this.cacheCapacity = cacheCapacity;
        cache = buildCache(vulnService, licenseService);
    }

    public LoadingCache<Gav, DependencyInfo> getCache() {
        return cache;
    }

    public void setVulnService(VulnerabilityDataService vulnService, LicenseDataService licenseService) {
        cache = buildCache(vulnService, licenseService);
    }

    public LoadingCache<Gav, DependencyInfo> buildCache(VulnerabilityDataService vulnService, LicenseDataService licenseService) {
        return CacheBuilder.newBuilder().maximumSize(cacheCapacity).expireAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<Gav, DependencyInfo>() {
                    @Override
                    public DependencyInfo load(final Gav gav)
                            throws ComponentLookupNotFoundException, IOException, URISyntaxException,
                            LicenseLookupNotFoundException, HubIntegrationException {

                        List<VulnerabilityItemPlusMeta> vulns = null;
                        if (vulnService != null) {
                            vulns = vulnService.getVulnsPlusMetaFromComponentVersion(gav.getNamespace().toString().toLowerCase(), gav.getGroupId(),
                                    gav.getArtifactId(), gav.getVersion());

                            if (vulns == null) {
                                throw new ComponentLookupNotFoundException(
                                        String.format("Hub could not find license information for component %1$s with namespace %2$s", gav,
                                                gav.getNamespace()));
                            }
                        } else {
                            throw new ComponentLookupNotFoundException("Unable to look up component in Hub");
                        }

                        ComplexLicensePlusMeta sLicense = null;
                        if (licenseService != null) {
                            sLicense = licenseService.getComplexLicensePlusMetaFromComponent(gav.getNamespace().toString().toLowerCase(), gav.getGroupId(),
                                    gav.getArtifactId(), gav.getVersion());

                            if (sLicense == null) {
                                throw new LicenseLookupNotFoundException(
                                        String.format("Hub could not find license information for component %1$s with namespace %2$s", gav,
                                                gav.getNamespace()));
                            }
                        } else {
                            throw new LicenseLookupNotFoundException("Unable to look up license info in Hub");
                        }

                        return new DependencyInfo(vulns, sLicense);
                    }
                });
    }

}
