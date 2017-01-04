package com.blackducksoftware.integration.eclipseplugin.views.providers;

import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;

public class DependencyIsTransitiveColumnLabelProvider extends DependencyTreeViewLabelProvider {
	
	private final DependencyTableViewContentProvider cp;
	
	public DependencyIsTransitiveColumnLabelProvider(DependencyTableViewContentProvider cp) {
		this.cp = cp;
	}
	
	@Override
	public String getText(Object input) {
		if (input instanceof GavWithParentProject) {
            if(((GavWithParentProject)input).getParentProject().equals(cp.getInputProject())) {
            	return "Direct Dependency";
            }
            return "Transitive Dependency";
        }
        if (input instanceof String) {
            return (String) input;
        }
        return "";
	}

	@Override
	public String getTitle() {
		return "Dependency Type";
	}

}
