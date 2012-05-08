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
import java.util.concurrent.atomic.AtomicInteger
import org.joda.time.Duration.millis
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verifyZeroInteractions, verify, times, only, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock
import rabbit.tracking.tests.TestImplicits.{ nTimes, funToAnswer }
import rabbit.tracking.tests.TestUtils.doInNewThreads
import rabbit.tracking.ListenableSpecBase
import rabbit.tracking.AbstractTrackerSpecBase

@RunWith(classOf[JUnitRunner])
final class RecorderSpec extends ListenableSpecBase[IRecordListener[Any], Recorder[Any]] {

  private class MockClock(instant: Instant, duration: Duration) extends IClock {
    private var _returnStartInstant: Boolean = true
    def returnStartInstant = synchronized { _returnStartInstant }
    def returnStartInstant_=(flag: Boolean) = synchronized { _returnStartInstant = flag }

    override def now = if (returnStartInstant) instant else instant.plus(duration)
  }

  private var recorder: Recorder[Any] = _

  private var clock: MockClock = _
  private var instant: Instant = _
  private var duration: Duration = _

  override def beforeEach() {
    super.beforeEach()
    instant = now
    duration = millis(1001)
    clock = new MockClock(instant, duration)
    recorder = Recorder.withClock(clock)
  }

  behavior of "Recorder"

  it must "not notify listener that has been removed" in {
    val listener = mockListener
    recorder addListener listener
    recorder removeListener listener
    recorder start null
    recorder.stop
    verifyZeroInteractions(listener)
  }

  it must "ignore subsequent calls to start with same data" in {
    val listener = mockListener
    recorder addListener listener
    recorder start 1
    recorder start 1
    recorder start 1
    clock.returnStartInstant = false
    recorder.stop

    verify(listener, only) onRecord whatever
  }

  it must "stop current session before starting a new one on different data" in {
    val listener = mockListener
    recorder addListener listener
    recorder start 1
    recorder start 2
    recorder.stop
    verify(listener, times(2)) onRecord whatever
  }

  it must "record with correct properties" in {
    var record: Record[Any] = null
    val listener = mockListener
    recorder addListener listener
    doAnswer { i: InvocationOnMock =>
      record = i.getArguments()(0).asInstanceOf[Record[Any]]
    } when listener onRecord whatever

    recorder.start("mydata")
    sleep(5)
    clock.returnStartInstant = false
    recorder.stop

    verify(listener, only) onRecord record
    check(record, "mydata")
  }

  it must "accept start argument as optional" in {
    recorder start null
  }

  it must "ignore subsequent calls to stop" in {
    val listener = mockListener
    recorder addListener listener
    recorder start "abc"
    recorder.stop
    recorder.stop
    recorder.stop
    verify(listener, only) onRecord whatever
  }

  it must "do nothing if stop is called but start wasn't" in {
    val listener = mockListener
    recorder addListener listener
    recorder.stop
    verifyZeroInteractions(listener)
  }

  it must "be able to start correctly concurrently with different user data" in {
    10 times {
      val counter = new AtomicInteger
      val listener = listenerWithDataCollection
      val recorder = createWithListeners(listener)
      doInNewThreads(20, 10 times (recorder start counter.getAndIncrement))
      listener.data.size must be(counter.get - 1)
    }
  }

  it must "be able to start correctly concurrently with same user data" in {
    10 times {
      val listener = listenerWithCounter
      val recorder = createWithListeners(listener)
      doInNewThreads(20, 10 times recorder.start())
      recorder.stop
      listener.counter.get must be(1)
    }
  }

  it must "be able to stop correctly concurrently" in {
    10 times {
      val listener = listenerWithCounter
      val recorder = createWithListeners(listener)
      recorder.start()
      doInNewThreads(20, 10 times recorder.stop)
      listener.counter.get must be(1)
    }
  }

  /*
   * Similar tests for "Recorder.create" has been performed by 
   * ListenableSpecBase since we return Recorder.create in createWithListeners
   */
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

  override protected def newUniqueListener() = new IRecordListener[Any] {
    override def onRecord(record: Record[Any]) {}
  }

  override protected def getListeners(listenable: Recorder[Any]) =
    listenable.getListeners

  override protected def create() = createWithListeners()

  private def createWithListeners(listeners: IRecordListener[Any]*): Recorder[Any] =
    Recorder.create(listeners: _*)

  private def whatever() = any[Record[Any]]

  private def mockListener() = mock[IRecordListener[Any]]

  private def listenerWithCounter() = new IRecordListener[Any] {
    val counter = new AtomicInteger
    override def onRecord(record: Record[Any]) { counter.incrementAndGet }
  }

  private def listenerWithDataCollection() = new IRecordListener[Any] {
    private val _data = new java.util.concurrent.ConcurrentHashMap[Any, Any]
    override def onRecord(record: Record[Any]) { _data.put(record.data, record.data) }
    def data = _data.keySet
  }

  private def check(record: Record[Any], data: Any) {
    record must not be (null)
    record.data must be(data)
    record.instant must be(instant)
    record.duration.getMillis must be(duration.getMillis)
  }
}
