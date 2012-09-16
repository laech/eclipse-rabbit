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
import static rabbit.tracking.internal.util.Workbenches.allPartServicesOf;
import static rabbit.tracking.internal.util.Workbenches.focusedWindowOf;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.event.PartFocusEvent;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;

/**
 * Posts {@link PartFocusEvent}s to an event bus as they occur.
 */
public final class PartFocusTracker extends AbstractTracker {

  private final IWindowListener windowListener = new IWindowListener() {

    @Override public void windowActivated(IWorkbenchWindow window) {
      IWorkbenchPart part = activePartOf(window);
      notifyFocusedIf(part != null, part);
    }

    @Override public void windowClosed(IWorkbenchWindow window) {
      window.getPartService().removePartListener(partListener);
    }

    @Override public void windowDeactivated(IWorkbenchWindow window) {
      IWorkbenchPart part = activePartOf(window);
      notifyUnfocusedIf(part != null, part);
    }

    @Override public void windowOpened(IWorkbenchWindow window) {
      window.getPartService().addPartListener(partListener);
    }

    private IWorkbenchPart activePartOf(IWorkbenchWindow window) {
      return window.getPartService().getActivePart();
    }
  };

  private final IPartListener partListener = new IPartListener() {

    @Override public void partActivated(IWorkbenchPart part) {
      notifyFocusedIf(isFocused(part), part);
    }

    @Override public void partBroughtToTop(IWorkbenchPart part) {
      // Do nothing
    }

    @Override public void partClosed(IWorkbenchPart part) {
      // Do nothing, a part is always deactivated before closed
    }

    @Override public void partDeactivated(IWorkbenchPart part) {
      notifyUnfocusedIf(isFocused(part), part);
    }

    @Override public void partOpened(IWorkbenchPart part) {
      // Do nothing, partActivated will be called if this part is active
    }

    private boolean isFocused(IWorkbenchPart part) {
      return part.getSite().getWorkbenchWindow() == focusedWindowOf(workbench);
    }
  };

  private final EventBus bus;
  private final IClock clock;
  private final IWorkbench workbench;

  public PartFocusTracker(EventBus bus, IClock clock, IWorkbench workbench) {
    this.bus = checkNotNull(bus, "bus");
    this.clock = checkNotNull(clock, "clock");
    this.workbench = checkNotNull(workbench, "workbench");
  }

  @Override protected void onStart() {
    registerWindowListener();
    registerPartListener();
  }

  @Override protected void onStop() {
    unregisterWindowListener();
    unregisterPartListener();
  }

  private void notifyFocusedIf(boolean conditionMet, IWorkbenchPart part) {
    if (conditionMet)
      bus.post(new PartFocusEvent(clock.now(), part, true));
  }

  private void notifyUnfocusedIf(boolean conditionMet, IWorkbenchPart part) {
    if (conditionMet)
      bus.post(new PartFocusEvent(clock.now(), part, false));
  }

  private void registerPartListener() {
    for (IPartService service : allPartServicesOf(workbench))
      service.addPartListener(partListener);
  }

  private void registerWindowListener() {
    workbench.addWindowListener(windowListener);
  }

  private void unregisterPartListener() {
    for (IPartService service : allPartServicesOf(workbench))
      service.removePartListener(partListener);
  }

  private void unregisterWindowListener() {
    workbench.removeWindowListener(windowListener);
  }
}
