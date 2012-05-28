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
import org.joda.time.{Instant, Duration}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.prop.TableDrivenPropertyChecks

@RunWith(classOf[JUnitRunner])
final class TimedEventSpec extends TimedEventSpecBase with TableDrivenPropertyChecks {

  private val differences = Table(
    ("event a", "event b"),
    (create(epoch, duration(0)), create(now, duration(0))), // Different instants
    (create(epoch, duration(1)), create(epoch, duration(2))), // Different durations
    (create(epoch, duration(1)), create(now, duration(2))) // Different durations and instants
    )

  behavior of classOf[TimedEvent].getSimpleName

  it must "return same hash code if properties are same" in {
    val a = create(epoch, ZERO)
    val b = create(epoch, ZERO)
    a.hashCode must be(b.hashCode)
  }

  it must "return different hash codes if any properties are different" in {
    forAll(differences) { (a, b) =>
      a.hashCode must not be b.hashCode
    }
  }

  it must "equal to itself" in {
    val event = create(now, duration(0))
    event must be(event)
  }

  it must "equal to another event with same properties" in {
    val a = new TimedEvent(new Instant(101), ZERO)
    val b = new TimedEvent(new Instant(101), ZERO)
    a must be(b)
  }

  it must "not equal to a different event" in {
    forAll(differences) { (a, b) =>
      a must not be b
    }
  }

  it must "not equal to another event of subtype even with same properties" in {
    val a = new TimedEvent(new Instant(0), ZERO)
    val b = new TimedEvent(new Instant(0), ZERO) {}
    a must not be b
  }

  it must "not equal to null" in {
    create(now, ZERO).equals(null) must be(false)
  }

  private def epoch() = new Instant(0)

  private def duration(millis: Long) = new Duration(millis)
}