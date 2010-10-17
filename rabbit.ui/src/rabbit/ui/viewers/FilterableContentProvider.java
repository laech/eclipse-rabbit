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

import static com.google.common.base.Predicates.not;
import static com.google.common.base.Predicates.or;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.ViewerFilter;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * Represents a content provider for {@link TreeViewer}s that can filter
 * elements.
 * <p>
 * The difference between using a {@link ViewerFilter} and this content provider
 * is that a {@link ViewerFilter} hides the filtered elements, but this class
 * will prevent the filtered element from being added to the tree in the first
 * place. Also if all elements of a parent node are filtered, when using a
 * {@link ViewerFilter} the tree will still display a plus/minus sign beside the
 * parent node, when using this class that won't happen.
 * </p>
 */
public abstract class FilterableContentProvider implements
    ITreePathContentProvider {

  private static final Object[] EMPTY_ARRAY = new Object[0];

  /** Immutable set of filters. */
  private final Set<Predicate<Object>> filters;

  /**
   * Constructor.
   * 
   * @param elementFilters
   *          The filters to filter unwanted elements.
   */
  @SuppressWarnings("unchecked")
  public FilterableContentProvider(Predicate<?>... elementFilters) {
    filters = ImmutableSet.of((Predicate<Object>[]) elementFilters);
  }

  @Override
  public final Object[] getChildren(TreePath parentPath) {
    return filter(doGetChildren(parentPath));
  }

  @Override
  public final Object[] getElements(Object inputElement) {
    return filter(doGetElements(inputElement));
  }

  /**
   * @return The filters to filter unwanted elements.
   */
  public Set<Predicate<Object>> getFilters() {
    return filters;
  }

  @Override
  public final boolean hasChildren(TreePath path) {
    return getChildren(path).length > 0;
  }

  /**
   * Gets the children of the given parent path. Subclasses should return the
   * unfiltered children, filtering will be done by the superclass.
   * 
   * @param parentPath
   *          The parent path.
   * @return The children of the parent path, not filtered.
   */
  protected abstract Object[] doGetChildren(TreePath parentPath);

  /**
   * Gets the elements of the input. Subclasses should return the unfiltered
   * elements, filtering will be done by the superclass.
   * 
   * @param inputElement
   *          The input element.
   * @return The root elements, not filtered.
   */
  protected abstract Object[] doGetElements(Object inputElement);

  /**
   * Filters the given elements using the filters.
   * 
   * @param elements
   *          The elements to be filtered.
   * @return The filtered elements, or an empty array if all elements are
   *         filtered out.
   */
  private Object[] filter(@Nullable Object[] elements) {
    if (elements == null) {
      return EMPTY_ARRAY;
    }
    List<Object> children = Arrays.asList(elements);
    return Collections2.filter(children, not(or(getFilters()))).toArray();
  }
}
