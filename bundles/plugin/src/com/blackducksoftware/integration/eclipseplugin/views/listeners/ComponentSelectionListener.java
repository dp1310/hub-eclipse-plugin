package com.blackducksoftware.integration.eclipseplugin.views.listeners;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;

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
		System.out.println(event.getSelection().getClass().toString());
		// Update secondary pane
		if(event.getSelection() instanceof StructuredSelection) {
			StructuredSelection structuredSel = ((StructuredSelection)event.getSelection());
			if(structuredSel.getFirstElement() instanceof GavWithParentProject) {
				GavWithParentProject gavWithParentProject = ((GavWithParentProject)structuredSel.getFirstElement());
				vulnerabilityView.setLastGavWithParentProject(gavWithParentProject);
				vulnerabilityView.setDrilldownInput(gavWithParentProject);
			}
		}
	}

}
