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

    // FIXME GavWithType has been removed
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
                                        "Hub could not find vulnerabilities for component " + gav + " with type " + gav.getNamespace());
                            }
                        } else {
                            throw new ComponentLookupNotFoundException("Unable to look up component in Hub");
                        }

                        ComplexLicensePlusMeta sLicense = null;
                        if (licenseService != null) {
                            sLicense = licenseService.getComplexLicensePlusMetaFromComponent(gav.toString().toLowerCase(), gav.getGroupId(),
                                    gav.getArtifactId(), gav.getVersion());

                            if (sLicense == null) {
                                throw new LicenseLookupNotFoundException(
                                        "Hub could not find license information for component " + gav + " with type " + gav.getNamespace());
                            }
                        } else {
                            throw new LicenseLookupNotFoundException("Unable to look up license info in Hub");
                        }

                        return new DependencyInfo(vulns, sLicense);
                    }
                });
    }

}
