/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.ui.internal.pages;

import rabbit.ui.CellPainter;
import rabbit.ui.IPage;
import rabbit.ui.TreeViewerSorter;
import rabbit.ui.internal.RabbitUI;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;

public abstract class AbstractTreeViewerPage2 implements IPage {

  private TreeViewerSorter valueSorter;
  private FilteredTree filteredTree;

  public AbstractTreeViewerPage2() {
  }

  @Override
  public void createContents(Composite parent) {
    int style = SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL;
    filteredTree = new FilteredTree(parent, style, createFilter(), false);
    filteredTree.setBackground(parent.getBackground());

    final TreeViewer viewer = filteredTree.getViewer();
    viewer.setContentProvider(createContentProvider(viewer));
    viewer.setLabelProvider(createLabelProvider(viewer));
    viewer.setUseHashlookup(true);
    viewer.getTree().setHeaderVisible(true);
    viewer.getTree().addDisposeListener(new DisposeListener() {
      @Override
      public void widgetDisposed(DisposeEvent e) {
        saveState();
      }
    });

    // Expand tree node on double click:
    viewer.addDoubleClickListener(new IDoubleClickListener() {
      @Override
      public void doubleClick(DoubleClickEvent e) {
        IStructuredSelection select = (IStructuredSelection) e.getSelection();
        Object o = select.getFirstElement();
        if (((ITreeContentProvider) viewer.getContentProvider()).hasChildren(o))
          viewer.setExpandedState(o, !viewer.getExpandedState(o));
      }
    });

    // Hide selection when user clicks on none selectable area:
    viewer.getTree().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseDown(MouseEvent e) {
        super.mouseDown(e);
        if (viewer.getTree().getItem(new Point(e.x, e.y)) == null)
          viewer.setSelection(StructuredSelection.EMPTY);
      }
    });

    final CellPainter painter = createCellPainter();
    valueSorter = new TreeViewerSorter(viewer) {

      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        long value1 = painter.getValueProvider().getValue(e1);
        long value2 = painter.getValueProvider().getValue(e2);
        if (value1 == value2)
          return 0;
        else
          return (value1 > value2) ? 1 : -1;
      }
    };

    createColumns(viewer);

    // Special column for painting:
    TreeViewerColumn graphCol = new TreeViewerColumn(viewer, SWT.LEFT);
    graphCol.setLabelProvider(painter);
    graphCol.getColumn().setWidth(100);
    graphCol.getColumn().addSelectionListener(valueSorter);

    for (TreeColumn column : viewer.getTree().getColumns()) {
      column.setMoveable(true);
      column.setResizable(true);
    }
    viewer.setComparator(createInitialComparator(viewer));
    restoreState();
  }

  /**
   * Gets the tree of this page.
   * 
   * @return The filtered tree.
   */
  public FilteredTree getFilteredTree() {
    return filteredTree;
  }

  /**
   * Gets the viewer.
   * 
   * @return The viewer.
   */
  public TreeViewer getViewer() {
    return filteredTree.getViewer();
  }

  /**
   * Creates the cell painting for painting the graph column.
   * 
   * @return A cell painter.
   */
  protected abstract CellPainter createCellPainter();

  /**
   * Creates the extra columns for the viewer. The graph column will be created
   * after this.
   * 
   * @param viewer The viewer.
   */
  protected abstract void createColumns(TreeViewer viewer);

  /**
   * Creates a content provider for this viewer.
   * 
   * @param viewer The viewer.
   * 
   * @return A content provider.
   */
  protected abstract ITreeContentProvider createContentProvider(
      TreeViewer viewer);

  /**
   * Creates a pattern filter for the {@link FilteredTree}.
   * 
   * @return A pattern filter.
   */
  protected abstract PatternFilter createFilter();

  /**
   * Creates a default comparator for sorting the viewer.
   * 
   * @param viewer The viewer.
   * @return A viewer comparator.
   */
  protected abstract ViewerComparator createInitialComparator(TreeViewer viewer);

  /**
   * Creates a label provider for the viewer.
   * 
   * @param viewer The viewer.
   * 
   * @return A label provider.
   */
  protected abstract ILabelProvider createLabelProvider(TreeViewer viewer);

  // TODO
  protected TreeViewerSorter getValueSorter() {
    return valueSorter;
  }

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
