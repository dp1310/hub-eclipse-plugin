package com.blackducksoftware.integration.eclipseplugin.views.listeners;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;

import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

public class PreferenceChangeDisplayUpdateListener implements IPropertyChangeListener {

    private final VulnerabilityView componentView;

    public PreferenceChangeDisplayUpdateListener(final VulnerabilityView componentView) {
        this.componentView = componentView;
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        if (componentView.getDependencyTableViewer() != null) {
            componentView.resetInput();
        }
    }
}
