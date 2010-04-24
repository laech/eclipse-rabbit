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

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;
import rabbit.ui.internal.viewers.TreeNodes;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewRegistry;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Content provider for a {@code TreeViewer}. Acceptable input is {@code
 * Collection<PartDataDescriptor>}. *
 * <p>
 * The following {@link ICategory}s are supported:
 * <ul>
 * <li>{@link Category#DATE}</li>
 * <li>{@link Category#WORKBENCH_TOOL}</li>
 * </ul>
 * </p>
 */
public class PartPageContentProvider extends AbstractValueContentProvider {

  /**
   * Constructs a new content provider for the given viewer.
   * @param treeViewer The viewer.
   * @throws NullPointerException If argument is null.
   */
  public PartPageContentProvider(TreeViewer treeViewer) {
    super(treeViewer);
  }

  @Override
  protected ICategory[] getAllSupportedCategories() {
    return new ICategory[] { Category.DATE, Category.WORKBENCH_TOOL };
  }

  @Override
  protected ICategory getDefaultPaintCategory() {
    return Category.WORKBENCH_TOOL;
  }

  @Override
  protected ICategory[] getDefaultSelectedCategories() {
    return new ICategory[] { Category.WORKBENCH_TOOL };
  }

  @Override
  protected ImmutableMap<ICategory, Predicate<Object>> initializeCategorizers() {
    return ImmutableMap.<ICategory, Predicate<Object>> builder()
        .put(Category.DATE, Predicates.instanceOf(LocalDate.class))
        .put(Category.WORKBENCH_TOOL, Predicates.instanceOf(IWorkbenchPartDescriptor.class))
        .build();
  }
  
  @SuppressWarnings("unchecked")
  @Override
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.doInputChanged(viewer, oldInput, newInput);
    getRoot().setChildren(null);
    
    Collection<PartDataDescriptor> data = (Collection<PartDataDescriptor>) newInput;
    IEditorRegistry editors = PlatformUI.getWorkbench().getEditorRegistry();
    IViewRegistry views = PlatformUI.getWorkbench().getViewRegistry();
    for (PartDataDescriptor des : data) {
      
      TreeNode node = getRoot();
      for (ICategory cat : selectedCategories) {
        if (Category.DATE == cat) {
          node = TreeNodes.findOrAppend(node, des.getDate());
          
        } else if (Category.WORKBENCH_TOOL == cat) {
          IWorkbenchPartDescriptor part = editors.findEditor(des.getPartId());
          if (part == null) 
            part = views.find(des.getPartId());
          if (part == null)
            part = new UndefinedWorkbenchPartDescriptor(des.getPartId());
          
          node = TreeNodes.findOrAppend(node, part);
        }
      }
      TreeNodes.appendToParent(node, des.getValue());
    }
  }
}
