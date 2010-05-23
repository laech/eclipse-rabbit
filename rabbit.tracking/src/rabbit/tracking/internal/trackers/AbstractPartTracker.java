/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
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
package rabbit.tracking.internal.trackers;

import rabbit.tracking.internal.TrackingPlugin;

import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IPartService;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.joda.time.DateTime;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Defines common behaviors for part trackers.
 * 
 * @param <E> The event type that is being tracked.
 */
public abstract class AbstractPartTracker<E> extends AbstractTracker<E>
    implements IPartListener, IWindowListener, Observer {

  /**
   * Indicates the start time (nanoseconds) of a tracking session on a workbench
   * part. Set using {@link System#nanoTime()}
   */
  private long startNanoTime;
  private Map<IWorkbenchPart, Boolean> partStates;

  private Runnable idleDetectorCode;

  /**
   * Constructor.
   */
  public AbstractPartTracker() {
    super();
    startNanoTime = Long.MAX_VALUE;
    partStates = new HashMap<IWorkbenchPart, Boolean>();

    idleDetectorCode = new Runnable() {
      @Override
      public void run() {
        if (!isEnabled()) {
          return;
        }
        IWorkbenchWindow win = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
        if (win == null) {
          return;
        }
        IWorkbenchPart part = win.getPartService().getActivePart();
        if (part == null) {
          return;
        }
        if (TrackingPlugin.getDefault().getIdleDetector().isUserActive()) {
          startSession(part);
        } else {
          endSession(part);
        }
      }
    };
  }

  @Override
  public void partActivated(IWorkbenchPart part) {
    startSession(part);
  }

  @Override
  public void partBroughtToTop(IWorkbenchPart part) {
  }

  @Override
  public void partClosed(IWorkbenchPart part) {
  }

  @Override
  public void partDeactivated(IWorkbenchPart part) {
    endSession(part);
  }

  @Override
  public void partOpened(IWorkbenchPart p) {
  }

  @Override
  public void update(Observable o, Object arg) {
    if (o == TrackingPlugin.getDefault().getIdleDetector()) {
      PlatformUI.getWorkbench().getDisplay().syncExec(idleDetectorCode);
    }
  }

  @Override
  public void windowActivated(IWorkbenchWindow window) {
    if (window.getPartService().getActivePart() != null) {
      startSession(window.getPartService().getActivePart());
    }
  }

  @Override
  public void windowClosed(IWorkbenchWindow window) {
    window.getPartService().removePartListener(this);
    if (window.getPartService().getActivePart() != null) {
      endSession(window.getPartService().getActivePart());
    }
  }

  @Override
  public void windowDeactivated(IWorkbenchWindow window) {
    if (window.getPartService().getActivePart() != null) {
      endSession(window.getPartService().getActivePart());
    }
  }

  @Override
  public void windowOpened(IWorkbenchWindow window) {
    window.getPartService().addPartListener(this);
    if (window.getPartService().getActivePart() != null) {
      startSession(window.getPartService().getActivePart());
    }
  }

  @Override
  protected void doDisable() {
    TrackingPlugin.getDefault().getIdleDetector().deleteObserver(this);
    PlatformUI.getWorkbench().removeWindowListener(this);
    for (IPartService s : getPartServices()) {
      s.removePartListener(this);
    }

    final IWorkbench wb = PlatformUI.getWorkbench();
    wb.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        if (win != null && win.getPartService().getActivePart() != null) {
          endSession(win.getPartService().getActivePart());
        }
      }
    });
  }

  @Override
  protected void doEnable() {
    TrackingPlugin.getDefault().getIdleDetector().addObserver(this);
    PlatformUI.getWorkbench().addWindowListener(this);
    for (IPartService s : getPartServices()) {
      s.addPartListener(this);
    }

    final IWorkbench wb = PlatformUI.getWorkbench();
    wb.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        IWorkbenchWindow win = wb.getActiveWorkbenchWindow();
        if (win != null && win.getPartService().getActivePart() != null) {
          startSession(win.getPartService().getActivePart());
        }
      }
    });
  }

  /**
   * Ends a session.
   * 
   * @param part The part to get data from.
   */
  protected void endSession(IWorkbenchPart part) {
    Boolean hasBeenStarted = partStates.get(part);
    if (hasBeenStarted == null || !hasBeenStarted) {
      return;
    }
    partStates.put(part, Boolean.FALSE);
    long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanoTime);
    if (durationMillis <= 0) {
      return;
    }

    startNanoTime = Long.MAX_VALUE;
    E event = tryCreateEvent(new DateTime(), durationMillis, part);
    if (event != null) {
      addData(event);
    }
  }

  /**
   * Starts a new session.
   */
  protected void startSession(IWorkbenchPart part) {
    startNanoTime = System.nanoTime();
    partStates.put(part, Boolean.TRUE);
  }

  /**
   * Try to create an event. This method is called when a session ends.
   * 
   * @param endTime The end time of the event.
   * @param duration The duration of the event in milliseconds.
   * @param part The workbench part of the event.
   * @return An event, or null if one should not be created.
   */
  protected abstract E tryCreateEvent(DateTime endTime, long duration,
      IWorkbenchPart part);

  /**
   * Gets all the {@link IPartService} from the currently opened windows.
   * 
   * @return A Set of IPartService.
   */
  private Set<IPartService> getPartServices() {
    Set<IPartService> result = new HashSet<IPartService>();
    IWorkbenchWindow[] ws = PlatformUI.getWorkbench().getWorkbenchWindows();
    for (IWorkbenchWindow w : ws) {
      result.add(w.getPartService());
    }
    return result;
  }
}
