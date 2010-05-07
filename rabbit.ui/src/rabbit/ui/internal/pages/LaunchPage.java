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
import rabbit.data.access.model.LaunchDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.DropDownAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.GroupByAction;
import rabbit.ui.internal.actions.ShowHideFilterControlAction;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.CellPainter;
import rabbit.ui.internal.viewers.DelegatingStyledCellLabelProvider;
import rabbit.ui.internal.viewers.TreeViewerLabelSorter;
import rabbit.ui.internal.viewers.TreeViewerSorter;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.PatternFilter;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * Displays launch events.
 */
public class LaunchPage extends AbstractAccessorPage {
  
  // Preference constants:
  private static final String PREF_SELECTED_CATEGORIES = "LaunchPage.SelectedCatgories";
  private static final String PREF_PAINT_CATEGORY = "LaunchPage.PaintCategory";

  private LaunchPageContentProvider contents;
  private LaunchPageLabelProvider labels;
  
  /**
   * Constructs a new page.
   */
  public LaunchPage() {
    super();
  }
  
  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {

    Category[] categories = new Category[] {
    // Categories for color by drop down action:
        Category.LAUNCH, // 
        Category.LAUNCH_MODE, // 
        Category.LAUNCH_TYPE, // 
        Category.DATE };

    // Color by actions:
    IAction[] colorActions = new IAction[categories.length];
    for (int i = 0; i < colorActions.length; i++) {
      final Category cat = categories[i];
      colorActions[i] = new Action(cat.getText(), cat.getImageDescriptor()) {
        @Override
        public void run() {
          contents.setPaintCategory(cat);
        }
      };
    }

    IAction filterAction = new ShowHideFilterControlAction(getFilteredTree());
    filterAction.run();

    IAction collapse = new CollapseAllAction(getViewer());
    IAction groupByLaunches = newGroupByLaunchesAction();
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filterAction), //
        new ActionContributionItem(new DropDownAction(
            collapse.getText(), collapse.getImageDescriptor(), 
            collapse, 
            collapse,
            new ExpandAllAction(getViewer()))),
        new ActionContributionItem(new GroupByAction(contents, groupByLaunches,
            groupByLaunches, //
            newGroupByLaunchModesAction(), //
            newGroupByLaunchConfigTypesAction(), //
            newGroupByDatesAction())), //
        new ActionContributionItem(new DropDownAction( //
            "Highlight " + colorActions[0].getText(), SharedImages.BRUSH, // 
            colorActions[0], //
            colorActions)) };

    for (IContributionItem item : items)
      toolBar.add(item);

    return items;
  }

  /**
   * Creates a cell painter for launch durations.
   */
  @Override
  protected CellPainter createCellPainter() {
    return new CellPainter(contents.getLaunchDurationValueProvider()) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 49, 132, 155);
      }
    };
  }

  /**
   * Creates a cell painter for launch counts.
   * 
   * @return A cell painter.
   */
  protected CellPainter createCellPainter2() {
    return new CellPainter(contents.getLaunchCountValueProvider()) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 118, 146, 60);
      }
    };
  }

  @Override
  protected void createColumns(TreeViewer viewer) {
    TreeViewerSorter countSorter = new TreeViewerSorter(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        long count1 = contents.getLaunchCountValueProvider().getValue(e1);
        long count2 = contents.getLaunchCountValueProvider().getValue(e2);
        if (count1 == count2)
          return 0;
        else
          return (count1 > count2) ? 1 : -1;
      }
    };
    
    TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
    viewerColumn.getColumn().setText("Name");
    viewerColumn.getColumn().setWidth(180);
    viewerColumn.getColumn().addSelectionListener(createInitialComparator(viewer));
    viewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(labels, false));

    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setText("Count");
    column.setWidth(80);
    column.addSelectionListener(countSorter);

    column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.setWidth(100);
    column.addSelectionListener(countSorter);
    new TreeViewerColumn(viewer, column).setLabelProvider(createCellPainter2());

    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setWidth(120);
    column.setText("Total Duration");
    column.addSelectionListener(getValueSorter());
  }

  @Override
  protected PatternFilter createFilter() {
    return new PatternFilter();
  }

  @Override
  protected TreeViewerLabelSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerLabelSorter(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (!(e1 instanceof TreeNode) || !(e2 instanceof TreeNode))
          return super.doCompare(v, e1, e2);

        Object element1 = ((TreeNode) e1).getValue();
        Object element2 = ((TreeNode) e2).getValue();
        if (element1 instanceof LocalDate && element2 instanceof LocalDate)
          return ((LocalDate) element1).compareTo(((LocalDate) element2));
        else
          return super.doCompare(v, e1, e2);
      }
    };
  }

  @Override
  protected IAccessor<?> getAccessor() {
    return DataHandler.getAccessor(LaunchDataDescriptor.class);
  }

  @Override
  protected void initializeViewer(TreeViewer viewer) {
    contents = new LaunchPageContentProvider(viewer);
    labels = new LaunchPageLabelProvider(contents);
    viewer.setContentProvider(contents);
    viewer.setLabelProvider(labels);
  }
  
  @Override
  protected void restoreState() {
    super.restoreState();
    IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();

    // Restores the selected categories of the content provider:
    String[] categoryStr = store.getString(PREF_SELECTED_CATEGORIES).split(",");
    List<Category> cats = Lists.newArrayList();
    for (String str : categoryStr) {
      try {
        cats.add(Enum.valueOf(Category.class, str));
      } catch (IllegalArgumentException e) {
        // Ignore invalid elements.
      }
    }
    contents.setSelectedCategories(cats.toArray(new ICategory[cats.size()]));

    // Restores the paint category of the content provider:
    String paintStr = store.getString(PREF_PAINT_CATEGORY);
    try {
      Category cat = Enum.valueOf(Category.class, paintStr);
      contents.setPaintCategory(cat);
    } catch (IllegalArgumentException e) {
      // Just let the content provider use its default paint category.
    }
  }
  
  @Override
  protected void saveState() {
    super.saveState();
    IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();

    // Saves the selected categories of the content provider:
    ICategory[] categories = contents.getSelectedCategories();
    store.setValue(PREF_SELECTED_CATEGORIES, Joiner.on(",").join(categories));

    // Saves the paint category of the content provider:
    store.setValue(PREF_PAINT_CATEGORY, contents.getPaintCategory().toString());
  }

  /** Action to group data by launch dates. */
  private IAction newGroupByDatesAction() {
    final ICategory cat = Category.DATE;
    return new Action(cat.getText(), cat.getImageDescriptor()) {
      @Override
      public void run() {
        contents.setSelectedCategories(cat, Category.LAUNCH);
      }
    };
  }

  /** Action to group data by launch configuration types. */
  private IAction newGroupByLaunchConfigTypesAction() {
    final ICategory cat = Category.LAUNCH_TYPE;
    return new Action(cat.getText(), cat.getImageDescriptor()) {
      @Override
      public void run() {
        contents.setSelectedCategories(cat, Category.LAUNCH);
      }
    };
  }

  /** Action to group data by launches. */
  private IAction newGroupByLaunchesAction() {
    final ICategory cat = Category.LAUNCH;
    return new Action(cat.getText(), cat.getImageDescriptor()) {
      @Override
      public void run() {
        contents.setSelectedCategories(cat);
      }
    };
  }

  /** Action to group data by launch modes. */
  private IAction newGroupByLaunchModesAction() {
    final ICategory cat = Category.LAUNCH_MODE;
    return new Action(cat.getText(), cat.getImageDescriptor()) {
      @Override
      public void run() {
        contents.setSelectedCategories(cat, Category.LAUNCH);
      }
    };
  }
}
