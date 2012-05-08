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

import org.eclipse.ui.PlatformUI.getWorkbench
import org.eclipse.ui.{ IWorkbenchWindow, IPerspectiveDescriptor }
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verifyZeroInteractions, verify, never, inOrder }
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.internal.util.Workbenches.getFocusedWindow
import rabbit.tracking.tests.TestImplicits.funToRunnable
import rabbit.tracking.tests.Workbenches.{ openRandomPerspective, closeAllPerspectives, closeAllParts, close }
import rabbit.tracking.tests.Workbenches

@RunWith(classOf[JUnitRunner])
final class PerspectiveFocusTrackerSpec extends AbstractTrackerSpecBase {

  private var windowsToClose: Seq[IWorkbenchWindow] = _
  private var listener: IPerspectiveFocusListener = _

  override protected type Tracker = PerspectiveFocusTracker

  override def beforeEach {
    super.beforeEach
    listener = mockListener
    tracker.addListener(listener)
    closeAllParts
    closeAllPerspectives
    windowsToClose = Seq.empty
  }

  override def afterEach {
    super.afterEach
    windowsToClose.foreach(close(_))
    closeAllPerspectives
    openRandomPerspective
  }

  behavior of classOf[PerspectiveFocusTracker].getSimpleName

  it must "notify perspective focused due to perspective switched" in {
    tracker.start
    val perspective = openRandomPerspective
    verify(listener).onPerspectiveFocused(perspective)
  }

  it must "notify perspective unfocused due to perspective switched" in {
    val perspective = openRandomPerspective
    tracker.start
    openRandomPerspective
    verify(listener).onPerspectiveUnfocused(perspective)
  }

  it must "notify perspective unfocused due to window unfocused" in {
    val perspective = openRandomPerspective
    tracker.start
    openWindow
    verify(listener).onPerspectiveUnfocused(perspective)
  }

  it must "notify perspective unfocused due to perspective closed" in {
    val perspective = openRandomPerspective
    tracker.start
    closeAllPerspectives
    verify(listener).onPerspectiveUnfocused(perspective)
  }

  it must "notify perspective unfocused due to foreground window being closed" in {
    val window = openWindow
    val perspective = openRandomPerspective(window)
    tracker.start

    close(window)
    verify(listener).onPerspectiveUnfocused(perspective)
  }

  it must "not notify perspective unfocused due to background window being closed" in {
    val background = openWindow
    openRandomPerspective(background)
    val foreground = openWindow
    closeAllPerspectives(foreground)

    tracker.start

    close(background)
    verifyZeroInteractions(listener)
  }

  it must "not notify perspective unfocused due to a non visible perspective being closed" in {
    val window = getFocusedWindow(getWorkbench)
    val background = openRandomPerspective(window)
    val foreground = openRandomPerspective(window)
    tracker.start

    window.getShell.getDisplay.syncExec(() => {
      window.getActivePage.closePerspective(background, false, false)
    })

    verify(listener, never).onPerspectiveUnfocused(any[IPerspectiveDescriptor])
  }

  it must "notify perspective unfocused on old perspective before notifying perspective focused on new perspective" in {
    closeAllPerspectives
    tracker.start
    openRandomPerspective
    openRandomPerspective

    val order = inOrder(listener)
    order.verify(listener).onPerspectiveUnfocused(whatever)
    order.verify(listener).onPerspectiveFocused(whatever)
    order.verifyNoMoreInteractions
  }

  it must "not notify when stopped" in {
    tracker.start
    tracker.stop
    openRandomPerspective
    verifyZeroInteractions(listener)
  }

  it must "track newly opened window" in {
    tracker.start
    val window = openWindow
    val perspective = openRandomPerspective(window)
    verify(listener).onPerspectiveFocused(perspective)
  }

  behavior of "WorkbenchWindow" // Current behaviors of the workbench that we care about

  it must "gain focus on perspective change" in {
    val background = openWindow
    val foreground = openWindow
    foreground must be(getFocusedWindow(getWorkbench))

    openRandomPerspective(background)

    background must be(getFocusedWindow(getWorkbench))
  }

  override protected def create = new PerspectiveFocusTracker(getWorkbench)

  private def currentPerspective =
    getWorkbench.getActiveWorkbenchWindow.getActivePage.getPerspective

  private def whatever = any[IPerspectiveDescriptor]

  private def mockListener = mock[IPerspectiveFocusListener]

  private def openWindow = {
    val window = Workbenches.openWindow
    windowsToClose :+= window
    window
  }
}