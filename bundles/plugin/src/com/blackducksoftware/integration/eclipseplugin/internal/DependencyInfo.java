package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.hub.api.component.version.License;
import com.blackducksoftware.integration.hub.api.component.version.LicenseInfo;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityItem;

public class DependencyInfo {
	
	private List<VulnerabilityItem> vulnList;
	private LicenseInfo licenseInfo;
	
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
