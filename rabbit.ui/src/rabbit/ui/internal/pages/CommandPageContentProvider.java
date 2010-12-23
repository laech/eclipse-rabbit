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
package rabbit.ui.internal.pages;

import rabbit.data.access.model.ICommandData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.ICategoryProvider;
import rabbit.ui.internal.viewers.TreeNodes;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;

import org.eclipse.core.commands.Command;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Collections;

/**
 * Acceptable input for this content provider is an instance of 
 * {@link IProvider}.
 * <p>
 * This {@link ICategoryProvider} supports the following {@link ICategory}:
 * <ul>
 * <li>{@link Category#DATE}</li>
 * <li>{@link Category#COMMAND}</li>
 * <li>{@link Category#WORKSPACE}</li>
 * </ul>
 * </p>
 */
class CommandPageContentProvider extends AbstractValueContentProvider {
  
  /**
   * A provider that provides collection of {@link ICommandData} 
   * objects. 
   */
  static interface IProvider extends rabbit.ui.IProvider<ICommandData> {
  }

  /**
   * Constructs a new content provider for the given viewer.
   * @param treeViewer The viewer of this content provider.
   */
  CommandPageContentProvider(TreeViewer treeViewer) {
    super(treeViewer);
  }
  
  @Override
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.doInputChanged(viewer, oldInput, newInput);
    getRoot().setChildren(null);
    
    Collection<ICommandData> data = Collections.emptyList();
    if (newInput instanceof IProvider) {
      data = ((IProvider) newInput).get();
    }

    for (ICommandData des : data) {
      TreeNode node = getRoot();
      for (ICategory category : getSelectedCategories()) {
        
        if (Category.DATE == category) {
          node = TreeNodes.findOrAppend(node, des.get(ICommandData.DATE));
        } else if (Category.COMMAND == category) {
          Command command = des.get(ICommandData.COMMAND);
          node = TreeNodes.findOrAppend(node, command);
        } else if (Category.WORKSPACE == category) {
          node = TreeNodes.findOrAppend(node, des.get(ICommandData.WORKSPACE));
        }
      }
      
      // Cast count to long for this content provider to work:
      TreeNodes.appendToParent(node, des.get(ICommandData.COUNT).longValue(), true);
    }
  }
  
  @Override
  protected ICategory[] getAllSupportedCategories() {
    return new ICategory[] { Category.DATE, Category.COMMAND, Category.WORKSPACE };
  }

  @Override
  protected ICategory getDefaultPaintCategory() {
    return Category.COMMAND;
  }

  @Override
  protected ICategory[] getDefaultSelectedCategories() {
    return new ICategory[] { Category.COMMAND };
  }

  @Override
  protected ImmutableMap<ICategory, Predicate<Object>> initializeCategorizers() {
    return ImmutableMap.<ICategory, Predicate<Object>> builder()
        .put(Category.DATE,    Predicates.instanceOf(LocalDate.class))
        .put(Category.COMMAND, Predicates.instanceOf(Command.class))
        .put(Category.WORKSPACE, Predicates.instanceOf(WorkspaceStorage.class))
        .build();
  }
}
