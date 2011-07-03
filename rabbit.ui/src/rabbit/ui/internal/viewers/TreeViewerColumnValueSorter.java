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
package rabbit.ui.internal.viewers;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * This sorter uses a {@link IValueProvider} to get the values of each tree path
 * and sorts the paths base on the values received.
 */
public final class TreeViewerColumnValueSorter extends TreeViewerColumnSorter {

  private final IValueProvider valueProvider;

  /**
   * @param viewer the parent viewer.
   * @param valueProvider the value provider for getting values of the tree
   *        paths.
   * @throws NullPointerException if any argument is null.
   */
  public TreeViewerColumnValueSorter(TreeViewer viewer,
      IValueProvider valueProvider) {
    super(viewer);
    this.valueProvider = checkNotNull(valueProvider);
  }

  @Override
  public void sort(Viewer viewer, TreePath parent, Object[] elements) {
    if (parent == null) {
      parent = TreePath.EMPTY;
    }
    final List<Object> tmpList = Lists
        .newArrayListWithCapacity(parent.getSegmentCount() + 1);
    for (int i = 0; i < parent.getSegmentCount(); ++i) {
      tmpList.add(parent.getSegment(i));
    }

    final Map<Object, Long> values = Maps
        .newHashMapWithExpectedSize(elements.length);
    for (Object obj : elements) {
      tmpList.add(obj);
      final long value = valueProvider.getValue(tmpList);
      values.put(obj, Long.valueOf(value));
      tmpList.remove(tmpList.size() - 1);
    }
    Arrays.sort(elements, new Comparator<Object>() {
      @Override
      public int compare(Object o1, Object o2) {
        int result = values.get(o1).compareTo(values.get(o2));
        if (getSortDirection() == SWT.DOWN) {
          result *= -1;
        }
        return result;
      }
    });
  }

  @Override
  public int doCompare(Viewer v, TreePath parentPath, Object e1, Object e2) {
    // Doing sorting here is slower, we override sort(Viewer, TreePath, Object[]
    // instead, which means this method won't get called anymore
    throw new UnsupportedOperationException();
  }
}
