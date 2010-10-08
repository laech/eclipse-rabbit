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

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;
import rabbit.ui.internal.viewers.TreeNodes;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableMap;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.LocalDate;

import java.util.Collection;
import java.util.Collections;

/**
 * Content provider for a {@code TreeViewer}. Accepts input as
 * {@link PerspectivePageContentProvider.IProvider}.
 * <p>
 * The following {@link ICategory}s are supported:
 * <ul>
 * <li>{@link Category#DATE}</li>
 * <li>{@link Category#PERSPECTIVE}</li>
 * </ul>
 * </p>
 */
class PerspectivePageContentProvider extends AbstractValueContentProvider {

  /**
   * Provides objects of type {@link PerspectiveDataDescriptor}.
   */
  static interface IProvider extends rabbit.ui.IProvider<PerspectiveDataDescriptor> {
  }

  /**
   * Constructor.
   * @param viewer The viewer.
   * @throws NullPointerException If argument is null.
   */
  PerspectivePageContentProvider(TreeViewer viewer) {
    super(viewer);
  }

  @Override
  protected void doInputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.doInputChanged(viewer, oldInput, newInput);
    getRoot().setChildren(null);

    Collection<PerspectiveDataDescriptor> data = Collections.emptySet();
    if (newInput instanceof IProvider) {
      data = ((IProvider) newInput).get();
    }
    
    for (PerspectiveDataDescriptor des : data) {
      TreeNode node = getRoot();
      for (ICategory cat : selectedCategories) {
        if (Category.DATE == cat) {
          node = TreeNodes.findOrAppend(node, des.getDate());
          
        } else if (Category.PERSPECTIVE == cat) {
          IPerspectiveDescriptor persp = des.findPerspective();
          if (persp == null) {
            persp = new UndefinedPerspectiveDescriptor(des.getPerspectiveId());
          }
          node = TreeNodes.findOrAppend(node, persp);
        }
      }
      TreeNodes.appendToParent(node, des.getDuration().getMillis(), true);
    }
  }

  @Override
  protected ICategory[] getAllSupportedCategories() {
    return new ICategory[] { Category.DATE, Category.PERSPECTIVE };
  }

  @Override
  protected ICategory getDefaultPaintCategory() {
    return Category.PERSPECTIVE;
  }

  @Override
  protected ICategory[] getDefaultSelectedCategories() {
    return new ICategory[] { Category.PERSPECTIVE };
  }

  @Override
  protected ImmutableMap<ICategory, Predicate<Object>> initializeCategorizers() {
    return ImmutableMap.<ICategory, Predicate<Object>> builder()
        .put(Category.DATE, Predicates.instanceOf(LocalDate.class))
        .put(Category.PERSPECTIVE, Predicates.instanceOf(IPerspectiveDescriptor.class))
        .build();
  }
}
