package com.blackducksoftware.integration.eclipseplugin.preferences.listeners;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;

public class HubHyperlinkAdapter extends HyperlinkAdapter {
	private final String url;
	
	public HubHyperlinkAdapter(final String url) {
		this.url = url;
	}
	
	@Override
	public void linkActivated(HyperlinkEvent e) {
		System.out.println("going to Url: " + url);
		
		try {
			final IWebBrowser brower = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(SWT.NONE, null, null, null);
			brower.openURL(new URL(this.url));
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
