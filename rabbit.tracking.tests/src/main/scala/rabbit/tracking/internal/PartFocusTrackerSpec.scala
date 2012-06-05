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
import org.eclipse.ui.{ IWorkbenchWindow, IWorkbench, IWindowListener, IPartService, IPartListener }
import org.joda.time.Instant.now
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verifyZeroInteractions, verify, times, only, never, inOrder, atLeast }
import org.mockito.ArgumentCaptor
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import com.google.common.eventbus.EventBus

import rabbit.tracking.event.PartFocusEvent
import rabbit.tracking.internal.util.Workbenches
import rabbit.tracking.tests.Workbenches.{ openRandomPart, hide, closeAllParts, close, activate }
import rabbit.tracking.tests.{ Workbenches => testutil }
import rabbit.tracking.util.IClock
import rabbit.tracking.AbstractTrackerSpecBase

@RunWith(classOf[JUnitRunner])
final class PartFocusTrackerSpec extends AbstractTrackerSpecBase {

  private var windowsToClose: Seq[IWorkbenchWindow] = _
  private var bus: EventBus = _
  private var clock: IClock = _

  override def beforeEach {
    closeAllParts
    windowsToClose = Seq.empty
    bus = mock[EventBus]
    clock = mock[IClock]
    given(clock.now).willReturn(now)

    super.beforeEach
  }

  override def afterEach {
    super.afterEach
    windowsToClose foreach (close(_))
  }

  behavior of classOf[PartFocusTracker].getSimpleName

  it must "attach listener to workbench when starting" in {
    val workbench = mockWorkbenchWithNoWindow

    tracker = create(workbench)
    tracker.start

    verify(workbench).addWindowListener(any[IWindowListener])
    verify(workbench, never).removeWindowListener(any[IWindowListener])
  }

  it must "dettach listener from workbench when stopping" in {
    val workbench = mockWorkbenchWithNoWindow

    tracker = create(workbench)
    tracker.start
    tracker.stop

    val order = inOrder(workbench)
    order.verify(workbench).addWindowListener(any[IWindowListener])
    order.verify(workbench).removeWindowListener(any[IWindowListener])
  }

  it must "attach listener to part service when starting" in {
    val (workbench, service) = mockPartService

    tracker = create(workbench)
    tracker.start

    verify(service, only).addPartListener(any[IPartListener])
  }

  it must "dettach listener from part service when stopping" in {
    val (workbench, service) = mockPartService

    tracker = create(workbench)
    tracker.start
    tracker.stop

    val order = inOrder(service)
    order.verify(service).removePartListener(any[IPartListener])
    order.verifyNoMoreInteractions
  }

  it must "notify part focused due to part opened" in {
    val instant = now
    given(clock.now).willReturn(instant)

    tracker.start
    val part = openRandomPart

    val arg = argCaptor[PartFocusEvent]
    verify(bus).post(arg.capture)
    arg.getValue must be(new PartFocusEvent(instant, part, true))
  }

  it must "notify part focused due to part selected" in {
    val instant = now
    given(clock.now).willReturn(instant)
    val part1 = openRandomPart
    val part2 = openRandomPart
    activate(part1)
    tracker.start

    activate(part2)

    val arg = argCaptor[PartFocusEvent]
    verify(bus, times(2)).post(arg.capture) // Once for unfocus, once for focus
    arg.getValue must be(new PartFocusEvent(instant, part2, true))
  }

  it must "notify part unfocused due to new part selected" in {
    val instant = now
    given(clock.now).willReturn(instant)
    val part1 = openRandomPart
    val part2 = openRandomPart
    activate(part1)
    tracker.start

    activate(part2)

    val arg = argCaptor[PartFocusEvent]
    verify(bus, times(2)).post(arg.capture) // Once for unfocus, once for focus
    val values = arg.getAllValues
    // Second to last event should be part1 unfocused
    values.get(values.size - 2) must be(new PartFocusEvent(instant, part1, false))
  }

