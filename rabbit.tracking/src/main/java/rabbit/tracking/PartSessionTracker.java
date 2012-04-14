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
import static rabbit.tracking.internal.util.Workbenches.getFocusedPart;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.util.IRecordListener;
import rabbit.tracking.util.IRecorder;
import rabbit.tracking.util.Record;

import com.google.inject.Inject;

/**
 * Tracks how long a workbench part has been in focus, can take into account the
 * user's state using a {@link IUserMonitor}. See {@link IPartSessionListener}
 * for the behaviors of this tracker.
 */
final class PartSessionTracker
    extends AbstractListenableTracker<IPartSessionListener> {

  private static enum NoMonitor implements IUserMonitor {
    INSTANCE;

    @Override public void addListener(IUserListener listener) {
      // Do nothing
    }

    @Override public void removeListener(IUserListener listener) {
      // Do nothing
    }

    @Override public boolean isUserActive() {
      throw new UnsupportedOperationException("How did this get called?");
    }
  }

  private final IRecorder recorder;
  private final ITracker partTracker;
  private final IUserMonitor monitor;
  private final IUserListener monitorListener;
  private final IWorkbench workbench;

  /**
   * Constructs a new tracker.
   * <p/>
   * This tracker is considered the owner or controller of the partTracker and
   * recorder passed in here and they should not be shared/interacted with
   * outside of this tracker, otherwise errors will occur.
   * 
   * @param workbench the workbench to track for
   * @param partTracker the part track to use for tracking part focus events
   * @param recorder the recorder to use for calculating elapsed time
   * @param monitor the optional user monitor for taking into account the user's
   *        activeness
   * @throws NullPointerException if workbench, partTracker, or recorder is null
   */
  @Inject PartSessionTracker(
      IWorkbench workbench,
      IListenableTracker<IPartFocusListener> partTracker,
      IRecorder recorder,
      IUserMonitor monitor) {

    this.workbench = checkNotNull(workbench, "workbench");
    this.recorder = config(checkNotNull(recorder, "recorder"));
    this.partTracker = config(checkNotNull(partTracker, "partTracker"));
    this.monitor = monitor != null ? monitor : NoMonitor.INSTANCE;
    this.monitorListener = createUserListener();
  }

  private IRecorder config(IRecorder recorder) {
    recorder.addListener(new IRecordListener() {
      @Override public void onRecord(Record r) {
        onPartEvent(r.instant(), r.duration(), (IWorkbenchPart)r.data());
      }
    });
    return recorder;
  }

  private IListenableTracker<IPartFocusListener> config(
      IListenableTracker<IPartFocusListener> tracker) {

    tracker.addListener(new IPartFocusListener() {
      @Override public void onPartFocused(IWorkbenchPart part) {
        recorder.start(part);
      }

      @Override public void onPartUnfocused(IWorkbenchPart part) {
        recorder.stop();
      }
    });
    return tracker;
  }

  private IUserListener createUserListener() {
    return new IUserListener() {
      @Override public void onInactive() {
        recorder.stop();
      }

      @Override public void onActive() {
        startRecorderIfFocusedPartExists();
      }
    };
  }

  @Override protected void onEnable() {
    partTracker.enable();
    monitor.addListener(monitorListener);

    startRecorderIfFocusedPartExists();
  }

  @Override protected void onDisable() {
    monitor.removeListener(monitorListener);
    partTracker.disable();
    recorder.stop();
  }

  private void onPartEvent(Instant start, Duration duration, IWorkbenchPart part) {
    for (IPartSessionListener listener : getListeners()) {
      listener.onPartSession(start, duration, part);
    }
  }

  private void startRecorderIfFocusedPartExists() {
    IWorkbenchPart focusedPart = getFocusedPart(workbench);
    if (focusedPart != null) {
      recorder.start(focusedPart);
    }
  }
}
