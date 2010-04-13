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
import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.handler.DataHandler2;
import rabbit.ui.CellPainter;
import rabbit.ui.Preferences;
import rabbit.ui.TreeLabelComparator;
import rabbit.ui.TreeViewerSorter;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.DropDownAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.FilteredTreeAction;
import rabbit.ui.internal.actions.GroupByAction;
import rabbit.ui.internal.pages.ResourcePageContentProvider.Category;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.ide.IDE;
import org.joda.time.LocalDate;

/**
 * A page for displaying time spent working on different files.
 */
public class ResourcePage extends AbstractTreeViewerPage2 {

  private ResourcePageContentProvider contents;
  private ResourcePageTableLabelProvider labels;
  private IAccessor2<FileDataDescriptor> accessor;

  public ResourcePage() {
    super();
    accessor = DataHandler2.getFileDataAccessor();
    contents = new ResourcePageContentProvider(this);
    labels = new ResourcePageTableLabelProvider(contents);
  }

  @Override
  public void createContents(Composite parent) {
    super.createContents(parent);
    getViewer().addFilter(new ViewerFilter() {
      @Override
      public boolean select(Viewer v, Object parentElement, Object element) {
        return !contents.shouldFilter(element);
      }
    });
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    IAction groupByAllResourcesAction = newGroupByAllResourcesAction();
    IAction colorByProjectsAction = newColorByProjectsAction();
    IAction filterTreeAction = new FilteredTreeAction(getFilteredTree());
    filterTreeAction.run(); // Hides the filter control

    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filterTreeAction), //
        new Separator(), //
        new ActionContributionItem(new ExpandAllAction(getViewer())),
        new ActionContributionItem(new CollapseAllAction(getViewer())),
        new Separator(), //
        new ActionContributionItem(new GroupByAction(contents,
            groupByAllResourcesAction, // default action
            newGroupByProjectsAction(), //
            newGroupByProjectsAndFoldersAction(), // 
            groupByAllResourcesAction)),
        new ActionContributionItem(new DropDownAction("Color by Projects",
            SharedImages.BRUSH, // 
            colorByProjectsAction, // default action
            newColorByDatesAction(), //
            colorByProjectsAction, //
            newColorByFoldersAction(), // 
            newColorByFilesAction())) };

    for (IContributionItem item : items)
      toolBar.add(item);

    return items;
  }

  @Override
  public void update(Preferences p) {
    Object[] elements = getViewer().getExpandedElements();

    LocalDate start = LocalDate.fromCalendarFields(p.getStartDate());
    LocalDate end = LocalDate.fromCalendarFields(p.getEndDate());
    getViewer().setInput(accessor.getData(start, end));
    try {
      getViewer().setExpandedElements(elements);
    } catch (IllegalArgumentException e) {
      // Just in case some of the elements are no longer valid.
    }
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
  protected ITreeContentProvider createContentProvider(TreeViewer viewer) {
    return contents;
  }

  @Override
  protected PatternFilter createFilter() {
    return new PatternFilter();
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeLabelComparator(viewer) {
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
  protected ILabelProvider createLabelProvider(TreeViewer viewer) {
    return labels;
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
   * Action to group the data by projects, then by folders, then files.
   */
  private IAction newGroupByAllResourcesAction() {
    IAction action = new Action("All Resources", PlatformUI.getWorkbench()
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
   * Action to group the data by projects, then by folders.
   */
  private IAction newGroupByProjectsAndFoldersAction() {
    IAction action = new Action("Projects and Folders", PlatformUI
        .getWorkbench().getSharedImages().getImageDescriptor(
            ISharedImages.IMG_OBJ_FOLDER)) {
      @Override
      public void run() {
        contents.setSelectedCategories(Category.PROJECT, Category.FOLDER);
      }
    };
    return action;
  }
}
