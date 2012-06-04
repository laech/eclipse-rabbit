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
import org.eclipse.ui.{ IWorkbenchPart, IWorkbench }
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verify, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.tests.Workbenches.{ openRandomPart, closeAllParts }
import rabbit.tracking.util.IRecorder

/*
 * Also see PerspectiveSessionTrackerSpec, which has similar test requirements.
 */
@RunWith(classOf[JUnitRunner])
final class PartSessionTrackerSpec
  extends AbstractSessionTrackerSpecBase[IWorkbenchPart, IPartSessionListener] {

  private var workbench: IWorkbench = _
  private var partTracker: IListenableTracker[IPartFocusListener] = _

  override def beforeEach {
    workbench = getWorkbench
    partTracker = new PartFocusTracker(workbench)
    super.beforeEach
  }

  behavior of classOf[PartSessionTracker].getSimpleName

  it must "start part tracker when starting" in {
    val partTracker = mock[IListenableTracker[IPartFocusListener]]
    val tracker = createTracker(partTracker = partTracker)
    tracker.start
    verify(partTracker).start
  }

  it must "stop part tracker when stopping" in {
    val partTracker = mock[IListenableTracker[IPartFocusListener]]
    val tracker = createTracker(partTracker = partTracker)
    tracker.start
    tracker.stop
    verify(partTracker).stop
  }

  it must "throw NullPointerException if constructing without a workbench" in {
    intercept[NullPointerException] {
      createTracker(workbench = null)
    }
  }

  it must "throw NullPointerException if constructing without a part tracker" in {
    intercept[NullPointerException] {
      createTracker(partTracker = null)
    }
  }

  override protected def create(
    recorder: IRecorder[IWorkbenchPart],
    monitor: IUserMonitor) =
    new PartSessionTracker(recorder, monitor, workbench, partTracker)

  private def createTracker(
    recorder: IRecorder[IWorkbenchPart] = recorder,
    monitor: IUserMonitor = monitor,
    workbench: IWorkbench = workbench,
    partTracker: IListenableTracker[IPartFocusListener] = partTracker) =
    new PartSessionTracker(recorder, monitor, workbench, partTracker)

  private def notNullUserMonitorListener = notNull.asInstanceOf[IUserListener]

  override protected def mockListenerWithResult() = {
    val listener = mock[IPartSessionListener]
    val actual = new Actual
    doAnswer({ invocation: InvocationOnMock =>
      val args = invocation.getArguments
      actual.instant = args(0).asInstanceOf[Instant]
      actual.duration = args(1).asInstanceOf[Duration]
      actual.target = args(2).asInstanceOf[IWorkbenchPart]
    }).when(listener).onPartSession(any[Instant], any[Duration], any[IWorkbenchPart])
    (listener, actual)
  }

  override protected def changeTarget() = openRandomPart

  override protected def removeAllTargets() = closeAllParts

  override protected type Tracker = PartSessionTracker
}