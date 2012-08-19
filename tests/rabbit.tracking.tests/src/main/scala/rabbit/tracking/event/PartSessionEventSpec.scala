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

import org.eclipse.ui.IWorkbenchPart
import org.joda.time.Duration.ZERO
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.{ FinalSpecBase, EqualsSpecBase }
import rabbit.tracking.tests.TestUtils.{ epoch, duration }

@RunWith(classOf[JUnitRunner])
final class PartSessionEventSpec extends TimedEventSpec with EqualsSpecBase with FinalSpecBase {

  behavior of clazz.getSimpleName

  it must "throw NullPointerException if constructing without a part" in {
    intercept[NullPointerException] {
      create(epoch, ZERO, null)
    }
  }

  it must "return the part" in {
    create(epoch, ZERO, part).part must be(part)
  }

  private val part = mock[IWorkbenchPart]

  override protected def differences() = Table(
    ("event a", "event b"), //
    (create(epoch, ZERO, part), create(now, ZERO, part)), // Different instants
    (create(epoch, ZERO, part), create(epoch, duration(1), part)), // Different durations
    (create(epoch, ZERO, part), create(epoch, ZERO, mock[IWorkbenchPart])), // Different parts
    (create(epoch, ZERO, part), create(now, duration(1), mock[IWorkbenchPart])) // Different all
    )

  override protected def clazz = classOf[PartSessionEvent]

  override protected def equalObject() = create(epoch, ZERO, part)

  override protected def create(instant: Instant, duration: Duration) =
    create(instant, duration, mock[IWorkbenchPart])

  private def create(instant: Instant, duration: Duration, part: IWorkbenchPart) =
    new PartSessionEvent(instant, duration, part)

}