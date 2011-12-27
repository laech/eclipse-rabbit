package rabbit.ui.internal.viewers

import org.junit.Test
import org.mockito.Mockito._
import org.eclipse.jface.viewers.IContentProvider
import org.eclipse.jface.viewers.Viewer

import rabbit.ui.internal.viewers.ForwardingContentProvider;

class ForwardingContentProviderTest extends ForwardingTest {

  @Test def disposeShouldBeDelegated {
    val obj = create
    obj.dispose
    verify(obj.delegate, only).dispose
  }

  @Test def inputChangedShouldBeDelegated {
    val viewer: Viewer = null
    val oldInput = new Object
    val newInput = new Object
    
    val obj = create
    obj.inputChanged(viewer, oldInput, newInput)
    verify(obj.delegate, only).inputChanged(viewer, oldInput, newInput)
  }

  override protected def create = new ForwardingContentProvider with Forwarding {
    override val delegate = mock(classOf[IContentProvider])
  }
}