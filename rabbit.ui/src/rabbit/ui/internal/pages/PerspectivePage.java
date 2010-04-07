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
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.ViewByDatesAction;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
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
import org.joda.time.LocalDate;

/**
 * A page displays perspective usage.
 */
public class PerspectivePage extends AbstractTreeViewerPage {

  private final IAccessor2<PerspectiveDataDescriptor> accessor;
  private final PerspectivePageContentProvider contents;
  private final PerspectivePageNameLabelProvider labels;

  public PerspectivePage() {
    super();
    accessor = DataHandler2.getPerspectiveDataAccessor();
    contents = new PerspectivePageContentProvider(this, true);
    labels = new PerspectivePageNameLabelProvider(contents);
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
    IContributionItem expand = new ActionContributionItem(new ExpandAllAction(
        getViewer()));
    toolBar.add(expand);

    IContributionItem collapse = new ActionContributionItem(
        new CollapseAllAction(getViewer()));
    toolBar.add(collapse);

    Separator sep = new Separator();
    toolBar.add(sep);

    IContributionItem viewByDates = new ActionContributionItem(
        new ViewByDatesAction(contents));
    toolBar.add(viewByDates);

    return new IContributionItem[] { expand, collapse, sep, viewByDates };
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
}
