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

import rabbit.ui.internal.viewers.ITreePathBuilder;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Lists.newArrayListWithCapacity;
import static com.google.common.collect.Sets.newLinkedHashSet;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;

import org.eclipse.jface.viewers.ITreePathContentProvider;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;

import static java.util.Collections.emptyList;

import java.util.List;
import java.util.Set;

import javax.annotation.Nullable;

/**
 * This type of content provider takes a collection of leaf {@link TreePath}s
 * from a {@link ITreePathBuilder} each time the input changes and returns
 * elements/children/parents based on the leaves.
 * <ul>
 * <li>
 * The root elements of the input will be the distinct first elements of all the
 * leaves.</li>
 * <li>
 * The children of a tree path {@code p} will be the distinct elements at index
 * {@code p.getSegmentCount()} of all the leaves that has A as their parent
 * path, where {@code leaf.getSegmentCount() > p.getSegmentCount()}</li>
 * <li>
 * The possible parents of a child will be sub paths of all the leaves that
 * contain the child as one of their segments. The range of a sub path will be
 * from segment 0 up to but exclude the child segment.</li>
 * </ul>
 */
public final class TreePathContentProvider implements ITreePathContentProvider {

  /**
   * Immutable list of leaves, the list may be empty but never null.
   */
  private List<TreePath> leaves;
  
  private final ITreePathBuilder builder;

  /**
   * Constructs a content provider.
   * @param builder the builder to builder data from input element.
   * @throws NullPointerException if {@code builder == null}.
   */
  public TreePathContentProvider(ITreePathBuilder builder) {
    this.builder = checkNotNull(builder, "builder");
    this.leaves = emptyList();
  }

  @Override
  public void dispose() {
    leaves = emptyList();
  }

  @Override
  public Object[] getChildren(@Nullable TreePath branch) {
    Set<Object> children = newLinkedHashSet();
    for (TreePath leaf : leaves) {
      if (leaf.getSegmentCount() > branch.getSegmentCount()
          && leaf.startsWith(branch, null)) {
        children.add(leaf.getSegment(branch.getSegmentCount()));
      }
    }
    return children.toArray();
  }

  /**
   * A {@link TreePathContentProvider} will always return an array of elements
   * from the latest input, regardless of what the parameter of this method is.
   * @param inputElement the input element, this parameter is ignored by this
   *        content provider.
   */
  @Override
  public Object[] getElements(@Nullable Object inputElement) {
    Set<Object> elements = Sets.newLinkedHashSet();
    for (TreePath leaf : leaves) {
      if (leaf.getFirstSegment() != null) {
        elements.add(leaf.getFirstSegment());
      }
    }
    return elements.toArray();
  }

  @Override
  public TreePath[] getParents(@Nullable Object element) {
    Set<TreePath> parents = Sets.newLinkedHashSet();
    for (TreePath leaf : leaves) {
      int index = indexOf(leaf, element);
      if (index < 0) {
        continue;
      }
      parents.add(headPath(leaf, index));
    }
    return parents.toArray(new TreePath[parents.size()]);
  }

  @Override
  public boolean hasChildren(@Nullable TreePath path) {
    return getChildren(path).length > 0;
  }

  /**
   * When input changes, the internal data of this content provider will be
   * updated.
   */
  @Override
  public void inputChanged(Viewer viewer,
                           @Nullable Object oldInput,
                           @Nullable Object newInput) {

    leaves = ImmutableList.copyOf(builder.build(newInput));
  }

  /**
   * Returns the index of the given segment in the given path.
   * @param path the tree path to search the segment.
   * @param segment the segment to find the index for.
   * @return the index, or a negative number if not found.
   */
  private int indexOf(TreePath path, Object segment) {
    for (int i = 0; i < path.getSegmentCount(); ++i) {
      if (Objects.equal(path.getSegment(i), segment)) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Gets the head path of a tree path.
   * @param path the path.
   * @param endIndex the ending index on the given path, exclusive.
   * @return the head path.
   * @throws IllegalArgumentException if {@code endIndex < 0}.
   */
  private TreePath headPath(TreePath path, int endIndex) {
    checkArgument(endIndex >= 0);
    List<Object> segments = newArrayListWithCapacity(endIndex);
    for (int i = 0; i < endIndex; ++i) {
      segments.add(path.getSegment(i));
    }
    return new TreePath(segments.toArray());
  }

}
