package com.blackducksoftware.integration.eclipseplugin.views.providers.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.forms.widgets.Hyperlink;

public class HubHyperlink extends Hyperlink {
	public HubHyperlink(Composite parent, int style) {
		super(parent, style);
		this.setUnderlined(true);
	}
	
	@Override
	public void paintText(GC gc, Rectangle bounds) {
		super.paintText(gc, bounds);
	}
	
	@Override
	protected void handleActivate(Event e) {
		super.handleActivate(e);
		System.out.println("link activated");
		IWebBrowser brower;
		try {
			brower = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(SWT.NONE, null, null, null);
			brower.openURL(new URL(this.getHref().toString()));
		} catch (PartInitException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}
}
