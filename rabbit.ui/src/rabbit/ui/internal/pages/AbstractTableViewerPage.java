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

import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.viewers.CellPainter;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;

public abstract class AbstractTableViewerPage extends AbstractValueProviderPage {

  private TableViewer viewer;
  private TableViewerColumn graphCol;

  public AbstractTableViewerPage() {
  }

  @Override
  public void createContents(Composite parent) {
    viewer = new TableViewer(parent, SWT.VIRTUAL | SWT.V_SCROLL | SWT.H_SCROLL);
    viewer.setContentProvider(createContentProvider());
    viewer.setLabelProvider(createLabelProvider());
    viewer.setUseHashlookup(true);
    viewer.getTable().setHeaderVisible(true);
    viewer.getTable().addDisposeListener(new DisposeListener() {
      @Override
      public void widgetDisposed(DisposeEvent e) {
        saveState();
      }
    });
    
    // Hide selection when user clicks on none selectable area:
    viewer.getTable().addMouseListener(new MouseAdapter() {
      @Override
      public void mouseDown(MouseEvent e) {
        super.mouseDown(e);
        if (viewer.getTable().getItem(new Point(e.x, e.y)) == null)
          viewer.setSelection(StructuredSelection.EMPTY);
      }
    });

    createColumns(viewer);

    // Special column for painting:
    graphCol = new TableViewerColumn(viewer, SWT.LEFT);
    graphCol.setLabelProvider(createCellPainter());
    graphCol.getColumn().setWidth(100);
    graphCol.getColumn()
        .addSelectionListener(createValueSorterForTable(viewer));

    for (TableColumn column : viewer.getTable().getColumns()) {
      column.setMoveable(true);
      column.setResizable(true);
    }
    restoreState();
  }

  public TableViewer getViewer() {
    return viewer;
  }

  @Override
  public boolean shouldPaint(Object element) {
    return true;
  }

  /**
   * Creates a cell painter for painting the graph cells.
   * 
   * @return A cell painter.
   */
  protected CellLabelProvider createCellPainter() {
    return new CellPainter(this);
  }

  protected abstract void createColumns(TableViewer viewer);

  protected abstract IContentProvider createContentProvider();

  protected abstract ITableLabelProvider createLabelProvider();

  /** Restores the state of the page. */
  protected void restoreState() {
    IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
    for (TableColumn column : getViewer().getTable().getColumns()) {
      int width = store.getInt(getWidthPreferenceString(column));
      if (width > 0) {
        column.setWidth(width);
      }
    }
  }

  /** Saves the state of the page. */
  protected void saveState() {
    IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
    for (TableColumn column : getViewer().getTable().getColumns()) {
      store.setValue(getWidthPreferenceString(column), column.getWidth());
    }
  }

  private String getWidthPreferenceString(TableColumn column) {
    return getClass().getSimpleName() + '.' + column.getText() + "Width";
  }

}
