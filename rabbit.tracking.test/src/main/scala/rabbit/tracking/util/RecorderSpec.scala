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

import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep

import org.joda.time.Instant.now
import org.joda.time.Duration
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verifyZeroInteractions, verify, times, only, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.mockito.stubbing.Answer
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar.mock
import org.scalatest.{ FlatSpec, BeforeAndAfter }

import rabbit.tracking.test.Tests.funToAnswer
import rabbit.tracking.util.Recorder.{ Record, IRecordListener }

@RunWith(classOf[JUnitRunner])
final class RecorderSpec extends FlatSpec with MustMatchers with BeforeAndAfter {

  private var recorder: Recorder = _

  before {
    recorder = Recorder.create()
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

    val preStart = currentTimeMillis()
    recorder.start("mydata")
    sleep(5)
    recorder.stop()
    val postEnd = currentTimeMillis()

    verify(listener, only).onRecord(record)
    check(record, preStart, postEnd, "mydata")
  }

  it must "accept start argument as optional" in {
    recorder.start(null)
  }

  it must "ignore subsequent calls to stop" in {
    val listener = mockListener()
    recorder.addListener(listener)
    recorder.start("abc")
    recorder.stop()
    recorder.stop()
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

  private def whatever() = any[Record]

  private def mockListener() = mock[IRecordListener]

  private def check(record: Record, preStart: Long, postEnd: Long, data: Any) {
    record must not be (null)
    record.getData must be(data)
    val start = record.getStart.getMillis;
    val end = start + record.getDuration.getMillis;
    start must be >= preStart
    end must be <= postEnd
  }
}