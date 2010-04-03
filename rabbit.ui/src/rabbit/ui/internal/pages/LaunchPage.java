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
import rabbit.data.access.model.LaunchDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.CellPainter;
import rabbit.ui.DisplayPreference;
import rabbit.ui.TreeLabelComparator;
import rabbit.ui.CellPainter.IValueProvider;
import rabbit.ui.internal.SharedImages;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import java.util.Set;

/**
 * Displays launch events.
 */
public class LaunchPage extends AbstractTreeViewerPage {

  private final IAccessor<Set<LaunchDescriptor>> accessor;
  private int maxCount = 0;

  private IAction collapseAllAction = new Action("Collapse All") {
    @Override
    public void run() {
      getViewer().collapseAll();
    }
  };

  private IAction expandAllAction = new Action("Expand All") {
    @Override
    public void run() {
      getViewer().expandAll();
    }
  };

  /**
   * Constructs a new page.
   */
  public LaunchPage() {
    accessor = DataHandler.getLaunchDataAccessor();
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    expandAllAction.setImageDescriptor(SharedImages.EXPAND_ALL);
    IContributionItem expandAll = new ActionContributionItem(expandAllAction);
    toolBar.add(expandAll);

    ISharedImages images = PlatformUI.getWorkbench().getSharedImages();
    ImageDescriptor img = images
        .getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL);
    collapseAllAction.setImageDescriptor(img);
    IContributionItem collapseAll = new ActionContributionItem(
        collapseAllAction);
    toolBar.add(collapseAll);

    return new IContributionItem[] { expandAll, collapseAll };
  }

  @Override
  public long getValue(Object element) {
    if (element instanceof LaunchDescriptor) {
      return ((LaunchDescriptor) element).getTotalDuration();
    }
    return 0;
  }

  @Override
  public void update(DisplayPreference preference) {
    Set<LaunchDescriptor> data = accessor.getData(preference.getStartDate(),
        preference.getEndDate());

    maxCount = 0;
    setMaxValue(0);
    for (LaunchDescriptor des : data) {
      if (des.getTotalDuration() > getMaxValue()) {
        setMaxValue(des.getTotalDuration());
      }
      if (des.getCount() > maxCount) {
        maxCount = des.getCount();
      }
    }

    Object[] elements = getViewer().getExpandedElements();
    getViewer().setInput(data);
    try {
      getViewer().setExpandedElements(elements);
    } catch (Exception e) {
      // Ignore.
    }
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
  protected void createColumns(TreeViewer viewer) {
    TreeLabelComparator textSorter = new TreeLabelComparator(viewer);
    TreeLabelComparator valueSorter = createValueSorterForTree(viewer);
    TreeLabelComparator countSorter = new TreeLabelComparator(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (e1 instanceof LaunchDescriptor && e2 instanceof LaunchDescriptor) {
          LaunchDescriptor des1 = (LaunchDescriptor) e1;
          LaunchDescriptor des2 = (LaunchDescriptor) e2;
          if (des1.getCount() == des2.getCount())
            return 0;
          else
            return des1.getCount() > des2.getCount() ? 1 : -1;
        }
        return super.doCompare(v, e1, e2);
      }
    };

    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.setText("Name");
    column.setWidth(180);
    column.addSelectionListener(textSorter);

    column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.setText("Mode");
    column.setWidth(80);
    column.addSelectionListener(textSorter);

    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setText("Count");
    column.setWidth(80);
    column.addSelectionListener(countSorter);

    final TreeViewerColumn countGraphColumn = new TreeViewerColumn(viewer,
        SWT.LEFT);
    countGraphColumn.getColumn().setWidth(100);
    countGraphColumn.getColumn().addSelectionListener(countSorter);
    countGraphColumn.setLabelProvider(new CellPainter(new IValueProvider() {

      @Override
      public int getColumnWidth() {
        return countGraphColumn.getColumn().getWidth();
      }

      @Override
      public long getMaxValue() {
        return maxCount;
      }

      @Override
      public long getValue(Object element) {
        if (element instanceof LaunchDescriptor)
          return ((LaunchDescriptor) element).getCount();

        return 0;
      }

      @Override
      public boolean shouldPaint(Object element) {
        return element instanceof LaunchDescriptor;
      }
    }) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 118, 146, 60);
      }
    });

    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setWidth(120);
    column.setText("Total Duration");
    column.addSelectionListener(valueSorter);
  }

  @Override
  protected ViewerComparator createComparator(TreeViewer viewer) {
    return new TreeLabelComparator(viewer) {
      @Override
      public int compare(Viewer v, Object e1, Object e2) {
        if (e1 instanceof LaunchDescriptor && e2 instanceof LaunchDescriptor) {
          LaunchDescriptor des1 = (LaunchDescriptor) e1;
          LaunchDescriptor des2 = (LaunchDescriptor) e2;
          return des1.getLaunchName().compareTo(des2.getLaunchName());
        }
        return super.compare(v, e1, e2);
      }
    };
  }

  @Override
  protected ITreeContentProvider createContentProvider() {
    return new LaunchPageContentProvider();
  }

  @Override
  protected ITableLabelProvider createLabelProvider() {
    return new LaunchPageLabelProvider();
  }

}
