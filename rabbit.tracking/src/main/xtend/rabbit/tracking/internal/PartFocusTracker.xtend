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
import org.eclipse.ui.IPartListener
import org.eclipse.ui.IWindowListener
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.IWorkbenchPart
import org.eclipse.ui.IWorkbenchWindow
import rabbit.tracking.AbstractTracker
import rabbit.tracking.event.PartFocusEvent
import rabbit.tracking.util.IClock

import static extension com.google.common.base.Preconditions.*
import static extension rabbit.tracking.internal.util.Workbenches.*

class PartFocusTracker extends AbstractTracker
    implements IWindowListener, IPartListener {
  
  val EventBus eventBus
  val IClock clock
  val IWorkbench workbench
  
  /**
   * @param bus the event bus to publish events
   * @param clock the clock to lookup time
   * @param workbench the workbench to track, not null
   * @throws NullPointerException if any argument is null
   */
  new(EventBus eventBus, IClock clock, IWorkbench workbench) {
    this.eventBus = eventBus.checkNotNull("eventBus")
    this.clock = clock.checkNotNull("clock")
    this.workbench = workbench.checkNotNull("workbench")
  }
  
  override protected onStart() {
    workbench.addWindowListener(this)
    workbench.partServices.forEach[addPartListener(this)]
  }
  
  override protected onStop() {
    workbench.removeWindowListener(this)
    workbench.partServices.forEach[removePartListener(this)]
  }
  
  def private onFocused(IWorkbenchPart part) {
    eventBus.post(new PartFocusEvent(clock.now, part, true))
  }
  
  def private onUnfocused(IWorkbenchPart part) {
    eventBus.post(new PartFocusEvent(clock.now, part, false))
  }
  
  // IWindowListener methods:
  
  override windowActivated(IWorkbenchWindow window) {
    window.partService.activePart?.onFocused
  }
  
  override windowClosed(IWorkbenchWindow window) {
    window.partService.removePartListener(this)
  }
  
  override windowDeactivated(IWorkbenchWindow window) {
    window.partService.activePart?.onUnfocused
  }
  
  override windowOpened(IWorkbenchWindow window) {
    window.partService.addPartListener(this)
  }
  
  // IPartListener methods:

  override partActivated(IWorkbenchPart part) {
    if (part.site.workbenchWindow == workbench.focusedWindow) {
      part.onFocused
    }
  }
  
  override partDeactivated(IWorkbenchPart part) {
    if (part.site.workbenchWindow == workbench.focusedWindow) {
      part.onUnfocused
    }
  }
  
  override partBroughtToTop(IWorkbenchPart part) {
    // Do nothing
  }
  
  override partClosed(IWorkbenchPart part) {
    // Do nothing, a part is always deactivated before closed
  }
  
  override partOpened(IWorkbenchPart part) {
    // Do nothing, partActivated will be called if this part is active
  }
  
}
