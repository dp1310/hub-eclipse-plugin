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

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;

import com.blackducksoftware.integration.eclipseplugin.views.providers.utils.ComponentModel;

public class DependencyNumVulnColumnLabelProvider extends DependencyTreeViewLabelProvider {

    private final DependencyTableViewContentProvider dependencyTableViewCp;

    public DependencyNumVulnColumnLabelProvider(DependencyTableViewContentProvider dependencyTableViewCp) {
        super();
        this.dependencyTableViewCp = dependencyTableViewCp;
    }

    public DependencyNumVulnColumnLabelProvider(int width, int alignment, DependencyTableViewContentProvider dependencyTableViewCp) {
        super(width, alignment);
        this.dependencyTableViewCp = dependencyTableViewCp;
    }

    @Override
    public String getText(Object input) {
        if (input instanceof ComponentModel) {
            if (!((ComponentModel) input).getComponentIsKnown()) {
                return VALUE_UNKNOWN;
            }
            int[] vulnSeverityCount = ((ComponentModel) input).getVulnerabilityCount();
            String highString = vulnSeverityCount[0] < 1000 ? vulnSeverityCount[0] + "" : "999+";
            String mediumString = vulnSeverityCount[1] < 1000 ? vulnSeverityCount[1] + "" : "999+";
            String lowString = vulnSeverityCount[2] < 1000 ? vulnSeverityCount[2] + "" : "999+";
            String text = StringUtils.join(new Object[] { " " + highString, mediumString, lowString + " " }, " : ");
            return text;
        }
        return "";
    }

    @Override
    public String getTitle() {
        return "Security Risk";
    }

    public DependencyTableViewContentProvider getDependencyTableViewCp() {
        return this.dependencyTableViewCp;
    }

    @Override
    public void styleCell(ViewerCell cell) {
        if (cell.getText().equals(VALUE_UNKNOWN)) {
            cell.setText("");
            return;
        }
        String[] vulnChunks = cell.getText().split(":");
        cell.setFont(JFaceResources.getTextFont());
        Display display = Display.getCurrent();
        final String noVulns = " 0 ";
        final Color textColor = display.getSystemColor(SWT.COLOR_WHITE);
        final Color highColor = decodeHex(display, "#b52b24");
        final Color mediumColor = decodeHex(display, "#eca4a0");
        final Color lowColor = decodeHex(display, "#999999");
        final Color invisible = display.getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
        final Color[] vulnColors = new Color[] { highColor, mediumColor, lowColor };
        StyleRange[] styleRanges = new StyleRange[vulnChunks.length];
        int lastLabelEnd = 0;
        cell.setText(String.format(
                "%1$-5s %2$-5s %3$-5s",
                StringUtils.center(vulnChunks[0], 5), StringUtils.center(vulnChunks[1], 5), StringUtils.center(vulnChunks[2], 5)));
        for (int i = 0; i < vulnChunks.length; i++) {
            int labelStart = cell.getText().indexOf(vulnChunks[i], lastLabelEnd);
            int labelSize = vulnChunks[i].length();
            if (vulnChunks[i].equals(noVulns)) {
                styleRanges[i] = new StyleRange(labelStart, labelSize, invisible, invisible);
            } else {
                styleRanges[i] = new StyleRange(labelStart, labelSize, textColor, vulnColors[i]);
            }
            lastLabelEnd = labelStart + labelSize;
        }
        cell.setStyleRanges(styleRanges);
    }

}
