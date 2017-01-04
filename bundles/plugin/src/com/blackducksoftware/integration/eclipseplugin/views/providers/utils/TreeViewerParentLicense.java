package com.blackducksoftware.integration.eclipseplugin.views.providers.utils;

import com.blackducksoftware.integration.hub.api.component.version.ComplexLicense;
import com.blackducksoftware.integration.hub.api.component.version.SimpleLicense;

public class TreeViewerParentLicense extends TreeViewerParent {

	private final SimpleLicense sLicense;
	
	public TreeViewerParentLicense(String dispName, GavWithParentProject gavWithParentProject, SimpleLicense sLicense) {
		super(dispName, gavWithParentProject);
		this.sLicense = sLicense;
	}

	@Override
	public boolean hasChildren() {
		return true;
	}

	@Override
	public Object[] getChildren() {
		ComplexLicense parentLicense = sLicense.getComplexLicense();
		int numLicense = parentLicense.getLicenses().size();
		Object[] children = new Object[numLicense + 1]; //Add type of the license as well as a child
		
		children[0] = ("Type: " + sLicense.getComplexLicense().getType().toString());
		
		for(int i=0; i<numLicense; i++) {
			children[i+1] = parentLicense.getLicenses().get(i);
		}
		
		return children;
	}
	
	public SimpleLicense getSimpleLicense() {
		return this.sLicense;
	}

}
