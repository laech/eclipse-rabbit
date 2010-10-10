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
package rabbit.ui.viewers;

import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

import java.util.Collection;
import java.util.Iterator;

import javax.annotation.Nullable;

/**
 * Represents a content provider that accepts [{@link TreeNode}s as input
 * elements, and can filter the {@link TreeNode}s base on the value that they
 * are holding ({@link TreeNode#getValue()}).
 * <p>
 * For example, creating a content provider using
 * 
 * <pre>
 *    new FilteredTreeNodeContentProvider(String.class, Integer.class)
 * </pre>
 * will create a content provider that will filter out all {@link TreeNode}s
 * that are holding {@code String}s or {@code Integer}s as their values.
 * </p>
 * <p>
 * Only the unfiltered children of a parent will be returned by
 * {@link #getChildren(Object)}. If all children of a parent node are filtered,
 * {@link #getChildren(Object)} will return an empty array, and
 * {@link #hasChildren(Object)} will return false.
 * </p>
 * <p>
 * The difference between using a {@link ViewerFilter} and this content provider
 * is that a {@link ViewerFilter} hides the filtered elements, but this class
 * will prevent the filtered element from being added to the tree in the first
 * place. Also if all elements of a parent node are filtered, when using a
 * {@link ViewerFilter} the tree will still display a plus/minus sign beside the
 * parent node, when using this class that won't happen.
 * </p>
 */
public class FilteredTreeNodeContentProvider implements ITreeContentProvider {
  
  protected static final Object[] EMPTY_ARRAY = new Object[0];
  
  private final Collection<Predicate<Object>> filters;
  
  /**
   * Constructs a new content provider.
   * 
   * @param elementFilters The filters for filtering elements.
   *          {@link TreeNode#getValue()} will be passed to these filters to
   *          determine whether the element should be filtered.
   */
  @SuppressWarnings("unchecked")
  public FilteredTreeNodeContentProvider(Predicate<?>... elementFilters) {
    filters = ImmutableSet.of((Predicate<Object>[]) elementFilters);
  }
  
  @Override
  public void dispose() {
  }

  @Override
  public Object[] getChildren(@Nullable Object parent) {
    if (parent instanceof TreeNode) {
      TreeNode[] children = ((TreeNode) parent).getChildren();
      if (children != null) {
        return filter(Lists.newArrayList(children)).toArray();
      }
    }
    return EMPTY_ARRAY;
  }
  
  @Override
  public Object[] getElements(@Nullable Object input) {
    return getChildren(input);
  }
  
  /**
   * @return The filters to filter elements.
   */
  public Collection<Predicate<Object>> getFilters() {
    return filters;
  }

  @Override
  public Object getParent(@Nullable Object element) {
    if (element instanceof TreeNode) {
      return ((TreeNode) element).getParent();
    }
    return null;
  }

  @Override
  public boolean hasChildren(@Nullable Object element) {
    Object[] children = getChildren(element);
    if (getFilters().isEmpty() && children.length > 0) {
      return true;
    }
    
    for (Object child : children) {
      for (Predicate<Object> filter : getFilters()) {
        if (!filter.apply(child)) {
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public void inputChanged(@Nullable Viewer viewer, 
                           @Nullable Object oldInput, 
                           @Nullable Object newInput) {
  }

  /**
   * Filters the given collection, the filtered elements will be removed from
   * the collection.
   * 
   * @param nodes The collection to be filtered.
   * @return The collection after it has been filtered.
   */
  private Collection<TreeNode> filter(Collection<TreeNode> nodes) {
    for (Iterator<TreeNode> it = nodes.iterator(); it.hasNext(); ) {
      TreeNode node = it.next();
      for (Predicate<Object> filter : getFilters()) {
        if (filter.apply(node.getValue())) {
          it.remove();
          break;
        }
      }
    }
    return nodes;
  }

}
