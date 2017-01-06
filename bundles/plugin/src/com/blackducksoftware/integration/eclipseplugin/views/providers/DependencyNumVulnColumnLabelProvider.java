package com.blackducksoftware.integration.eclipseplugin.views.providers;

import java.util.List;
import java.util.Map;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.eclipseplugin.internal.DependencyInfo;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;
import com.blackducksoftware.integration.hub.dataservice.vulnerability.VulnerabilityItemPlusLink;

public class DependencyNumVulnColumnLabelProvider extends DependencyTreeViewLabelProvider {

	private final DependencyTableViewContentProvider dependencyTableViewCp;
	
	public DependencyNumVulnColumnLabelProvider(DependencyTableViewContentProvider dependencyTableViewCp) {
		this.dependencyTableViewCp = dependencyTableViewCp;
	}
	
	@Override
	public String getText(Object input) {
		if (input instanceof GavWithParentProject) {
            Map<Gav, List<VulnerabilityItemPlusLink>> vulnsMap = dependencyTableViewCp.getProjectInformation().getVulnMap(dependencyTableViewCp.getInputProject());
			String text = "" + vulnsMap.get(((GavWithParentProject)input).getGav()).size();
            return text;
        }
        if (input instanceof String) {
            return (String) input;
        }
        return "";
	}

	@Override
	public String getTitle() {
		return "Num Vulns";
	}
	
	public DependencyTableViewContentProvider getDependencyTableViewCp() {
		return this.dependencyTableViewCp;
	}

}
