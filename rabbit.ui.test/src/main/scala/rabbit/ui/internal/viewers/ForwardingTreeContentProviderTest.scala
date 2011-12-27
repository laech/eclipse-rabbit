package rabbit.ui.internal.viewers

import org.eclipse.jface.viewers.ITreeContentProvider
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.only
import org.mockito.Mockito.verify

import rabbit.ui.internal.viewers.ForwardingTreeContentProvider

class ForwardingTreeContentProviderTest
  extends ForwardingStructuredContentProviderTest {

  @Test def getChildrenShouldBeDelegated {
    val obj = create
    val parent = "aParent"
    obj.getChildren(parent)
    verify(obj.delegate, only).getChildren(parent)
  }

  @Test def getParentShouldBeDelegated {
    val obj = create
    val child = "aChild"
    obj.getParent(child)
    verify(obj.delegate, only).getParent(child)
  }

  @Test def hasChildrenShouldBeDelegated {
    val obj = create
    val parent = "aParent"
    obj.hasChildren(parent)
    verify(obj.delegate, only).hasChildren(parent)
  }

  override protected def create =
    new ForwardingTreeContentProvider with Forwarding {
      override val delegate = mock(classOf[ITreeContentProvider])
    }
}