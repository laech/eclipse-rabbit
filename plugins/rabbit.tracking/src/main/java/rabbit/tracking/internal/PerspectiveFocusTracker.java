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
import static rabbit.tracking.internal.util.Workbenches.focusedWindowOf;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PerspectiveAdapter;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.event.PerspectiveFocusEvent;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;

/**
 * Posts {@link PerspectiveFocusEvent}s to an event bus as they happen.
 */
public final class PerspectiveFocusTracker extends AbstractTracker {

  private static IPerspectiveDescriptor perspectiveOf(IWorkbenchWindow window) {
    IWorkbenchPage page = window.getActivePage();
    return page != null ? page.getPerspective() : null;
  }

  private final IWindowListener windowListener =
      new IWindowListener() {

        @Override public void windowActivated(IWorkbenchWindow window) {
          IPerspectiveDescriptor perspective = perspectiveOf(window);
          notifyFocusedIf(perspective != null, perspective);
        }

        @Override public void windowClosed(IWorkbenchWindow window) {
          window.removePerspectiveListener(perspectiveListener);
        }

        @Override public void windowDeactivated(IWorkbenchWindow window) {
          IPerspectiveDescriptor perspective = perspectiveOf(window);
          notifyUnfocusedIf(perspective != null, perspective);
        }

        @Override public void windowOpened(IWorkbenchWindow window) {
          window.addPerspectiveListener(perspectiveListener);
        }
      };

  private final IPerspectiveListener perspectiveListener =
      new PerspectiveAdapter() {

        @Override public void perspectiveActivated(
            IWorkbenchPage page, IPerspectiveDescriptor perspective) {
          notifyFocusedIf(isFocused(page), perspective);
        }

        @Override public void perspectiveClosed(
            IWorkbenchPage page, IPerspectiveDescriptor perspective) {
          notifyUnfocusedIf(
              isFocused(page) && page.getPerspective() == perspective,
              perspective);
        }

        @Override public void perspectiveDeactivated(
            IWorkbenchPage page, IPerspectiveDescriptor perspective) {
          notifyUnfocusedIf(isFocused(page), perspective);
        }

        private boolean isFocused(IWorkbenchPage page) {
          return page.getWorkbenchWindow() == focusedWindowOf(workbench);
        }
      };

  private final EventBus bus;
  private final IClock clock;
  private final IWorkbench workbench;

  public PerspectiveFocusTracker(
      EventBus bus, IClock clock, IWorkbench workbench) {
    this.bus = checkNotNull(bus, "bus");
    this.clock = checkNotNull(clock, "clock");
    this.workbench = checkNotNull(workbench, "workbench");
  }

  @Override protected void onStart() {
    registerWindowListener();
    registerPerspectiveListener();
  }

  @Override protected void onStop() {
    unregisterWindowListener();
    unregisterPerspectiveListener();
  }

  private void notifyFocusedIf(
      boolean conditionMet, IPerspectiveDescriptor perspective) {
    if (conditionMet)
      bus.post(new PerspectiveFocusEvent(clock.now(), perspective, true));
  }

  private void notifyUnfocusedIf(
      boolean conditionMet, IPerspectiveDescriptor perspective) {
    if (conditionMet)
      bus.post(new PerspectiveFocusEvent(clock.now(), perspective, false));
  }

  private void registerPerspectiveListener() {
    for (IWorkbenchWindow window : workbench.getWorkbenchWindows())
      window.addPerspectiveListener(perspectiveListener);
  }

  private void registerWindowListener() {
    workbench.addWindowListener(windowListener);
  }

  private void unregisterPerspectiveListener() {
    for (IWorkbenchWindow window : workbench.getWorkbenchWindows())
      window.removePerspectiveListener(perspectiveListener);
  }

  private void unregisterWindowListener() {
    workbench.removeWindowListener(windowListener);
  }

}
