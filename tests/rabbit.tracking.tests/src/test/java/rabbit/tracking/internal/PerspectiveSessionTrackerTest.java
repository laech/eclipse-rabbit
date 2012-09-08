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

import static com.google.common.collect.Lists.newArrayList;
import static java.util.Arrays.asList;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.joda.time.Instant.now;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static rabbit.tracking.tests.TestWorkbenches.closePerspectivesOfCurrentWindow;
import static rabbit.tracking.tests.TestWorkbenches.openRandomPerspectiveOnCurrentWindow;

import java.util.List;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

import rabbit.tracking.event.PerspectiveFocusEvent;
import rabbit.tracking.event.PerspectiveSessionEvent;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;

public final class PerspectiveSessionTrackerTest
    extends EventBusTrackerTestBase<PerspectiveSessionTracker> {

  private static class MockEventBus extends EventBus {
    List<PerspectiveSessionEvent> sessions = newArrayList();

    @Override public void post(Object event) {
      super.post(event);
      if (event instanceof PerspectiveSessionEvent)
        sessions.add((PerspectiveSessionEvent)event);
    }
  }

  private MockEventBus bus;
  private IClock clock;
  private IWorkbench workbench;

  @Override public void after() {
    super.after();
    closePerspectivesOfCurrentWindow();
    openRandomPerspectiveOnCurrentWindow();
  }

  @Test public void recordsNothingOnStartIfFocusPerspectiveAbsent() {
    closePerspectivesOfCurrentWindow();
    tracker().start();
    tracker().stop();
    assertThat(bus.sessions.size(), is(0));
  }

  @Test public void recordsSesionOnPerspectiveSwitch() throws Exception {
    PerspectiveFocusTracker helper =
        new PerspectiveFocusTracker(bus, clock, workbench);
    helper.start();

    try {
      tracker().start();

      Instant start = setupClock(new Instant(10));
      IPerspectiveDescriptor p = openRandomPerspectiveOnCurrentWindow();

      Instant stop = setupClock(start.plus(100));
      openRandomPerspectiveOnCurrentWindow();

      assertThat(bus.sessions, is(asList(session(start, stop, p))));

    } finally {
      helper.stop();
    }
  }

  @Test public void recordsSessionOnStartIfFocusPerspectivePresent()
      throws Exception {
    IPerspectiveDescriptor p = openRandomPerspectiveOnCurrentWindow();
    Instant start = startTracker(new Instant(1));
    Instant stop = stopTracker(start.plus(100));
    assertThat(bus.sessions, is(asList(session(start, stop, p))));
  }

  @Test public void recordsSessionOnStopIfSessionWasInProgress() {
    tracker().start();
    bus.post(event(now(), mockPerspective(), focus()));
    tracker().stop();
    assertThat(bus.sessions.size(), is(1));
  }

  @Test public void recordsSessionUsingEventBus() {
    IPerspectiveDescriptor perspective = mockPerspective();
    PerspectiveFocusEvent start = event(new Instant(10), perspective, focus());
    PerspectiveFocusEvent stop = event(new Instant(100), perspective, unfocus());

    tracker().start();
    bus.post(start);
    bus.post(stop);

    assertThat(bus.sessions, is(asList(session(start, stop))));
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutClock() {
    newTracker(bus, null, workbench);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutEventBus() {
    newTracker(null, clock, workbench);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutWorkbench() {
    newTracker(bus, clock, null);
  }

  @Override protected void init() {
    super.init();
    workbench = getWorkbench();
    bus = new MockEventBus();
    clock = mock(IClock.class);
    given(clock.now()).willReturn(now());

    closePerspectivesOfCurrentWindow();
  }

  @Override protected PerspectiveSessionTracker newTracker() {
    return newTracker(bus);
  }

  @Override protected PerspectiveSessionTracker newTracker(EventBus bus) {
    return newTracker(bus, clock, workbench);
  }

  private PerspectiveFocusEvent event(
      Instant instant, IPerspectiveDescriptor p, boolean focused) {
    return new PerspectiveFocusEvent(instant, p, focused);
  }

  private boolean focus() {
    return true;
  }

  private IPerspectiveDescriptor mockPerspective() {
    return mock(IPerspectiveDescriptor.class);
  }

  private PerspectiveSessionTracker newTracker(
      EventBus bus, IClock clock, IWorkbench workbench) {
    return new PerspectiveSessionTracker(bus, clock, workbench);
  }

  private PerspectiveSessionEvent session(
      Instant start, Instant stop, IPerspectiveDescriptor perspective) {
    Duration duration = new Duration(start, stop);
    return new PerspectiveSessionEvent(start, duration, perspective);
  }

  private PerspectiveSessionEvent session(
      PerspectiveFocusEvent start, PerspectiveFocusEvent stop) {
    return new PerspectiveSessionEvent(
        start.instant(),
        new Duration(start.instant(), stop.instant()),
        start.perspective());
  }

  private Instant setupClock(Instant epoch) {
    given(clock.now()).willReturn(epoch);
    return epoch;
  }

  private Instant startTracker(Instant time) {
    Instant start = setupClock(time);
    tracker().start();
    return start;
  }

  private Instant stopTracker(Instant time) {
    Instant stop = setupClock(time);
    tracker().stop();
    return stop;
  }

  private boolean unfocus() {
    return false;
  }
}
