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

import rabbit.tracking.internal.util.IClock;
import org.scalatest.{ FlatSpec, BeforeAndAfter }

import rabbit.tracking.tests.TestImplicits.{ funToRunnable, funToAnswer }
import rabbit.tracking.util.Recorder.{ Record, IRecordListener }

@RunWith(classOf[JUnitRunner])
final class RecorderSpec extends FlatSpec with MustMatchers with BeforeAndAfter {

  private class RecorderTester(recorder: Recorder) {

    def startInNewThread(data: Any = null) {
      doInNewThread({ () =>
        recorder.start(data)
      })
    }

    def stopInNewThread() {
      doInNewThread(() => {
        recorder.stop()
      })
    }

    private def doInNewThread(f: () => Unit) {
      val startSignal = new CountDownLatch(1)
      val doneSignal = new CountDownLatch(1)
      new Thread({ () =>
        startSignal.await()
        f()
        doneSignal.countDown()
      }).start()
      startSignal.countDown()
      doneSignal.await()
    }
  }

  private class MockClock(instant: Instant, duration: Duration) extends IClock {
    private var _returnStartInstant: Boolean = true
    def returnStartInstant = synchronized { _returnStartInstant }
    def returnStartInstant_=(flag: Boolean) = synchronized { _returnStartInstant = flag }

    override def now = if (returnStartInstant) instant else instant.plus(duration)
  }

  private var recorder: Recorder = _

  private var clock: MockClock = _
  private var instant: Instant = _
  private var duration: Duration = _

  before {
    instant = now
    duration = millis(1001)
    clock = new MockClock(instant, duration)
    recorder = Recorder.withClock(clock)
  }

  behavior of "Recorder"

  it must "throw NullPointerException if tried to construct a record without a duration" in {
    intercept[NullPointerException] {
      Record.create(now, null, "data");
    }
  }

  it must "throw NullPointerException if tried to construct a record without a start time" in {
    intercept[NullPointerException] {
      Record.create(null, Duration.ZERO, "data");
    }
  }

  it must "not throw exception if tried to construct a record without user data" in {
    Record.create(now, Duration.ZERO, null)
  }

  it must "not notify listener that has been removed" in {
    val listener = mockListener()
    recorder.addListener(listener)
    recorder.removeListener(listener)
    recorder.start(null)
    recorder.stop()
    verifyZeroInteractions(listener)
  }

  it must "ignore subsequent calls to start with same data" in {
    val listener = mockListener()
    recorder.addListener(listener)
    recorder.start(1)
    recorder.start(1)
    recorder.start(1)
    clock.returnStartInstant = false
    recorder.stop()

    verify(listener, only).onRecord(whatever)
  }

  it must "stop current session before starting a new one on different data" in {
    val listener = mockListener()
    recorder.addListener(listener)
    recorder.start(1)
    recorder.start(2)
    recorder.stop()
    verify(listener, times(2)).onRecord(whatever)
  }

  it must "record with correct properties" in {
    var record: Record = null
    val listener = mockListener()
    recorder.addListener(listener)
    doAnswer({ invocation: InvocationOnMock =>
      record = invocation.getArguments()(0).asInstanceOf[Record]
    }).when(listener).onRecord(whatever)

    recorder.start("mydata")
    sleep(5)
    clock.returnStartInstant = false
    recorder.stopInNewThread()

    verify(listener, only).onRecord(record)
    check(record, "mydata")
  }

  it must "accept start argument as optional" in {
    recorder.start(null)
  }

  it must "ignore subsequent calls to stop" in {
    val listener = mockListener()
    recorder.addListener(listener)
    recorder.startInNewThread("abc")
    recorder.stop()
    recorder.stopInNewThread()
    recorder.stop()
    verify(listener, only).onRecord(whatever)
  }

  it must "throw NullPointerException if adding a null listener" in {
    intercept[NullPointerException] {
      recorder.addListener(null)
    }
  }

  it must "throw NullPointerException if removing a null listener" in {
    intercept[NullPointerException] {
      recorder.removeListener(null)
    }
  }

  it must "do nothing if stop is called but start wasn't" in {
    val listener = mockListener()
    recorder.addListener(listener)
    recorder.stop()
    verifyZeroInteractions(listener)
  }

  it must "add listeners to be notified when creating with listeners" in {
    val listener1 = mockListener()
    val listener2 = mockListener()
    recorder = Recorder.withListeners(listener1, listener2)
    recorder.start("data")
    recorder.stop()
    verify(listener1).onRecord(whatever)
    verify(listener2).onRecord(whatever)
  }

  behavior of "Recorder.withListeners"

  it must "throw NullPointerException if creating with null listeners" in {
    intercept[NullPointerException] {
      Recorder.withListeners(null)
    }
  }

  it must "throw NullPointerException if creating with listener list that contains null" in {
    intercept[NullPointerException] {
      Recorder.withListeners(mockListener, null)
    }
  }

  behavior of "Recorder.withClock"

  it must "throw NullPointerException if creating with null listeners" in {
    intercept[NullPointerException] {
      Recorder.withClock(clock, null)
    }
  }

  it must "throw NullPointerException if creating with listener list that contains null" in {
    intercept[NullPointerException] {
      Recorder.withClock(clock, mockListener, null)
    }
  }

  it must "throw NullPointerException if creating with null clock" in {
    intercept[NullPointerException] {
      Recorder.withClock(null)
    }
  }

  private def whatever() = any[Record]

  private def mockListener() = mock[IRecordListener]

  private def check(record: Record, data: Any) {
    record must not be (null)
    record.data must be(data)
    record.instant must be(instant)
    record.duration.getMillis must be(duration.getMillis)
  }

  private implicit def recorderToMyRecorder(recorder: Recorder): RecorderTester =
    new RecorderTester(recorder)
}
