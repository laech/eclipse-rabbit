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
import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preferences;
import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.DropDownAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.GroupByAction;
import rabbit.ui.internal.actions.ShowHideFilterControlAction;
import rabbit.ui.internal.pages.ResourcePageContentProvider.Category;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.CellPainter;
import rabbit.ui.internal.viewers.TreeViewerLabelSorter;
import rabbit.ui.internal.viewers.TreeViewerSorter;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.ide.IDE;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * A page for displaying time spent working on different files.
 */
public class ResourcePage extends AbstractFilteredTreePage {

  // Preference constants:
  private static final String PREF_SELECTED_CATEGORIES = "ResourcePage.SelectedCatgories";
  private static final String PREF_PAINT_CATEGORY = "ResourcePage.PaintCategory";

  private ResourcePageContentProvider contents;
  private ResourcePageTableLabelProvider labels;
  private IAccessor<FileDataDescriptor> accessor;

  public ResourcePage() {
    super();
    accessor = DataHandler.getFileDataAccessor();
  }
  
  @Override
  protected void initializeViewer(TreeViewer viewer) {
    contents = new ResourcePageContentProvider(viewer);
    labels = new ResourcePageTableLabelProvider(contents);
    viewer.setContentProvider(contents);
    viewer.setLabelProvider(labels);
    viewer.addFilter(new ViewerFilter() {
      @Override
      public boolean select(Viewer v, Object parentElement, Object element) {
        return !contents.shouldFilter(element);
      }
    });
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    IAction groupByFilesAction = newGroupByAllResourcesAction();
    IAction colorByProjectsAction = newColorByProjectsAction();
    IAction filterTreeAction = new ShowHideFilterControlAction(getFilteredTree());
    filterTreeAction.run(); // Hides the filter control

    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filterTreeAction), //
        new Separator(), //
        new ActionContributionItem(new ExpandAllAction(getViewer())),
        new ActionContributionItem(new CollapseAllAction(getViewer())),
        new Separator(), //
        new ActionContributionItem(new GroupByAction(contents,
            groupByFilesAction, // Default action 
            groupByFilesAction, // First menu item
            newGroupByFoldersAction(), //
            newGroupByProjectsAction(), //
            newGroupByDatesAndFilesAction())),
        new ActionContributionItem(new DropDownAction("Color by Projects",
            SharedImages.BRUSH, // 
            colorByProjectsAction, // Default action
            newColorByFilesAction(),
            newColorByFoldersAction(),
            colorByProjectsAction, //
            newColorByDatesAction())) };

    for (IContributionItem item : items)
      toolBar.add(item);

    return items;
  }

  @Override
  public void update(Preferences p) {
    TreePath[] expandedPaths = getViewer().getExpandedTreePaths();

    LocalDate start = LocalDate.fromCalendarFields(p.getStartDate());
    LocalDate end = LocalDate.fromCalendarFields(p.getEndDate());
    getViewer().setInput(accessor.getData(start, end));
    
    getViewer().setExpandedTreePaths(expandedPaths);
  }

  @Override
  protected CellPainter createCellPainter() {
    return new CellPainter(contents) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 136, 177, 231);
      }
    };
  }

  @Override
  protected void createColumns(TreeViewer viewer) {
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.setText("Name");
    column.setWidth(200);
    column.addSelectionListener(createInitialComparator(viewer));

    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setText("Time Spent");
    column.setWidth(150);
    column.addSelectionListener(getValueSorter());
  }

  @Override
  protected PatternFilter createFilter() {
    return new PatternFilter();
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerLabelSorter(viewer) {
      
      @Override
      public int category(Object element) {
        if (element instanceof TreeNode) {
          TreeNode node = (TreeNode) element;
          if (node.getValue() instanceof IFile)
            return 1;
          if (node.getValue() instanceof IFolder)
            return 2;
          if (node.getValue() instanceof IProject)
            return 3;
          if (node.getValue() instanceof LocalDate)
            return 4;
        }
        return super.category(element);
      }
      
      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (!(e1 instanceof TreeNode) || !(e1 instanceof TreeNode))
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

  /**
   * Action to color the dates.
   */
  private IAction newColorByDatesAction() {
    IAction action = new Action("Dates", SharedImages.CALENDAR) {
      @Override
      public void run() {
        contents.setPaintCategory(Category.DATE);
      }
    };
    return action;
  }

  /**
   * Action to color the files.
   */
  private IAction newColorByFilesAction() {
    IAction action = new Action("Files", PlatformUI.getWorkbench()
        .getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE)) {
      @Override
      public void run() {
        contents.setPaintCategory(Category.FILE);
      }
    };
    return action;
  }

  /**
   * Action to color the folders.
   */
  private IAction newColorByFoldersAction() {
    IAction action = new Action("Folders", PlatformUI.getWorkbench()
        .getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER)) {
      @Override
      public void run() {
        contents.setPaintCategory(Category.FOLDER);
      }
    };
    return action;
  }

  /**
   * Action to color the projects.
   */
  private IAction newColorByProjectsAction() {
    IAction action = new Action("Projects", PlatformUI.getWorkbench()
        .getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT)) {
      @Override
      public void run() {
        contents.setPaintCategory(Category.PROJECT);
      }
    };
    return action;
  }

  /**
   * Action to group the data by files.
   */
  private IAction newGroupByAllResourcesAction() {
    IAction action = new Action("Files", PlatformUI.getWorkbench()
        .getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FILE)) {
      @Override
      public void run() {
        contents.setSelectedCategories();
      }
    };
    return action;
  }

  /**
   * Action to group the data by projects.
   */
  private IAction newGroupByProjectsAction() {
    IAction action = new Action("Projects", PlatformUI.getWorkbench()
        .getSharedImages().getImageDescriptor(IDE.SharedImages.IMG_OBJ_PROJECT)) {
      @Override
      public void run() {
        contents.setSelectedCategories(Category.PROJECT);
      }
    };
    return action;
  }

  /**
   * Action to group the data by folders.
   */
  private IAction newGroupByFoldersAction() {
    IAction action = new Action("Folders", PlatformUI
        .getWorkbench().getSharedImages().getImageDescriptor(
            ISharedImages.IMG_OBJ_FOLDER)) {
      @Override
      public void run() {
        contents.setSelectedCategories(Category.PROJECT, Category.FOLDER);
      }
    };
    return action;
  }
  
  /**
   * Action to group the data by dates and files.
   */
  private IAction newGroupByDatesAndFilesAction() {
    IAction action = new Action("Dates", SharedImages.CALENDAR) {
      @Override
      public void run() {
        contents.setSelectedCategories(Category.DATE, Category.PROJECT, 
            Category.FOLDER, Category.FILE);
      }
    };
    return action;
  }
}
