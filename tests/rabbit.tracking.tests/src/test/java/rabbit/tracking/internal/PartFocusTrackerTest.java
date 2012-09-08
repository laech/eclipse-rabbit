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

package rabbit.tracking.internal;

import static com.google.common.collect.Lists.newArrayListWithExpectedSize;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.is;
import static org.joda.time.Instant.now;
import static org.junit.Assume.assumeThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static rabbit.tracking.internal.util.Workbenches.focusedWindowOf;
import static rabbit.tracking.tests.Instants.epoch;
import static rabbit.tracking.tests.TestWorkbenches.activate;
import static rabbit.tracking.tests.TestWorkbenches.close;
import static rabbit.tracking.tests.TestWorkbenches.closePartsOfCurrentWindow;
import static rabbit.tracking.tests.TestWorkbenches.closePartsOf;
import static rabbit.tracking.tests.TestWorkbenches.hide;
import static rabbit.tracking.tests.TestWorkbenches.openRandomPartOnCurrentWindow;
import static rabbit.tracking.tests.TestWorkbenches.openRandomPartOn;

import java.util.List;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.joda.time.Instant;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import rabbit.tracking.AbstractTrackerTestBase;
import rabbit.tracking.event.PartFocusEvent;
import rabbit.tracking.tests.TestWorkbenches;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;

