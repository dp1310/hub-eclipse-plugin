package com.blackducksoftware.integration.eclipseplugin.views.listeners;

import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComplexLicenseWithParentGav;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.VulnerabilityWithParentGav;

public class TreeDoubleClickListener implements IDoubleClickListener {

	@Override
	public void doubleClick(DoubleClickEvent event) {
		TreeViewer viewer = (TreeViewer) event.getViewer();
		IStructuredSelection selection = (IStructuredSelection)event.getSelection();
		Object selectedObject = selection.getFirstElement();
		if(selectedObject instanceof VulnerabilityWithParentGav) {
			VulnerabilityWithParentGav vulnWithGav = (VulnerabilityWithParentGav)selectedObject;
			System.out.println("link activated");
			String link = vulnWithGav.getVuln().getLink();
			IWebBrowser browser;

			//Authenticate first
			/*
			 * Currently, the hub will "redirect" calls only if authenticated, proper redirection will come in hub 3.5 (maybe)
			 */
			
			try {
				//browser = PlatformUI.getWorkbench().getBrowserSupport().createBrowser(SWT.NONE, null, null, null);
				browser = PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser();
				browser.openURL(new URL(link));
			} catch (PartInitException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (MalformedURLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			
			return;
			
		}
		
		if(selectedObject instanceof ComplexLicenseWithParentGav) {
			ComplexLicenseWithParentGav cLicenseWithGav = (ComplexLicenseWithParentGav)selectedObject;
			//String link = cLicenseWithGav.getComplexLicense();
		}
	}

}
