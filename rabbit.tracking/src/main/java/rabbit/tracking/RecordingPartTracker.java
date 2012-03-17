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

package rabbit.tracking;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static rabbit.tracking.internal.util.Arrays.checkedCopyAsList;
import static rabbit.tracking.internal.util.Sets.newCopyOnWriteSet;
import static rabbit.tracking.internal.util.Workbenches.getFocusedPart;

import java.util.Set;

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.IUserMonitor.IUserListener;
import rabbit.tracking.PartTracker.IPartFocusListener;
import rabbit.tracking.util.Recorder;
import rabbit.tracking.util.Recorder.IRecordListener;
import rabbit.tracking.util.Recorder.Record;

/**
 * Tracks how long a workbench part has been in focus, can take into account the
 * user's state using a {@link IUserMonitor}.
 * <p/>
 * If a part is focused, then when it becomes unfocused (or the user becomes
 * inactive if the {@link IUserMonitor} is configured),
 * {@link IPartRecordListener#onPartEvent(Instant, Duration, IWorkbenchPart)}
 * will be called with the captured event. When a part becomes focused (or when
 * the user becomes active again if the {@link IUserMonitor} is configured), a
 * new tracking session will be started if appropriate.
 * <p/>
 * A part is consider focused if it's the active part and its parent window has
 * the focus.
 * 
 * @since 2.0
 */
public final class RecordingPartTracker extends AbstractTracker {

  /**
   * Listens to part focused duration events.
   * 
   * @since 2.0
   */
  public static interface IPartRecordListener {

    /**
     * Called when a new event is captured.
     * 
     * @param start the start time of this event, not null
     * @param duration the duration of this event, not null
     * @param part the workbench part of this event, not null
     */
    void onPartEvent(Instant start, Duration duration, IWorkbenchPart part);
  }

  /**
   * Gets a tracker, using the workbench's default {@link IUserMonitor}.
   * 
   * @return a tracker, not null
   */
  public static RecordingPartTracker get() {
    return withMonitor(workbenchUserMonitor());
  }

  /**
   * Gets a tracker, with the workbench's default {@link IUserMonitor} and the
   * given listeners attached.
   * 
   * @param listeners the listeners to be attached
   * @return a tracker, not null
   * @throws NullPointerException if any listener is null
   */
  public static RecordingPartTracker withListeners(
      IPartRecordListener... listeners) {
    return withMonitor(workbenchUserMonitor(), listeners);
  }

  /**
   * Gets a tracker, with the given {@link IUserMonitor} and listeners.
   * 
   * @param monitor the monitor to use, optional
   * @param listeners the listeners to be attached
   * @return a tracker, not null
   * @throws NullPointerException if any listener is null
   */
  public static RecordingPartTracker withMonitor(
      IUserMonitor monitor, IPartRecordListener... listeners) {
    return new RecordingPartTracker(monitor, listeners);
  }

  private static IUserMonitor workbenchUserMonitor() {
    return (IUserMonitor)getWorkbench().getService(IUserMonitor.class);
  }

  private final Recorder recorder = Recorder.withListeners(
      new IRecordListener() {
        @Override public void onRecord(Record record) {
          onPartEvent(
              record.getStart(),
              record.getDuration(),
              (IWorkbenchPart)record.getData());
        }
      });

  private final ITracker partTracker = PartTracker.withListeners(
      new IPartFocusListener() {
        @Override public void onPartFocused(IWorkbenchPart part) {
          recorder.start(part);
        }

        @Override public void onPartUnfocused(IWorkbenchPart part) {
          recorder.stop();
        }
      });

  private final Set<IPartRecordListener> listeners;
  private final IUserMonitor monitor;
  private final IUserListener monitorListener;

  private RecordingPartTracker(
      IUserMonitor monitor, IPartRecordListener... listeners) {
    this.listeners = newCopyOnWriteSet(checkedCopyAsList(listeners));
    this.monitor = monitor;

    if (monitor != null) {
      monitorListener = new IUserListener() {
        @Override public void onInactive() {
          recorder.stop();
        }

        @Override public void onActive() {
          IWorkbenchPart part = getFocusedPart();
          if (part != null) {
            recorder.start(part);
          }
        }
      };
    } else {
      monitorListener = null;
    }
  }

  /**
   * Adds a listener to be notified of part events. Has no affect if an
   * identical listener has already been added.
   * 
   * @param listener the listener to add
   * @throws NullPointerException if listener is null
   */
  public void addListener(IPartRecordListener listener) {
    listeners.add(checkNotNull(listener, "listener"));
  }

  /**
   * Removes a listener from receiving part events.
   * 
   * @param listener the listener to be removed
   * @throws NullPointerException if listener is null
   */
  public void removeListener(IPartRecordListener listener) {
    listeners.remove(checkNotNull(listener, "listener"));
  }

  @Override protected void onEnable() {
    partTracker.enable();
    if (monitor != null) {
      monitor.addListener(monitorListener);
    }
  }

  @Override protected void onDisable() {
    if (monitor != null) {
      monitor.removeListener(monitorListener);
    }
    partTracker.disable();
    recorder.stop();
  }

  private void onPartEvent(Instant start, Duration duration, IWorkbenchPart part) {
    for (IPartRecordListener listener : listeners) {
      listener.onPartEvent(start, duration, part);
    }
  }
}
