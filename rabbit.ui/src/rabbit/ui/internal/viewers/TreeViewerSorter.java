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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.Widget;

public abstract class TreeViewerSorter extends ViewerComparator implements
    SelectionListener {

  /**
   * One of {@link SWT#NONE}, {@link SWT#UP}, {@link SWT#DOWN}.
   */
  private int sortDirection;
  private TreeViewer viewer;
  private TreeColumn selectedColumn;

  public TreeViewerSorter(TreeViewer parent) {
    sortDirection = SWT.NONE;
    selectedColumn = null;
    viewer = parent;
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
  protected TreeViewer getViewer() {
    return viewer;
  }

  protected abstract int doCompare(Viewer v, Object e1, Object e2);
}
