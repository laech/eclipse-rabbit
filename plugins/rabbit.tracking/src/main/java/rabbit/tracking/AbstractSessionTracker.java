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

import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.util.IRecordListener;
import rabbit.tracking.util.IRecorder;
import rabbit.tracking.util.Record;

/**
 * Base tracker for tracking elapsed time on a target, can take into account the
 * user's state using a {@link IUserMonitor}.
 * <p/>
 * Default implementation starts the recorder {@link #onStart()} if
 * {@link #findTarget()} returns none null, and stops the recorder
 * {@link #onStop()}. If an {@link IUserMonitor} is supplied, the recorder is
 * also started if the user becomes active and {@link #findTarget()} returns
 * none null, and the recorder will be stopped when the user becomes inactive.
 * <p/>
 * Subclasses can start/stop the recorder when appropriate, for example, start
 * the recorder when a workbench part becomes focused, and stop it when the part
 * becomes unfocused.
 * 
 * @param <E> the type of target this tracker will track
 * @param <T> the type of listeners for this {@link IListenableTracker}
 */
abstract class AbstractSessionTracker<E, T>
    extends AbstractListenableTracker<T> {

  private static enum NoMonitor implements IUserMonitor {
    INSTANCE;

    @Override public void addListener(IUserListener listener) {
      // Do nothing
    }

    @Override public boolean isUserActive() {
      throw new UnsupportedOperationException("How did this get called?");
    }

    @Override public void removeListener(IUserListener listener) {
      // Do nothing
    }
  }

  private final IRecorder<E> recorder;
  private final IRecordListener<E> recordListener;
  private final IUserMonitor monitor;
  private final IUserListener monitorListener;

  /**
   * @param recorder the recorder to use for elapsed time
   * @param monitor the optional monitor to use for user activeness
   * @throws NullPointerException if recorder is null
   */
  AbstractSessionTracker(IRecorder<E> recorder, IUserMonitor monitor) {
    this.recorder = checkNotNull(recorder, "recorder");
    this.monitor = monitor != null ? monitor : NoMonitor.INSTANCE;
    this.monitorListener = createUserListener();
    this.recordListener = createRecordListener();
  }

  /**
   * Finds the current target that is suitable to start tracking on. Or null if
   * none.
   */
  protected abstract E findTarget();

  @Override protected void onStop() {
    monitor.removeListener(monitorListener);

    // Stop recorder before removing listener so that the last event will be
    // notified
    recorder.stop();
    recorder.removeListener(recordListener);
  }

  @Override protected void onStart() {
    monitor.addListener(monitorListener);
    recorder.addListener(recordListener);
    startRecordingIfTargetExists();
  }

  /**
   * Called when a new event is captured.
   */
  protected abstract void onSession(Instant instant, Duration duration, E data);

  /**
   * The recorder used to record durations.
   */
  protected final IRecorder<E> recorder() {
    return recorder;
  }

  private IRecordListener<E> createRecordListener() {
    return new IRecordListener<E>() {
      @Override public void onRecord(Record<E> record) {
        onSession(record.instant(), record.duration(), record.data());
      }
    };
  }

  private IUserListener createUserListener() {
    return new IUserListener() {
      @Override public void onActive() {
        startRecordingIfTargetExists();
      }

      @Override public void onInactive() {
        recorder().stop();
      }
    };
  }

  private void startRecordingIfTargetExists() {
    E target = findTarget();
    if (target != null) {
      recorder().start(target);
    }
  }
}
