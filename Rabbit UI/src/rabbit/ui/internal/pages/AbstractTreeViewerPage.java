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
package rabbit.ui.internal.pages;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;

import rabbit.ui.CellPainter;
import rabbit.ui.internal.RabbitUI;

public abstract class AbstractTreeViewerPage extends AbstractValueProviderPage {

	private TreeViewer viewer;
	private TreeViewerColumn graphCol;

	public AbstractTreeViewerPage() {
	}

	@Override
	public void createContents(Composite parent) {
		viewer = new TreeViewer(parent, SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
		viewer.setContentProvider(createContentProvider());
		viewer.setLabelProvider(createLabelProvider());
		viewer.setUseHashlookup(true);
		viewer.getTree().setHeaderVisible(true);
		viewer.getTree().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				saveState();
			}
		});
		viewer.addDoubleClickListener(new IDoubleClickListener() {
			@Override
			public void doubleClick(DoubleClickEvent e) {
				IStructuredSelection select = (IStructuredSelection) e.getSelection();
				Object o = select.getFirstElement();
				if (((ITreeContentProvider) viewer.getContentProvider()).hasChildren(o)) {
					viewer.setExpandedState(o, !viewer.getExpandedState(o));
				}
			}
		});

		createColumns(viewer);

		// Special column for painting:
		graphCol = new TreeViewerColumn(viewer, SWT.LEFT);
		graphCol.setLabelProvider(createCellPainter());
		graphCol.getColumn().setWidth(100);
		graphCol.getColumn().addSelectionListener(createValueSorterForTree(viewer));

		for (TreeColumn column : viewer.getTree().getColumns()) {
			column.setMoveable(true);
			column.setResizable(true);
		}
		viewer.setComparator(createComparator(viewer));
		restoreState();
	}

	/**
	 * Creates a cell painter for painting the graph cells.
	 * @return A cell painter.
	 */
	protected CellLabelProvider createCellPainter() {
		return new CellPainter(this);
	}

	@Override
	public int getColumnWidth() {
		return graphCol.getColumn().getWidth();
	}

	public TreeViewer getViewer() {
		return viewer;
	}

	@Override
	public boolean shouldPaint(Object element) {
		return true;
	}

	/**
	 * Creates the extra columns for the viewer. The graph column will be
	 * created after this.
	 * 
	 * @param viewer
	 *            The viewer.
	 */
	protected abstract void createColumns(TreeViewer viewer);

	/**
	 * Creates a default comparator for sorting the viewer.
	 * 
	 * @param viewer
	 *            The viewer.
	 * @return A viewer comparator.
	 */
	protected abstract ViewerComparator createComparator(TreeViewer viewer);

	/**
	 * Creates a content provider for this viewer.
	 * 
	 * @return A content provider.
	 */
	protected abstract ITreeContentProvider createContentProvider();

	/**
	 * Creates a label provider for this viewer.
	 * 
	 * @return A label provider.
	 */
	protected abstract ITableLabelProvider createLabelProvider();

	/** Restores the state of the page. */
	protected void restoreState() {
		IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
		for (TreeColumn column : getViewer().getTree().getColumns()) {
			int width = store.getInt(getWidthPreferenceString(column));
			if (width > 0) {
				column.setWidth(width);
			}
		}
	}

	/** Saves the state of the page. */
	protected void saveState() {
		IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
		for (TreeColumn column : getViewer().getTree().getColumns()) {
			store.setValue(getWidthPreferenceString(column), column.getWidth());
		}
	}

	private String getWidthPreferenceString(TreeColumn column) {
		return getClass().getSimpleName() + '.' + column.getText() + "Width";
	}
}
