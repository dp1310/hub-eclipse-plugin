package com.blackducksoftware.integration.eclipseplugin.views.providers.utils;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.hub.api.component.version.ComplexLicense;

public class ComplexLicenseWithParentGav {
	
	private final Gav gav;
	private final ComplexLicense complexLicense;
	
	public ComplexLicenseWithParentGav(Gav gav, ComplexLicense complexLicense) {
		this.gav = gav;
		this.complexLicense = complexLicense;
	}
	
	public Gav getGav() {
		return this.gav;
	}
	
	public ComplexLicense getComplexLicense() {
		return this.complexLicense;
	}
}
