/*
 * Copyright 2012 The Rabbit Eclipse Plug-in Project
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

package rabbit.tracking.event

import org.eclipse.ui.IPerspectiveDescriptor
import org.joda.time.Instant.now
import org.joda.time.Instant
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.{ FinalSpecBase, EqualsSpecBase }

@RunWith(classOf[JUnitRunner])
final class PerspectiveFocusEventSpec extends EventSpec with EqualsSpecBase with FinalSpecBase {

  behavior of clazz.getSimpleName

  it must "throw NullPointerException if constructing without a perspective" in {
    intercept[NullPointerException] {
      create(now, null, true)
    }
  }

  it must "return the perspective" in {
    val perspective = mock[IPerspectiveDescriptor]
    create(perspective = perspective).perspective must be(perspective)
  }

  it must "return the focus" in {
    val focus = true
    create(focus = focus).isFocused must be(focus)
  }

  private val perspective = mock[IPerspectiveDescriptor]

  override protected def differences() = Table(
    ("event a", "event b"),
    (create(epoch, perspective, true), create(now, perspective, true)), // Different instants
    (create(epoch, perspective, true), create(epoch, mock[IPerspectiveDescriptor], true)), // Different perspectives
    (create(epoch, perspective, true), create(epoch, perspective, false)), // Different focuses
    (create(epoch, perspective, true), create(now, mock[IPerspectiveDescriptor], false)) // Different all 
    )

  override protected def clazz = classOf[PartFocusEvent]

  override protected def equalObject() = create(epoch, perspective, true)

  override protected def create(instant: Instant): PerspectiveFocusEvent =
    create(instant, mock[IPerspectiveDescriptor], true)

  private def create(
    instant: Instant = now,
    perspective: IPerspectiveDescriptor = mock[IPerspectiveDescriptor],
    focus: Boolean = true) =
    new PerspectiveFocusEvent(instant, perspective, focus)

  private def epoch() = new Instant(0)

}