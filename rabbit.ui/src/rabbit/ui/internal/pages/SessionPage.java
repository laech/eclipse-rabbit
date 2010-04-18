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

import rabbit.data.access.IAccessor2;
import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.CellPainter;
import rabbit.ui.Preferences;
import rabbit.ui.TableViewerSorter;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TableColumn;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * A page displaying how much time is spent on using Eclipse every day.
 */
public class SessionPage extends AbstractTableViewerPage {

  private final IAccessor2<SessionDataDescriptor> accessor;
  private final SessionPageLabelProvider labels;

  /** Constructs a new page. */
  public SessionPage() {
    accessor = DataHandler.getSessionDataAccessor();
    labels = new SessionPageLabelProvider();
  }

  @Override
  public long getValue(Object o) {
    if (o instanceof SessionDataDescriptor)
      return ((SessionDataDescriptor) o).getValue();
    else
      return 0;
  }

  @Override
  public void update(Preferences p) {
    setMaxValue(0);
    labels.updateState();

    LocalDate start = LocalDate.fromCalendarFields(p.getStartDate());
    LocalDate end = LocalDate.fromCalendarFields(p.getEndDate());
    Collection<SessionDataDescriptor> data = accessor.getData(start, end);
    for (SessionDataDescriptor des : data) {
      if (des.getValue() > getMaxValue())
        setMaxValue(des.getValue());
    }
    getViewer().setInput(data);
  }

  @Override
  protected CellLabelProvider createCellPainter() {
    return new CellPainter(this) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 208, 145, 60);
      }
    };
  }

  @Override
  protected void createColumns(TableViewer viewer) {
    TableViewerSorter sorter = new TableViewerSorter(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (e1 instanceof SessionDataDescriptor
            && e2 instanceof SessionDataDescriptor) {
          SessionDataDescriptor des1 = (SessionDataDescriptor) e1;
          SessionDataDescriptor des2 = (SessionDataDescriptor) e2;
          return des1.getDate().compareTo(des2.getDate());
        }
        return 0;
      }
    };
    
    TableColumn column = new TableColumn(viewer.getTable(), SWT.LEFT);
    column.setText("Date");
    column.setWidth(200);
    column.addSelectionListener(sorter);
    
    column = new TableColumn(viewer.getTable(), SWT.RIGHT);
    column.setText("Duration");
    column.setWidth(150);
    column.addSelectionListener(createValueSorterForTable(viewer));
    
    getViewer().setComparator(sorter);
  }
  
  @Override
  protected ITreeContentProvider createContentProvider() {
    return new CollectionContentProvider();
  }

  @Override
  protected ITableLabelProvider createLabelProvider() {
    return new SessionPageLabelProvider();
  }
}
