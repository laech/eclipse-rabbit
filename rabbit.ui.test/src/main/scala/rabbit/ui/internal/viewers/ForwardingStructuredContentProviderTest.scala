package rabbit.ui.internal.viewers

import rabbit.ui.internal.viewers.ForwardingStructuredContentProvider
import org.mockito.Mockito._
import org.junit.Test
import org.eclipse.jface.viewers.IStructuredContentProvider

class ForwardingStructuredContentProviderTest
  extends ForwardingContentProviderTest {

  @Test def getElementsShouldBeDelegated {
    val input = new Object
    val obj = create
    obj.getElements(input)
    verify(obj.delegate, only).getElements(input)
  }

  override protected def create =
    new ForwardingStructuredContentProvider with Forwarding {
      override val delegate = mock(classOf[IStructuredContentProvider])
    }
}