/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.tracking.internal.trackers;

import rabbit.data.handler.DataHandler;
import rabbit.data.store.IStorer;
import rabbit.data.store.model.SessionEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;

import org.eclipse.ui.PlatformUI;
import org.joda.time.DateTime;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

/**
 * Tracks duration of Eclipse sessions.
 */
public class SessionTracker extends AbstractTracker<SessionEvent> implements
    Observer {

  /**
   * Variable to indicate the start time of a session, in nanoseconds. If the 
   * value is less than zero, that means a session has not been started. The
   * value must be reset to a negative value after a session is finished. Note
   * that is important to use {@link System#nanoTime()} to set this time,
   * because {@link System#nanoTime()} is independent of the system time, which
   * won't cause errors when the user changes the system time.
   */
  private long startNanoTime = -1;
  
  /**
   * Updates {@link #startNanoTime} to {@link System#nanoTime()} only if
   * there is a currently active workbench window.
   */
  private final Runnable updateStartNanoTime = new Runnable() {
    @Override public void run() {
      // Check for active shell instead of active workbench window to include
      // dialogs:
      if (PlatformUI.getWorkbench().getDisplay().getActiveShell() != null) {
        startNanoTime = System.nanoTime();
      }
    }
  };

  /**
   * Constructor.
   */
  public SessionTracker() {
  }

  @Override
  protected IStorer<SessionEvent> createDataStorer() {
    return DataHandler.getStorer(SessionEvent.class);
  }

  @Override
  protected void doDisable() {
    TrackingPlugin.getDefault().getIdleDetector().deleteObserver(this);
    tryEndSession();
  }

  @Override
  protected void doEnable() {
    TrackingPlugin.getDefault().getIdleDetector().addObserver(this);
    tryStartSession();
  }

  @Override
  public void update(Observable observable, Object arg) {
    if (observable != TrackingPlugin.getDefault().getIdleDetector() || !isEnabled()) {
      return;
    }
    
    if (((IdleDetector) observable).isUserActive()) {
      tryStartSession();
    } else {
      tryEndSession();
    }
  }
  
  /**
   * Tries to end a tracking session if there is a started session.
   */
  private void tryEndSession() {
    if (startNanoTime < 0) {
      return;
    }
    long durationMillis = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNanoTime);
    if (durationMillis > 0) {
      addData(new SessionEvent(new DateTime(), durationMillis));
    }
    resetSession();
  }
  
  /**
   * Reset, to be called at end of each tracking session.
   */
  private void resetSession() {
    startNanoTime = -1;
  }
  
  /**
   * Tries to start a tracking session if there is any active workbench window.
   */
  private void tryStartSession() {
    PlatformUI.getWorkbench().getDisplay().syncExec(updateStartNanoTime);
  }
}
