package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.List;

import com.blackducksoftware.integration.hub.api.component.version.SimpleLicense;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;

public class DependencyInfo {
	
	private final List<VulnerabilityItem> vulnList;
	private final SimpleLicense simpleLicense;
	
	public DependencyInfo(final List<VulnerabilityItem> vulnList, final SimpleLicense simpleLicense){
		this.vulnList = vulnList;
		this.simpleLicense = simpleLicense;
	}

	public List<VulnerabilityItem> getVulnList() {
		return vulnList;
	}

	public SimpleLicense getSimpleLicense() {
		return simpleLicense;
	}
	
	
	
}
