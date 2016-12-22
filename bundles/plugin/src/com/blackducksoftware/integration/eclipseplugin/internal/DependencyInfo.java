package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityItem;

public class DependencyInfo {
	
	private List<VulnerabilityItem> vulnList;
	private List<License> licenses;
	
	public DependencyInfo(List<VulnerabilityItem> vulnList, String license){
		this.vulnList = vulnList;
		this.license = license;
	}

	public List<VulnerabilityItem> getVulnList() {
		return vulnList;
	}

	public void setVulnMap(List<VulnerabilityItem> vulnList) {
		this.vulnList = vulnList;
	}

	public String getLicense() {
		return license;
	}

	public void setLicense(String license) {
		this.license = license;
	}
	
	
	
}
