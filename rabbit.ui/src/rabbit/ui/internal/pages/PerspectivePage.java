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
import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preferences;
import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.GroupByDatesAction;
import rabbit.ui.internal.viewers.CellPainter;
import rabbit.ui.internal.viewers.TreeViewerSorter;

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
import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.LocalDate;

/**
 * A page displays perspective usage.
 */
public class PerspectivePage extends AbstractTreeViewerPage {

  /**
   * Preference constants for displaying the data by date.
   */
  private static final String DISPLAY_BY_DATE_PREF = "PerspectivePage.displayByDates";

  private final IAccessor<PerspectiveDataDescriptor> accessor;
  private final PerspectivePageContentProvider contents;
  private final PerspectivePageLabelProvider labels;

  /**
   * Constructs a new page.
   */
  public PerspectivePage() {
    super();
    IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();
    store.setDefault(DISPLAY_BY_DATE_PREF, true);

    boolean displayByDate = store.getBoolean(DISPLAY_BY_DATE_PREF);
    contents = new PerspectivePageContentProvider(this, displayByDate);
    labels = new PerspectivePageLabelProvider(contents);
    accessor = DataHandler.getPerspectiveDataAccessor();
  }

  @Override
  public void createColumns(TreeViewer viewer) {
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.setText("Name");
    column.setWidth(200);
    column.addSelectionListener(createInitialComparator(viewer));

    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setText("Usage");
    column.setWidth(200);
    column.addSelectionListener(createValueSorterForTree(viewer));
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
      // Just in case something goes wrong while restoring the viewer's state
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
  protected ITreeContentProvider createContentProvider() {
    return contents;
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerSorter(viewer) {

      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (e1 instanceof LocalDate && e2 instanceof LocalDate)
          return ((LocalDate) e1).compareTo((LocalDate) e2);

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
