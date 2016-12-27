package com.blackducksoftware.integration.eclipseplugin.internal;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;
import java.util.concurrent.TimeUnit;

import com.blackducksoftware.integration.build.GavWithType;
import com.blackducksoftware.integration.eclipseplugin.internal.exception.ComponentLookupNotFoundException;
import com.blackducksoftware.integration.eclipseplugin.internal.exception.LicenseLookupNotFoundException;
import com.blackducksoftware.integration.hub.api.component.version.LicensesInfo;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityItem;
import com.blackducksoftware.integration.hub.dataservice.license.LicenseDataService;
import com.blackducksoftware.integration.hub.dataservices.vulnerability.VulnerabilityDataService;
import com.blackducksoftware.integration.hub.exception.BDRestException;
import com.blackducksoftware.integration.hub.exception.UnexpectedHubResponseException;
import com.blackducksoftware.integration.hub.exception.VersionDoesNotExistException;
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
/*
    public void setVulnService(VulnerabilityDataService vulnService) {
        cache = buildCache(vulnService);
    }
*/
    public LoadingCache<GavWithType, DependencyInfo> buildCache(VulnerabilityDataService vulnService, LicenseDataService licenseService) {
        return CacheBuilder.newBuilder().maximumSize(cacheCapacity).expireAfterWrite(1, TimeUnit.HOURS)
                .build(new CacheLoader<GavWithType, DependencyInfo>() {
                    @Override
                    public DependencyInfo load(final GavWithType gav)
                            throws ComponentLookupNotFoundException, IOException, URISyntaxException, BDRestException, UnexpectedHubResponseException,
                            VersionDoesNotExistException {
                        
                    	List<VulnerabilityItem> vulns = null;
                    	if (vulnService != null) {
                            vulns = vulnService.getVulnsFromComponent(gav.getType().toString().toLowerCase(), gav.getGav().getGroupId(),
                                    gav.getGav().getArtifactId(), gav.getGav().getVersion());
                            
                            if (vulns == null) {
                                throw new ComponentLookupNotFoundException(
                                    "Hub could not find vulnerabilities for component " + gav.getGav() + " with type " + gav.getType());
                            }  
                        } else {
                            throw new ComponentLookupNotFoundException("Unable to look up component in Hub");
                        }
                        
                    	LicensesInfo licensesInfo = null;
                        if (licenseService != null) {
                        	licensesInfo = licenseService.getLicensesInfo(gav.getType().toString().toLowerCase(), gav.getGav().getGroupId(),
                                    gav.getGav().getArtifactId(), gav.getGav().getVersion());
                        	
                        	if(licensesInfo == null) {
                        		throw new LicenseLookupNotFoundException(
                        			"Hub could not find license information for component " + gav.getGav() + " with type " + gav.getType());
                        	}
                        } else {
                        	throw new LicenseLookupNotFoundException("Unable to look up license info in Hub");
                        }
                        
                        //dummy return
                        return new DependencyInfo(vulns, licensesInfo);
                    }
                });
    }

}
