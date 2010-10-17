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
import rabbit.ui.internal.actions.ShowHideFilterControlAction;
import rabbit.ui.internal.viewers.CellPainter;
import rabbit.ui.internal.viewers.DeepPatternFilter;
import rabbit.ui.internal.viewers.DelegatingStyledCellLabelProvider;
import rabbit.ui.internal.viewers.TreeNodes;
import rabbit.ui.internal.viewers.TreeViewerLabelSorter;
import rabbit.ui.internal.viewers.TreeViewerSorter;

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

/**
 * A page displaying how much time is spent on using Eclipse every day.
 */
public class SessionPage extends InternalPage<SessionDataDescriptor> 
    implements SessionPageContentProvider.IProvider {

  private SessionPageLabelProvider labels;
  private SessionPageContentProvider contents;

  /** Constructs a new page. */
  public SessionPage() {
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    ShowHideFilterControlAction filter = 
        new ShowHideFilterControlAction(getFilteredTree());
    filter.run();
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filter)
    };
    
    for (IContributionItem item : items) {
      toolBar.add(item);
    }
    
    return items;
  }
  
  @Override
  protected CellPainter createCellPainter() {
    return new CellPainter(contents) {
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
    return new DeepPatternFilter();
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerLabelSorter(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        e1 = TreeNodes.getObject(e1);
        e2 = TreeNodes.getObject(e2);
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
    contents = new SessionPageContentProvider(viewer);
    labels = new SessionPageLabelProvider(contents);
    viewer.setLabelProvider(labels);
    viewer.setContentProvider(contents);
  }

  @Override
  protected IAccessor<SessionDataDescriptor> getAccessor() {
    return DataHandler.getAccessor(SessionDataDescriptor.class);
  }

}
