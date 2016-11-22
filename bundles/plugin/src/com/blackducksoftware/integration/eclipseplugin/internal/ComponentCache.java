package com.blackducksoftware.integration.eclipseplugin.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.blackducksoftware.integration.build.GavWithType;
import com.blackducksoftware.integration.eclipseplugin.internal.exception.ComponentLookupNotFoundException;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityItem;
import com.blackducksoftware.integration.hub.dataservices.vulnerability.VulnerabilityDataService;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;
import com.blackducksoftware.integration.hub.exception.VersionDoesNotExistException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ComponentCache {

    private LoadingCache<GavWithType, List<VulnerabilityItem>> cache;

    private int cacheCapacity;

    public ComponentCache(final VulnerabilityDataService vulnService, final int cacheCapacity) {
        this.cacheCapacity = cacheCapacity;
        cache = buildCache(vulnService);
    }

    public LoadingCache<GavWithType, List<VulnerabilityItem>> getCache() {
        return cache;
    }

    public void setVulnService(VulnerabilityDataService vulnService) {
        cache = buildCache(vulnService);
    }

    public LoadingCache<GavWithType, List<VulnerabilityItem>> buildCache(VulnerabilityDataService vulnService) {
        return CacheBuilder.newBuilder().maximumSize(cacheCapacity).expireAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<GavWithType, List<VulnerabilityItem>>() {
                    @Override
                    public List<VulnerabilityItem> load(final GavWithType gav)
                            throws ComponentLookupNotFoundException, IOException, URISyntaxException, BDRestException, UnexpectedHubResponseException,
                            VersionDoesNotExistException {
                        if (vulnService != null) {
                            List<VulnerabilityItem> vulns = vulnService.getVulnsFromComponent(gav.getType().toString().toLowerCase(), gav.getGav().getGroupId(),
                                    gav.getGav().getArtifactId(), gav.getGav().getVersion());
                            if (vulns != null) {
                                return vulns;
                            }
                            throw new ComponentLookupNotFoundException(
                                    "Hub could not find vulnerabilities for component " + gav.getGav() + " with type " + gav.getType());
                        } else {
                            throw new ComponentLookupNotFoundException("Unable to look up component in Hub");
                        }
                    }
                });
    }

}