  it must "notify part unfocused due to new part opened" in {
    val instant = now
    given(clock.now).willReturn(instant)
    tracker.start
    val part = openRandomPart

    openRandomPart

    val arg = argCaptor[PartFocusEvent]
    verify(bus, times(3)).post(arg.capture) // Unfocus, focus, unfocus
    val values = arg.getAllValues
    // Second to last event should be part1 unfocused
    values.get(values.size - 2) must be(new PartFocusEvent(instant, part, false))
  }

  it must "notify part unfocused due to window unfocused" in {
    val instant = now
    given(clock.now).willReturn(instant)
    tracker.start
    val part = openRandomPart

    openWindow

    val arg = argCaptor[PartFocusEvent]
    verify(bus, times(2)).post(arg.capture) // Focus, unfocus
    arg.getValue must be(new PartFocusEvent(instant, part, false))
  }

  it must "notify part unfocused due to part closed" in {
    val instant = now
    given(clock.now).willReturn(instant)
    val part = openRandomPart
    tracker.start

    hide(part)

    val arg = argCaptor[PartFocusEvent]
    verify(bus).post(arg.capture)
    arg.getValue must be(new PartFocusEvent(instant, part, false))
  }

  it must "notify part unfocused due to foreground window being closed" in {
    val instant = now
    given(clock.now).willReturn(instant)
    val window = openWindow
    var part = openRandomPart(window)
    tracker.start
    verify(bus, never).post(any)

    close(window)

    val arg = argCaptor[PartFocusEvent]
    verify(bus).post(arg.capture)
    arg.getValue must be(new PartFocusEvent(instant, part, false))
  }

  it must "not notify part unfocused due to background window being closed" in {
    val background = openWindow
    openRandomPart(background)
    val foreground = openWindow
    closeAllParts(foreground)

    tracker.start

    close(background)
    verifyZeroInteractions(bus)
  }

  it must "notify part unforcused on old part before notifying part focused on new part" in {
    val instant = now
    given(clock.now).willReturn(instant)
    tracker.start
    val part1 = openRandomPart
    val part2 = openRandomPart

    val order = inOrder(bus)
    order.verify(bus).post(new PartFocusEvent(instant, part1, false))
    order.verify(bus).post(new PartFocusEvent(instant, part2, true))
    order.verifyNoMoreInteractions
  }

  it must "not notify when stopped" in {
    tracker.start
    tracker.stop
    openWindow
    openRandomPart
    openRandomPart
    verifyZeroInteractions(bus)
  }

  it must "track newly opened window" in {
    tracker.start
    val window = openWindow
    val part = openRandomPart(window)
    verify(bus, atLeast(1)).post(any[PartFocusEvent])
  }

  behavior of "WorkbenchWindow"

  it must "gain focus if a part is activated" in {

    /*
     * Current behavior of a window - if a part is activated programmatically in
     * a background window, it will bring that window into focus. We put it here
     * as a test case so that we know if this behavior changes and do something
     * about it.
     */

    val background = openWindow
    val part1 = openRandomPart(background)
    val part2 = openRandomPart(background)
    openWindow
    Workbenches.getFocusedWindow(getWorkbench) must not be background

    activate(part1)
    activate(part2)
    Workbenches.getFocusedWindow(getWorkbench) must be(background)
  }

  override protected type Tracker = PartFocusTracker

  override protected def create = new PartFocusTracker(bus, clock, getWorkbench)

  private def create(workbench: IWorkbench) = new PartFocusTracker(bus, clock, workbench)

  private def mockWorkbenchWithNoWindow = {
    val workbench = mock[IWorkbench]
    given(workbench.getWorkbenchWindows).willReturn(Array.empty[IWorkbenchWindow])
    given(workbench.getDisplay).willReturn(getWorkbench.getDisplay)
    workbench
  }

  private def mockPartService = {
    val service = mock[IPartService]
    val window = mock[IWorkbenchWindow]
    val workbench = mock[IWorkbench]
    given(workbench.getDisplay).willReturn(getWorkbench.getDisplay)
    given(window.getPartService).willReturn(service)
    given(workbench.getWorkbenchWindows).willReturn(Array(window))
    (workbench, service)
  }

  private def openWindow = {
    val window = testutil.openWindow
    windowsToClose = windowsToClose :+ window
    window
  }

  private def argCaptor[T: Manifest] = ArgumentCaptor.forClass(manifest[T].erasure)
}
