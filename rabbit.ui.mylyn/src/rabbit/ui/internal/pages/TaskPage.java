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
import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.Preferences;
import rabbit.ui.internal.RabbitUI;
import rabbit.ui.internal.SharedImages;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.DropDownAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.GroupByAction;
import rabbit.ui.internal.actions.ShowHideFilterControlAction;
import rabbit.ui.internal.pages.AbstractFilteredTreePage;
import rabbit.ui.internal.pages.TaskPageContentProvider.Category;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.CellPainter;
import rabbit.ui.internal.viewers.TreeViewerLabelSorter;

import com.google.common.base.Joiner;
import com.google.common.collect.Lists;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
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
public class TaskPage extends AbstractFilteredTreePage {
  
  // Preference constants:
  private static final String PREF_SELECTED_CATEGORIES = "TaskPage.SelectedCatgories";
  private static final String PREF_PAINT_CATEGORY = "TaskPage.PaintCategory";

  private IAccessor<TaskFileDataDescriptor> accessor;
  private TaskPageContentProvider contentProvider;
  private TaskPageDecoratingLabelProvider labelProvider;

  public TaskPage() {
    super();
    accessor = DataHandler.getTaskFileDataAccessor();
  }
  
  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    ShowHideFilterControlAction filterAction = new ShowHideFilterControlAction(getFilteredTree());
    filterAction.run(); // Hides the filter control
    
    ICategory[] categories = new ICategory[] {
      Category.FILE, Category.FOLDER, Category.PROJECT, Category.TASK, Category.DATE  
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
    
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filterAction),
        new Separator(),
        new ActionContributionItem(new ExpandAllAction(getViewer())),
        new ActionContributionItem(new CollapseAllAction(getViewer())),
        new Separator(),
        new ActionContributionItem(new GroupByAction(contentProvider, 
            defaultGroupAction,
            defaultGroupAction,
            newGroupByDatesAction())),
        new ActionContributionItem(new DropDownAction(
            "Color by " + defaultColorAction.getText(), SharedImages.BRUSH, 
            defaultColorAction, 
            colorByActions))
    };
    
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
    return new CellPainter(contentProvider) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 75, 172, 98);
      }
    };
  }
  
//  private class P extends BaseLabelProvider implements IStyledLabelProvider, IFontProvider, IColorProvider {
//    
//    private final TaskPageLabelProvider provider = new TaskPageLabelProvider();
//    
//    @Override
//    public void dispose() {
//      super.dispose();
//      provider.dispose();
//    }
//
//    @Override
//    public Image getImage(Object element) {
//      return provider.getImage(element);
//    }
//
//    @Override
//    public StyledString getStyledText(Object element) {
//      String string = provider.getText(element);
//      return (string != null ) ? new StyledString(string) : new StyledString("");
//    }
//
//    @Override
//    public Font getFont(Object element) {
//      return provider.getFont(element);
//    }
//
//    @Override
//    public Color getBackground(Object element) {
//      return provider.getBackground(element);
//    }
//
//    @Override
//    public Color getForeground(Object element) {
//      return provider.getForeground(element);
//    }
//    
//  }
  
  @Override
  protected void createColumns(final TreeViewer viewer) {
//    final DecoratingStyledCellLabelProvider p = new DecoratingStyledCellLabelProvider(new P(), new TreeNodeDecorator(), null);
//    p.addListener(new ILabelProviderListener() {
//      
//      @Override
//      public void labelProviderChanged(LabelProviderChangedEvent event) {
//        if (!viewer.getTree().isVisible())
//          return;
//        
//        Object[] elements = event.getElements();
//        if (elements == null) {
//          viewer.refresh();
//        } else {
//          for (Object element : elements) {
//            TreeNode node = null;
//            if (element instanceof TreeNode) {
//              node = (TreeNode) element;
//            } else {
//              node = TreeNodes.findChildRecursively(contentProvider.getRoot(), element);
//            }
//            if (node != null) {
//              viewer.update(node, null);
//            }
//            System.out.print(element + " ");
//            System.out.println(element.getClass());
//          }
//        }
//        
//      }
//    });
//    TreeViewerColumn vc = new TreeViewerColumn(viewer, SWT.RIGHT);
//    vc.getColumn().setWidth(200);
//    vc.setLabelProvider(p);
    
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.setText("Name");
    column.setWidth(300);
    column.addSelectionListener(createInitialComparator(viewer));
    
    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.setText("Time Spent");
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
  protected void initializeViewer(TreeViewer viewer) {
    contentProvider = new TaskPageContentProvider(viewer);
    labelProvider = new TaskPageDecoratingLabelProvider(contentProvider);
    viewer.setContentProvider(contentProvider);
    viewer.setLabelProvider(labelProvider);
    viewer.addFilter(new ViewerFilter() {
      @Override
      public boolean select(Viewer viewer, Object parentElement, Object element) {
        return !contentProvider.shouldFilter(element);
      }
    });
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
    contentProvider.setSelectedCategories(cats.toArray(new ICategory[cats.size()]));

    // Restores the paint category of the content provider:
    String paintStr = store.getString(PREF_PAINT_CATEGORY);
    try {
      Category cat = Enum.valueOf(Category.class, paintStr);
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
      Category.DATE, Category.TASK, Category.PROJECT, Category.FOLDER, Category.FILE };
    
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
      Category.TASK, Category.PROJECT, Category.FOLDER, Category.FILE };
    
    IAction action = new Action(cats[0].getText(), cats[0].getImageDescriptor()) {
      @Override
      public void run() {
        contentProvider.setSelectedCategories(cats);
      }
    };
    return action;
  }
}
