package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.List;

import com.blackducksoftware.integration.hub.api.component.version.SimpleLicense;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityItemPlusLink;

public class DependencyInfo {
	
	private final List<VulnerabilityItemPlusLink> vulnList;
	private final SimpleLicense simpleLicense;
	
	public DependencyInfo(final List<VulnerabilityItemPlusLink> vulnList, final SimpleLicense simpleLicense){
		this.vulnList = vulnList;
		this.simpleLicense = simpleLicense;
	}

	public List<VulnerabilityItemPlusLink> getVulnList() {
		return vulnList;
	}

	public SimpleLicense getSimpleLicense() {
		return simpleLicense;
	}
	
	
	
}
