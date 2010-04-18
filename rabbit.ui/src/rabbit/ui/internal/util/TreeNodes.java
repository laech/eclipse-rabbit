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
package rabbit.ui.internal.util;

import org.eclipse.jface.viewers.TreeNode;

import java.util.Arrays;

// TODO test
/**
 * Class containing utility methods for working with {@link TreeNode}.
 */
public class TreeNodes {

  /**
   * Appends a child node to a parent node.
   * 
   * @param parent The parent node.
   * @param newChildValue The value of the child node to be created.
   * @return A new {@code TreeNode} containing the value.
   */
  public static TreeNode appendToParent(TreeNode parent, Object newChildValue) {
    TreeNode[] oldChildren = parent.getChildren();
    if (oldChildren == null) {
      oldChildren = new TreeNode[0];
    }

    TreeNode newChild = new TreeNode(newChildValue);
    newChild.setParent(parent);

    TreeNode[] newChildren = Arrays.copyOf(oldChildren, oldChildren.length + 1);
    newChildren[newChildren.length - 1] = newChild;
    parent.setChildren(newChildren);

    return newChild;
  }

  /**
   * Finds a child node who has the given value.
   * 
   * @param parent The parent node.
   * @param childValue The value of the child node.
   * @return The child node who has the value, or null if no immediate child of
   *         the parent has that value.
   */
  public static TreeNode findChild(TreeNode parent, Object childValue) {
    TreeNode[] oldChildren = parent.getChildren();
    if (oldChildren == null) {
      return null;
    }
    for (TreeNode node : oldChildren) {
      if (childValue.equals(node.getValue())) {
        return node;
      }
    }
    return null;
  }

  /**
   * Try to find a child node who has the given value, if not found, one will be
   * created.
   * 
   * @param parent The parent node.
   * @param childValue The value of the child node.
   * @return An existing child who has the value, if not found, a new child with
   *         the value will be created and appended to the parent's children
   *         list.
   */
  public static TreeNode findOrAppend(TreeNode parent, Object childValue) {
    TreeNode childNode = findChild(parent, childValue);
    return (childNode != null) ? childNode : appendToParent(parent, childValue);
  }

  /**
   * Gets the long value of a node. If the object value of the tree node is of
   * type {@code Long}, the object value is returned, otherwise the sum of the
   * children's values is returned.
   * 
   * @param node The tree node to get the value for.
   * @return The value of the node.
   */
  public static long getLongValue(TreeNode node) {
    Object element = node.getValue();
    if (element instanceof Long) {
      return (Long) element;
    }

    TreeNode[] children = node.getChildren();
    if (children == null) {
      return 0;
    }

    long value = 0;
    for (TreeNode child : children) {
      value += getLongValue(child);
    }
    return value;
  }
  
  public static int getIntegerValue(TreeNode node) {
    Object element = node.getValue();
    if (element instanceof Integer) {
      return (Integer) element;
    }

    TreeNode[] children = node.getChildren();
    if (children == null) {
      return 0;
    }

    int value = 0;
    for (TreeNode child : children) {
      value += getIntegerValue(child);
    }
    return value;
  }

  public static long findMaxLongValue(TreeNode root, Class<?> clazz) {
    if (clazz.isAssignableFrom(root.getValue().getClass())) {
      return getLongValue(root);
    }

    TreeNode[] children = root.getChildren();
    if (children == null) {
      return 0;
    }

    long max = 0;
    for (TreeNode node : children) {
      long value = findMaxLongValue(node, clazz);
      if (value > max) {
        max = value;
      }
    }
    return max;
  }
  
  public static int findMaxIntegerValue(TreeNode root, Class<?> clazz) {
    if (clazz.isAssignableFrom(root.getValue().getClass())) {
      return getIntegerValue(root);
    }

    TreeNode[] children = root.getChildren();
    if (children == null) {
      return 0;
    }

    int max = 0;
    for (TreeNode node : children) {
      int value = findMaxIntegerValue(node, clazz);
      if (value > max) {
        max = value;
      }
    }
    return max;
  }
}
