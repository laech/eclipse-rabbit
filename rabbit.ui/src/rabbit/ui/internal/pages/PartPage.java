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
import rabbit.ui.TreeLabelComparator;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.ViewByDatesAction;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.joda.time.LocalDate;

/**
 * A page displays workbench part usage.
 */
public class PartPage extends AbstractTreeViewerPage {

  private final IAccessor2<PartDataDescriptor> accessor;
  private final PartPageContentProvider contents;

  /**
   * Constructs a new page.
   */
  public PartPage() {
    super();
    accessor = DataHandler2.getPartDataAccessor();
    contents = new PartPageContentProvider(this, true);
  }

  @Override
  public void createColumns(TreeViewer viewer) {
    TreeLabelComparator valueSorter = createValueSorterForTree(viewer);
    TreeLabelComparator textSorter = new TreeLabelComparator(viewer);

    int[] widths = new int[] { 200, 150 };
    int[] styles = new int[] { SWT.LEFT, SWT.RIGHT };
    String[] names = new String[] { "Name", "Usage" };
    for (int i = 0; i < names.length; i++) {
      TreeColumn column = new TreeColumn(viewer.getTree(), styles[i]);
      column.setText(names[i]);
      column.setWidth(widths[i]);
      column.addSelectionListener((names.length - 1 == i) ? valueSorter
          : textSorter);
    }
    getViewer().setLabelProvider(createLabelProvider());
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
    return 0;
  }

  @Override
  public void update(Preferences p) {
    setMaxValue(0);
    LocalDate start = LocalDate.fromCalendarFields(p.getStartDate());
    LocalDate end = LocalDate.fromCalendarFields(p.getEndDate());
    getViewer().setInput(accessor.getData(start, end));
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

  protected ITableLabelProvider createLabelProvider() {
    return new PartPageLabelProvider(this);
  }

  @Override
  protected ViewerComparator createInitialComparator(TreeViewer viewer) {
    return new TreeLabelComparator(viewer);
  }
}
