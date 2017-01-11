package com.blackducksoftware.integration.eclipseplugin.views.providers.utils;

import com.blackducksoftware.integration.hub.api.component.version.ComplexLicense;
import com.blackducksoftware.integration.hub.api.component.version.ComplexLicensePlusMeta;

public class TreeViewerParentLicense extends TreeViewerParent {

	private final ComplexLicensePlusMeta complexLicense;
	
	public TreeViewerParentLicense(String dispName, GavWithParentProject gavWithParentProject, ComplexLicensePlusMeta complexLicense) {
		super(dispName, gavWithParentProject);
		this.complexLicense = complexLicense;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public Object[] getChildren() {
		ComplexLicense parentLicense = complexLicense.getComplexLicense();
		int numLicense = parentLicense.getLicenses().size();
		Object[] children = new Object[numLicense + 1]; //Add type of the license as well as a child
		
		children[0] = ("Type: " + complexLicense.getComplexLicense().getType().toString());
		
		for(int i=0; i<numLicense; i++) {
			children[i+1] = new ComplexLicenseWithParentGav(gavWithParentProject.getGav(), complexLicense.getSubLicensesPlusMeta().get(i));
		}
		
		return children;
	}
	
	public ComplexLicensePlusMeta getComplexLicense() {
		return this.complexLicense;
	}

}
