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

import java.lang.System.currentTimeMillis

import org.eclipse.ui.{ IWorkbenchPart, part }
import org.joda.time.Instant.now
import org.joda.time.Instant
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

@RunWith(classOf[JUnitRunner])
final class PartFocusEventSpec extends EventSpecBase {

  behavior of classOf[PartFocusEvent].getSimpleName

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
}