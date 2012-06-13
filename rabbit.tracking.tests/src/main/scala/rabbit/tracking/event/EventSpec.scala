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

import org.joda.time.Instant.now
import org.joda.time.Instant
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.FlatSpec

@RunWith(classOf[JUnitRunner])
class EventSpec extends FlatSpec with MustMatchers {

  behavior of classOf[Event].getSimpleName

  it must "throw NullPointerExeption if constructing without an instant" in {
    intercept[NullPointerException] {
      create(null)
    }
  }

  it must "return the instant" in {
    val instant = now
    create(instant).instant must be(instant)
  }

  protected def create(instant: Instant) = new Event(instant)
}