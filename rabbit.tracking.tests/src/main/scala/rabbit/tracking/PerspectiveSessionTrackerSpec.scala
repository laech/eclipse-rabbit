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
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verify, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.tests.Workbenches.{ openRandomPerspective, closeAllPerspectives, closeAllParts }
import rabbit.tracking.util.IRecorder

/*
 * Also see PartSessionTrackerSpec, which has similar test requirements.
 */
@RunWith(classOf[JUnitRunner])
final class PerspectiveSessionTrackerSpec
  extends AbstractSessionTrackerSpecBase[IPerspectiveDescriptor, IPerspectiveSessionListener] {

  private var workbench: IWorkbench = _
  private var perspectiveTracker: IListenableTracker[IPerspectiveFocusListener] = _

  override def beforeEach {
    workbench = getWorkbench
    perspectiveTracker = new PerspectiveFocusTracker(workbench)
    super.beforeEach
    closeAllParts
  }

  behavior of classOf[PerspectiveSessionTracker].getSimpleName

  it must "enable perspective tracker when enabling" in {
    val perspectiveTracker = mock[IListenableTracker[IPerspectiveFocusListener]]
    val tracker = createTracker(perspectiveTracker = perspectiveTracker)
    tracker.enable
    verify(perspectiveTracker).enable
  }

  it must "disable perspective tracker when disabling" in {
    val perspectiveTracker = mock[IListenableTracker[IPerspectiveFocusListener]]
    val tracker = createTracker(perspectiveTracker = perspectiveTracker)
    tracker.enable
    tracker.disable
    verify(perspectiveTracker).disable
  }

  it must "throw NullPointerException if constructing without a workbench" in {
    intercept[NullPointerException] {
      createTracker(workbench = null)
    }
  }

  it must "throw NullPointerException if constructing without a perspective tracker" in {
    intercept[NullPointerException] {
      createTracker(perspectiveTracker = null)
    }
  }

  override protected def create(
    recorder: IRecorder[IPerspectiveDescriptor],
    monitor: IUserMonitor) =
    createTracker(recorder, monitor, workbench, perspectiveTracker)

  private def createTracker(
    recorder: IRecorder[IPerspectiveDescriptor] = recorder,
    monitor: IUserMonitor = monitor,
    workbench: IWorkbench = workbench,
    perspectiveTracker: IListenableTracker[IPerspectiveFocusListener] = perspectiveTracker) = {
    new PerspectiveSessionTracker(recorder, monitor, workbench, perspectiveTracker)
  }

  override protected def mockListenerWithResult() = {
    val listener = mock[IPerspectiveSessionListener]
    val actual = new Actual
    doAnswer { i: InvocationOnMock =>
      val args = i.getArguments
      actual.start = args(0).asInstanceOf[Instant]
      actual.duration = args(1).asInstanceOf[Duration]
      actual.target = args(2).asInstanceOf[IPerspectiveDescriptor]
    } when listener onPerspectiveSession (anyInstant, anyDuration, anyPerspective)

    (listener, actual)
  }

  override protected def changeTarget() = openRandomPerspective

  override protected def removeAllTargets() = closeAllPerspectives

  override protected type Tracker = PerspectiveSessionTracker

  private def anyInstant = any[Instant]
  private def anyDuration = any[Duration]
  private def anyPerspective = any[IPerspectiveDescriptor]
}