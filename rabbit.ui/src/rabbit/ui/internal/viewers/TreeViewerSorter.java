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
package rabbit.ui.internal.viewers;

import static com.google.common.base.Preconditions.checkNotNull;

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
 * Subclasses need to override {@link #doCompare(Viewer, Object, Object)} to
 * do the actual comparing of the elements. 
 * </p>
 */
public abstract class TreeViewerSorter extends ViewerComparator implements
    SelectionListener {

  /**
   * One of {@link SWT#NONE}, {@link SWT#UP}, {@link SWT#DOWN}.
   */
  private int sortDirection;
  private TreeViewer viewer;
  private TreeColumn selectedColumn;

  /**
   * Constructor.
   * @param parent The parent viewer.
   * @throws NullPointerException If argument is null.
   */
  public TreeViewerSorter(TreeViewer parent) {
    viewer = checkNotNull(parent);
    sortDirection = SWT.NONE;
    selectedColumn = null;
  }

  @Override
  public int compare(Viewer v, Object e1, Object e2) {
    int cat1 = category(e1);
    int cat2 = category(e2);

    int value = 0;
    if (cat1 != cat2) {
      value = cat1 - cat2;
    } else {
      value = doCompare(v, e1, e2);
    }

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

  /**
   * Gets the viewer registered.
   * @return The viewer.
   */
  public TreeViewer getViewer() {
    return viewer;
  }

  /**
   * Compares the given elements. Subclasses overriding this method does not
   * need to care about sorting descendingly, this will be handled by the super
   * class.
   * 
   * @param v The viewer.
   * @param e1 The first element.
   * @param e2 The second element.
   * @return A negative value if the first element is consider less than the
   *         second element, a zero if the first element is consider equal to
   *         the second element, a positive value if the first element is
   *         consider greater than the second element,
   */
  protected abstract int doCompare(Viewer v, Object e1, Object e2);
}
