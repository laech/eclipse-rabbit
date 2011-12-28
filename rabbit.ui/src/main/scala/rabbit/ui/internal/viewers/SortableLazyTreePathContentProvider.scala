/*
 * Copyright 2011 The Rabbit Eclipse Plug-in Project
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
package rabbit.ui.internal.viewers

import org.eclipse.jface.viewers.ILazyTreePathContentProvider
import org.eclipse.jface.viewers.TreePath
import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.jface.viewers.Viewer

/** A sortable `ILazyTreePathContentProvider` designed for stable trees.
  *
  * The current implementation uses an internal cache for managing children
  * elements, and the cache will be cleared on input changes only, which makes
  * it suitable for stable trees only, that means, each parent path will always
  * have the exact same children.
  */
trait SortableLazyTreePathContentProvider
  extends ILazyTreePathContentProvider with Sortable {
  
  // TODO experimental

  private var cache = emptyCache
  private var viewer: TreeViewer = _

  /** Indicates whether children returned by the tree path provider can be
    * sorted in place, if `false` a copy of the returned array will be made and
    * sorted instead.
    */
  protected val canModifyChildren: Boolean

  /** Provides children for parent paths. */
  protected val provider: TreePathChildrenProvider

  override def ordering_=(ordering: Ordering[Any]) = {
    super.ordering_=(ordering)
    cache.clear
  }

  override def dispose {
    cache = emptyCache
  }

  override def inputChanged(viewer: Viewer, oldInput: Any, newInput: Any) {
    this.viewer = viewer.asInstanceOf[TreeViewer]
    cache = emptyCache
  }

  override def updateChildCount(parent: TreePath, currentChildCount: Int) {
    val children = cache.getOrElseUpdate(parent, getSortedChildren(parent))
    viewer.setChildCount(parent, children.length)
  }

  override def updateElement(parent: TreePath, index: Int) {
    val child = cache.getOrElseUpdate(parent, getSortedChildren(parent))(index)
    viewer.replace(parent, index, child)
    updateHasChildren(parent.createChildPath(child))
  }

  override def updateHasChildren(parent: TreePath) =
    updateChildCount(parent, -1)

  private def emptyCache = collection.mutable.Map.empty[TreePath, Array[Any]]

  private def getSortedChildren(parent: TreePath) = {
    val children = {
      var original = provider.getChildren(parent)
      if (original == null) {
        original = Array.empty[Any]
      } else if (!canModifyChildren) {
        val copy = new Array[Any](original.length)
        System.arraycopy(original, 0, copy, 0, copy.length)
        original = copy
      }
      original
    }
    if (ordering != null) {
      children.sorted(ordering)
    }
    children
  }
}