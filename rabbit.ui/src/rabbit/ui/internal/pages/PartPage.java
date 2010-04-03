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

import rabbit.data.access.IAccessor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.CellPainter;
import rabbit.ui.DisplayPreference;
import rabbit.ui.TableLabelComparator;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewRegistry;

import java.util.HashMap;
import java.util.Map;

/**
 * A page displays workbench part usage.
 */
public class PartPage extends AbstractTableViewerPage {

  private Map<IWorkbenchPartDescriptor, Long> dataMapping;
  private IAccessor<Map<String, Long>> dataStore;

  /**
   * Constructs a new page.
   */
  public PartPage() {
    super();
    dataStore = DataHandler.getPartDataAccessor();
    dataMapping = new HashMap<IWorkbenchPartDescriptor, Long>();
  }

  @Override
  public void createColumns(TableViewer viewer) {
    TableLabelComparator valueSorter = createValueSorterForTable(viewer);
    TableLabelComparator textSorter = new TableLabelComparator(viewer);

    int[] widths = new int[] { 200, 150 };
    int[] styles = new int[] { SWT.LEFT, SWT.RIGHT };
    String[] names = new String[] { "Name", "Usage" };
    for (int i = 0; i < names.length; i++) {
      TableColumn column = new TableColumn(viewer.getTable(), styles[i]);
      column.setText(names[i]);
      column.setWidth(widths[i]);
      column.addSelectionListener((names.length - 1 == i) ? valueSorter
          : textSorter);
    }
  }

  @Override
  public long getValue(Object o) {
    Long value = dataMapping.get(o);
    return (value == null) ? 0 : value;
  }

  @Override
  public void update(DisplayPreference p) {
    dataMapping.clear();
    setMaxValue(0);

    IViewRegistry viewReg = PlatformUI.getWorkbench().getViewRegistry();
    IEditorRegistry editReg = PlatformUI.getWorkbench().getEditorRegistry();

    Map<String, Long> data = dataStore
        .getData(p.getStartDate(), p.getEndDate());
    for (Map.Entry<String, Long> entry : data.entrySet()) {

      IWorkbenchPartDescriptor part = viewReg.find(entry.getKey());
      if (part == null) {
        part = editReg.findEditor(entry.getKey());
      }
      if (part == null) {
        part = new UndefinedWorkbenchPartDescriptor(entry.getKey());
      }

      if (entry.getValue() > getMaxValue()) {
        setMaxValue(entry.getValue());
      }
      dataMapping.put(part, entry.getValue());
    }
    getViewer().setInput(dataMapping.keySet());
  }

  @Override
  protected CellLabelProvider createCellPainter() {
    return new CellPainter(this) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 49, 132, 155);
      }
    };
  }

  @Override
  protected IContentProvider createContentProvider() {
    return new CollectionContentProvider();
  }

  @Override
  protected ITableLabelProvider createLabelProvider() {
    return new PartPageLabelProvider(this);
  }
}
