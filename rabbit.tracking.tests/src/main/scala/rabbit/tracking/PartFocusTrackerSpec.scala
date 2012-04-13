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
import org.eclipse.ui.{ IWorkbenchWindow, IWorkbenchPart, IWorkbench, IWindowListener, IPartService, IPartListener }
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verifyZeroInteractions, verify, only, never, inOrder }
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.internal.util.Workbenches
import rabbit.tracking.tests.WorkbenchTestUtil.{ openRandomPart, hide, closeAllParts, close, activate }
import rabbit.tracking.tests.WorkbenchTestUtil

@RunWith(classOf[JUnitRunner])
final class PartFocusTrackerSpec extends AbstractTrackerSpecBase {

  override protected type Tracker = PartFocusTracker

  private var windowsToClose: Seq[IWorkbenchWindow] = _
  private var listener: IPartFocusListener = _

  override def beforeEach() {
    super.beforeEach()
    listener = mockListener()
    tracker.addListener(listener)
    closeAllParts();
    windowsToClose = Seq.empty
  }

  override def afterEach() {
    super.afterEach()
    windowsToClose foreach (close(_))
  }

  behavior of "PartTracker"

  it must "attach listener to workbench when enabling" in {
    val workbench = mockWorkbenchWithNoWindow()

    tracker = create(workbench)
    tracker.enable()

    verify(workbench).addWindowListener(any[IWindowListener])
    verify(workbench, never).removeWindowListener(any[IWindowListener])
  }

  it must "dettach listener from workbench when disabling" in {
    val workbench = mockWorkbenchWithNoWindow()

    tracker = create(workbench)
    tracker.enable()
    tracker.disable()

    val order = inOrder(workbench)
    order.verify(workbench).addWindowListener(any[IWindowListener])
    order.verify(workbench).removeWindowListener(any[IWindowListener])
  }

  it must "attach listener to part service when enabling" in {
    val (workbench, service) = mockPartService

    tracker = create(workbench)
    tracker.enable()

    verify(service, only).addPartListener(any[IPartListener])
  }

  it must "dettach listener from part service when disabling" in {
    val (workbench, service) = mockPartService

    tracker = create(workbench)
    tracker.enable()
    tracker.disable()

    val order = inOrder(service)
    order.verify(service).removePartListener(any[IPartListener])
    order.verifyNoMoreInteractions()
  }

  it must "notify part focused when a part is already focused when enabled" in {
    val part = activate(openRandomPart())
    tracker.enable()
    verify(listener, only).onPartFocused(part)
  }

  it must "notify part focused due to part opened" in {
    tracker.enable()
    val part = openRandomPart()
    verify(listener).onPartFocused(part)
  }

  it must "notify part focused due to part selected" in {
    val part1 = openRandomPart()
    val part2 = openRandomPart()
    activate(part1)
    tracker.enable()

    activate(part2)
    verify(listener).onPartFocused(part2)
  }

  it must "notify part unfocused due to new part selected" in {
    val part1 = openRandomPart()
    val part2 = openRandomPart()
    activate(part1)
    tracker.enable()

    activate(part2)
    verify(listener).onPartUnfocused(part1)
  }

  it must "notify part unfocused due to new part opened" in {
    tracker.enable()
    val part = openRandomPart()

    openRandomPart()
    verify(listener).onPartUnfocused(part)
  }

  it must "notify part unfocused due to window unfocused" in {
    tracker.enable()
    val part = openRandomPart()

    openWindow()
    verify(listener).onPartUnfocused(part)
  }

  it must "notify part unfocused due to part closed" in {
    val part = openRandomPart();
    tracker.enable()

    hide(part)
    verify(listener).onPartUnfocused(part)
  }

  it must "notify part unfocused due to window closed" in {
    val window = openWindow()
    var part = openRandomPart(window)
    tracker.enable()
    verify(listener, never).onPartUnfocused(whatever)

    close(window)
    verify(listener).onPartUnfocused(part)
  }

  it must "notify part unforcused on old part before notifying part focused on new part" in {
    tracker.enable()
    openRandomPart()
    openRandomPart()

    val order = inOrder(listener)
    order.verify(listener).onPartUnfocused(whatever)
    order.verify(listener).onPartFocused(whatever)
    order.verifyNoMoreInteractions()
  }

  it must "not notify when disabled" in {
    tracker.disable()
    openWindow()
    openRandomPart()
    openRandomPart()
    verifyZeroInteractions(listener)
  }

  it must "track newly opened window" in {
    tracker.enable()
    val window = openWindow()
    val part = openRandomPart(window)
    verify(listener).onPartFocused(part)
  }

  behavior of "WorkbenchWindow"

  it must "gain focus if a part is activated" in {

    /*
     * Current behavior of a window - if a part is activated programmatically in
     * a background window, it will bring that window into focus. We put it here
     * as a test case so that we know if this behavior changes and do something
     * about it.
     */

    val background = openWindow()
    val part1 = openRandomPart(background)
    val part2 = openRandomPart(background)
    openWindow()
    Workbenches.getFocusedWindow(getWorkbench) must not be background

    activate(part1)
    activate(part2)
    Workbenches.getFocusedWindow(getWorkbench) must be(background)
  }

  override protected def create() = new PartFocusTracker(getWorkbench)

  private def create(workbench: IWorkbench) = new PartFocusTracker(workbench)

  private def whatever = any[IWorkbenchPart]

  private def mockListener() = mock[IPartFocusListener]

  private def mockWorkbenchWithNoWindow() = {
    val workbench = mock[IWorkbench]
    given(workbench.getWorkbenchWindows).willReturn(Array.empty[IWorkbenchWindow])
    given(workbench.getDisplay).willReturn(getWorkbench.getDisplay())
    workbench
  }

  private def mockPartService() = {
    val service = mock[IPartService]
    val window = mock[IWorkbenchWindow]
    val workbench = mock[IWorkbench]
    given(workbench.getDisplay).willReturn(getWorkbench.getDisplay())
    given(window.getPartService).willReturn(service)
    given(workbench.getWorkbenchWindows).willReturn(Array(window))
    (workbench, service)
  }

  private def openWindow() = {
    val window = WorkbenchTestUtil.openWindow()
    windowsToClose = windowsToClose :+ window
    window
  }
}