public final class PartFocusTrackerTest
    extends AbstractTrackerTestBase<PartFocusTracker> {

  private static class WorkbenchPartServicePair {
    final IWorkbench workbench;
    final IPartService service;

    WorkbenchPartServicePair(IWorkbench workbench, IPartService service) {
      this.workbench = workbench;
      this.service = service;
    }
  }

  private List<IWorkbenchWindow> windows;
  private IClock clock;
  private EventBus bus;

  @Override public void after() {
    super.after();
    for (IWorkbenchWindow window : windows)
      close(window);
  }

  /*
   * Current behavior of a window - if a part is activated programmatically in a
   * background window, it will bring that window into focus. We put it here as
   * a test case so that we know if this behavior changes and do something about
   * it.
   */
  @Test public void assumesWindowWillGainFocusOnPartActivate() {
    IWorkbenchWindow bg = openWindow();
    IViewPart part1 = openRandomPartOn(bg);
    IViewPart part2 = openRandomPartOn(bg);
    openWindow();
    assumeThat(focusedWindowOf(getWorkbench()), is(bg));

    activate(part1);
    activate(part2);
    assumeThat(focusedWindowOf(getWorkbench()), is(bg));
  }

  @Test public void attachesPartListenerOnStart() {
    WorkbenchPartServicePair pair = mockPartService();
    PartFocusTracker tracker = newTracker(pair.workbench);

    try {
      tracker.start();
      verify(pair.service, only()).addPartListener(anyPartListener());

    } finally {
      tracker.stop();
    }
  }

  @Test public void attachesWorkbenchListenerOnStart() {
    IWorkbench bench = mockWindowlessWorkbench();
    PartFocusTracker tracker = newTracker(bench);

    try {
      tracker.start();

      verify(bench).addWindowListener(anyWindowListener());
      verify(bench, never()).removeWindowListener(anyWindowListener());

    } finally {
      tracker.stop();
    }
  }

  @Test public void detachesPartListenerOnStop() {
    WorkbenchPartServicePair pair = mockPartService();
    PartFocusTracker tracker = newTracker(pair.workbench);

    try {
      tracker.start();
      tracker.stop();

      InOrder order = inOrder(pair.service);
      order.verify(pair.service).removePartListener(anyPartListener());
      order.verifyNoMoreInteractions();

    } finally {
      tracker.stop();
    }
  }

  @Test public void detachesWorkbenchListenerOnStop() {
    IWorkbench bench = mockWindowlessWorkbench();
    PartFocusTracker tracker = newTracker(bench);

    try {
      tracker.start();
      tracker.stop();

      InOrder order = inOrder(bench);
      order.verify(bench).addWindowListener(anyWindowListener());
      order.verify(bench).removeWindowListener(anyWindowListener());

    } finally {
      tracker.stop();
    }
  }

  @Test public void notifiesOldPartUnfocusedBeforeNotifyingNewPartFocused() {
    given(clock.now()).willReturn(epoch());
    tracker().start();
    IViewPart part1 = openRandomPartOnCurrentWindow();
    IViewPart part2 = openRandomPartOnCurrentWindow();

    InOrder order = inOrder(bus);
    order.verify(bus).post(event(epoch(), part1, false));
    order.verify(bus).post(event(epoch(), part2, true));
    order.verifyNoMoreInteractions();
  }

  @Test public void notifiesUnfocusFocusOnPartOpen() {
    Instant time = now();
    given(clock.now()).willReturn(time);

    IViewPart part1 = openRandomPartOnCurrentWindow();
    tracker().start();
    IViewPart part2 = openRandomPartOnCurrentWindow();

    ArgumentCaptor<PartFocusEvent> arg = captureEvent();
    verify(bus, times(2)).post(arg.capture());
    assertThat(arg.getAllValues(), hasItems(
        event(time, part1, false),
        event(time, part2, true)));
  }

  @Test public void notifiesUnfocusFocusOnPartSelect() {
    given(clock.now()).willReturn(epoch());

    IViewPart part1 = openRandomPartOnCurrentWindow();
    IViewPart part2 = openRandomPartOnCurrentWindow();
    activate(part1);
    tracker().start();

    activate(part2);

    ArgumentCaptor<PartFocusEvent> arg = captureEvent();
    verify(bus, times(2)).post(arg.capture());
    assertThat(arg.getAllValues(), hasItems(
        event(epoch(), part1, false),
        event(epoch(), part2, true)));
  }

  @Test public void notifiesUnfocusOnForegroundWindowClose() {
    given(clock.now()).willReturn(epoch());
    IWorkbenchWindow window = openWindow();
    IViewPart part = openRandomPartOn(window);
    tracker().start();
    verify(bus, never()).post(any());

    close(window);

    ArgumentCaptor<PartFocusEvent> arg = captureEvent();
    verify(bus).post(arg.capture());
    assertThat(arg.getValue(), is(event(epoch(), part, false)));
  }

  @Test public void notifiesUnfocusOnPartClose() {
    given(clock.now()).willReturn(epoch());
    IViewPart part = openRandomPartOnCurrentWindow();
    tracker().start();

    hide(part);

    ArgumentCaptor<PartFocusEvent> arg = captureEvent();
    verify(bus).post(arg.capture());
    assertThat(arg.getValue(), is(event(epoch(), part, false)));
  }

  @Test public void notifiesUnfocusOnWindowUnfocus() {
    given(clock.now()).willReturn(epoch());

    tracker().start();
    IViewPart part = openRandomPartOnCurrentWindow();

    openWindow();

    ArgumentCaptor<PartFocusEvent> arg = captureEvent();
    verify(bus, times(2)).post(arg.capture());
    assertThat(arg.getValue(), is(event(epoch(), part, false)));
  }

  @Test public void notNotifyUnfocusOnBackgroundWindowClose() {
    IWorkbenchWindow bg = openWindow();
    openRandomPartOn(bg);

    IWorkbenchWindow fg = openWindow();
    closePartsOf(fg);

    tracker().start();
    close(bg);

    verifyZeroInteractions(bus);
  }

  @Test public void notNotifyWhenStopped() {
    tracker().start();
    tracker().stop();
    openWindow();
    openRandomPartOnCurrentWindow();
    openRandomPartOnCurrentWindow();
    verifyZeroInteractions(bus);
  }

  @Test public void tracksNewlyOpenedWindow() {
    tracker().start();
    openRandomPartOn(openWindow());
    verify(bus, atLeast(2)).post(any(PartFocusEvent.class));
  }

  @Override protected void init() {
    super.init();
    windows = newArrayListWithExpectedSize(1);
    bus = mock(EventBus.class);
    clock = mock(IClock.class);
    given(clock.now()).willReturn(now());

    closePartsOfCurrentWindow();
  }

  @Override protected PartFocusTracker newTracker() {
    return new PartFocusTracker(bus, clock, getWorkbench());
  }

  private IPartListener anyPartListener() {
    return any(IPartListener.class);
  }

  private IWindowListener anyWindowListener() {
    return any(IWindowListener.class);
  }

  private <T> T[] array(T... xs) {
    return xs;
  }

  private ArgumentCaptor<PartFocusEvent> captureEvent() {
    return ArgumentCaptor.forClass(PartFocusEvent.class);
  }

  private PartFocusEvent event(Instant time, IViewPart part, boolean focused) {
    return new PartFocusEvent(time, part, focused);
  }

  private WorkbenchPartServicePair mockPartService() {
    IPartService service = mock(IPartService.class);

    IWorkbenchWindow window = mock(IWorkbenchWindow.class);
    given(window.getPartService()).willReturn(service);

    IWorkbench bench = mock(IWorkbench.class);
    given(bench.getDisplay()).willReturn(getWorkbench().getDisplay());
    given(bench.getWorkbenchWindows()).willReturn(array(window));

    return new WorkbenchPartServicePair(bench, service);
  }

  private IWorkbench mockWindowlessWorkbench() {
    IWorkbench workbench = mock(IWorkbench.class);
    given(workbench.getWorkbenchWindows()).willReturn(new IWorkbenchWindow[0]);
    given(workbench.getDisplay()).willReturn(getWorkbench().getDisplay());
    return workbench;
  }

  private PartFocusTracker newTracker(IWorkbench workbench) {
    return new PartFocusTracker(bus, clock, workbench);
  }

  private IWorkbenchWindow openWindow() {
    IWorkbenchWindow window = TestWorkbenches.openWindow();
    windows.add(window);
    return window;
  }
}
