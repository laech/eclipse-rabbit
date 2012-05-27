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

@RunWith(classOf[JUnitRunner])
final class EventSpec extends EventSpecBase {

  behavior of classOf[Event].getSimpleName

  it must "return same hash code if properties are same" in {
    val a = new Event(new Instant(101))
    val b = new Event(new Instant(101))
    a.hashCode must be(b.hashCode)
  }

  it must "return different hash codes if instants are different" in {
    val a = new Event(now)
    val b = new Event(now plus 1)
    a.hashCode must not be b.hashCode
  }

  it must "equal to itself" in {
    val event = new Event(now)
    event.equals(event) must be(true)
  }

  it must "equal to another event with same properties" in {
    val a = new Event(new Instant(101))
    val b = new Event(new Instant(101))
    a.equals(b) must be(true)
  }

  it must "not equal to another event of subtype even with same properties" in {
    val a = new Event(new Instant(101))
    val b = new Event(new Instant(101)) {}
    a.equals(b) must be(false)
  }

  it must "not equal to null" in {
    new Event(now).equals(null) must be(false)
  }
}