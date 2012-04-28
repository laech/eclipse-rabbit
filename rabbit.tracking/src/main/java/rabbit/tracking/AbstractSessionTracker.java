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
 * Default implementation starts the recorder {@link #onEnable()} if
 * {@link #findTarget()} returns none null, and stops the recorder
 * {@link #onDisable()}. If an {@link IUserMonitor} is supplied, the recorder is
 * also started if the user becomes active and {@link #findTarget()} returns
 * none null, and the recorder will be stopped when the user becomes inactive.
 * <p/>
 * Subclasses can start/stop the recorder when appropriate, for example, start
 * the recorder when a workbench part becomes focused, and stop it when the part
 * becomes unfocused.
 */
abstract class AbstractSessionTracker<T, L>
    extends AbstractListenableTracker<L> {

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

  private final IRecorder<T> recorder;
  private final IUserMonitor monitor;
  private final IUserListener monitorListener;

  AbstractSessionTracker(IRecorder<T> recorder, IUserMonitor monitor) {
    this.recorder = config(checkNotNull(recorder, "recorder"));
    this.monitor = monitor != null ? monitor : NoMonitor.INSTANCE;
    this.monitorListener = createUserListener();
  }

  /**
   * Finds the current target that is suitable to start tracking on. Or null if
   * none.
   */
  protected abstract T findTarget();

  @Override protected void onDisable() {
    monitor.removeListener(monitorListener);
    recorder.stop();
  }

  @Override protected void onEnable() {
    monitor.addListener(monitorListener);
    startRecordingIfTargetExists();
  }

  /**
   * Called when a new event is captured.
   */
  protected abstract void onSession(Instant instant, Duration duration, T data);

  /**
   * The recorder used to record durations.
   */
  protected final IRecorder<T> recorder() {
    return recorder;
  }

  private IRecorder<T> config(IRecorder<T> recorder) {
    recorder.addListener(new IRecordListener<T>() {
      @Override public void onRecord(Record<T> r) {
        onSession(r.instant(), r.duration(), r.data());
      }
    });
    return recorder;
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
    T target = findTarget();
    if (target != null) {
      recorder().start(target);
    }
  }
}
