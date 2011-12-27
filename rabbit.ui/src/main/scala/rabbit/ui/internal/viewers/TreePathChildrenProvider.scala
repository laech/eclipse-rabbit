package rabbit.ui.internal.viewers

import org.eclipse.jface.viewers.TreePath

trait TreePathChildrenProvider {

  def getChildren(path: TreePath): Array[Any]
}