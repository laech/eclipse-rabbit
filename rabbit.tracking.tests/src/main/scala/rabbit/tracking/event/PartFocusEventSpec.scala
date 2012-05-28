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

import java.lang.reflect.Modifier

import org.eclipse.ui.{ IWorkbenchPart, part }
import org.joda.time.Instant.now
import org.joda.time.Instant
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock
import org.scalatest.prop.TableDrivenPropertyChecks

@RunWith(classOf[JUnitRunner])
final class PartFocusEventSpec extends EventSpecBase with TableDrivenPropertyChecks {

  private val part = mock[IWorkbenchPart]

  private val differences = Table(
    ("event a", "event b"),
    (create(epoch, part, true), create(now, part, true)), // Different instants
    (create(epoch, part, true), create(epoch, mock[IWorkbenchPart], true)), // Different parts
    (create(epoch, part, true), create(epoch, part, false)), // Different focuses
    (create(epoch, part, true), create(now, mock[IWorkbenchPart], false)) // Different all 
    )

  behavior of classOf[PartFocusEvent].getSimpleName

  it must "return same hash code if properties are same" in {
    val a = create(epoch, part, true)
    val b = create(epoch, part, true)
    a.hashCode must be(b.hashCode)
  }

  it must "return different hash codes if any properties are different" in {
    forAll(differences) { (a, b) =>
      a.hashCode must not be b.hashCode
    }
  }

  it must "equal to itself" in {
    val event = create(epoch, part, true)
    event must be(event)
  }

  it must "equal to another event with same properties" in {
    val a = create(epoch, part, true)
    val b = create(epoch, part, true)
    a must be(b)
  }

  it must "not equal to a different event" in {
    forAll(differences) { (a, b) =>
      a must not be b
    }
  }

  it must "be final" in {
    Modifier.isFinal(classOf[PartFocusEvent].getModifiers) must be(true)
  }

  it must "not equal to null" in {
    create(epoch, part, true).equals(null) must be(false)
  }

  it must "throw NullPointerException if constructing without a part" in {
    intercept[NullPointerException] {
      create(now, null, true)
    }
  }

  it must "return the part" in {
    val part = mock[IWorkbenchPart]
    create(part = part).part must be(part)
  }

  it must "return the focus" in {
    val focus = true
    create(focus = focus).isFocused must be(focus)
  }

  override protected def create(instant: Instant) =
    create(instant, mock[IWorkbenchPart], true)

  private def create(
    instant: Instant = now,
    part: IWorkbenchPart = mock[IWorkbenchPart],
    focus: Boolean = true) =
    new PartFocusEvent(instant, part, focus)

  private def epoch() = new Instant(0)

}