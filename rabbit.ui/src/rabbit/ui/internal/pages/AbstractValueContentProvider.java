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

import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.viewers.TreeNodes;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

import com.google.common.collect.Maps;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import java.util.IdentityHashMap;

/**
 * Internal subclass of {@link AbstractCategoryContentProvider} that also
 * behaviors as an {@link IValueProvider}.
 * 
 * <p>
 * This content provider builds a tree from the input data, and every leaf node
 * of the tree is containing a java.util.Long value, these values are the values
 * of each tree path. This way, we can calculate the total value of every
 * subtree by traversal. For example:
 * 
 * <pre>
 * Parent +-- Child1 --- Long
 *        |
 *        +-- Child2 --- Long
 *        |
 *        +-- Child3 --- Long
 * </pre>
 * 
 * Subclasses should override {@link #doInputChanged(Viewer, Object, Object)} to
 * build the tree from the input.
 * 
 * </p>
 */
public abstract class AbstractValueContentProvider 
    extends AbstractCategoryContentProvider implements IValueProvider {

  /**
   * A cached map of tree nodes and the total duration of that subtree. We use
   * an identity hash map here because two tree nodes can contain the same
   * object but still have different durations.
   * <p>
   * For example, a user worked on fileA for 10 minutes on Monday, 20 minutes on
   * Tuesday, then we can have two tree nodes containing fileA but different
   * durations (10 minutes and 20 minutes).
   * </p>
   */
  private IdentityHashMap<TreeNode, Long> treeNodeValues;

  /** {@link #getMaxValue()} */
  protected long maxValue;

  /**
   * Constructor a content provider for the given viewer.
   * 
   * @param viewer The viewer.
   * @throws NullPointerException If argument is null.
   */
  public AbstractValueContentProvider(TreeViewer viewer) {
    super(viewer);
    maxValue = 0;
    treeNodeValues = Maps.newIdentityHashMap();
  }

  @Override
  public long getMaxValue() {
    return maxValue;
  }
  
  @Override
  public long getValue(Object element) {
    if (false == element instanceof TreeNode)
      return 0;

    TreeNode node = (TreeNode) element;
    Long value = treeNodeValues.get(node);
    if (value == null) {
      value = TreeNodes.longValueOfSubtree(node);
      treeNodeValues.put(node, value);
    }
    return value;
  }

  @Override
  public boolean hasChildren(Object element) {
    TreeNode node = (TreeNode) element;
    if (node.getChildren() == null) {
      return false;
    }

    /*
     * Hides the pure numeric tree nodes, these are nodes with java.lang.Long
     * objects hanging at the end of the branches, we only use those to
     * calculate values for the parents, not to be shown to the users:
     */
    if (node.getChildren()[0].getValue() instanceof Long) {
      return false;
    }

    return true;
  }

  @Override
  public void setPaintCategory(ICategory cat) {
    super.setPaintCategory(cat);
    updateMaxValue();
  }

  @Override
  public boolean shouldPaint(Object element) {
    if (!(element instanceof TreeNode))
      return false;
    
    TreeNode node = (TreeNode) element;
    return getCategorizers().get(getPaintCategory()).apply(node.getValue());
  }

  /**
   * Updates the max value for painting.
   * @see #getMaxValue()
   */
  protected void updateMaxValue() {
    maxValue = TreeNodes.findMaxLong(getRoot(), getCategorizers().get(getPaintCategory()));
  }
  
  @Override
  public final void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
    super.inputChanged(viewer, oldInput, newInput);
    if (newInput != null) {
      treeNodeValues.clear();
      updateMaxValue();
    }
  }
}