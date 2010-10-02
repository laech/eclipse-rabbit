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
import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preference;
import rabbit.ui.internal.actions.ShowHideFilterControlAction;
import rabbit.ui.internal.viewers.CellPainter;
import rabbit.ui.internal.viewers.DelegatingStyledCellLabelProvider;
import rabbit.ui.internal.viewers.TreeViewerLabelSorter;
import rabbit.ui.internal.viewers.TreeViewerSorter;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.PatternFilter;

import java.util.Collection;

/**
 * A page displaying how much time is spent on using Eclipse every day.
 */
public class SessionPage extends AbstractFilteredTreePage 
    implements IValueProvider {

  private final IAccessor<SessionDataDescriptor> accessor;
  private SessionPageLabelProvider labels;
  private long maxValue;

  /** Constructs a new page. */
  public SessionPage() {
    accessor = DataHandler.getAccessor(SessionDataDescriptor.class);
    maxValue = 0;
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    ShowHideFilterControlAction filter = new ShowHideFilterControlAction(getFilteredTree());
    filter.run();
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filter) };
    
    for (IContributionItem item : items)
      toolBar.add(item);
    
    return items;
  }

  @Override
  public long getMaxValue() {
    return maxValue;
  }

  @Override
  public long getValue(Object o) {
    if (o instanceof SessionDataDescriptor)
      return ((SessionDataDescriptor) o).getDuration().getMillis();
    else
      return 0;
  }

  @Override
  public boolean shouldPaint(Object element) {
    return true;
  }

  @Override
  public Job updateJob(final Preference p) {
    return AbstractAccessorPage.newUpdateJob(getViewer(), p, accessor, Job.SHORT);
  }
  
  @Override
  protected CellPainter createCellPainter() {
    return new CellPainter(this) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 208, 145, 60);
      }
    };
  }

  @Override
  protected void createColumns(TreeViewer viewer) {
    TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
    viewerColumn.getColumn().setText("Name");
    viewerColumn.getColumn().setWidth(200);
    viewerColumn.getColumn().addSelectionListener(createInitialComparator(viewer));
    viewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(labels, false));
    
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setText("Duration");
    column.setWidth(150);
    column.addSelectionListener(getValueSorter());
  }

  @Override
  protected PatternFilter createFilter() {
    return new PatternFilter();
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerLabelSorter(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (e1 instanceof SessionDataDescriptor
            && e2 instanceof SessionDataDescriptor) {
          SessionDataDescriptor des1 = (SessionDataDescriptor) e1;
          SessionDataDescriptor des2 = (SessionDataDescriptor) e2;
          return des1.getDate().compareTo(des2.getDate());
        }
        return super.doCompare(v, e1, e2);
      }
    };
  }

  @Override
  protected void initializeViewer(TreeViewer viewer) {
    labels = new SessionPageLabelProvider();
    viewer.setLabelProvider(labels);
    viewer.setContentProvider(new CollectionContentProvider() {
      @SuppressWarnings("unchecked")
      @Override
      public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        super.inputChanged(viewer, oldInput, newInput);
        if (newInput == null) {
          return;
        }
        maxValue = 0;
        Collection<SessionDataDescriptor> data = (Collection<SessionDataDescriptor>) newInput;
        for (SessionDataDescriptor des : data) {
          if (des.getDuration().getMillis() > maxValue) {
            maxValue = des.getDuration().getMillis();
          }
        }
      }
    });
  }

}
