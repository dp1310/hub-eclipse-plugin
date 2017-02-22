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
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.blackducksoftware.integration.eclipseplugin.internal.exception.ComponentLookupNotFoundException;
import com.blackducksoftware.integration.eclipseplugin.internal.exception.LicenseLookupNotFoundException;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.exception.IntegrationException;
import com.blackducksoftware.integration.hub.api.component.version.ComplexLicenseItem;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;
import com.blackducksoftware.integration.hub.buildtool.Gav;
import com.blackducksoftware.integration.hub.dataservice.license.LicenseDataService;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityDataService;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;

public class ComponentCache {

    private final int TTL_IN_MILLIS = 3600000;

    private ConcurrentHashMap<Gav, DependencyInfo> cache;

    private ConcurrentHashMap<Gav, Timestamp> cacheKeyTTL;

    private Timestamp oldestKeyAge;

    private int cacheCapacity;

    public ComponentCache(final int cacheCapacity) {
        this.cacheCapacity = cacheCapacity;
        cache = buildCache();
    }

    private ConcurrentHashMap<Gav, DependencyInfo> buildCache() {
        cache = new ConcurrentHashMap<>();
        cacheKeyTTL = new ConcurrentHashMap<>();
        return cache;
    }

    public DependencyInfo get(Gav gav) throws IntegrationException {
        DependencyInfo depInfo = cache.get(gav);
        Timestamp stalestamp = new Timestamp(System.currentTimeMillis() - TTL_IN_MILLIS);
        if (oldestKeyAge != null && oldestKeyAge.before(stalestamp)) {
            removeStaleKeys(stalestamp);
        }
        if (depInfo == null) {
            try {
                depInfo = load(gav);
            } catch (IOException | URISyntaxException | ComponentLookupNotFoundException | LicenseLookupNotFoundException e) {
                throw new IntegrationException(e);
            }
            // If over capacity, pop least recently used
            if (cache.size() == cacheCapacity) {
                removeLeastRecentlyUsedKey();
            }
            cache.put(gav, depInfo);
            cacheKeyTTL.put(gav, new Timestamp(System.currentTimeMillis()));
        }
        return depInfo;
    }

    public void removeLeastRecentlyUsedKey() {
        cache.remove(Collections.min(cacheKeyTTL.entrySet(),
                (entry1, entry2) -> entry1.getValue().getNanos() - entry2.getValue().getNanos()).getKey());
    }

    public void removeStaleKeys(Timestamp stalestamp) {
        oldestKeyAge = null;
        cacheKeyTTL.forEach(cacheCapacity, (livingGav, timestamp) -> {
            if (timestamp.before(stalestamp)) {
                cache.remove(livingGav);
                cacheKeyTTL.remove(livingGav);
            } else {
                oldestKeyAge = (oldestKeyAge == null || oldestKeyAge.after(timestamp)) ? timestamp : oldestKeyAge;
            }
        });
    }

    public DependencyInfo load(final Gav gav)
            throws ComponentLookupNotFoundException, IOException, URISyntaxException,
            LicenseLookupNotFoundException, IntegrationException {

        VulnerabilityDataService vulnService = Activator.getPlugin().getConnectionService().getVulnerabilityDataService();
        List<VulnerabilityItem> vulns = null;
        ComplexLicenseItem sLicense = null;
        try {
            if (vulnService != null) {
                vulns = vulnService.getVulnsFromComponentVersion(gav.getNamespace().toLowerCase(), gav.getGroupId(),
                        gav.getArtifactId(), gav.getVersion());

                // if (vulns == null) {
                // throw new ComponentLookupNotFoundException(
                // String.format("Hub could not find license information for component %1$s with namespace %2$s", gav,
                // gav.getNamespace()));
                // }
            } else {
                throw new ComponentLookupNotFoundException("Unable to look up component in Hub");
            }

            LicenseDataService licenseService = Activator.getPlugin().getConnectionService().getLicenseDataService();
            if (licenseService != null) {
                sLicense = licenseService.getComplexLicenseItemFromComponent(gav.getNamespace().toLowerCase(), gav.getGroupId(),
                        gav.getArtifactId(), gav.getVersion());

                // if (sLicense == null) {
                // return new DependencyInfo(vulns, sLicense);
                // throw new LicenseLookupNotFoundException(
                // String.format("Hub could not find license information for component %1$s with namespace %2$s", gav,
                // gav.getNamespace()));
                // }
            } else {
                throw new LicenseLookupNotFoundException("Unable to look up license info in Hub");
            }
        } catch (HubIntegrationException e) {
            // Do nothing
            // TODO: Eventually this should do something more graceful than create an object with null values
        }

        return new DependencyInfo(vulns, sLicense);
    }

}
