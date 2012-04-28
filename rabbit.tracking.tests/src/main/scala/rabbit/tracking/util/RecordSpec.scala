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

package rabbit.tracking.util

import java.lang.Thread.sleep
import java.util.Arrays.asList
import java.util.concurrent.{ CountDownLatch, ConcurrentLinkedQueue }
import org.joda.time.Duration.millis
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verifyZeroInteractions, verify, times, only, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar.mock
import org.scalatest.{ FlatSpec, BeforeAndAfter }
import rabbit.tracking.tests.TestImplicits.{ funToRunnable, funToAnswer }
import rabbit.tracking.ListenableSpecBase
import org.scalatest.BeforeAndAfterEach

@RunWith(classOf[JUnitRunner]) // TODO inherit from TimedEventTest
final class RecordSpec extends FlatSpec with MustMatchers {

  behavior of "Record"

  it must "throw NullPointerException if tried to construct without a duration" in {
    intercept[NullPointerException] {
      Record.create(now, null, "data");
    }
  }

  it must "throw NullPointerException if tried to construct without a start time" in {
    intercept[NullPointerException] {
      Record.create(null, Duration.ZERO, "data");
    }
  }

  it must "not throw exception if tried to construct without user data" in {
    Record.create(now, Duration.ZERO, null)
  }

  it must "return the instant" in {
    val instant = now
    Record.create(instant, Duration.ZERO, "data").instant must be(instant)
  }

  it must "return the duration" in {
    val duration = new Duration(10)
    Record.create(now, duration, "data").duration must be(duration)
  }

  it must "return the data" in {
    val data = "data"
    Record.create(now, Duration.ZERO, data).data must be(data)
  }
}
