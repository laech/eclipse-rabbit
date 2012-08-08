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

import org.joda.time.Instant.now
import org.joda.time.Instant
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import rabbit.tracking.tests.EqualsSpecBase

@RunWith(classOf[JUnitRunner])
final class UserStateEventSpec extends EventSpec with EqualsSpecBase {

  behavior of classOf[UserStateEvent].getSimpleName

  it must "be final" in {
    Modifier.isFinal(classOf[UserStateEvent].getModifiers) must be(true)
  }

  it must "not equal to null" in {
    create(epoch, true).equals(null) must be(false)
  }

  it must "return the user state" in {
    val active = true
    create(userActive = active).isUserActive must be(active)
  }

  override protected def differences() = Table(
    ("event a", "event b"),
    (create(epoch, true), create(epoch, false)), // Different states
    (create(epoch, true), create(now, true)), // Different instants
    (create(epoch, true), create(now, false)) // Different all
    )

  override protected def equalObject() = create(epoch, true)

  override protected def create(instant: Instant) = create(instant, true)

  private def create(instant: Instant = now, userActive: Boolean = true) =
    new UserStateEvent(instant, userActive)

  private def epoch() = new Instant(0)
}