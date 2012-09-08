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
import static java.util.Arrays.asList;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.Assume.assumeThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static rabbit.tracking.internal.util.Workbenches.focusedWindowOf;
import static rabbit.tracking.tests.Instants.epoch;
import static rabbit.tracking.tests.TestWorkbenches.close;
import static rabbit.tracking.tests.TestWorkbenches.closePartsOfCurrentWindow;
import static rabbit.tracking.tests.TestWorkbenches.closePerspective;
import static rabbit.tracking.tests.TestWorkbenches.closePerspectivesOf;
import static rabbit.tracking.tests.TestWorkbenches.closePerspectivesOfCurrentWindow;
import static rabbit.tracking.tests.TestWorkbenches.openRandomPerspectiveOn;
import static rabbit.tracking.tests.TestWorkbenches.openRandomPerspectiveOnCurrentWindow;

import java.util.List;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.joda.time.Instant;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import rabbit.tracking.AbstractTrackerTestBase;
import rabbit.tracking.event.PerspectiveFocusEvent;
import rabbit.tracking.tests.TestWorkbenches;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;

public final class PerspectiveFocusTrackerTest
    extends AbstractTrackerTestBase<PerspectiveFocusTracker> {

  private EventBus bus;
  private List<IWorkbenchWindow> windows;
  private IClock epochClock;
  private ArgumentCaptor<PerspectiveFocusEvent> arg;

  @Override public void after() {
    super.after();
    close(windows);
    closePerspectivesOfCurrentWindow();
    openRandomPerspectiveOnCurrentWindow();
  }

  @Test public void assumesWindowWillGainFocusOnPerspectiveChange() {
    IWorkbench workbench = getWorkbench();
    IWorkbenchWindow bg = openWindow();
    IWorkbenchWindow fg = openWindow();
    assumeThat(fg, is(focusedWindowOf(workbench)));

    openRandomPerspectiveOn(bg);
    assumeThat(bg, is(focusedWindowOf(workbench)));
  }

  @Test public void notifiesOldPerspectiveUnfocusBeforeNotifyingNewPerspectiveFocus() {
    closePerspectivesOfCurrentWindow();
    tracker().start();

    IPerspectiveDescriptor p1 = openRandomPerspectiveOnCurrentWindow();
    IPerspectiveDescriptor p2 = openRandomPerspectiveOnCurrentWindow();

    verify(bus, times(3)).post(arg.capture());
    assertThat(arg.getAllValues(), is(asList(
        event(epoch(), p1, true),
        event(epoch(), p1, false),
        event(epoch(), p2, true))));
  }

  @Test public void notifiesUnfocusFocusOnPerspectiveSwitch() {
    IPerspectiveDescriptor p1 = openRandomPerspectiveOnCurrentWindow();
    tracker().start();
    IPerspectiveDescriptor p2 = openRandomPerspectiveOnCurrentWindow();

    verify(bus, times(2)).post(arg.capture());
    assertThat(arg.getAllValues(), is(asList(
        event(epoch(), p1, unfocused()),
        event(epoch(), p2, focused()))));
  }

  @Test public void notifiesUnfocusOnForegroundWindowClose() {
    IWorkbenchWindow window = openWindow();
    IPerspectiveDescriptor perspective = openRandomPerspectiveOn(window);
    tracker().start();

    close(window);

    verify(bus).post(arg.capture());
    assertThat(arg.getValue(), is(event(epoch(), perspective, unfocused())));
  }

  @Test public void notifiesUnfocusOnPerspectiveClose() {
    IPerspectiveDescriptor perspective = openRandomPerspectiveOnCurrentWindow();
    tracker().start();

    closePerspectivesOfCurrentWindow();

    verify(bus).post(arg.capture());
    assertThat(arg.getValue(), is(event(epoch(), perspective, unfocused())));
  }

  @Test public void notifiesUnfocusOnWindowUnfocus() {
    IPerspectiveDescriptor p = openRandomPerspectiveOnCurrentWindow();
    tracker().start();

    openWindow();

    verify(bus, times(2)).post(arg.capture()); // [unfocusOfOld, focusOfNew]
    assertThat(arg.getAllValues().get(0), is(event(epoch(), p, unfocused())));
  }

  @Test public void notNotifyUnfocusOnBackgroundWindowClose() {
    IWorkbenchWindow bg = openWindow();
    openRandomPerspectiveOn(bg);

    IWorkbenchWindow fg = openWindow();
    closePerspectivesOf(fg);

    tracker().start();
    close(bg);

    verifyZeroInteractions(bus);
  }

  @Test public void notNotifyUnfocusOnInvisiblePerspectiveClose() {
    final IWorkbenchWindow window = focusedWindowOf(getWorkbench());
    final IPerspectiveDescriptor bg = openRandomPerspectiveOn(window);
    openRandomPerspectiveOn(window);
    tracker().start();

    closePerspective(bg, window);

    verifyZeroInteractions(bus);
  }

  @Test public void notNotifyWhenStopped() {
    tracker().start();
    tracker().stop();
    openRandomPerspectiveOnCurrentWindow();
    verifyZeroInteractions(bus);
  }

  @Test public void tracksNewlyOpenedWindow() {
    tracker().start();
    openRandomPerspectiveOn(openWindow());
    verify(bus, atLeast(2)).post(any()); // 1 for old window, some for new
  }

  @Override protected void init() {
    super.init();
    arg = captureEvent();
    windows = newArrayListWithExpectedSize(1);
    bus = mock(EventBus.class);
    epochClock = mock(IClock.class);
    given(epochClock.now()).willReturn(epoch());

    closePartsOfCurrentWindow();
    closePerspectivesOfCurrentWindow();
  }

  @Override protected PerspectiveFocusTracker newTracker() {
    return new PerspectiveFocusTracker(bus, epochClock, getWorkbench());
  }

  private ArgumentCaptor<PerspectiveFocusEvent> captureEvent() {
    return ArgumentCaptor.forClass(PerspectiveFocusEvent.class);
  }

  private PerspectiveFocusEvent event(
      Instant instant, IPerspectiveDescriptor perspective, boolean focused) {
    return new PerspectiveFocusEvent(instant, perspective, focused);
  }

  private boolean focused() {
    return true;
  }

  private IWorkbenchWindow openWindow() {
    IWorkbenchWindow window = TestWorkbenches.openWindow();
    windows.add(window);
    return window;
  }

  private boolean unfocused() {
    return false;
  }
}
