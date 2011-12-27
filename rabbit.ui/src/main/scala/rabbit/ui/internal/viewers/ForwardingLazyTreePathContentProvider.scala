package rabbit.ui.internal.viewers

import org.eclipse.jface.viewers.ILazyTreePathContentProvider
import org.eclipse.jface.viewers.TreePath

trait ForwardingLazyTreePathContentProvider
  extends ForwardingContentProvider with ILazyTreePathContentProvider {

  override protected val delegate: ILazyTreePathContentProvider

  override def getParents(element: Any) =
    delegate.getParents(element)

  override def updateChildCount(parent: TreePath, currentChildCount: Int) =
    delegate.updateChildCount(parent, currentChildCount)

  override def updateElement(parent: TreePath, index: Int) =
    delegate.updateElement(parent, index)

  override def updateHasChildren(parent: TreePath) =
    delegate.updateHasChildren(parent)
}