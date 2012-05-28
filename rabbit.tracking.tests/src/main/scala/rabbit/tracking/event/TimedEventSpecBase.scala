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

import org.joda.time.Duration.ZERO
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class TimedEventSpecBase extends EventSpecBase {

  behavior of classOf[TimedEvent].getSimpleName

  it must "throw NullPointerException if constructing without a duration" in {
    intercept[NullPointerException] {
      create(now, null)
    }
  }

  it must "return the duration" in {
    val duration = Duration.millis(11)
    create(now, duration).duration must be(duration)
  }

  override protected final def create(instant: Instant) =
    create(instant, ZERO)

  protected def create(instant: Instant, duration: Duration) =
    new TimedEvent(instant, duration)
}