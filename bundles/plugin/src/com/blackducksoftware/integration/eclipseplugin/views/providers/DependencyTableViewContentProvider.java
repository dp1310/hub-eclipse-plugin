package com.blackducksoftware.integration.eclipseplugin.views.providers;

import java.util.List;
import java.util.Map;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

import com.blackducksoftware.integration.build.Gav;
import com.blackducksoftware.integration.eclipseplugin.internal.ProjectDependencyInformation;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.hub.api.vulnerability.VulnerabilityItem;

public class DependencyTableViewContentProvider implements IStructuredContentProvider {

    public static final String[] NO_SELECTED_PROJECT = new String[] { "No open project currently selected" };

    public static final String[] PROJECT_NOT_ACTIVATED = new String[] {
            "Black Duck scan not activated for current project" };

    public static final String[] ERR_UNKNOWN_INPUT = new String[] { "Input is of unknown type" };

    public static final String[] NO_VULNERABILITIES_TO_SHOW = new String[] { "No vulnerabilities to show!" };

    public static final String[] NO_HUB_CONNECTION = new String[] { "Cannot display vulnerabilities because you are not currently connected to the Hub" };

	
	private final IPreferenceStore preferenceStore;
	private final ProjectDependencyInformation projectInformation;
	private String inputProject;
	
	public DependencyTableViewContentProvider(IPreferenceStore preferenceStore, ProjectDependencyInformation projectInformation) {
		this.preferenceStore = preferenceStore;
		this.projectInformation = projectInformation;
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
        if (inputElement instanceof String) {
            String projectName = (String) inputElement;
            inputProject = projectName;
            if (projectName.equals("")) {
                return NO_SELECTED_PROJECT;
            }
            boolean isActivated = preferenceStore.getBoolean(projectName);
            if (isActivated) {
                if (Activator.getDefault().hasActiveHubConnection()) {
                    final Gav[] gavs = projectInformation.getAllDependencyGavs(projectName);
                    GavWithParentProject[] gavsWithParents = new GavWithParentProject[gavs.length];
                    for (int i = 0; i < gavs.length; i++) {
                        Gav gav = gavs[i];
                        Map<Gav, List<VulnerabilityItem>> vulnMap = projectInformation.getVulnMap(projectName);
                        boolean hasVulns = vulnMap.get(gav) != null && vulnMap.get(gav).size() > 0;
                        gavsWithParents[i] = new GavWithParentProject(gav, projectName, hasVulns);
                    }
                    return gavsWithParents;
                }
                return NO_HUB_CONNECTION;
            }
            return PROJECT_NOT_ACTIVATED;
        }
        return ERR_UNKNOWN_INPUT;
	}

}
