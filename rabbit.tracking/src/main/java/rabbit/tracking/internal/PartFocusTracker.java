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
import static rabbit.tracking.internal.util.Workbenches.getFocusedWindow;
import static rabbit.tracking.internal.util.Workbenches.getPartServices;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.event.PartFocusEvent;
import rabbit.tracking.internal.util.PartListener;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;

/**
 * Tracks {@link PartFocusEvent} and publishes them to an {@link EventBus}.
 */
public final class PartFocusTracker extends AbstractTracker {

  private class MyPartListener extends PartListener {
    @Override public void partActivated(IWorkbenchPart part) {
      super.partActivated(part);
      if (part.getSite().getWorkbenchWindow() == getFocusedWindow(workbench)) {
        onPartFocused(part);
      }
    }

    @Override public void partDeactivated(IWorkbenchPart part) {
      super.partDeactivated(part);
      if (part.getSite().getWorkbenchWindow() == getFocusedWindow(workbench)) {
        onPartUnfocused(part);
      }
    }
  }

  private class MyWindowListener implements IWindowListener {
    @Override public void windowActivated(IWorkbenchWindow window) {
      IWorkbenchPart part = window.getPartService().getActivePart();
      if (part != null) {
        onPartFocused(part);
      }
    }

    @Override public void windowClosed(IWorkbenchWindow window) {
      window.getPartService().removePartListener(partListener);
    }

    @Override public void windowDeactivated(IWorkbenchWindow window) {
      IWorkbenchPart part = window.getPartService().getActivePart();
      if (part != null) {
        onPartUnfocused(part);
      }
    }

    @Override public void windowOpened(IWorkbenchWindow window) {
      window.getPartService().addPartListener(partListener);
    }
  }

  private final IWorkbench workbench;
  private final IPartListener partListener;
  private final IWindowListener windowListener;
  private final EventBus bus;
  private final IClock clock;

  /**
   * @param bus the event bus to publish events
   * @param clock the clock to lookup time
   * @param workbench the workbench to track, not null
   * @throws NullPointerException if any argument is null
   */
  public PartFocusTracker(EventBus bus, IClock clock, IWorkbench workbench) {
    this.bus = checkNotNull(bus, "bus");
    this.workbench = checkNotNull(workbench, "workbench");
    this.clock = checkNotNull(clock, "clock");
    this.partListener = new MyPartListener();
    this.windowListener = new MyWindowListener();
  }

  @Override protected void onStop() {
    workbench.removeWindowListener(windowListener);
    for (IPartService service : getPartServices(workbench)) {
      service.removePartListener(partListener);
    }
  }

  @Override protected void onStart() {
    workbench.addWindowListener(windowListener);
    for (IPartService service : getPartServices(workbench)) {
      service.addPartListener(partListener);
    }
  }

  private void onPartFocused(IWorkbenchPart part) {
    bus.post(new PartFocusEvent(clock.now(), part, true));
  }

  private void onPartUnfocused(IWorkbenchPart part) {
    bus.post(new PartFocusEvent(clock.now(), part, false));
  }
}
