package rabbit.ui.internal.viewers

import org.eclipse.jface.viewers.TreeViewer
import org.eclipse.swt.SWT.DOWN
import org.eclipse.swt.SWT.UP
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.events.SelectionListener
import org.eclipse.swt.widgets.TreeColumn
import org.eclipse.swt.SWT

final class TreeViewerColumnSorter(
  private val viewer: TreeViewer,
  private val sortable: Sortable,
  private val asc: Ordering[Any]) extends SelectionListener {

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