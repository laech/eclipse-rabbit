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
import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.data.handler.DataHandler2;
import rabbit.ui.CellPainter;
import rabbit.ui.Preferences;
import rabbit.ui.TreeViewerSorter;
import rabbit.ui.internal.SharedImages;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;

/**
 * A page displays perspective usage.
 */
public class PerspectivePage extends AbstractTreeViewerPage {

  private final IAccessor2<PerspectiveDataDescriptor> accessor;
  private final PerspectivePageContentProvider contents;
  private final PerspectivePageNameLabelProvider labels;

  private final IAction expandAllAction;
  private final IAction collapseAllAction;
  private final IAction viewByDayAction;

  public PerspectivePage() {
    super();
    accessor = DataHandler2.getPerspectiveDataAccessor();
    contents = new PerspectivePageContentProvider(this);
    labels = new PerspectivePageNameLabelProvider(contents);

    expandAllAction = createExpandAllAction();
    collapseAllAction = createCollapseAllAction();
    viewByDayAction = createViewByDateAction();
  }

  @Override
  public void createColumns(TreeViewer viewer) {
    TreeViewerSorter nameSorter = createInitialComparator(viewer);

    TreeViewerColumn column = new TreeViewerColumn(viewer, SWT.LEFT);
    column.setLabelProvider(new PerspectivePageNameLabelProvider(contents));
    column.getColumn().setText("Name");
    column.getColumn().setWidth(200);
    column.getColumn().addSelectionListener(nameSorter);

    column = new TreeViewerColumn(viewer, SWT.RIGHT);
    column.setLabelProvider(new PerspectivePageDurationLabelProvider(contents));
    column.getColumn().setText("Usage");
    column.getColumn().setWidth(200);
    column.getColumn().addSelectionListener(createValueSorterForTree(viewer));

    viewer.setComparator(nameSorter);
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    IContributionItem expand = new ActionContributionItem(expandAllAction);
    toolBar.add(expand);

    IContributionItem collapse = new ActionContributionItem(collapseAllAction);
    toolBar.add(collapse);

    Separator sep = new Separator();
    toolBar.add(sep);

    IContributionItem view = new ActionContributionItem(viewByDayAction);
    toolBar.add(view);

    return new IContributionItem[] { expand, collapse, sep, view };
  }

  @Override
  public long getValue(Object o) {
    if (o instanceof IPerspectiveDescriptor)
      return contents.getValueOfPerspective((IPerspectiveDescriptor) o);

    if (o instanceof PerspectiveDataDescriptor)
      return ((PerspectiveDataDescriptor) o).getValue();

    return 0;
  }

  @Override
  public boolean shouldPaint(Object element) {
    return !(element instanceof LocalDate);
  }

  @Override
  public void update(Preferences p) {
    setMaxValue(0);
    labels.updateState();

    Object[] elements = getViewer().getExpandedElements();
    ISelection selection = getViewer().getSelection();

    LocalDate start = LocalDate.fromCalendarFields(p.getStartDate());
    LocalDate end = LocalDate.fromCalendarFields(p.getEndDate());
    getViewer().setInput(accessor.getData(start, end));
    try {
      getViewer().setExpandedElements(elements);
      getViewer().setSelection(selection);
    } catch (Exception e) {
      // Just in case something went wrong while restoring the
      // viewer's state
    }
  }

  @Override
  protected CellLabelProvider createCellPainter() {
    return new CellPainter(this) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 218, 176, 0);
      }
    };
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerSorter(viewer) {

      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (e1 instanceof LocalDate && e2 instanceof LocalDate)
          return e1.toString().compareToIgnoreCase(e2.toString());

        if (e1 instanceof IPerspectiveDescriptor
            && e2 instanceof IPerspectiveDescriptor) {
          IPerspectiveDescriptor p1 = (IPerspectiveDescriptor) e1;
          IPerspectiveDescriptor p2 = (IPerspectiveDescriptor) e2;
          return p1.getLabel().compareToIgnoreCase(p2.getLabel());
        }

        if (e1 instanceof PerspectiveDataDescriptor
            && e2 instanceof PerspectiveDataDescriptor) {
          PerspectiveDataDescriptor des1 = (PerspectiveDataDescriptor) e1;
          PerspectiveDataDescriptor des2 = (PerspectiveDataDescriptor) e2;
          return contents.getPerspective(des1).getLabel().compareToIgnoreCase(
              contents.getPerspective(des2).getLabel());
        }

        return 0;
      }
    };
  }

  @Override
  protected ITreeContentProvider createContentProvider() {
    return contents;
  }

  private IAction createCollapseAllAction() {
    ImageDescriptor img = PlatformUI.getWorkbench().getSharedImages()
        .getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL);
    return new Action("Collapse All", img) {
      @Override
      public void run() {
        getViewer().collapseAll();
      }
    };
  }

  private IAction createExpandAllAction() {
    return new Action("Expand All", SharedImages.EXPAND_ALL) {
      @Override
      public void run() {
        getViewer().expandAll();
      }
    };
  }

  private IAction createViewByDateAction() {
    IAction viewByDay = new Action("View by Date", IAction.AS_CHECK_BOX) {
      @Override
      public void run() {
        contents.setDisplayByDate(isChecked());
        expandAllAction.setEnabled(isEnabled());
        collapseAllAction.setEnabled(isEnabled());
      }
    };
    viewByDay.setImageDescriptor(SharedImages.TIME_HIERARCHY);
    viewByDay.setChecked(contents.isDisplayingByDate());
    return viewByDay;
  }
}
