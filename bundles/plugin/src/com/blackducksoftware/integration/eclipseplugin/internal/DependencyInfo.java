package com.blackducksoftware.integration.eclipseplugin.internal;

import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.hub.api.component.version.License;
import com.blackducksoftware.integration.hub.api.component.version.LicensesInfo;
import com.blackducksoftware.integration.hub.api.vulnerabilities.VulnerabilityItem;

public class DependencyInfo {
	
	private List<VulnerabilityItem> vulnList;
	private LicensesInfo licensesInfo;
	
	public DependencyInfo(List<VulnerabilityItem> vulnList, LicensesInfo licensesInfo){
		this.vulnList = vulnList;
		this.setLicensesInfo(licensesInfo);
	}

	public List<VulnerabilityItem> getVulnList() {
		return vulnList;
	}

	public void setVulnMap(List<VulnerabilityItem> vulnList) {
		this.vulnList = vulnList;
	}

	public LicensesInfo getLicensesInfo() {
		return licensesInfo;
	}

	public void setLicensesInfo(LicensesInfo licensesInfo) {
		this.licensesInfo = licensesInfo;
	}

	
	
	
}
