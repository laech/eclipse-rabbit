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

import static com.google.common.base.Preconditions.checkNotNull;
import static rabbit.tracking.internal.util.Workbenches.focusedPerspectiveOf;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.event.PerspectiveFocusEvent;
import rabbit.tracking.event.PerspectiveSessionEvent;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Listens for {@link PerspectiveFocusEvent}s from an event bus and posts
 * {@link PerspectiveSessionEvent}s to the same event bus based on the focus
 * events received.
 */
public final class PerspectiveSessionTracker extends AbstractTracker {

  private class PerspectiveFocusEventHandler {
    @Subscribe public void handle(PerspectiveFocusEvent event) {
      if (isSessionStart(event))
        handleSessionStart(event);
      else
        handleSessionEnd(event);
    }
  }

  private final EventBus bus;
  private final IClock clock;
  private final IWorkbench workbench;
  private final AtomicReference<PerspectiveFocusEvent> startRef;
  private final PerspectiveFocusEventHandler handler;

  public PerspectiveSessionTracker(
      EventBus bus, IClock clock, IWorkbench workbench) {
    this.bus = checkNotNull(bus, "bus");
    this.clock = checkNotNull(clock, "clock");
    this.workbench = checkNotNull(workbench, "workbench");
    this.startRef = new AtomicReference<PerspectiveFocusEvent>();
    this.handler = new PerspectiveFocusEventHandler();
  }

  @Override protected void onStart() {
    startSessionIfFocusedPerspectivePresent();
    bus.register(handler);
  }

  @Override protected void onStop() {
    bus.unregister(handler);
    stopSessionIfStarted();
  }

  private PerspectiveFocusEvent focused(IPerspectiveDescriptor perspective) {
    return new PerspectiveFocusEvent(clock.now(), perspective, true);
  }

  private void handleSessionEnd(PerspectiveFocusEvent end) {
    PerspectiveFocusEvent start = startRef.getAndSet(null);
    postSessionEventIf(sessionIsValid(start, end), start, end);
  }

  private void handleSessionStart(PerspectiveFocusEvent start) {
    startRef.set(start);
  }

  private boolean isSessionStart(PerspectiveFocusEvent event) {
    return event.isFocused();
  }

  private void postSessionEventIf(boolean conditionMet,
      PerspectiveFocusEvent start, PerspectiveFocusEvent end) {
    if (conditionMet)
      bus.post(session(start, end));
  }

  private PerspectiveSessionEvent session(
      PerspectiveFocusEvent start, PerspectiveFocusEvent end) {
    Instant instant = start.instant();
    Duration duration = new Duration(start.instant(), end.instant());
    IPerspectiveDescriptor perspective = start.perspective();
    return new PerspectiveSessionEvent(instant, duration, perspective);
  }

  private boolean sessionIsValid(
      PerspectiveFocusEvent start, PerspectiveFocusEvent end) {
    return start != null && start.perspective().equals(end.perspective());
  }

  private void startSessionIfFocusedPerspectivePresent() {
    IPerspectiveDescriptor perspective = focusedPerspectiveOf(workbench);
    if (perspective != null)
      handler.handle(focused(perspective));
  }

  private void stopSessionIfStarted() {
    PerspectiveFocusEvent start = startRef.get();
    if (start != null)
      handler.handle(unfocused(start.perspective()));
  }

  private PerspectiveFocusEvent unfocused(IPerspectiveDescriptor perspective) {
    return new PerspectiveFocusEvent(clock.now(), perspective, false);
  }
}
