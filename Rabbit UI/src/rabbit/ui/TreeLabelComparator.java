/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;

/**
 * <p>
 * A comparator for sorting the a {@link TreeViewer} when the user is clicked on
 * a column.
 * </p>
 * <p>
 * To register a column for sorting, simply call the column's
 * {@link TreeColumn#addSelectionListener(TableColumnComparator)} method and
 * pass in an instance of this class. An instance of this class can be shared by
 * multiple columns of the same viewer.
 * </p>
 * <p>
 * The viewer must have a {@link ITableLabelProvider} or a
 * {@link ILabelProvider}.
 * </p>
 * 
 * @see SelectionListener
 * @see ITableLabelProvider
 * @see ILabelProvider
 * @see TreeViewer#setLabelProvider(ITableLabelProvider)
 */
public class TreeLabelComparator extends ViewerComparator implements SelectionListener {

	/**
	 * One of {@link SWT#NONE}, {@link SWT#UP}, {@link SWT#DOWN}.
	 */
	private int sortDirection;
	private TreeViewer viewer;
	private TreeColumn selectedColumn;

	public TreeLabelComparator(TreeViewer parent) {
		sortDirection = SWT.NONE;
		selectedColumn = null;
		viewer = parent;
	}

	@Override
	public int compare(Viewer v, Object e1, Object e2) {
		int cat1 = category(e1);
		int cat2 = category(e2);

		if (cat1 != cat2) {
			return cat1 - cat2;
		}

		int value = doCompare(v, e1, e2);
		if (sortDirection == SWT.DOWN) {
			value *= -1;
		}
		return value;
	}

	/**
	 * Gets the currently selected column.
	 * 
	 * @return The selected column.
	 */
	public TreeColumn getSelectedColumn() {
		return selectedColumn;
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		Widget item = e.widget;
		if (!(item instanceof TreeColumn)) {
			return;
		}
		Object[] expandedElements = viewer.getExpandedElements();

		selectedColumn = (TreeColumn) e.widget;
		Tree table = selectedColumn.getParent();
		TreeColumn previousColumn = table.getSortColumn();
		sortDirection = table.getSortDirection();

		if (previousColumn == selectedColumn) {
			sortDirection = (sortDirection == SWT.UP) ? SWT.DOWN : SWT.UP;
		} else {
			table.setSortColumn(selectedColumn);
			sortDirection = SWT.UP;
			viewer.setComparator(this);
		}
		table.setSortDirection(sortDirection);
		viewer.refresh();
		viewer.setExpandedElements(expandedElements);
	}

	protected int doCompare(Viewer v, Object e1, Object e2) {
		IBaseLabelProvider provider = viewer.getLabelProvider();

		String s1 = null;
		String s2 = null;

		if (provider instanceof ILabelProvider) {
			s1 = ((ILabelProvider) provider).getText(e1);
			s2 = ((ILabelProvider) provider).getText(e2);

		} else if (provider instanceof ITableLabelProvider) {
			int index = 0;
			if (selectedColumn != null) {
				index = viewer.getTree().indexOf(selectedColumn);
			}
			s1 = ((ITableLabelProvider) provider).getColumnText(e1, index);
			s2 = ((ITableLabelProvider) provider).getColumnText(e2, index);
		}

		int value = 0;
		if (s1 != null && s2 != null) {
			value = s1.compareToIgnoreCase(s2);
		} else {
			value = (s1 == null) ? -1 : 1;
		}
		return value;
	}

}
