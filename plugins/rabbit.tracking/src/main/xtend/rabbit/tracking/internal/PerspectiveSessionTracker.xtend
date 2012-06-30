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
import rabbit.tracking.event.PerspectiveFocusEvent
import rabbit.tracking.event.PerspectiveSessionEvent
import rabbit.tracking.util.IClock

import static extension com.google.common.base.Preconditions.*
import static extension rabbit.tracking.internal.util.Workbenches.*

/**
 * Publishes {@link PerspectiveSessionEvent}s based on the
 * {@link PerspectiveFocusEvent}s received from an event bus.
 */
class PerspectiveSessionTracker extends AbstractTracker {
  
  val EventBus eventBus
  val IWorkbench workbench
  val IClock clock
  
  val startEvent = new AtomicReference<PerspectiveFocusEvent>()
  
  /**
   * @param eventBus the event bus to listen and publish events
   * @param clock the clock for getting the current time
   * @param workbench the workbench to track
   * @throws NullPointerException if any argument is null
   */
  new(EventBus eventBus, IClock clock, IWorkbench workbench) {
    this.eventBus = eventBus.checkNotNull("eventBus")
    this.workbench = workbench.checkNotNull("workbench")
    this.clock = clock.checkNotNull("clock")
  }

  override protected onStart() {
    val perspective = workbench.focusedPerspective
    if (perspective != null)
      onEvent(new PerspectiveFocusEvent(clock.now, perspective, true))
      
    eventBus.register(this)
  }
  
  override protected onStop() {
    eventBus.unregister(this)
    
    val event = startEvent.get
    if (event != null)
      onEvent(new PerspectiveFocusEvent(clock.now, event.perspective, false))
  }
  
  @Subscribe def void onEvent(PerspectiveFocusEvent event) {
    if (event.focused) {
      startEvent.set(event)
      return
    }
    
    val start = startEvent.getAndSet(null)
    if (start == null || start.perspective != event.perspective)
      return
    
    val duration = new Duration(start.instant, event.instant)
    eventBus.post(new PerspectiveSessionEvent(start.instant, duration, start.perspective))
  }
}