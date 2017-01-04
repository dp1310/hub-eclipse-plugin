package com.blackducksoftware.integration.eclipseplugin.views.listeners;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;

import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

public class ComponentSelectionListener implements ISelectionChangedListener {

	private final VulnerabilityView vulnerabilityView;
	
	public ComponentSelectionListener(VulnerabilityView vulnerabilityView) {
		this.vulnerabilityView = vulnerabilityView;
	}
	
	@Override
	public void selectionChanged(SelectionChangedEvent event) {
		// TODO Auto-generated method stub
		System.out.println("arg0: " + event.toString() + " : " + event.getSelection().toString());
		// Update secondary pane
		if(event.getSelection() instanceof GavWithParentProject) {
			GavWithParentProject gavWithParentProject = (GavWithParentProject)event.getSelection();
			vulnerabilityView.setLastGavWithParentProject(gavWithParentProject);
			vulnerabilityView.setDrilldownInput(gavWithParentProject);
		}
	}

}
