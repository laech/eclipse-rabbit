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

import com.google.common.base.Predicate;

import org.eclipse.jface.viewers.TreeNode;

import java.util.Arrays;

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
    if (oldChildren == null)
      oldChildren = new TreeNode[0];

    TreeNode newChild = new TreeNode(newChildValue);
    newChild.setParent(parent);

    TreeNode[] newChildren = Arrays.copyOf(oldChildren, oldChildren.length + 1);
    newChildren[newChildren.length - 1] = newChild;
    parent.setChildren(newChildren);

    return newChild;
  }

  /**
   * Finds a child node who has the given value (using
   * {@link Object#equals(Object)}). This method only looks at the immediate
   * children of the parent node.
   * 
   * @param parent The parent node.
   * @param childValue The value of the child node.
   * @return The child node who has the value, or null if no immediate child of
   *         the parent has that value.
   * @see #findChildRecursively(TreeNode, Object)
   */
  public static TreeNode findChild(TreeNode parent, Object childValue) {
    TreeNode[] oldChildren = parent.getChildren();
    if (oldChildren == null)
      return null;

    for (TreeNode node : oldChildren) {
      if (childValue.equals(node.getValue()))
        return node;
    }
    return null;
  }
  
  /**
   * Finds a child node who has the given value (using
   * {@link Object#equals(Object)}). This method looks at all the tree nodes of
   * the given subtree. If the given parent node contains the value, the parent
   * is returned; if a tree node containing the value is found, that tree node
   * is returned without further looking into the tree. This method may not be
   * suitable to use if multiple elements are consider equal in the subtree.
   * 
   * @param parent The root of the subtree.
   * @param childValue The value of the tree node.
   * @return The tree node who has the given value, or null if not found.
   * @see #findChild(TreeNode, Object)
   */
  public static TreeNode findChildRecursively(TreeNode parent, Object childValue) {
    if (childValue.equals(parent.getValue()))
      return parent;
    
    if (parent.getChildren() == null)
      return null;
    
    for (TreeNode node : parent.getChildren()) {
      TreeNode result = findChildRecursively(node, childValue);
      if (result != null)
        return result;
    }
    
    return null;
  }

  /**
   * Finds the max int value of a subtree for all the tree nodes who's object
   * value returns true on {@link Predicate#apply(Object)}.
   * 
   * @param root The root of the subtree to search the value for.
   * @param predicate The predicate to test elements.
   * @return The max value of the subtree.
   * @see #intValueOfSubtree(TreeNode)
   */
  public static int findMaxInt(TreeNode root,Predicate<Object> predicate) {
    if (predicate.apply(root.getValue()))
      return intValueOfSubtree(root);

    TreeNode[] children = root.getChildren();
    if (children == null)
      return 0;

    int max = 0;
    for (TreeNode node : children) {
      int value = findMaxInt(node, predicate);
      if (value > max)
        max = value;
    }
    return max;
  }

  /**
   * Finds the max long value of a subtree for all the tree nodes who's object
   * value returns true on {@link Predicate#apply(Object)}.
   * 
   * @param root The root of the subtree to search the value for.
   * @param predicate The predicate to test elements.
   * @return The max value of the subtree.
   * @see #longValueOfSubtree(TreeNode)
   */
  public static long findMaxLong(TreeNode root, Predicate<Object> predicate) {
    if (predicate.apply(root.getValue()))
      return longValueOfSubtree(root);

    TreeNode[] children = root.getChildren();
    if (children == null)
      return 0;

    long max = 0;
    for (TreeNode node : children) {
      long value = findMaxLong(node, predicate);
      if (value > max)
        max = value;
    }
    return max;
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
   * Gets the int value of a subtree, using the given tree node as the root of
   * the subtree, inclusive. If a tree node's enclosing object is of type
   * java.util.Integer, the numeric value of that tree node is the object value,
   * if a tree node's enclosing object is not of type java.util.Integer, the
   * value of that tree node is 0. The sum of all the values of the subtree will
   * be returned.
   * 
   * @param node The root node of the subtree to get the value for.
   * @return The sum of all the values of the subtree.
   */
  public static int intValueOfSubtree(TreeNode node) {
    int value = 0;
    Object element = node.getValue();
    if (element instanceof Integer)
      value += (Integer) element;

    TreeNode[] children = node.getChildren();
    if (children == null)
      return value;

    for (TreeNode child : children)
      value += intValueOfSubtree(child);

    return value;
  }

  /**
   * Gets the long value of a subtree, using the given tree node as the root of
   * the subtree, inclusive. If a tree node's enclosing object is of type
   * java.util.Long, the numeric value of that tree node is the object value, if
   * a tree node's enclosing object is not of type java.util.Long, the value of
   * that tree node is 0. The sum of all the values of the subtree will be
   * returned.
   * 
   * @param node The root node of the subtree to get the value for.
   * @return The sum of all the values of the subtree.
   */
  public static long longValueOfSubtree(TreeNode node) {
    long value = 0;
    Object element = node.getValue();
    if (element instanceof Long)
      value += (Long) element;

    TreeNode[] children = node.getChildren();
    if (children == null)
      return value;

    for (TreeNode child : children)
      value += longValueOfSubtree(child);

    return value;
  }
}
