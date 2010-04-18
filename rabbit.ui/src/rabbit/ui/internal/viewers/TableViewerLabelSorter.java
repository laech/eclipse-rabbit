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

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TableColumn;
// TODO clean up all the sorters
/**
 * <p>
 * A comparator for sorting the a {@link TableViewer} when the user is clicked
 * on a column.
 * </p>
 * <p>
 * To register a table column for sorting, simply call the column's
 * {@link TableColumn#addSelectionListener(TableColumnComparator)} method and
 * pass in an instance of this class. An instance of this class can be shared by
 * multiple columns of the same viewer.
 * </p>
 * <p>
 * The viewer must have a {@link ITableLabelProvider}.
 * </p>
 * 
 * @see SelectionListener
 * @see ITableLabelProvider
 * @see TreeViewer#setLabelProvider(ITableLabelProvider)
 */
public class TableViewerLabelSorter extends TableViewerSorter {

  public TableViewerLabelSorter(TableViewer parent) {
    super(parent);
  }

  @Override
  protected int doCompare(Viewer v, Object e1, Object e2) {
    IBaseLabelProvider provider = getViewer().getLabelProvider();

    String s1 = null;
    String s2 = null;
    if (provider instanceof ITableLabelProvider) {
      int index = 0;
      if (getSelectedColumn() != null) {
        index = getViewer().getTable().indexOf(getSelectedColumn());
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
