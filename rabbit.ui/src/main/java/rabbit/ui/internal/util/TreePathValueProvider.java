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
package rabbit.ui.internal.util;

import rabbit.ui.IProvider;
import rabbit.ui.internal.viewers.IValueProvider;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.alwaysTrue;

import com.google.common.base.Objects;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.eclipse.jface.viewers.TreePath;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Observable;

import javax.annotation.Nullable;

/**
 * A value provider for tree paths.
 * <p/>
 * {@link #getValue(Object)} can be used to find out the value of a particular
 * tree path (it supports arguments as {@link TreePath}, or {@link List}), the
 * path doesn't need to be a complete path from the root of the tree to a leaf
 * node, it can be a sub path such that it's a parent path of one or more child
 * paths, than the value of this parent path will be the sum of all its child
 * paths.
 * <p/>
 * This value provider also take a {@link IProvider} for getting the paths, the
 * paths supplied should be complete paths (all are tree leaves), and the value
 * of each of these paths is determined by a {@link IConverter}.
 * <p/>
 * This class also accepts a {@link ICategorizer} for getting the category of
 * each path, if the category of a given path is equal to
 * {@link #getVisualCategory()} then {@link #shouldPaint(Object)} will return
 * true on the given path.
 */
public final class TreePathValueProvider extends Observable
    implements IValueProvider, IVisualProvider {

  private final ICategorizer categorizer;
  private final IProvider<TreePath> pathProvider;
  private final IConverter<TreePath> converter;

  private ICategory visual;
  private long max;

  /**
   * Constructor.
   * 
   * @param categorizer for categorizing tree paths.
   * @param treePathProvider for getting the tree leaf paths.
   * @param converter for getting value of a tree path supplied by
   *        {@code treePathProvider}.
   * @throws NullPointerException if any argument is null.
   */
  public TreePathValueProvider(
      ICategorizer categorizer,
      IProvider<TreePath> treePathProvider,
      IConverter<TreePath> converter) {
    this(categorizer, treePathProvider, converter, null);
  }

  /**
   * @param categorizer for categorizing tree paths.
   * @param treePathProvider for getting the tree leaf paths.
   * @param converter for getting value of a tree path supplied by
   *        {@code treePathProvider}.
   * @param visualCategory the default visual category.
   * @throws NullPointerException if any of
   *         {@code categorizer, treePathProvider, converter} is null.
   */
  public TreePathValueProvider(
      ICategorizer categorizer,
      IProvider<TreePath> treePathProvider,
      IConverter<TreePath> converter,
      @Nullable ICategory visualCategory) {

    this.categorizer = checkNotNull(categorizer, "categorizer");
    this.pathProvider = checkNotNull(treePathProvider, "treePathProvider");
    this.converter = checkNotNull(converter, "converter");
    this.visual = visualCategory;
  }

  /**
   * @return the categorizer for categorizing the elements.
   */
  public ICategorizer getCategorizer() {
    return categorizer;
  }

  /**
   * @return the converter for converting a tree leaf to a value.
   */
  public IConverter<TreePath> getConverter() {
    return converter;
  }

  @Override
  public long getMaxValue() {
    return max;
  }

  /**
   * @return the provider that provides tree leaves.
   */
  public IProvider<TreePath> getProvider() {
    return pathProvider;
  }

  /**
   * Gets the value of the given tree path. If the given tree path is a parent
   * path of one or more child paths, then the sum of the children values will
   * be returned.
   * 
   * @param element the tree path to get the value for.
   * @return the value of the tree path, may be zero. Zero is also returned if
   *         the given element is not a tree path.
   */
  @Override
  public long getValue(@Nullable Object element) {
    if (element instanceof TreePath) {
      return getValue((TreePath)element, getProvider().get());
    } else if (element instanceof List) {
      return getValue((List<?>)element, getProvider().get());
    } else {
      return 0;
    }
  }

  @Override
  public ICategory getVisualCategory() {
    return visual;
  }

  /**
   * Sets the visual category and calculates the max value using the category.
   * If the category is not supported by the categorizer then it will be ignored
   * and this method will return false, otherwise {@code true} will be returned,
   * and observers will be notified.
   * 
   * @param category the new category
   * @return {@code true} if the category is accepted, {@code false} otherwise
   */
  @Override
  public boolean setVisualCategory(ICategory category) {
    return setVisualCategory(category, alwaysTrue());
  }

  /**
   * Sets the visual category and calculates the max value using the category.
   * If the category is not supported by the categorizer then it will be ignored
   * and this method will return {@code false}, otherwise {@code true} will be
   * returned, and observers will be notified.
   * 
   * @param category the new category
   * @param predicate the predicate to select wanted elements, only elements
   *        passes the predicate will be included when calculating the max value
   * @return {@code true} if the category is accepted, {@code false} otherwise
   */
  public boolean setVisualCategory(ICategory category,
      Predicate<? super Object> predicate) {

    if (getCategorizer().hasCategory(category)) {
      ICategory oldCategory = getVisualCategory();
      long oldMax = getMaxValue();
      visual = category;
      setMaxValue(category, predicate);
      if (!Objects.equal(oldCategory, visual) || oldMax != getMaxValue()) {
        setChanged();
        notifyObservers();
      }
      return true;
    }
    return false;
  }

  @Override
  public boolean shouldPaint(Object element) {
    return Objects.equal(
        getVisualCategory(),
        getCategorizer().getCategory(element));
  }

  /**
   * Gets the value of the given tree path. If the given tree path is a parent
   * path of one or more child paths, then the sum of the children values will
   * be returned.
   * 
   * @param path the path.
   * @param leaves the leaves to match.
   * @return the value of the tree path, may be zero.
   */
  private long getValue(List<?> path, Collection<TreePath> leaves) {
    long value = 0;
    outer: for (TreePath leaf : leaves) {
      if (leaf.getSegmentCount() < path.size()) {
        continue;
      }
      for (int i = 0; i < path.size(); i++) {
        if (!Objects.equal(leaf.getSegment(i), path.get(i))) {
          continue outer;
        }
      }
      value += converter.convert(leaf);
    }
    return value;
  }

  /**
   * Gets the value of the given tree path. If the given tree path is a parent
   * path of one or more child paths, then the sum of the children values will
   * be returned.
   * 
   * @param path the path.
   * @param leaves the leaves to match.
   * @return the value of the tree path, may be zero.
   */
  private long getValue(TreePath path, Collection<TreePath> leaves) {
    long value = 0;
    for (TreePath leaf : leaves) {
      if (leaf.startsWith(path, null)) {
        value += converter.convert(leaf);
      }
    }
    return value;
  }

  private void setMaxValue(ICategory category,
      Predicate<? super Object> predicate) {

    final Collection<TreePath> leaves = getProvider().get();
    final Map<List<Object>, Long> values = Maps.newHashMap();
    final List<Object> keyBuilder = Lists.newArrayList();
    for (TreePath leaf : leaves) {
      keyBuilder.clear();
      for (int i = 0; i < leaf.getSegmentCount(); ++i) {
        final Object segment = leaf.getSegment(i);
        keyBuilder.add(segment);

        final ICategory another = getCategorizer().getCategory(segment);
        if (Objects.equal(category, another) && predicate.apply(segment)) {
          final List<Object> id = Lists.newArrayList(keyBuilder);
          Long val = values.get(id);
          if (val == null) {
            val = 0L;
          }
          values.put(id, val + getConverter().convert(leaf));
          break;
        }
      }
    }

    if (values.isEmpty()) {
      setMaxValue(0);
    } else {
      setMaxValue(Collections.max(values.values()));
    }
  }

  private void setMaxValue(long max) {
    this.max = max;
  }
}