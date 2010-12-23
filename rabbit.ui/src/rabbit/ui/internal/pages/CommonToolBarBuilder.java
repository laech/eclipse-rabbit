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

import rabbit.ui.internal.actions.CategoryAction2;
import rabbit.ui.internal.actions.CollapseAllAction;
import rabbit.ui.internal.actions.ColorByAction;
import rabbit.ui.internal.actions.DropDownAction;
import rabbit.ui.internal.actions.ExpandAllAction;
import rabbit.ui.internal.actions.GroupByAction2;
import rabbit.ui.internal.actions.PaintCategoryAction2;
import rabbit.ui.internal.actions.ShowHideFilterControlAction;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider2;
import rabbit.ui.internal.util.TreePathValueProvider;

import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.Lists;

import org.eclipse.jface.action.ActionContributionItem;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.ui.dialogs.FilteredTree;

import java.util.List;

/**
 * TODO review and test me
 * Builder to build common tool bar actions for pages. The order of the actions are predefined by
 * this builder, that means the order of the items will be consistent, regardless of the order of
 * the methods called.
 */
public final class CommonToolBarBuilder {

  private IAction filterAction;
  private IAction treeAction;
  private List<IAction> groupByActions;
  private List<IAction> colorByActions;
  private ICategoryProvider2 categoryProvider;
  private TreePathValueProvider valueProvider;

  public CommonToolBarBuilder() {
    groupByActions = Lists.newLinkedList();
    colorByActions = Lists.newLinkedList();
  }

  /**
   * @throws IllegalStateException if {@link #enableColorByAction(TreePathValueProvider)} has not
   *         been called.
   */
  public CommonToolBarBuilder addColorByAction(ICategory visualCategory) {
    checkState(valueProvider != null);
    colorByActions.add(new PaintCategoryAction2(valueProvider, visualCategory));
    return this;
  }

  /**
   * @throws IllegalStateException if {@link #enableGroupByAction(ICategoryProvider2)} has not been
   *         called.
   */
  public CommonToolBarBuilder addGroupByAction(ICategory... categories) {
    checkState(categoryProvider != null);
    groupByActions.add(new CategoryAction2(categoryProvider, categories));
    return this;
  }
  
  /**
   * Builds a collection of actions from the configuration.
   * @return a collection of actions.
   */
  public List<IContributionItem> build() {
    List<IContributionItem> items = Lists.newArrayList();
    if (filterAction != null) {
      items.add(new ActionContributionItem(filterAction));
    }
    if (treeAction != null) {
      items.add(new ActionContributionItem(treeAction));
    }
    if (categoryProvider != null) {
      items.add(new ActionContributionItem(
          new GroupByAction2(categoryProvider, groupByActions.toArray(new IAction[0]))));
    }
    if (valueProvider != null) {
      items.add(new ActionContributionItem(
          new ColorByAction(colorByActions.toArray(new IAction[0]))));
    }
    return items;
  }
  
  /**
   * Enables the color-by action group.
   * @param valueProvider the provider to accept the action events. 
   * @return this
   * @see TreePathValueProvider#getVisualCategory()
   * @see TreePathValueProvider#setVisualCategory(ICategory)
   */
  public CommonToolBarBuilder enableColorByAction(TreePathValueProvider valueProvider) {
    this.valueProvider = valueProvider;
    return this;
  }
  
  /**
   * Enables the show/hide filter control action.
   * @param tree the tree hosting the filter control.
   * @param hideControl true to hide the control by default.
   * @return this
   */
  public CommonToolBarBuilder enableFilterControlAction(FilteredTree tree, boolean hideControl) {
    filterAction = new ShowHideFilterControlAction(tree, hideControl);
    return this;
  }
  
  /**
   * Enables the group-by action group.
   * @param categoryProvider the object to receive the action events.
   * @return this
   * @see ICategoryProvider2#setSelected(ICategory...)
   * @see ICategoryProvider2#getSelected()
   */
  public CommonToolBarBuilder enableGroupByAction(ICategoryProvider2 categoryProvider) {
    this.categoryProvider = categoryProvider;
    return this;
  }
  
  /**
   * Enables the collapse/expand actions for the given viewer.
   * @param viewer the viewer to receive the action events.
   * @return this
   * @see TreeViewer#expandAll()
   * @see TreeViewer#collapseAll()
   */
  public CommonToolBarBuilder enableTreeAction(TreeViewer viewer) {
    treeAction = new DropDownAction(new CollapseAllAction(viewer), new ExpandAllAction(viewer));
    return this;
  }
}
