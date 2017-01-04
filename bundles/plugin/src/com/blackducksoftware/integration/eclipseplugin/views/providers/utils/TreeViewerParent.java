package com.blackducksoftware.integration.eclipseplugin.views.providers.utils;

public abstract class TreeViewerParent {
	
	protected final String dispName;
	protected final GavWithParentProject gavWithParentProject;
	
	public TreeViewerParent(String dispName, GavWithParentProject gavWithParentProject) {
		this.dispName = dispName;
		this.gavWithParentProject = gavWithParentProject;
	}
	
	public abstract boolean hasChildren();
	public abstract Object[] getChildren();
	
	public String getDispName() {
		return this.dispName;
	}
	
	public GavWithParentProject getGavWithParentProject() {
		return this.gavWithParentProject;
	}
}
