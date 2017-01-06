package com.blackducksoftware.integration.eclipseplugin.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.blackducksoftware.integration.build.GavWithType;
import com.blackducksoftware.integration.eclipseplugin.internal.exception.ComponentLookupNotFoundException;
import com.blackducksoftware.integration.eclipseplugin.internal.exception.LicenseLookupNotFoundException;
import com.blackducksoftware.integration.hub.api.component.version.SimpleLicense;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;
import com.blackducksoftware.integration.hub.dataservice.license.LicenseDataService;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityDataService;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityItemPlusLink;
//import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.HubIntegrationException;
//import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;
//import com.blackducksoftware.integration.hub.exception.VersionDoesNotExistException;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class ComponentCache {

    private LoadingCache<GavWithType, DependencyInfo> cache;

    private int cacheCapacity;

    public ComponentCache(final VulnerabilityDataService vulnService, final LicenseDataService licenseService, final int cacheCapacity) {
        this.cacheCapacity = cacheCapacity;
        cache = buildCache(vulnService, licenseService);
    }

    public LoadingCache<GavWithType, DependencyInfo> getCache() {
        return cache;
    }

    public void setVulnService(VulnerabilityDataService vulnService, LicenseDataService licenseService) {
        cache = buildCache(vulnService, licenseService);
    }

    public LoadingCache<GavWithType, DependencyInfo> buildCache(VulnerabilityDataService vulnService, LicenseDataService licenseService) {
        return CacheBuilder.newBuilder().maximumSize(cacheCapacity).expireAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<GavWithType, DependencyInfo>() {
                    @Override
                    public DependencyInfo load(final GavWithType gav)
                            throws ComponentLookupNotFoundException, IOException, URISyntaxException,
                            LicenseLookupNotFoundException, HubIntegrationException {
                        
                    	List<VulnerabilityItemPlusLink> vulns = null;
                    	if (vulnService != null) {
                            vulns = vulnService.getVulnsPlusLinkFromComponentVersion(gav.getType().toString().toLowerCase(), gav.getGav().getGroupId(),
                                    gav.getGav().getArtifactId(), gav.getGav().getVersion());
                            
                            if (vulns == null) {
                                throw new ComponentLookupNotFoundException(
                                    "Hub could not find vulnerabilities for component " + gav.getGav() + " with type " + gav.getType());
                            }  
                        } else {
                            throw new ComponentLookupNotFoundException("Unable to look up component in Hub");
                        }
                        
                    	SimpleLicense sLicense = null;
                        if (licenseService != null) {
                        	sLicense = licenseService.getSimpleLicenseFromComponent(gav.getType().toString().toLowerCase(), gav.getGav().getGroupId(),
                                    gav.getGav().getArtifactId(), gav.getGav().getVersion());
                        	
                        	if(sLicense == null) {
                        		throw new LicenseLookupNotFoundException(
                        			"Hub could not find license information for component " + gav.getGav() + " with type " + gav.getType());
                        	}
                        } else {
                        	throw new LicenseLookupNotFoundException("Unable to look up license info in Hub");
                        }
                        
                        return new DependencyInfo(vulns, sLicense);
                    }
                });
    }

}
