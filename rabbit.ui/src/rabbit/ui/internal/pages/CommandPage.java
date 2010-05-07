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
import rabbit.data.access.model.CommandDataDescriptor;
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
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.PatternFilter;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * A page for displaying command usage.
 */
public class CommandPage extends AbstractAccessorPage {
  
  // Preference constants:
  private static final String PREF_SELECTED_CATEGORIES = "CommandPage.SelectedCatgories";
  private static final String PREF_PAINT_CATEGORY = "CommandPage.PaintCategory";

  private CommandPageContentProvider contents;
  private CommandPageLabelProvider labels;
  
  /**
   * Constructor.
   */
  public CommandPage() {
    super();
  }

  @Override
  public void createColumns(TreeViewer viewer) {
    TreeViewerColumn viewerColumn = new TreeViewerColumn(viewer, SWT.LEFT);
    viewerColumn.getColumn().setText("Name");
    viewerColumn.getColumn().setWidth(150);
    viewerColumn.getColumn().addSelectionListener(createInitialComparator(viewer));
    viewerColumn.setLabelProvider(new DelegatingStyledCellLabelProvider(labels, false));

    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.addSelectionListener(new TreeViewerLabelSorter(viewer));
    column.setText("Description");
    column.setWidth(200);

    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.addSelectionListener(getValueSorter());
    column.setText("Usage Count");
    column.setWidth(100);
  }
  
  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    ShowHideFilterControlAction filterAction = new ShowHideFilterControlAction(getFilteredTree());
    filterAction.run();
    
    final ICategory cmd = Category.COMMAND;
    final ICategory date = Category.DATE;
    
    IAction groupByCmd = new Action(cmd.getText(), cmd.getImageDescriptor()) {
      @Override public void run() {
        contents.setSelectedCategories(cmd);
      }
    };
    IAction groupByDate = new Action(date.getText(), date.getImageDescriptor()) {
      @Override public void run() {
        contents.setSelectedCategories(date, cmd);
      }
    };
    IAction colorByCmd = new Action(cmd.getText(), cmd.getImageDescriptor()) {
      @Override public void run() {
        contents.setPaintCategory(cmd);
      }
    };
    IAction colorByDate = new Action(date.getText(), date.getImageDescriptor()) {
      @Override public void run() {
        contents.setPaintCategory(date);
      }
    };
    
    IAction collapse = new CollapseAllAction(getViewer());
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filterAction),
        new ActionContributionItem(new DropDownAction(
            collapse.getText(), collapse.getImageDescriptor(), 
            collapse, 
            collapse,
            new ExpandAllAction(getViewer()))),
        new ActionContributionItem(new GroupByAction(contents, groupByCmd,
            groupByCmd, groupByDate)),
        new ActionContributionItem(new DropDownAction(
            "Highlight " + colorByCmd.getText(), SharedImages.BRUSH, 
            colorByCmd, colorByCmd, colorByDate))};

    for (IContributionItem item : items)
      toolBar.add(item);

    return items;
  }

  @Override
  protected CellPainter createCellPainter() {
    return new CellPainter(contents);
  }

  @Override
  protected PatternFilter createFilter() {
    return new PatternFilter();
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerLabelSorter(viewer) {

      @Override
      protected int doCompare(Viewer v, Object e1, Object e2) {
        if (e1 instanceof TreeNode)
          e1 = ((TreeNode) e1).getValue();
        if (e2 instanceof TreeNode)
          e2 = ((TreeNode) e2).getValue();
        
        if (e1 instanceof LocalDate && e2 instanceof LocalDate)
          return ((LocalDate) e1).compareTo((LocalDate) e2);
        else
          return super.doCompare(v, e1, e2);
      }
    };
  }

  @Override
  protected IAccessor<?> getAccessor() {
    return DataHandler.getAccessor(CommandDataDescriptor.class);
  }

  @Override
  protected void initializeViewer(TreeViewer viewer) {
    contents = new CommandPageContentProvider(viewer);
    labels = new CommandPageLabelProvider(contents);
    viewer.setLabelProvider(labels);
    viewer.setContentProvider(contents);
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
}
