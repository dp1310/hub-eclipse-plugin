package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.hub.api.component.version.SimpleLicense;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;

public class DependencyInfo {
	
	private List<VulnerabilityItem> vulnList;
	private SimpleLicense licenseInfo;
	
	public DependencyInfo(List<VulnerabilityItem> vulnList, LicenseInfo licenseInfo){
		this.vulnList = vulnList;
		this.setLicenseInfo(licenseInfo);
	}

	public List<VulnerabilityItem> getVulnList() {
		return vulnList;
	}

	public void setVulnMap(List<VulnerabilityItem> vulnList) {
		this.vulnList = vulnList;
	}

	public LicenseInfo getLicensesInfo() {
		return licenseInfo;
	}

	public void setLicenseInfo(LicenseInfo licenseInfo) {
		this.licenseInfo = licenseInfo;
	}

	
	
	
}
