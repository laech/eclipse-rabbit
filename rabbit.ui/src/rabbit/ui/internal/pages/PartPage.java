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
import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.GroupByDatesAction;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.joda.time.LocalDate;

/**
 * A page displays workbench part usage.
 */
public class PartPage extends AbstractTreeViewerPage {

  /**
   * Preference constants for displaying the data by date.
   */
  private static final String DISPLAY_BY_DATE_PREF = "PartPage.displayByDates";

  private final IAccessor2<PartDataDescriptor> accessor;
  private final PartPageContentProvider contents;
  private final PartPageLabelProvider labels;

  /**
   * Constructs a new page.
   */
  public PartPage() {
    super();
    IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
    store.setDefault(DISPLAY_BY_DATE_PREF, true);

    boolean displayByDate = store.getBoolean(DISPLAY_BY_DATE_PREF);
    contents = new PartPageContentProvider(this, displayByDate);
    labels = new PartPageLabelProvider(contents);
    accessor = DataHandler2.getPartDataAccessor();
  }

  @Override
  public void createColumns(TreeViewer viewer) {
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.addSelectionListener(createInitialComparator(viewer));
    column.setText("Name");
    column.setWidth(200);

    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.addSelectionListener(createValueSorterForTree(viewer));
    column.setText("Usage");
    column.setWidth(200);
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(new ExpandAllAction(getViewer())),
        new ActionContributionItem(new CollapseAllAction(getViewer())),
        new Separator(), // 
        new ActionContributionItem(new GroupByDatesAction(contents)) };

    for (IContributionItem item : items)
      toolBar.add(item);

    return items;
  }

  @Override
  public long getValue(Object element) {
    if (element instanceof IWorkbenchPartDescriptor)
      return contents.getValueOfPart((IWorkbenchPartDescriptor) element);

    else if (element instanceof PartDataDescriptor)
      return ((PartDataDescriptor) element).getValue();

    else
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
          return ((LocalDate) x).compareTo((LocalDate) y);

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

  @Override
  protected ITableLabelProvider createLabelProvider() {
    return labels;
  }

  @Override
  protected void saveState() {
    super.saveState();
    RabbitUI.getDefault().getPreferenceStore().setValue(DISPLAY_BY_DATE_PREF,
        contents.isDisplayingByDate());
  }
}
