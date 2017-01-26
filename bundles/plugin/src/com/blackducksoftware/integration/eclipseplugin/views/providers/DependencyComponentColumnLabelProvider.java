/**
 * hub-eclipse-plugin
 *
 * Copyright (C) 2017 Black Duck Software, Inc.
 * http://www.blackducksoftware.com/
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package com.blackducksoftware.integration.eclipseplugin.views.providers;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.graphics.Image;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PathsToIconFiles;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.GavWithParentProject;

public class DependencyComponentColumnLabelProvider extends DependencyTreeViewLabelProvider implements IStyledLabelProvider {

	@Override
	public String getText(Object input) {
        if (input instanceof GavWithParentProject) {
            String text = "" + ((GavWithParentProject) input).getGav().toString();
            return text;
        }
        if (input instanceof String) {
            return (String) input;
        }
        return "";
	}

	@Override
	public String getTitle() {
		return "Component";
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
