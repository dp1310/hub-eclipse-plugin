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

import org.eclipse.jface.resource.FontDescriptor;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

import com.blackducksoftware.integration.eclipseplugin.common.constants.PathsToIconFiles;
import com.blackducksoftware.integration.eclipseplugin.startup.Activator;
import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;
import com.blackducksoftware.integration.hub.buildtool.Gav;

public class DependencyComponentColumnLabelProvider extends DependencyTreeViewLabelProvider {

    @Override
    public String getText(Object input) {
        if (input instanceof ComponentModel) {
            Gav gav = ((ComponentModel) input).getGav();
            String text = gav.getArtifactId() + ":" + gav.getVersion();
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
        if (input instanceof ComponentModel) {
            ComponentModel validObject = ((ComponentModel) input);
            if (!validObject.getComponentIsKnown() || !validObject.getLicenseIsKnown()) {
                ImageDescriptor descriptor = Activator.getImageDescriptor(PathsToIconFiles.WARNING);
                return descriptor == null ? null : descriptor.createImage();
            }
        }
        return null;
    }

    @Override
    public void styleCell(ViewerCell cell) {
        String[] compChunks = cell.getText().split(":");
        cell.setText(String.format("%1$s  %2$s ", compChunks[0], compChunks[1]));
        Display display = Display.getCurrent();
        final Color versionColor = decodeHex(display, "#285F8F");
        final Color backgroundColor = decodeHex(display, "#fafafa");
        final Color borderColor = decodeHex(display, "#dddddd");
        final StyleRange versionStyle = new StyleRange(compChunks[0].length() + 1, compChunks[1].length() + 2, versionColor, backgroundColor);
        versionStyle.borderStyle = SWT.BORDER_SOLID;
        versionStyle.borderColor = borderColor;
        int versionHeight = (int) (cell.getFont().getFontData()[0].getHeight() * 0.85);
        versionStyle.font = FontDescriptor.createFrom(cell.getFont()).setHeight(versionHeight).createFont(display);
        cell.setStyleRanges(new StyleRange[] { versionStyle });
    }

}
