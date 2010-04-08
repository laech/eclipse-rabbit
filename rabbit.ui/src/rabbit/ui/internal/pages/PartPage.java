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
import rabbit.data.access.model.PartDataDescriptor;
import rabbit.data.handler.DataHandler2;
import rabbit.ui.CellPainter;
import rabbit.ui.Preferences;
import rabbit.ui.TreeViewerSorter;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.ViewByDatesAction;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

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
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.joda.time.LocalDate;

/**
 * A page displays workbench part usage.
 */
public class PartPage extends AbstractTreeViewerPage {

  private final IAccessor2<PartDataDescriptor> accessor;
  private final PartPageContentProvider contents;
  private final PartPageNameLabelProvider nameLabels;

  /**
   * Constructs a new page.
   */
  public PartPage() {
    super();
    accessor = DataHandler2.getPartDataAccessor();
    contents = new PartPageContentProvider(this, true);
    nameLabels = new PartPageNameLabelProvider(contents);
  }

  @Override
  public void createColumns(TreeViewer viewer) {
    TreeViewerColumn column = new TreeViewerColumn(viewer, SWT.LEFT);
    column.getColumn().addSelectionListener(createInitialComparator(viewer));
    column.getColumn().setText("Name");
    column.getColumn().setWidth(200);
    column.setLabelProvider(nameLabels);

    column = new TreeViewerColumn(viewer, SWT.RIGHT);
    column.getColumn().addSelectionListener(createValueSorterForTree(viewer));
    column.getColumn().setText("Usage");
    column.getColumn().setWidth(200);
    column.setLabelProvider(new ValueColumnLabelProvider(this) {
      @Override
      public void update(ViewerCell cell) {
        super.update(cell);
        if (cell.getElement() instanceof UndefinedWorkbenchPartDescriptor)
          cell.setForeground(nameLabels.getUndefindWorkbenchPartForeground());
        else
          cell.setForeground(null);
      }
    });
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
  public long getValue(Object element) {
    if (element instanceof IWorkbenchPartDescriptor)
      return contents.getValueOfPart((IWorkbenchPartDescriptor) element);

    if (element instanceof PartDataDescriptor)
      return ((PartDataDescriptor) element).getValue();

    return 0;
  }

  @Override
  public boolean shouldPaint(Object element) {
    return !(element instanceof LocalDate);
  }

  @Override
  public void update(Preferences p) {
    setMaxValue(0);
    nameLabels.updateState();

    Object[] elements = getViewer().getExpandedElements();
    ISelection selection = getViewer().getSelection();

    LocalDate start = LocalDate.fromCalendarFields(p.getStartDate());
    LocalDate end = LocalDate.fromCalendarFields(p.getEndDate());
    getViewer().setInput(accessor.getData(start, end));
    try {
      getViewer().setExpandedElements(elements);
      getViewer().setSelection(selection);
    } catch (Exception e) {
      // Just in case something goes wrong while restoring the viewer's state
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
  protected ITreeContentProvider createContentProvider() {
    return contents;
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerSorter(viewer) {

      @Override
      protected int doCompare(Viewer v, Object x, Object y) {
        if (x instanceof LocalDate && y instanceof LocalDate) {
          return x.toString().compareTo(y.toString());

        } else if (x instanceof IWorkbenchPartDescriptor
            && y instanceof IWorkbenchPartDescriptor) {
          IWorkbenchPartDescriptor a = (IWorkbenchPartDescriptor) x;
          IWorkbenchPartDescriptor b = (IWorkbenchPartDescriptor) y;
          return a.getLabel().compareToIgnoreCase(b.getLabel());

        } else if (x instanceof PartDataDescriptor
            && y instanceof PartDataDescriptor) {
          IWorkbenchPartDescriptor a = contents.getPart((PartDataDescriptor) x);
          IWorkbenchPartDescriptor b = contents.getPart((PartDataDescriptor) y);
          return a.getLabel().compareToIgnoreCase(b.getLabel());
        }
        return 0;
      }
    };
  }
}
