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

package rabbit.tracking

import java.lang.Thread.sleep

import scala.collection.immutable.Set

import org.eclipse.ui.IWorkbenchPart
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verifyZeroInteractions, verify, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.IUserMonitor.IUserListener
import rabbit.tracking.RecordingPartTracker.IPartRecordListener
import rabbit.tracking.tests.Tests.funToAnswer
import rabbit.tracking.tests.WorkbenchTestUtil.{ openRandomPart, closeAllParts }

@RunWith(classOf[JUnitRunner])
final class RecordingPartTrackerSpec extends AbstractTrackerSpecBase {

  private class Expected {
    var preStart: Instant = _
    var postStart: Instant = _
    var preEnd: Instant = _
    var postEnd: Instant = _
    var part: IWorkbenchPart = _
  }

  private class Actual {
    var start: Instant = _
    var duration: Duration = _
    var part: IWorkbenchPart = _
  }

  type Tracker = RecordingPartTracker

  private var monitor: IUserMonitor = _
  private var monitorListeners: Set[IUserListener] = _

  private var actual: Actual = _
  private var listener: IPartRecordListener = _

  override def beforeEach() {
    // Initialize monitor before super, create() depends on this
    monitorListeners = Set.empty
    monitor = mock[IUserMonitor]
    doAnswer((invocation: InvocationOnMock) => {
      monitorListeners += invocation.getArguments()(0).asInstanceOf[IUserListener]
    }).when(monitor).addListener(any[IUserListener])

    super.beforeEach()

    actual = new Actual
    listener = mock[IPartRecordListener]
    tracker.addListener(listener)
    doAnswer((invocation: InvocationOnMock) => {
      val args = invocation.getArguments
      actual.start = args(0).asInstanceOf[Instant]
      actual.duration = args(1).asInstanceOf[Duration]
      actual.part = args(2).asInstanceOf[IWorkbenchPart]
    }).when(listener).onPartEvent(any[Instant], any[Duration], any[IWorkbenchPart])
  }

  behavior of "RecordingPartTracker"

  it must "detactch from user monitor when disabling" in {
    tracker.enable()
    tracker.disable()
    verify(monitor).removeListener(notNullUserMonitorListener)
  }

  it must "attatch to user monitor when enabling" in {
    tracker.enable()
    verify(monitor).addListener(notNullUserMonitorListener)
  }

  it must "be able to record without a user monitor" in {
    tracker = withMonitor(null)
    tracker.enable();
    tracker.addListener(listener)

    val expected = openRandomPart()
    tracker.disable()

    actual.part must be(expected)
  }

  it must "not notify if disabled" in {
    tracker.disable()
    openRandomPart()
    openRandomPart()
    tracker.disable()
    verifyZeroInteractions(listener)
  }

  it must "not notify removed listeners" in {
    tracker.enable()
    tracker.addListener(listener)
    tracker.removeListener(listener)
    openRandomPart()
    tracker.disable()
    verifyZeroInteractions(listener)
  }

  it must "ignore identical listeners that has already been added" in {
    val listener1 = equalsToEveryThingListener
    val listener2 = equalsToEveryThingListener

    tracker.enable()
    tracker.removeListener(listener)
    tracker.addListener(listener1)
    tracker.addListener(listener2)
    openRandomPart()
    tracker.disable()

    listener1.called must be(true)
    listener2.called must be(false)
  }

  it must "throw NullPointerException if adding a null listener" in {
    intercept[NullPointerException] {
      tracker.addListener(null)
    }
  }

  it must "throw NullPointerException if removing a null listener" in {
    intercept[NullPointerException] {
      tracker.removeListener(null)
    }
  }

  it must "throw NullPointerException if creating with null listener" in {
    intercept[NullPointerException] {
      withListeners(listener, null)
    }
    intercept[NullPointerException] {
      withMonitor(monitor, listener, null)
    }
  }

  it must "record part focused duration" in {
    tracker.enable()

    val expected = new Expected
    expected.preStart = now()
    expected.part = openRandomPart()
    expected.postStart = now()

    sleep(2)

    expected.preEnd = now()
    openRandomPart()
    expected.postEnd = now()

    verifyEvent(expected)
  }

  it must "stop recording on user inactive" in {
    tracker.enable()

    val expected = new Expected
    expected.preStart = now()
    expected.part = openRandomPart()
    expected.postStart = now()

    sleep(2)

    expected.preEnd = now()
    monitorListeners.foreach(_.onInactive())
    expected.postEnd = now()

    verifyEvent(expected)
  }

  it must "start recording if there is an active part on user active" in {
    val expected = openRandomPart()
    tracker.enable()
    sleep(2)
    tracker.disable()
    actual.part must be(expected)
  }

  it must "not start recording if there is no active part on user active" in {
    closeAllParts()
    tracker.enable()
    sleep(2)
    tracker.disable()
    actual.part must be(null)
  }

  it must "stop recording on disable" in {
    tracker.enable()

    val expected = new Expected
    expected.preStart = now()
    expected.part = openRandomPart()
    expected.postStart = now()

    sleep(2)

    expected.preEnd = now()
    tracker.disable()
    expected.postEnd = now()

    verifyEvent(expected)
  }

  override protected def create() = RecordingPartTracker.withMonitor(monitor)

  private def withListeners(listeners: IPartRecordListener*) =
    RecordingPartTracker.withListeners(listeners: _*)

  private def withMonitor(monitor: IUserMonitor, listeners: IPartRecordListener*) =
    RecordingPartTracker.withMonitor(monitor, listeners: _*)

  private def verifyEvent(expected: Expected) {
    actual.part must be(expected.part)

    val start = actual.start.getMillis
    start must be >= expected.preStart.getMillis
    start must be <= expected.postStart.getMillis

    val end = start + actual.duration.getMillis
    end must be >= expected.preEnd.getMillis
    end must be <= expected.postEnd.getMillis
  }

  private def equalsToEveryThingListener = new IPartRecordListener {
    var called = false
    override def onPartEvent(start: Instant, duration: Duration, part: IWorkbenchPart) {
      called = true
    }

    override def equals(a: Any) = true
    override def hashCode() = 0
  }

  private def notNullUserMonitorListener = notNull(classOf[IUserListener])
}