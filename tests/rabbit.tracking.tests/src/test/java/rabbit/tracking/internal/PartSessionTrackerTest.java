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
import static java.lang.Thread.sleep;
import static java.util.Arrays.asList;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.joda.time.Instant.now;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static rabbit.tracking.tests.Instants.epoch;
import static rabbit.tracking.tests.TestWorkbenches.closePartsOfCurrentWindow;
import static rabbit.tracking.tests.TestWorkbenches.openRandomPartOnCurrentWindow;

import java.util.List;

import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

import rabbit.tracking.event.PartFocusEvent;
import rabbit.tracking.event.PartSessionEvent;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;

public final class PartSessionTrackerTest
    extends EventBusTrackerTestBase<PartSessionTracker> {

  private static class MockEventBus extends EventBus {
    List<PartSessionEvent> sessions = newArrayList();

    @Override public void post(Object event) {
      super.post(event);
      if (event instanceof PartSessionEvent)
        sessions.add((PartSessionEvent)event);
    }
  }

  private MockEventBus bus;
  private IClock clock;
  private IWorkbench workbench;

  @Test public void recordsNothingOnStartIfFocusPartAbsent() {
    closePartsOfCurrentWindow();
    tracker().start();
    tracker().stop();
    assertThat(bus.sessions.size(), is(0));
  }

  @Test public void recordsSessionOnPartSwitch() throws Exception {
    PartFocusTracker helper = new PartFocusTracker(bus, clock, workbench);
    helper.start();
    try {
      tracker().start();

      Instant start = setupClock(new Instant(10));
      IViewPart part = openRandomPartOnCurrentWindow();

      sleep(10);

      Instant end = setupClock(start.plus(100));
      openRandomPartOnCurrentWindow();

      assertThat(bus.sessions, is(listOf(session(start, end, part))));

    } finally {
      helper.stop();
    }
  }

  @Test public void recordsSessionOnStartIfFocusPartPresent() throws Exception {
    IViewPart part = openRandomPartOnCurrentWindow();

    Instant start = setupClock(epoch());
    tracker().start();

    sleep(10);

    Instant end = setupClock(start.plus(100));
    tracker().stop();

    assertThat(bus.sessions, is(listOf(session(start, end, part))));
  }

  @Test public void recordsSessionOnStopIfSessionWasInProcess() {
    tracker().start();
    bus.post(focusEvent());
    tracker().stop();
    assertThat(bus.sessions.size(), is(1));
  }

  @Test public void recordsSessionUsingEventBus() {
    IWorkbenchPart part = mockPart();
    PartFocusEvent start = event(new Instant(10), part, focused());
    PartFocusEvent end = event(new Instant(100), part, unfocused());

    tracker().start();
    bus.post(start);
    bus.post(end);

    assertThat(bus.sessions, is(listOf(session(start, end, part))));
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
    initWorkbench();
    initEventBus();
    initClock();
    closePartsOfCurrentWindow();
  }

  @Override protected PartSessionTracker newTracker() {
    return newTracker(bus);
  }

  @Override protected PartSessionTracker newTracker(EventBus bus) {
    return newTracker(bus, clock, workbench);
  }

  private PartFocusEvent event(
      Instant instant, IWorkbenchPart part, boolean focused) {
    return new PartFocusEvent(instant, part, focused);
  }

  private boolean focused() {
    return true;
  }

  private PartFocusEvent focusEvent() {
    return event(now(), mockPart(), focused());
  }

  private void initClock() {
    clock = mock(IClock.class);
    given(clock.now()).willReturn(now());
  }

  private void initEventBus() {
    bus = new MockEventBus();
  }

  private void initWorkbench() {
    workbench = getWorkbench();
  }

  private <T> List<T> listOf(T... xs) {
    return asList(xs);
  }

  private IWorkbenchPart mockPart() {
    return mock(IWorkbenchPart.class);
  }

  private PartSessionTracker newTracker(
      EventBus bus, IClock clock, IWorkbench workbench) {
    return new PartSessionTracker(bus, clock, workbench);
  }

  private PartSessionEvent session(
      Instant start, Instant end, IWorkbenchPart part) {
    return new PartSessionEvent(start, new Duration(start, end), part);
  }

  private PartSessionEvent session(
      PartFocusEvent start, PartFocusEvent end, IWorkbenchPart part) {
    Duration duration = new Duration(start.instant(), end.instant());
    return new PartSessionEvent(start.instant(), duration, part);
  }

  private Instant setupClock(Instant time) {
    given(clock.now()).willReturn(time);
    return time;
  }

  private boolean unfocused() {
    return false;
  }
}
