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

import org.eclipse.ui.PlatformUI.getWorkbench
import org.eclipse.ui.{ IWorkbench, IPerspectiveDescriptor }
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verifyZeroInteractions, verify, inOrder, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.tests.Workbenches.{ openRandomPerspective, closeAllPerspectives, closeAllParts }
import rabbit.tracking.util.{ Recorder, IRecorder }

/*
 * Also see PartSessionTrackerSpec, which has similar test requirements.
 */
@RunWith(classOf[JUnitRunner])
final class PerspectiveSessionTrackerSpec extends AbstractTrackerSpecBase {

  private class Expected {
    var preStart: Instant = _
    var postStart: Instant = _
    var preEnd: Instant = _
    var postEnd: Instant = _
    var perspective: IPerspectiveDescriptor = _
  }

  private class Actual {
    var start: Instant = _
    var duration: Duration = _
    var perspective: IPerspectiveDescriptor = _
  }

  private var workbench: IWorkbench = _
  private var perspectiveTracker: IListenableTracker[IPerspectiveFocusListener] = _
  private var recorder: IRecorder[IPerspectiveDescriptor] = _
  private var monitor: IUserMonitor = _
  private var monitorListeners: Seq[IUserListener] = _

  override def beforeEach {
    workbench = getWorkbench
    perspectiveTracker = new PerspectiveFocusTracker(workbench)
    recorder = Recorder.create()
    monitor = mock[IUserMonitor]
    monitorListeners = Seq.empty
    doAnswer { i: InvocationOnMock =>
      monitorListeners :+= i.getArguments()(0).asInstanceOf[IUserListener]
    } when monitor addListener any[IUserListener]

    super.beforeEach

    closeAllParts
  }

  behavior of classOf[PerspectiveSessionTracker].getSimpleName

  it must "be able to record without a user monitor" in {
    val (listener, actual) = mockListenerWithResult
    val tracker = create(monitor = null)
    tracker.enable;
    tracker.addListener(listener)

    val expected = openRandomPerspective
    tracker.disable

    actual.perspective must be(expected)
  }

  it must "not notify if disabled" in {
    val (listener, _) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.disable
    openRandomPerspective
    openRandomPerspective
    tracker.disable
    verifyZeroInteractions(listener)
  }

  it must "not notify removed listeners" in {
    val (listener, _) = mockListenerWithResult
    tracker.enable
    tracker.addListener(listener)
    tracker.removeListener(listener)
    openRandomPerspective
    tracker.disable
    verifyZeroInteractions(listener)
  }

  it must "record perspective focused duration" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.enable

    val expected = new Expected
    expected.preStart = now
    expected.perspective = openRandomPerspective
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    openRandomPerspective
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "stop recording on user inactive" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.enable

    val expected = new Expected
    expected.preStart = now
    expected.perspective = openRandomPerspective
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    monitorListeners.foreach(_.onInactive)
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "start recording if there is an active perspective on user active" in {
    val expected = openRandomPerspective
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.enable
    sleep(2)
    tracker.disable
    actual.perspective must be(expected)
  }

  it must "not start recording if there is no active perspective on user active" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    closeAllPerspectives

    tracker.enable
    sleep(2)
    tracker.disable

    actual.perspective must be(null)
  }

  it must "stop recording on disable" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)
    tracker.enable

    val expected = new Expected
    expected.preStart = now
    expected.perspective = openRandomPerspective
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    tracker.disable
    expected.postEnd = now

    verifyEvent(actual, expected)
  }

  it must "start recording on enable if there is a focused perspective" in {
    val (listener, actual) = mockListenerWithResult
    tracker.addListener(listener)

    val expected = new Expected
    expected.perspective = openRandomPerspective

    expected.preStart = now
    tracker.enable
    expected.postStart = now

    sleep(2)

    expected.preEnd = now
    openRandomPerspective
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

  it must "enable perspective tracker when enabling" in {
    val perspectiveTracker = mock[IListenableTracker[IPerspectiveFocusListener]]
    val tracker = create(perspectiveTracker = perspectiveTracker)
    tracker.enable
    verify(perspectiveTracker).enable
  }

  it must "disable perspective tracker when disabling" in {
    val perspectiveTracker = mock[IListenableTracker[IPerspectiveFocusListener]]
    val tracker = create(perspectiveTracker = perspectiveTracker)
    tracker.enable
    tracker.disable
    verify(perspectiveTracker).disable
  }

  it must "stop recorder when disabling" in {
    val recorder = mock[IRecorder[IPerspectiveDescriptor]]
    val tracker = create(recorder = recorder)
    tracker.enable
    tracker.disable
    val order = inOrder(recorder)
    order.verify(recorder).stop
    order.verifyNoMoreInteractions
  }

  it must "throw NullPointerException if constructing without a workbench" in {
    intercept[NullPointerException] {
      create(workbench = null)
    }
  }

  it must "throw NullPointerException if constructing without a perspective tracker" in {
    intercept[NullPointerException] {
      create(perspectiveTracker = null)
    }
  }

  it must "throw NullPointerException if constructing without a recorder" in {
    intercept[NullPointerException] {
      create(recorder = null)
    }
  }

  it must "not throw exception if constructing without a user monitor" in {
    create(monitor = null)
  }

  override protected type Tracker = PerspectiveSessionTracker

  override protected def create() = create(recorder, monitor, workbench, perspectiveTracker)

  private def create(
    recorder: IRecorder[IPerspectiveDescriptor] = recorder,
    monitor: IUserMonitor = monitor,
    workbench: IWorkbench = workbench,
    perspectiveTracker: IListenableTracker[IPerspectiveFocusListener] = perspectiveTracker) = {
    new PerspectiveSessionTracker(recorder, monitor, workbench, perspectiveTracker)
  }

  private def mockListenerWithResult() = {
    val listener = mock[IPerspectiveSessionListener]
    val actual = new Actual
    doAnswer { i: InvocationOnMock =>
      val args = i.getArguments
      actual.start = args(0).asInstanceOf[Instant]
      actual.duration = args(1).asInstanceOf[Duration]
      actual.perspective = args(2).asInstanceOf[IPerspectiveDescriptor]
    } when listener onPerspectiveSession (anyInstant, anyDuration, anyPerspective)

    (listener, actual)
  }

  private def verifyEvent(actual: Actual, expected: Expected) {
    actual.perspective must be(expected.perspective)

    val start = actual.start.getMillis
    start must be >= expected.preStart.getMillis
    start must be <= expected.postStart.getMillis

    val end = start + actual.duration.getMillis
    end must be >= expected.preEnd.getMillis
    end must be <= expected.postEnd.getMillis
  }

  private def anyInstant = any[Instant]
  private def anyDuration = any[Duration]
  private def anyPerspective = any[IPerspectiveDescriptor]
}