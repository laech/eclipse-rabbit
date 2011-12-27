package rabbit.ui.internal.viewers

import org.eclipse.jface.viewers.ILazyTreePathContentProvider
import org.eclipse.jface.viewers.TreePath
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify

import rabbit.ui.internal.viewers.ForwardingLazyTreePathContentProvider

final class ForwardingLazyTreePathContentProviderTest
  extends ForwardingContentProviderTest {

  @Test def getParentsShouldBeDelegated {
    val element = "elem"
    val obj = create
    obj.getParents(element)
    verify(obj.delegate, only).getParents(element)
  }

  @Test def updateChildCountShouldBeDelegated {
    val path = new TreePath(Array("1", "2"))
    val currentChildCount = 19
    val obj = create
    obj.updateChildCount(path, currentChildCount)
    verify(obj.delegate, only).updateChildCount(path, currentChildCount)
  }

  @Test def updateElementShouldBeDelegated {
    val path = new TreePath(Array("1", "2", "4"))
    val index = 11
    val obj = create
    obj.updateElement(path, index)
    verify(obj.delegate, only).updateElement(path, index)
  }

  @Test def updateHasChildrenShouldBeDelegated {
    val path = new TreePath(Array("1", "7"))
    val obj = create
    obj.updateHasChildren(path)
    verify(obj.delegate, only).updateHasChildren(path)
  }

  override protected def create =
    new ForwardingLazyTreePathContentProvider with Forwarding {
      override val delegate = mock(classOf[ILazyTreePathContentProvider])
    }
}