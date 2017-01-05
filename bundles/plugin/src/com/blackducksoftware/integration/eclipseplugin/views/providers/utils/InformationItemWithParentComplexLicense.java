package com.blackducksoftware.integration.eclipseplugin.views.providers.utils;

import com.blackducksoftware.integration.hub.api.component.version.ComplexLicense;

public class InformationItemWithParentComplexLicense {

	private String informationItem;
	
	private ComplexLicense complexLicense;
	
	public InformationItemWithParentComplexLicense(String informationItem, ComplexLicense complexLicense) {
		this.informationItem = informationItem;
		this.complexLicense = complexLicense;
	}
	
	public String getInformationItem() {
		return this.informationItem;
	}
	
	public ComplexLicense getComplexLicense() {
		return this.complexLicense;
	}
}
