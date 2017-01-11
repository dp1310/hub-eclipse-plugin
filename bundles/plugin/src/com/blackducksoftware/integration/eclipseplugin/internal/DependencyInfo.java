package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.List;

import com.blackducksoftware.integration.hub.api.component.version.ComplexLicensePlusMeta;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityItemPlusMeta;

public class DependencyInfo {
	
	private final List<VulnerabilityItemPlusMeta> vulnList;
	private final ComplexLicensePlusMeta simpleLicense;
	
	public DependencyInfo(final List<VulnerabilityItemPlusMeta> vulnList, final ComplexLicensePlusMeta simpleLicense){
		this.vulnList = vulnList;
		this.simpleLicense = simpleLicense;
	}

	public List<VulnerabilityItemPlusMeta> getVulnList() {
		return vulnList;
	}

	public ComplexLicensePlusMeta getSimpleLicense() {
		return simpleLicense;
	}
	
	
	
}
