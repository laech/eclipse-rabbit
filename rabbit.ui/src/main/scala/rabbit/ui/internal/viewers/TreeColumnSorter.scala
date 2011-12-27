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

import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.swt.SWT.DOWN
import org.eclipse.swt.SWT.UP
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.widgets.TreeColumn
import org.eclipse.swt.SWT

/** A `SelectionListener` that can be attached to a `TreeColumn` in responds to
  * selection events of the column, then forwards sorting requests to a sortable
  * object (usually a content provider) and updates the sorting indicator of the
  * column, finally refreshes the viewer.
  *
  * @param viewer the parent `TreeViewer`
  * @param sortable the object to delegate sorting requests to
  * @param asc the natural ordering to pass to the sortable object when the
  *      column is to be sorting ascendingly, descending sorting will be
  *      handled by this instance
  */
final class TreeViewerColumnSorter(
  private val viewer: TreeViewer,
  private val sortable: Sortable,
  private val asc: Ordering[Any]) extends SelectionListener {

  if (viewer == null) throw new NullPointerException("viewer")
  if (sortable == null) throw new NullPointerException("sortable")
  if (asc == null) throw new NullPointerException("asc")

  private val desc = new ReversedOrdering(asc)
  private val virtual = (viewer.getTree.getStyle & SWT.VIRTUAL) != 0

  override def widgetSelected(e: SelectionEvent) {

    // Note: don't expand virtual tree paths, it will cause all tree items to be
    // created

    val paths = if (virtual) null else viewer.getExpandedTreePaths

    val tree = viewer.getTree
    var direction = tree.getSortDirection
    val selected = e.widget.asInstanceOf[TreeColumn]
    val previous = tree.getSortColumn

    if (previous == selected) {
      direction = if (direction == UP) DOWN else UP
    } else {
      tree.setSortColumn(selected)
      direction = UP
    }
    sortable.ordering = if (direction == UP) asc else desc
    tree.setRedraw(false)
    viewer.refresh()
    tree.setRedraw(true)
    tree.setSortDirection(direction)

    if (!virtual) {
      viewer.setExpandedTreePaths(paths)
    }
  }

  override def widgetDefaultSelected(e: SelectionEvent) {}
}