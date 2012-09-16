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
import static rabbit.tracking.internal.util.Workbenches.focusedPartOf;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.event.PartFocusEvent;
import rabbit.tracking.event.PartSessionEvent;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

/**
 * Listens for {@link PartFocusEvent}s on an event bus and posts
 * {@link PartSessionEvent}s to the same event bus base on the focus events
 * received.
 */
public final class PartSessionTracker extends AbstractTracker {

  private class PartFocusEventHandler {
    @Subscribe public void handle(PartFocusEvent event) {
      if (isSessionStart(event))
        handleSessionStart(event);
      else
        handleSessionEnd(event);
    }
  }

  private final EventBus bus;
  private final IClock clock;
  private final IWorkbench workbench;
  private final AtomicReference<PartFocusEvent> startRef;
  private final PartFocusEventHandler handler;

  public PartSessionTracker(EventBus bus, IClock clock, IWorkbench workbench) {
    this.bus = checkNotNull(bus, "bus");
    this.clock = checkNotNull(clock, "clock");
    this.workbench = checkNotNull(workbench, "workbench");
    this.startRef = new AtomicReference<PartFocusEvent>();
    this.handler = new PartFocusEventHandler();
  }

  @Override protected void onStart() {
    startSessionIfFocusedPartPresent();
    bus.register(handler);
  }

  @Override protected void onStop() {
    bus.unregister(handler);
    stopSessionIfStarted();
  }

  private PartFocusEvent focused(IWorkbenchPart part) {
    return new PartFocusEvent(clock.now(), part, true);
  }

  private void handleSessionEnd(PartFocusEvent end) {
    PartFocusEvent start = startRef.getAndSet(null);
    postNewSessionEventIf(sessionIsValid(start, end), start, end);
  }

  private void handleSessionStart(PartFocusEvent start) {
    startRef.set(start);
  }

  private boolean isSessionStart(PartFocusEvent event) {
    return event.isFocused();
  }

  private void postNewSessionEventIf(
      boolean doIt, PartFocusEvent start, PartFocusEvent end) {
    if (doIt)
      bus.post(session(start, end));
  }

  private PartSessionEvent session(PartFocusEvent start, PartFocusEvent end) {
    Instant instant = start.instant();
    Duration duration = new Duration(start.instant(), end.instant());
    return new PartSessionEvent(instant, duration, end.part());
  }

  private boolean sessionIsValid(PartFocusEvent start, PartFocusEvent end) {
    return start != null && start.part().equals(end.part());
  }

  private void startSessionIfFocusedPartPresent() {
    IWorkbenchPart part = focusedPartOf(workbench);
    if (part != null)
      handler.handle(focused(part));
  }

  private void stopSessionIfStarted() {
    PartFocusEvent start = startRef.get();
    if (start != null)
      handler.handle(unfocused(start.part()));
  }

  private PartFocusEvent unfocused(IWorkbenchPart part) {
    return new PartFocusEvent(clock.now(), part, false);
  }
}
