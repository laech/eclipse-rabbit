package rabbit.ui.internal.viewers

import org.eclipse.jface.viewers.ITreePathContentProvider
import org.eclipse.jface.viewers.TreePath
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify

import rabbit.ui.internal.viewers.ForwardingTreePathContentProvider

class ForwardingTreePathContentProviderTest
  extends ForwardingStructuredContentProviderTest {

  @Test def getChildrenShouldBeDelegated {
    val parent = TreePath.EMPTY
    val obj = create
    obj.getChildren(parent)
    verify(obj.delegate, only).getChildren(parent)
  }

  @Test def getParentsShouldBeDelegated {
    val child = new Object
    val obj = create
    obj.getParents(child)
    verify(obj.delegate, only).getParents(child)
  }

  @Test def hasChildrenShouldBeDelegated {
    val parent = TreePath.EMPTY
    val obj = create
    obj.hasChildren(parent)
    verify(obj.delegate, only).hasChildren(parent)
  }

  override protected def create =
    new ForwardingTreePathContentProvider with Forwarding {
      override val delegate = mock(classOf[ITreePathContentProvider])
    }
}