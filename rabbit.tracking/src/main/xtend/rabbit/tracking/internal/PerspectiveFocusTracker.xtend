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
import org.eclipse.ui.IPerspectiveDescriptor
import org.eclipse.ui.IPerspectiveListener3
import org.eclipse.ui.IWindowListener
import org.eclipse.ui.IWorkbench
import org.eclipse.ui.IWorkbenchPage
import org.eclipse.ui.IWorkbenchPartReference
import org.eclipse.ui.IWorkbenchWindow
import rabbit.tracking.AbstractTracker
import rabbit.tracking.event.PerspectiveFocusEvent

import static extension com.google.common.base.Preconditions.*
import static extension rabbit.tracking.internal.PerspectiveFocusTracker.*
import static extension rabbit.tracking.internal.util.Workbenches.*
import rabbit.tracking.util.IClock

/**
 * Tracks {@link PerspectiveFocusEvent}.
 */
class PerspectiveFocusTracker extends AbstractTracker
  implements IWindowListener, IPerspectiveListener3 {
    
  def private static IPerspectiveDescriptor perspective(IWorkbenchWindow window) {
    window.activePage?.perspective
  }
    
  private val EventBus eventBus
  private val IClock clock
  private val IWorkbench workbench
  
  /**
   * @param eventBus the event bus to publish events
   * @param clock the clock to provide the current time
   * @param workbench the workbench to track events
   * @throws NullPointerException if any argument is null
   */
  new(EventBus eventBus, IClock clock, IWorkbench workbench) {
    this.eventBus = eventBus.checkNotNull("eventBus")
    this.clock = clock.checkNotNull("clock")
    this.workbench = workbench.checkNotNull("workbench")
  }

  override protected onStart() {
    workbench.addWindowListener(this)
    workbench.workbenchWindows.forEach[addPerspectiveListener(this)]
  }
  
  override protected onStop() {
    workbench.removeWindowListener(this)
    workbench.workbenchWindows.forEach[removePerspectiveListener(this)]
  }
  
  def private void onFocused(IPerspectiveDescriptor perspective) {
    eventBus.post(new PerspectiveFocusEvent(clock.now, perspective, true))
  }
  
  def private void onUnfocused(IPerspectiveDescriptor perspective) {
    eventBus.post(new PerspectiveFocusEvent(clock.now, perspective, false))
  }
  
  // Window listener methods:

  override windowActivated(IWorkbenchWindow window) {
    window.perspective?.onFocused
  }
  
  override windowDeactivated(IWorkbenchWindow window) {
    window.perspective?.onUnfocused
  }
  
  override windowClosed(IWorkbenchWindow window) {
    window.removePerspectiveListener(this)
  }
  
  override windowOpened(IWorkbenchWindow window) {
    window.addPerspectiveListener(this)
  }
  
  // Perspective listener methods:
  
  override perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
    if (page.workbenchWindow == workbench.focusedWindow)
      perspective.onFocused
  }
  
  override perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
    if (page.workbenchWindow == workbench.focusedWindow)
      perspective.onUnfocused
  }

  override perspectiveClosed(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
    if (page.workbenchWindow == workbench.focusedWindow
        && page.perspective == perspective)
      perspective.onUnfocused
  }
  
  override perspectiveOpened(IWorkbenchPage page, IPerspectiveDescriptor perspective) {}
  override perspectiveSavedAs(IWorkbenchPage page, IPerspectiveDescriptor oldPerspective, IPerspectiveDescriptor newPerspective) {}
  override perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, String changeId) {}
  override perspectiveChanged(IWorkbenchPage page, IPerspectiveDescriptor perspective, IWorkbenchPartReference partRef, String changeId) {}
  
}