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
import rabbit.data.access.model.PartDataDescriptor;
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
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.dialogs.PatternFilter;
import org.joda.time.LocalDate;

import java.util.List;

/**
 * A page displays workbench part usage.
 */
public class PartPage extends InternalPage<PartDataDescriptor>
    implements PartPageContentProvider.IProvider {

  // Preference constants:
  private static final String PREF_SELECTED_CATEGORIES = "PartPage.SelectedCatgories";
  private static final String PREF_PAINT_CATEGORY = "PartPage.PaintCategory";

  private PartPageContentProvider contents;
  private PartPageLabelProvider labels;
  
  /**
   * Constructs a new page.
   */
  public PartPage() {
    super();
  }

  @Override
  public void createColumns(TreeViewer viewer) {
    TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
    column.setText("Name");
    column.setWidth(200);
    column.addSelectionListener(createInitialComparator(viewer));

    column = new TreeColumn(viewer.getTree(), SWT.RIGHT);
    column.addSelectionListener(getValueSorter());
    column.setText("Usage");
    column.setWidth(200);
  }

  @Override
  public IContributionItem[] createToolBarItems(IToolBarManager toolBar) {
    final ICategory date = Category.DATE;
    final ICategory part = Category.WORKBENCH_TOOL;
    
    IAction colorByDate = new Action(date.getText(), date.getImageDescriptor()) {
      @Override public void run() {
        contents.setPaintCategory(date);
      }
    };
    IAction colorByPart = new Action(part.getText(), part.getImageDescriptor()) {
      @Override public void run() {
        contents.setPaintCategory(part);
      }
    };
    IAction groupByDate = new Action(date.getText(), date.getImageDescriptor()) {
      @Override public void run() {
        contents.setSelectedCategories(date, part);
      }
    };
    IAction groupByPart = new Action(part.getText(), part.getImageDescriptor()) {
      @Override public void run() {
        contents.setSelectedCategories(part);
      }
    };
    IAction hideEditors = new Action("Hide Editors", IAction.AS_CHECK_BOX) {
      @Override public void run() {
        contents.setHideEditors(isChecked());
      }
    };
    hideEditors.setChecked(contents.isHidingEditors());
    hideEditors.setImageDescriptor(SharedImages.EDITOR);
    
    IAction hideViews = new Action("Hide Views", IAction.AS_CHECK_BOX) {
      @Override public void run() {
        contents.setHideViews(isChecked());
      }
    };
    hideViews.setChecked(contents.isHidingViews());
    hideViews.setImageDescriptor(SharedImages.VIEW);
    
    ShowHideFilterControlAction filter = new ShowHideFilterControlAction(getFilteredTree());
    filter.run();
    
    IAction collapse = new CollapseAllAction(getViewer());
    IContributionItem[] items = new IContributionItem[] {
        new ActionContributionItem(filter),
        new ActionContributionItem(new DropDownAction(
            collapse.getText(), collapse.getImageDescriptor(), 
            collapse, 
            collapse,
            new ExpandAllAction(getViewer()))),
        new ActionContributionItem(new GroupByAction(contents, 
            groupByPart, 
            groupByPart, 
            groupByDate)), 
        new ActionContributionItem(new DropDownAction(
            "Highlight " + colorByPart.getText(), SharedImages.BRUSH, 
            colorByPart, 
            colorByPart, 
            colorByDate)),
        new ActionContributionItem(hideViews),
        new ActionContributionItem(hideEditors)
        };

    for (IContributionItem item : items)
      toolBar.add(item);

    return items;
  }

  @Override
  protected CellPainter createCellPainter() {
    return new CellPainter(contents) {
      @Override
      protected Color createColor(Display display) {
        return new Color(display, 49, 132, 155);
      }
    };
  }
  
  @Override
  protected PatternFilter createFilter() {
    return new PatternFilter();
  }

  @Override
  protected TreeViewerSorter createInitialComparator(TreeViewer viewer) {
    return new TreeViewerLabelSorter(viewer) {

      @Override
      protected int doCompare(Viewer v, Object x, Object y) {
        if (x instanceof TreeNode) x = ((TreeNode) x).getValue();
        if (y instanceof TreeNode) y = ((TreeNode) y).getValue();
        
        if (x instanceof LocalDate && y instanceof LocalDate)
          return ((LocalDate) x).compareTo((LocalDate) y);
        else
          return super.doCompare(v, x, y);
      }
    };
  }

  @Override
  protected IAccessor<PartDataDescriptor> getAccessor() {
    return DataHandler.getAccessor(PartDataDescriptor.class);
  }

  @Override
  protected void initializeViewer(TreeViewer viewer) {
    contents = new PartPageContentProvider(viewer);
    labels = new PartPageLabelProvider(contents);
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
}
