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

import org.eclipse.jface.viewers.TreeNode;

/**
 * A custom implementation of a {@link TreeNode}.
 * 
 * Note that this class violates the equality defined in {@link TreeNode}, an
 * {@link IdentityNode} equals only to itself. Therefore this class should not
 * be used together with {@link TreeNode}.
 */
public class IdentityNode extends TreeNode {
  
  // This class is created to solve issue #7.

  /**
   * Constructs a new node.
   * @param value The value of this node, may be anything.
   */
  public IdentityNode(Object value) {
    super(value);
  }
  
  @Override
  public boolean equals(Object object) {
    return this == object;
  }

  @Override
  public int hashCode() {
    return System.identityHashCode(this);
  }
}
