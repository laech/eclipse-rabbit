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
import org.eclipse.ui.IWorkbenchPart
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verifyZeroInteractions, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.tests.WorkbenchTestUtil.{ openRandomPart, closeAllParts }
import rabbit.tracking.util.Recorder

/*
 * Integration tests for PartSessionTracker, see PartSessionTrackerSpec
 * for some unit tests.
 */
@RunWith(classOf[JUnitRunner])
final class PartSessionTrackerIntegrationSpec extends AbstractTrackerSpecBase {

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

  private var monitor: IUserMonitor = _
  private var monitorListeners: Set[IUserListener] = _

  private var actual: Actual = _
  private var listener: IPartSessionListener = _

  override def beforeEach() {
    // Initialize monitor before super, create() depends on this
    monitorListeners = Set.empty
    monitor = mock[IUserMonitor]
    doAnswer({ invocation: InvocationOnMock =>
      monitorListeners += invocation.getArguments()(0).asInstanceOf[IUserListener]
    }).when(monitor).addListener(any[IUserListener])

    super.beforeEach()

    actual = new Actual
    listener = mock[IPartSessionListener]
    tracker.addListener(listener)
    doAnswer({ invocation: InvocationOnMock =>
      val args = invocation.getArguments
      actual.start = args(0).asInstanceOf[Instant]
      actual.duration = args(1).asInstanceOf[Duration]
      actual.part = args(2).asInstanceOf[IWorkbenchPart]
    }).when(listener).onPartSession(any[Instant], any[Duration], any[IWorkbenchPart])
  }

  behavior of "RecordingPartTracker"

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

  override protected def create() = new PartSessionTracker(
    getWorkbench, new PartFocusTracker(getWorkbench), Recorder.create(), monitor)

  private def withMonitor(monitor: IUserMonitor, listeners: IPartSessionListener*) = {
    val PartSessionTrackerker = new PartSessionTracker(
      getWorkbench, new PartFocusTracker(getWorkbench), Recorder.create(), monitor)
    listeners.foreach(tracker.addListener(_))
    tracker
  }

  private def verifyEvent(expected: Expected) {
    actual.part must be(expected.part)

    val start = actual.start.getMillis
    start must be >= expected.preStart.getMillis
    start must be <= expected.postStart.getMillis

    val end = start + actual.duration.getMillis
    end must be >= expected.preEnd.getMillis
    end must be <= expected.postEnd.getMillis
  }

  private def notNullUserMonitorListener = notNull(classOf[IUserListener])

  override protected type Tracker = PartSessionTracker
}