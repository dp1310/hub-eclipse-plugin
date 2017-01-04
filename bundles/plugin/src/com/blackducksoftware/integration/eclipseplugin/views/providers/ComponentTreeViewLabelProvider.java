/*
 * Copyright (C) 2016 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.eclipseplugin.views.providers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PathsToIconFiles;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.InformationItemWithParentVulnerability;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.TreeViewerParent;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.VulnerabilityWithParentGav;
import com.blackducksoftware.integration.hub.api.component.version.ComplexLicense;

public class ComponentTreeViewLabelProvider extends LabelProvider implements IStyledLabelProvider {

    @Override
    public String getText(Object input) {
//        if (input instanceof GavWithParentProject) {
//            String text = "Component: " + ((GavWithParentProject) input).getGav().toString();
//            return text;
//        }
    	if (input instanceof TreeViewerParent) {
    		return ((TreeViewerParent)input).getDispName();
    	}
    	
        if (input instanceof VulnerabilityWithParentGav) {
            String text = "Name: " + ((VulnerabilityWithParentGav) input).getVuln().getVulnerabilityName();
            return text;
        }
        if (input instanceof InformationItemWithParentVulnerability) {
            return ((InformationItemWithParentVulnerability) input).getInformationItem();
        }
        if (input instanceof ComplexLicense) {
        	return ((ComplexLicense)input).getName();
        }
        if (input instanceof String) {
            return (String) input;
        }
        
        return "";
    }

    @Override
    public Image getImage(Object input) {
        if (input instanceof GavWithParentProject) {
            ImageDescriptor descriptor;
            if (!((GavWithParentProject) input).hasVulns()) {
                descriptor = Activator.getImageDescriptor(PathsToIconFiles.GREEN_CHECK);
            } else {
                descriptor = Activator.getImageDescriptor(PathsToIconFiles.RED_X);
            }
            return descriptor == null ? null : descriptor.createImage();
        }
        return null;
    }

    @Override
    public StyledString getStyledText(Object element) {
        String text = getText(element);
        return new StyledString(text);
    }

}
