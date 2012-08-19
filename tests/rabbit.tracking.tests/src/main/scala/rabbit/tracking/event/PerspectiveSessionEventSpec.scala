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
import org.joda.time.Duration.ZERO
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.{ FinalSpecBase, EqualsSpecBase }

@RunWith(classOf[JUnitRunner])
final class PerspectiveSessionEventSpec extends TimedEventSpec with EqualsSpecBase with FinalSpecBase {

  behavior of clazz.getSimpleName

  it must "throw NullPointerException if constructing without a perspective" in {
    intercept[NullPointerException] {
      create(epoch, ZERO, null)
    }
  }

  it must "return the perspective" in {
    create(epoch, ZERO, perspective).perspective must be(perspective)
  }

  private val perspective = mock[IPerspectiveDescriptor]

  override protected def differences() = Table(
    ("event a", "event b"), //
    (create(epoch, ZERO, perspective), create(now, ZERO, perspective)), // Different instants
    (create(epoch, ZERO, perspective), create(epoch, duration(1), perspective)), // Different durations
    (create(epoch, ZERO, perspective), create(epoch, ZERO, mock[IPerspectiveDescriptor])), // Different perspective
    (create(epoch, ZERO, perspective), create(now, duration(1), mock[IPerspectiveDescriptor])) // Different all
    )

  override protected def clazz = classOf[PerspectiveSessionEvent]

  override protected def equalObject() = create(epoch, ZERO, perspective)

  override protected def create(instant: Instant, duration: Duration) =
    create(instant, duration, mock[IPerspectiveDescriptor])

  private def create(instant: Instant, duration: Duration, perspective: IPerspectiveDescriptor) =
    new PerspectiveSessionEvent(instant, duration, perspective)

  private def epoch() = new Instant(0)

  private def duration(millis: Long) = new Duration(millis)
}