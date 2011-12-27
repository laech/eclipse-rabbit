package rabbit.ui.internal.viewers

import org.hamcrest.CoreMatchers.is
import org.junit.Assert.assertThat
import org.junit.Test

import rabbit.ui.internal.viewers.ReversedOrdering

final class ReversedOrderingTest {

  @Test def compareReturnsNegativeFromPositive {
    val negative = new ReversedOrdering(new Ordering[Any]() {
      override def compare(a: Any, b: Any) = 1
    })
    assertThat(negative.compare(null, null), is(-1))
  }

  @Test def compareReturnsPositiveFromNegative {
    val positive = new ReversedOrdering(new Ordering[Any]() {
      override def compare(a: Any, b: Any) = -1
    })
    assertThat(positive.compare(null, null), is(1))
  }

  @Test def compareReturnsZeroFromZero {
    val zero = new ReversedOrdering(new Ordering[Any] {
      override def compare(a: Any, b: Any) = 0
    })
    assertThat(zero.compare(0, 0), is(0))
  }
}