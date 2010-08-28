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
package rabbit.ui.internal.pages.mylyn;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.DropDownAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.GroupByAction;
import rabbit.ui.internal.actions.ShowHideFilterControlAction;
import rabbit.ui.internal.pages.AbstractAccessorPage;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.CellPainter;
import rabbit.ui.internal.viewers.DeepPatternFilter;
import rabbit.ui.internal.viewers.DelegatingStyledCellLabelProvider;
import rabbit.ui.internal.viewers.TreeViewerLabelSorter;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.preference.IPreferenceStore;
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
 * A page for displaying time spent working on different tasks and resources.
 */
public class TaskPage extends AbstractAccessorPage {
  
  // Preference constants:
  private static final String PREF_SELECTED_CATEGORIES = "TaskPage.SelectedCatgories";
  private static final String PREF_PAINT_CATEGORY = "TaskPage.PaintCategory";

  private TaskPageContentProvider contentProvider;
  private TaskPageDecoratingLabelProvider labelProvider;
  
  public TaskPage() {
    super();
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    ShowHideFilterControlAction filterAction = new ShowHideFilterControlAction(getFilteredTree());
    filterAction.run(); // Hides the filter control
    
    ICategory[] categories = new ICategory[] {
      MylynCategory.FILE, MylynCategory.FOLDER, MylynCategory.PROJECT, MylynCategory.TASK, MylynCategory.DATE  
    };
    IAction[] colorByActions = new IAction[categories.length];
    for (int i = 0; i < colorByActions.length; i++) {
      final ICategory cat = categories[i];
      colorByActions[i] = new Action(cat.getText(), cat.getImageDescriptor()) {
        @Override
        public void run() {
          contentProvider.setPaintCategory(cat);
        }
      };
    }
    IAction defaultColorAction = colorByActions[3];
    IAction defaultGroupAction = newGroupByTasksAction();
    
    IAction collapse = new CollapseAllAction(getViewer());
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filterAction),
        new ActionContributionItem(new DropDownAction(
            collapse.getText(), collapse.getImageDescriptor(), 
            collapse, 
            collapse,
            new ExpandAllAction(getViewer()))),
        new ActionContributionItem(new GroupByAction(contentProvider, 
            defaultGroupAction,
            defaultGroupAction,
            newGroupByDatesAction())),
        new ActionContributionItem(new DropDownAction(
            "Highlight " + defaultColorAction.getText(), SharedImages.BRUSH, 
            defaultColorAction, 
            colorByActions))
    };
    
    for (IContributionItem item : items)
      toolBar.add(item);
    
    return items;
  }
  
  @Override
  protected CellPainter createCellPainter() {
    return new CellPainter(contentProvider) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 75, 172, 98);
      }
    };
  }
  
  @Override
  protected void createColumns(final TreeViewer viewer) {
    TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
    viewerColumn.getColumn().setText("Name");
    viewerColumn.getColumn().setWidth(300);
    viewerColumn.getColumn().addSelectionListener(createInitialComparator(viewer));
    viewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider, false));
    
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setText("Time Spent");
    column.setWidth(100);
    column.addSelectionListener(getValueSorter());
  }
  
  @Override
  protected PatternFilter createFilter() {
    return new DeepPatternFilter();
  }

  @Override
  protected TreeViewerLabelSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerLabelSorter(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (e1 instanceof LocalDate && e2 instanceof LocalDate) {
          LocalDate date1 = (LocalDate) e1;
          LocalDate date2 = (LocalDate) e2;
          return date1.compareTo(date2);
        }
        return super.doCompare(v, e1, e2);
      }
    };
  }

  @Override
  protected IAccessor<?> getAccessor() {
    return DataHandler.getAccessor(TaskFileDataDescriptor.class);
  }

  @Override
  protected void initializeViewer(TreeViewer viewer) {
    contentProvider = new TaskPageContentProvider(viewer);
    labelProvider = new TaskPageDecoratingLabelProvider(contentProvider);
    viewer.setContentProvider(contentProvider);
    viewer.setLabelProvider(labelProvider);
  }
  
  @Override
  protected void restoreState() {
    super.restoreState();
    IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();

    // Restores the selected categories of the content provider:
    String[] categoryStr = store.getString(PREF_SELECTED_CATEGORIES).split(",");
    List<MylynCategory> cats = Lists.newArrayList();
    for (String str : categoryStr) {
      try {
        cats.add(Enum.valueOf(MylynCategory.class, str));
      } catch (IllegalArgumentException e) {
        // Ignore invalid elements.
      }
    }
    contentProvider.setSelectedCategories(cats.toArray(new ICategory[cats.size()]));

    // Restores the paint category of the content provider:
    String paintStr = store.getString(PREF_PAINT_CATEGORY);
    try {
      MylynCategory cat = Enum.valueOf(MylynCategory.class, paintStr);
      contentProvider.setPaintCategory(cat);
    } catch (IllegalArgumentException e) {
      // Just let the content provider use its default paint category.
    }
  }

  @Override
  protected void saveState() {
    super.saveState();
    IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();

    // Saves the selected categories of the content provider:
    ICategory[] categories = contentProvider.getSelectedCategories();
    store.setValue(PREF_SELECTED_CATEGORIES, Joiner.on(",").join(categories));

    // Saves the paint category of the content provider:
    store.setValue(PREF_PAINT_CATEGORY, contentProvider.getPaintCategory().toString());
  }

  private IAction newGroupByDatesAction() {
    final ICategory[] cats = new ICategory[] { 
      MylynCategory.DATE, MylynCategory.TASK, MylynCategory.PROJECT, MylynCategory.FOLDER, MylynCategory.FILE };
    
    IAction action = new Action(cats[0].getText(), cats[0].getImageDescriptor()) {
      @Override
      public void run() {
        contentProvider.setSelectedCategories(cats);
      }
    };
    return action;
  }

  private IAction newGroupByTasksAction() {
    final ICategory[] cats = new ICategory[] { 
      MylynCategory.TASK, MylynCategory.PROJECT, MylynCategory.FOLDER, MylynCategory.FILE };
    
    IAction action = new Action(cats[0].getText(), cats[0].getImageDescriptor()) {
      @Override
      public void run() {
        contentProvider.setSelectedCategories(cats);
      }
    };
    return action;
  }
}
