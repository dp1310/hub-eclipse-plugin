package com.blackducksoftware.integration.eclipseplugin.views.listeners;

import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import com.blackducksoftware.integration.eclipseplugin.views.ui.VulnerabilityView;

@RunWith(MockitoJUnitRunner.class)
public class PreferenceChangeDisplayUpdateListenerTest {

    @Mock
    VulnerabilityView componentView;

    @Mock
    PropertyChangeEvent e;

    @Mock
    TreeViewer tree;

    @Test
    public void testSettingTableInput() {
        Mockito.when(componentView.getComponentViewer()).thenReturn(tree);
        final PreferenceChangeDisplayUpdateListener listener = new PreferenceChangeDisplayUpdateListener(componentView);
        listener.propertyChange(e);
        Mockito.verify(componentView, Mockito.times(1)).resetInput();
    }

    @Test
    public void testWhenTableNull() {
        final PreferenceChangeDisplayUpdateListener listener = new PreferenceChangeDisplayUpdateListener(componentView);
        listener.propertyChange(e);
        Mockito.verify(componentView, Mockito.times(0)).resetInput();
    }

}
