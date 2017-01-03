package com.blackducksoftware.integration.eclipseplugin.views.providers;

import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TableColumn;

/*
 * Eclipse 4 Plug-in Development by Example.pdf pg. 102
 */
public abstract class DependencyTreeViewLabelProvider extends ColumnLabelProvider {
	public abstract String getText(Object object);
	public abstract String getTitle();
	public int getWidth() {
		return 250;
	}
	
	public TableViewerColumn addColumnTo(TableViewer viewer) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);
		TableColumn column = tableViewerColumn.getColumn();
		column.setMoveable(true);
		column.setResizable(true);
		column.setText(getTitle());
		column.setWidth(getWidth());
		tableViewerColumn.setLabelProvider(this);
		return tableViewerColumn;
	}
}
