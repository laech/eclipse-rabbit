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

import com.google.common.eventbus.EventBus
import com.google.common.eventbus.Subscribe
import java.util.concurrent.atomic.AtomicReference
import org.eclipse.ui.IWorkbench
import org.joda.time.Duration
import rabbit.tracking.AbstractTracker
import rabbit.tracking.event.PartFocusEvent
import rabbit.tracking.event.PartSessionEvent
import rabbit.tracking.util.IClock
import org.eclipse.ui.IWorkbenchPart

import static extension com.google.common.base.Preconditions.*
import static extension rabbit.tracking.internal.util.Workbenches.*

/**
 * Posts {@link PartSessionEvent}s to an event bus generated base on the 
 * {@link PartFocusEvent}s received from the event bus.  
 */
class PartSessionTracker extends AbstractTracker {
  
  val EventBus eventBus
  val IWorkbench workbench
  val IClock clock
  
  val startEventRef = new AtomicReference<PartFocusEvent>()
  
  /**
   * @param eventBus the event bus to listen and publish events
   * @param clock the clock to get the current time
   * @param workbench the workbench to track events for
   * @throws NullPointerException if any argument is null
   */
  new(EventBus eventBus, IClock clock, IWorkbench workbench) {
    this.eventBus = eventBus.checkNotNull("eventBus")
    this.workbench = workbench.checkNotNull("workbench")
    this.clock = clock.checkNotNull("clock")
  }

  override protected onStart() {
    startSessionIfNotNull(workbench.focusedPart)
    eventBus.register(this)
  }
  
  override protected onStop() {
    eventBus.unregister(this)
    stopSessionIfNotNull(startEventRef.get?.part)
  }
  
  def private void startSessionIfNotNull(IWorkbenchPart part) {
    if (part != null) handle(focused(part))
  }
  
  def private void stopSessionIfNotNull(IWorkbenchPart part) {
    if (part != null) handle(unfocused(part))
  }
  
  def private focused(IWorkbenchPart part) {
    new PartFocusEvent(clock.now, part, true)
  }
  
  def private unfocused(IWorkbenchPart part) {
    new PartFocusEvent(clock.now, part, false)
  }
  
  @Subscribe def void handle(PartFocusEvent event) {
    if (isSessionStart(event)) handleSessionStart(event) else handleSessionEnd(event)
  }
  
  def private isSessionStart(PartFocusEvent event) {
    event.focused
  }
  
  def private void handleSessionStart(PartFocusEvent startEvent) {
    startEventRef.set(startEvent)
  }
  
  def private void handleSessionEnd(PartFocusEvent endEvent) {
    val startEvent = startEventRef.getAndSet(null)
    postNewSessionEventIf(isValidSession(startEvent, endEvent), startEvent, endEvent)
  }
  
  def private isValidSession(PartFocusEvent startEvent, PartFocusEvent endEvent) {
    startEvent != null && startEvent.part == endEvent.part
  }
  
  def private postNewSessionEventIf(boolean doIt, PartFocusEvent startEvent, PartFocusEvent endEvent) {
    if (doIt) {
      val instant = startEvent.instant
      val duration = new Duration(startEvent.instant, endEvent.instant)
      eventBus.post(new PartSessionEvent(instant, duration, endEvent.part))
    }
  }
}