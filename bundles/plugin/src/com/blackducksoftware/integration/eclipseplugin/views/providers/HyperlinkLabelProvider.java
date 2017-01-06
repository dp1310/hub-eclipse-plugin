package com.blackducksoftware.integration.eclipseplugin.views.providers;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;

import com.blackducksoftware.integration.eclipseplugin.preferences.listeners.HubHyperlinkAdapter;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComplexLicenseWithParentGav;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.HubHyperlink;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.InformationItemWithParentComplexLicense;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.InformationItemWithParentVulnerability;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.TreeViewerParent;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.VulnerabilityWithParentGav;

public class HyperlinkLabelProvider extends StyledCellLabelProvider {
	HubHyperlink link;
	
	public HyperlinkLabelProvider(Composite parent) {
		link = new HubHyperlink(parent, SWT.WRAP);
	}
	
	@Override
	protected void paint(Event event, Object element) {
		String sValue = getDisplayText(element);
		link.setText(sValue);
		link.setHref("http://www.reddit.com/");
		link.setEnabled(true);
		link.setToolTipText("Test tooltip");
		link.setForeground(new Color(null, 0, 0, 225));
		//TODO give actual URLs
		link.addHyperlinkListener(new IHyperlinkListener(){
			public void linkActivated(HyperlinkEvent e) {
				System.out.println("link activated");
				IWebBrowser brower;
				try {
					brower = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(SWT.NONE, null, null, null);
					brower.openURL(new URL(link.getHref().toString()));
				} catch (PartInitException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (MalformedURLException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
			}

			@Override
			public void linkEntered(HyperlinkEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void linkExited(HyperlinkEvent arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		
		GC gc = event.gc;
		Rectangle cellRect = new Rectangle(event.x, event.y, event.width, event.height);
		cellRect.width = 500;
		
		link.paintText(gc, cellRect);
	}
	
	private String getDisplayText(Object input) {
    	if (input instanceof TreeViewerParent) {
    		return ((TreeViewerParent)input).getDispName();
    	}
    	
        if (input instanceof VulnerabilityWithParentGav) {
            //TODO hyperlink impl
        	String text = "Name: " + ((VulnerabilityWithParentGav) input).getVuln().getVulnItem().getVulnerabilityName()
        			+ " - " + ((VulnerabilityWithParentGav)input).getVuln().getLink();
            return text;
        }
        if (input instanceof InformationItemWithParentVulnerability) {
            return ((InformationItemWithParentVulnerability) input).getInformationItem();
        }
        if (input instanceof ComplexLicenseWithParentGav) {
        	return ((ComplexLicenseWithParentGav)input).getComplexLicense().getName();
        }
        if (input instanceof InformationItemWithParentComplexLicense) {
        	return ((InformationItemWithParentComplexLicense)input).getInformationItem();
        }
        if (input instanceof String) {
            return (String) input;
        }
        
        return "";
	}
}
