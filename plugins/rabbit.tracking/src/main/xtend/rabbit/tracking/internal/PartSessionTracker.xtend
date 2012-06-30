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
  
  val startEvent = new AtomicReference<PartFocusEvent>()
  
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
    val part = workbench.focusedPart
    if (part != null)
      onEvent(new PartFocusEvent(clock.now, part, true))

    eventBus.register(this)
  }
  
  override protected onStop() {
    eventBus.unregister(this)
    val event = startEvent.get
    if (event != null)
      onEvent(new PartFocusEvent(clock.now, event.part, false))
  }
  
  @Subscribe def void onEvent(PartFocusEvent event) {
    if (event.focused) {
      startEvent.set(event)
      return
    }
    
    val start = startEvent.getAndSet(null)
    if (start == null || start.part != event.part)
      return
    
    val instant = start.instant
    val duration = new Duration(start.instant, event.instant)
    eventBus.post(new PartSessionEvent(instant, duration, event.part))
  }
}