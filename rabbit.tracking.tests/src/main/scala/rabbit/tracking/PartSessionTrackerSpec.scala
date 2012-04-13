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

import scala.collection.immutable.Set

import org.eclipse.ui.{ PlatformUI, IWorkbenchPart, IWorkbench }
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verifyZeroInteractions, verify, inOrder, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.util.IRecordListener
import rabbit.tracking.util.{ Record, IRecorder }

/* 
 * Unit tests for PartSessionTracker, see PartSessionTrackerIntegrationSpec
 * for some integration tests
 */
@RunWith(classOf[JUnitRunner])
final class PartSessionTrackerSpec extends AbstractTrackerSpecBase {

  private case class Result(val instant: Instant, val duration: Duration, val part: IWorkbenchPart)

  private var workbench: IWorkbench = _
  private var partTracker: IListenableTracker[IPartFocusListener] = _

  private var recorder: IRecorder = _
  private var recorderListeners: Set[IRecordListener] = _

  private var monitor: IUserMonitor = _
  private var monitorListeners: Set[IUserListener] = _

  private var actual: Result = _
  private var listener: IPartSessionListener = _

  override def beforeEach() {
    // Initialize monitor before super, create() depends on this
    recorderListeners = Set.empty
    recorder = mock[IRecorder]
    doAnswer({ invocation: InvocationOnMock =>
      recorderListeners += invocation.getArguments()(0).asInstanceOf[IRecordListener]
    }).when(recorder).addListener(any[IRecordListener])

    partTracker = mock[IListenableTracker[IPartFocusListener]]

    workbench = PlatformUI.getWorkbench

    monitorListeners = Set.empty
    monitor = mock[IUserMonitor]
    doAnswer({ invocation: InvocationOnMock =>
      monitorListeners += invocation.getArguments()(0).asInstanceOf[IUserListener]
    }).when(monitor).addListener(any[IUserListener])

    super.beforeEach()

    listener = mock[IPartSessionListener]
    tracker.addListener(listener)
    doAnswer({ invocation: InvocationOnMock =>
      val args = invocation.getArguments
      val start = args(0).asInstanceOf[Instant]
      val duration = args(1).asInstanceOf[Duration]
      val part = args(2).asInstanceOf[IWorkbenchPart]
      actual = Result(start, duration, part)
    }).when(listener).onPartSession(any[Instant], any[Duration], any[IWorkbenchPart])
  }

  behavior of "RecordingPartTracker"

  it must "detactch listener from user monitor when disabling" in {
    tracker.enable()
    tracker.disable()
    verify(monitor).removeListener(notNull(classOf[IUserListener]))
  }

  it must "attatch listener to user monitor when enabling" in {
    tracker.enable()
    verify(monitor).addListener(notNull(classOf[IUserListener]))
  }

  it must "enable part tracker when enabling" in {
    tracker.enable()
    verify(partTracker).enable()
  }

  it must "disable part tracker when disabling" in {
    tracker.enable()
    tracker.disable()
    verify(partTracker).disable()
  }

  it must "attach listener to part tracker for tracking part events" in {
    verify(partTracker).addListener(notNull(classOf[IPartFocusListener]))
  }

  it must "attach listener to recorder for recording elapsed time" in {
    verify(recorder).addListener(notNull(classOf[IRecordListener]))
  }

  it must "stop recorder when disabling" in {
    tracker.enable()
    tracker.disable()
    val order = inOrder(recorder)
    order.verify(recorder).stop()
    order.verifyNoMoreInteractions()
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

  it must "be able to record without a user monitor" in {
    tracker = create(workbench, partTracker, recorder, null)
    tracker.enable();
    tracker.addListener(listener)

    val part = mock[IWorkbenchPart]
    recorderListeners.foreach(_.onRecord(new Record(now, new Duration(10), part)))

    actual.part must be(part)
  }

  it must "not notify removed listeners" in {
    tracker.enable()
    tracker.addListener(listener)
    tracker.removeListener(listener)

    recorderListeners.foreach(_.onRecord(new Record(now, new Duration(10), mock[IWorkbenchPart])))

    tracker.disable()
    verifyZeroInteractions(listener)
  }

  it must "record part focused duration" in {
    tracker.enable()

    val instant = now
    val duration = new Duration(100)
    val part = mock[IWorkbenchPart]
    val expected = Result(instant, duration, part)

    recorderListeners.foreach(_.onRecord(new Record(instant, duration, part)))

    actual must be(expected)
  }

  it must "stop recording on user inactive" in {
    tracker.enable()
    monitorListeners.foreach(_.onInactive())
    verify(recorder).stop()
  }

  override protected type Tracker = PartSessionTracker

  override protected def create() = create(workbench, partTracker, recorder, monitor)

  private def create(
    workbench: IWorkbench,
    partTracker: IListenableTracker[IPartFocusListener],
    recorder: IRecorder,
    monitor: IUserMonitor) =
    new PartSessionTracker(workbench, partTracker, recorder, monitor)
}