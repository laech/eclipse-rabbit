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

import org.eclipse.ui.PlatformUI.getWorkbench
import org.eclipse.ui.{ IWorkbenchPart, IWorkbench }
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verifyZeroInteractions, verify, inOrder, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.tests.Workbenches.{ openRandomPart, closeAllParts }
import rabbit.tracking.util.{ Recorder, IRecorder }

@RunWith(classOf[JUnitRunner])
final class PartSessionTrackerSpec extends AbstractTrackerSpecBase {

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

  private var workbench: IWorkbench = _
  private var partTracker: IListenableTracker[IPartFocusListener] = _
  private var recorder: IRecorder = _
  private var monitor: IUserMonitor = _
  private var monitorListeners: Set[IUserListener] = _

  override def beforeEach {
    // Initialize monitor before super, create depends on this
    workbench = getWorkbench
    partTracker = new PartFocusTracker(workbench)
    recorder = Recorder.create()
    monitorListeners = Set.empty
    monitor = mock[IUserMonitor]
    doAnswer({ invocation: InvocationOnMock =>
      monitorListeners += invocation.getArguments()(0).asInstanceOf[IUserListener]
    }).when(monitor).addListener(any[IUserListener])

    super.beforeEach
  }

  behavior of "RecordingPartTracker"

  it must "be able to record without a user monitor" in {
    val (listener, actual) = mockListenerWithResult
    val tracker = create(monitor = null)
    tracker.enable;
    tracker.addListener(listener)

    val expected = openRandomPart
    tracker.disable

    actual.part must be(expected)
  }

  it must "not notify if disabled" in {
    val (listener, _) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.disable
    openRandomPart
    openRandomPart
    tracker.disable
    verifyZeroInteractions(listener)
  }

  it must "not notify removed listeners" in {
    val (listener, _) = mockListenerWithResult
    tracker.enable
    tracker.addListener(listener)
    tracker.removeListener(listener)
    openRandomPart
    tracker.disable
    verifyZeroInteractions(listener)
  }

  it must "record part focused duration" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.enable

    val expected = new Expected
    expected.preStart = now
    expected.part = openRandomPart
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    openRandomPart
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "stop recording on user inactive" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.enable

    val expected = new Expected
    expected.preStart = now
    expected.part = openRandomPart
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    monitorListeners.foreach(_.onInactive)
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "start recording if there is an active part on user active" in {
    val expected = openRandomPart
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.enable
    sleep(2)
    tracker.disable
    actual.part must be(expected)
  }

  it must "not start recording if there is no active part on user active" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    closeAllParts

    tracker.enable
    sleep(2)
    tracker.disable

    actual.part must be(null)
  }

  it must "stop recording on disable" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.enable

    val expected = new Expected
    expected.preStart = now
    expected.part = openRandomPart
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    tracker.disable
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "start recording on enable if there is a focused part" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)

    val expected = new Expected
    expected.part = openRandomPart

    expected.preStart = now
    tracker.enable
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    openRandomPart
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "detactch listener from user monitor when disabling" in {
    val monitor = mock[IUserMonitor]
    val tracker = create(monitor = monitor)
    tracker.enable
    tracker.disable
    verify(monitor).removeListener(notNull(classOf[IUserListener]))
  }

  it must "attatch listener to user monitor when enabling" in {
    val monitor = mock[IUserMonitor]
    val tracker = create(monitor = monitor)
    tracker.enable
    verify(monitor).addListener(notNull(classOf[IUserListener]))
  }

  it must "enable part tracker when enabling" in {
    val partTracker = mock[IListenableTracker[IPartFocusListener]]
    val tracker = create(partTracker = partTracker)
    tracker.enable
    verify(partTracker).enable
  }

  it must "disable part tracker when disabling" in {
    val partTracker = mock[IListenableTracker[IPartFocusListener]]
    val tracker = create(partTracker = partTracker)
    tracker.enable
    tracker.disable
    verify(partTracker).disable
  }

  it must "stop recorder when disabling" in {
    val recorder = mock[IRecorder]
    val tracker = create(recorder = recorder)
    tracker.enable
    tracker.disable
    val order = inOrder(recorder)
    order.verify(recorder).stop
    order.verifyNoMoreInteractions
  }

  it must "throw NullPointerException if constructing without a workbench" in {
    intercept[NullPointerException] {
      create(null, partTracker, recorder, monitor)
    }
  }

  it must "throw NullPointerException if constructing without a part tracker" in {
    intercept[NullPointerException] {
      create(workbench, null, recorder, monitor)
    }
  }

  it must "throw NullPointerException if constructing without a recorder" in {
    intercept[NullPointerException] {
      create(workbench, partTracker, null, monitor)
    }
  }

  it must "not throw exception if constructing without a user monitor" in {
    create(workbench, partTracker, recorder, null)
  }

  override protected def create() =
    new PartSessionTracker(workbench, partTracker, recorder, monitor)

  private def create(
    workbench: IWorkbench = workbench,
    partTracker: IListenableTracker[IPartFocusListener] = partTracker,
    recorder: IRecorder = recorder,
    monitor: IUserMonitor = monitor) =
    new PartSessionTracker(workbench, partTracker, recorder, monitor)

  private def verifyEvent(actual: Actual, expected: Expected) {
    actual.part must be(expected.part)

    val start = actual.start.getMillis
    start must be >= expected.preStart.getMillis
    start must be <= expected.postStart.getMillis

    val end = start + actual.duration.getMillis
    end must be >= expected.preEnd.getMillis
    end must be <= expected.postEnd.getMillis
  }

  private def notNullUserMonitorListener = notNull(classOf[IUserListener])

  private def mockListenerWithResult() = {
    val listener = mock[IPartSessionListener]
    val actual = new Actual
    doAnswer({ invocation: InvocationOnMock =>
      val args = invocation.getArguments
      actual.start = args(0).asInstanceOf[Instant]
      actual.duration = args(1).asInstanceOf[Duration]
      actual.part = args(2).asInstanceOf[IWorkbenchPart]
    }).when(listener).onPartSession(any[Instant], any[Duration], any[IWorkbenchPart])
    (listener, actual)
  }

  override protected type Tracker = PartSessionTracker
}