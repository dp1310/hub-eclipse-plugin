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

import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;

/*
 * Eclipse 4 Plug-in Development by Example.pdf pg. 102
 */
public abstract class DependencyTreeViewLabelProvider extends StyledCellLabelProvider {

    public final int alignment;

    public final int width;

    public final static String VALUE_UNKNOWN = "UNKNOWN";

    public DependencyTreeViewLabelProvider() {
        this(200, SWT.LEFT);
    }

    public DependencyTreeViewLabelProvider(int width, int alignment) {
        this.alignment = alignment;
        this.width = width;
    }

    public abstract String getText(Object input);

    public abstract String getTitle();

    public Image getImage(Object input) {
        return null;
    }

    public static Color decodeHex(Display display, String hexString) {
        java.awt.Color c = java.awt.Color.decode(hexString);
        return new Color(display, c.getRed(), c.getGreen(), c.getBlue());
    }

    public TableViewerColumn addColumnTo(TableViewer viewer) {
        TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, alignment);
        TableColumn column = tableViewerColumn.getColumn();
        column.setMoveable(true);
        column.setResizable(true);
        column.setText(getTitle());
        column.setWidth(width);
        tableViewerColumn.setLabelProvider(this);
        return tableViewerColumn;
    }

    @Override
    public void update(ViewerCell cell) {
        super.update(cell);
        cell.setText(getText(cell.getElement()));
        cell.setImage(getImage(cell.getElement()));
        styleCell(cell);
    }

    public void styleCell(ViewerCell cell) {
        // Do nothing, override if you want to style the cell
    }
}
