package com.blackducksoftware.integration.eclipseplugin.views.providers;

import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.eclipseplugin.internal.DependencyInfo;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;

public class DependencyLicenseColumnLabelProvider extends DependencyTreeViewLabelProvider {

	private DependencyTableViewContentProvider dependencyTableViewCp;
	
	public DependencyLicenseColumnLabelProvider(DependencyTableViewContentProvider dependencyTableViewCp) {
        this.dependencyTableViewCp = dependencyTableViewCp;
    }
	
	@Override
	public String getText(Object input) {
		if (input instanceof GavWithParentProject) {
            Map<Gav, DependencyInfo> dependencyInfoMap = dependencyTableViewCp.getProjectInformation().getDependencyInfoMap(dependencyTableViewCp.getInputProject());
			String text = "" + dependencyInfoMap.get((GavWithParentProject)input).getSimpleLicense().getLicenseDisplay();
            return text;
        }
        if (input instanceof String) {
            return (String) input;
        }
        return "";
	}

	@Override
	public String getTitle() {
		return "License";
	}

}
