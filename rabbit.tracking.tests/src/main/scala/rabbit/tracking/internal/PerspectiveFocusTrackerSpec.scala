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

package rabbit.tracking.internal

import org.eclipse.ui.PlatformUI.getWorkbench
import org.eclipse.ui.{ IWorkbenchWindow, IPerspectiveDescriptor }
import org.joda.time.Instant.now
import org.joda.time.Instant
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verifyZeroInteractions, verify, times, inOrder, atLeast }
import org.mockito.ArgumentCaptor
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import com.google.common.eventbus.EventBus

import rabbit.tracking.event.PerspectiveFocusEvent
import rabbit.tracking.internal.util.Workbenches.getFocusedWindow
import rabbit.tracking.tests.TestImplicits.funToRunnable
import rabbit.tracking.tests.Workbenches.{ openRandomPerspective, closeAllPerspectives, closeAllParts, close }
import rabbit.tracking.tests.Workbenches
import rabbit.tracking.util.IClock
import rabbit.tracking.AbstractTrackerSpecBase

@RunWith(classOf[JUnitRunner])
final class PerspectiveFocusTrackerSpec extends AbstractTrackerSpecBase {

  behavior of classOf[PerspectiveFocusTracker].getSimpleName

  it must "notify perspective focused due to perspective switched" in {
    tracker.start

    val perspective = openRandomPerspective

    val arg = argCaptor[PerspectiveFocusEvent]
    verify(eventBus).post(arg.capture)
    arg.getValue must be(perspectiveFocusEvent(clock.now, perspective, true))
  }

  it must "notify perspective unfocused due to perspective switched" in {
    val perspective = openRandomPerspective
    tracker.start

    openRandomPerspective

    val arg = argCaptor[PerspectiveFocusEvent]
    verify(eventBus, times(2)).post(arg.capture) // Unfocus, then focus
    arg.getAllValues().get(0) must be(perspectiveFocusEvent(clock.now, perspective, false))
  }

  it must "notify perspective unfocused due to window unfocused" in {
    val perspective = openRandomPerspective
    tracker.start

    openWindow

    val arg = argCaptor[PerspectiveFocusEvent]
    verify(eventBus, times(2)).post(arg.capture) // Unfocus, then focus
    arg.getAllValues().get(0) must be(perspectiveFocusEvent(clock.now, perspective, false))
  }

  it must "notify perspective unfocused due to perspective closed" in {
    val perspective = openRandomPerspective
    tracker.start

    closeAllPerspectives

    val arg = argCaptor[PerspectiveFocusEvent]
    verify(eventBus).post(arg.capture)
    arg.getValue must be(perspectiveFocusEvent(clock.now, perspective, false))
  }

  it must "notify perspective unfocused due to foreground window being closed" in {
    val window = openWindow
    val perspective = openRandomPerspective(window)
    tracker.start

    close(window)

    val arg = argCaptor[PerspectiveFocusEvent]
    verify(eventBus).post(arg.capture) // Unfocus, then focus
    arg.getValue must be(perspectiveFocusEvent(clock.now, perspective, false))
  }

  it must "not notify perspective unfocused due to background window being closed" in {
    val background = openWindow
    openRandomPerspective(background)
    val foreground = openWindow
    closeAllPerspectives(foreground)
    tracker.start

    close(background)

    verifyZeroInteractions(eventBus)
  }

  it must "not notify perspective unfocused due to a non visible perspective being closed" in {
    val window = getFocusedWindow(getWorkbench)
    val background = openRandomPerspective(window)
    val foreground = openRandomPerspective(window)
    tracker.start

    window.getShell.getDisplay.syncExec(() => {
      window.getActivePage.closePerspective(background, false, false)
    })

    verifyZeroInteractions(eventBus)
  }

  it must "notify perspective unfocused on old perspective before notifying perspective focused on new perspective" in {
    closeAllPerspectives
    tracker.start

    val p1 = openRandomPerspective
    val p2 = openRandomPerspective

    val arg = argCaptor[PerspectiveFocusEvent]
    verify(eventBus, times(3)).post(arg.capture) // Focus, unfocus, focus
    arg.getAllValues.get(0) must be(perspectiveFocusEvent(clock.now, p1, true))
    arg.getAllValues.get(1) must be(perspectiveFocusEvent(clock.now, p1, false))
    arg.getAllValues.get(2) must be(perspectiveFocusEvent(clock.now, p2, true))

    println(arg.getAllValues)

    //    val order = inOrder(eventBus)
    //    order.verify(eventBus).post(perspectiveFocusEvent(clock.now, p1, false))
    //    order.verify(eventBus).post(perspectiveFocusEvent(clock.now, p2, true))
    //    order.verifyNoMoreInteractions
  }

  it must "not notify when stopped" in {
    tracker.start
    tracker.stop
    openRandomPerspective
    verifyZeroInteractions(eventBus)
  }

  it must "track newly opened window" in {
    tracker.start
    val window = openWindow
    val perspective = openRandomPerspective(window)
    verify(eventBus, atLeast(2)).post(any) // One for old window, some for new window
  }

  behavior of "WorkbenchWindow" // Current behaviors of the workbench that we care about

  it must "gain focus on perspective change" in {
    val background = openWindow
    val foreground = openWindow
    foreground must be(getFocusedWindow(getWorkbench))

    openRandomPerspective(background)

    background must be(getFocusedWindow(getWorkbench))
  }

  private var windowsToClose: Seq[IWorkbenchWindow] = _
  private var eventBus: EventBus = _
  private var clock: IClock = _

  override protected type Tracker = PerspectiveFocusTracker

  override def beforeEach {
    closeAllParts
    closeAllPerspectives

    windowsToClose = Seq.empty
    eventBus = mock[EventBus]
    clock = mock[IClock]
    given(clock.now).willReturn(now)

    super.beforeEach
  }

  override def afterEach {
    super.afterEach
    windowsToClose.foreach(close(_))
    closeAllPerspectives
    openRandomPerspective
  }

  override protected def create =
    new PerspectiveFocusTracker(eventBus, clock, getWorkbench)

  private def currentPerspective =
    getWorkbench.getActiveWorkbenchWindow.getActivePage.getPerspective

  private def whatever = any[IPerspectiveDescriptor]

  private def openWindow = {
    val window = Workbenches.openWindow
    windowsToClose :+= window
    window
  }

  private def perspectiveFocusEvent(instant: Instant, perspective: IPerspectiveDescriptor, focused: Boolean) =
    new PerspectiveFocusEvent(instant, perspective, focused)

  private def argCaptor[T: Manifest] = ArgumentCaptor.forClass(manifest[T].erasure)
}