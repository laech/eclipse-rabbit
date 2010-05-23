/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.ui.internal.pages.java;

import rabbit.data.access.IAccessor;
import rabbit.data.access.model.JavaDataDescriptor;
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
import rabbit.ui.internal.viewers.DelegatingStyledCellLabelProvider;
import rabbit.ui.internal.viewers.TreeViewerLabelSorter;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.eclipse.jdt.ui.JavaElementComparator;
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
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.PatternFilter;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * Page to display time spent on Java elements.
 */
public class JavaPage extends AbstractAccessorPage {

  // Preference constants:
  private static final String PREF_SELECTED_CATEGORIES = "JavaPage.SelectedCatgories";
  private static final String PREF_PAINT_CATEGORY = "JavaPage.PaintCategory";
  
  private final IAccessor<JavaDataDescriptor> accessor;
  private JavaPageContentProvider contentProvider;
  private JavaPageLabelProvider labelProvider;
  
  public JavaPage() {
    accessor = DataHandler.getAccessor(JavaDataDescriptor.class);
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    ShowHideFilterControlAction filter = new ShowHideFilterControlAction(getFilteredTree());
    filter.run();
    
    IAction collapse = new CollapseAllAction(getViewer());
    
    ICategory cat = JavaCategory.DATE;
    IAction groupByDate = new Action(cat.getText(), cat.getImageDescriptor()) {
      @Override public void run() {
        contentProvider.setSelectedCategories(
            JavaCategory.DATE,
            JavaCategory.PROJECT,
            JavaCategory.PACKAGE_ROOT,
            JavaCategory.PACKAGE,
            JavaCategory.TYPE_ROOT,
            JavaCategory.MEMBER);
      }
    };
    
    cat = JavaCategory.PROJECT;
    IAction groupByProj = new Action(cat.getText(), cat.getImageDescriptor()) {
      @Override public void run() {
        contentProvider.setSelectedCategories(
            JavaCategory.PROJECT,
            JavaCategory.PACKAGE_ROOT,
            JavaCategory.PACKAGE,
            JavaCategory.TYPE_ROOT,
            JavaCategory.MEMBER);
      }
    };
    
    ICategory[] categories = new ICategory[] {
        JavaCategory.METHOD,
        JavaCategory.TYPE,
        JavaCategory.TYPE_ROOT,
        JavaCategory.PACKAGE,
        JavaCategory.PACKAGE_ROOT,
        JavaCategory.PROJECT,
        JavaCategory.DATE
    };
    IAction[] hightlightActions = new IAction[categories.length];
    for (int i = 0; i < hightlightActions.length; i++) {
      final ICategory category = categories[i];
      hightlightActions[i] = new Action(category.getText(), category.getImageDescriptor()) {
        @Override public void run() {
          contentProvider.setPaintCategory(category);
        }
      };
    }
    
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filter),
        new ActionContributionItem(new DropDownAction(
            collapse.getText(), collapse.getImageDescriptor(), 
            collapse, 
            collapse,
            new ExpandAllAction(getViewer()))),
        new ActionContributionItem(new GroupByAction(contentProvider, 
            groupByProj, 
            groupByProj, 
            groupByDate)),
        new ActionContributionItem(new DropDownAction(
            "Highlight " + hightlightActions[0].getText(), SharedImages.BRUSH, 
            hightlightActions[0], 
            hightlightActions)),
    };
    
    for (IContributionItem item : items) {
      toolBar.add(item);
    }
    
    return items;
  }

  @Override
  protected CellPainter createCellPainter() {
    return new CellPainter(contentProvider);
  }

  @Override
  protected void createColumns(TreeViewer viewer) {
    TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
    viewerColumn.getColumn().setText("Name");
    viewerColumn.getColumn().setWidth(200);
    viewerColumn.getColumn().addSelectionListener(new TreeViewerLabelSorter(viewer) {
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (e1 instanceof TreeNode) {
          e1 = ((TreeNode) e1).getValue();
        }
        if (e2 instanceof TreeNode) {
          e2 = ((TreeNode) e2).getValue();
        }
        
        if (e1 instanceof LocalDate && e2 instanceof LocalDate) {
          return ((LocalDate) e1).compareTo((LocalDate) e2);
        }
        return super.doCompare(v, e1, e2);
      }
    });
    viewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(labelProvider, false));
    // TOOD
    
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.addSelectionListener(getValueSorter());
    column.setWidth(100);
    column.setText("Time Spent");
  }

  @Override
  protected PatternFilter createFilter() {
    return new PatternFilter();
  }

  @Override
  protected ViewerComparator createInitialComparator(TreeViewer viewer) {
    return new JavaElementComparator() {
      
      @Override
      public int category(Object element) {
        if (element instanceof TreeNode) {
          element = ((TreeNode) element).getValue();
        }
        return super.category(element);
      }
      
      @Override
      public int compare(Viewer viewer, Object e1, Object e2) {
        if (e1 instanceof TreeNode) {
          e1 = ((TreeNode) e1).getValue();
        }
        if (e2 instanceof TreeNode) {
          e2 = ((TreeNode) e2).getValue();
        }
        return super.compare(viewer, e1, e2);
      }
    };
  }

  @Override
  protected IAccessor<?> getAccessor() {
    return accessor;
  }

  @Override
  protected void initializeViewer(TreeViewer viewer) {
    contentProvider = new JavaPageContentProvider(viewer);
    labelProvider = new JavaPageLabelProvider(contentProvider);
    viewer.setContentProvider(contentProvider);
    viewer.setLabelProvider(labelProvider);
  }
  
  @Override
  protected void restoreState() {
    super.restoreState();
    IPreferenceStore store = RabbitUI.getDefault().getPreferenceStore();

    // Restores the selected categories of the content provider:
    String[] categoryStr = store.getString(PREF_SELECTED_CATEGORIES).split(",");
    List<JavaCategory> cats = Lists.newArrayList();
    for (String str : categoryStr) {
      try {
        cats.add(Enum.valueOf(JavaCategory.class, str));
      } catch (IllegalArgumentException e) {
        // Ignore invalid elements.
      }
    }
    contentProvider.setSelectedCategories(cats.toArray(new ICategory[cats.size()]));

    // Restores the paint category of the content provider:
    String paintStr = store.getString(PREF_PAINT_CATEGORY);
    try {
      JavaCategory cat = Enum.valueOf(JavaCategory.class, paintStr);
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

}
