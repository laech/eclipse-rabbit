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

import org.hamcrest.CoreMatchers.is
import org.junit.Assert.assertThat
import org.junit.Test

final class ReversedOrderingTest {

  @Test(expected = classOf[NullPointerException])
  def constructorThrowsExceptionIfArgumentIsNull {
    new ReversedOrdering(null)
  }

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