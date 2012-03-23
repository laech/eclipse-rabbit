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

package rabbit.tracking.util;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static rabbit.tracking.internal.util.Arrays.checkedCopyAsList;
import static rabbit.tracking.internal.util.Sets.newCopyOnWriteSet;

import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.TimedEvent;
import rabbit.tracking.internal.util.IClock;
import rabbit.tracking.internal.util.SystemClock;

/**
 * A helper class for recording durations with associated data.
 * <p/>
 * Recording is done sequentially, the currently recording will be stopped
 * before a new one is started.
 * <p/>
 * This class is thread safe.
 * 
 * @see #create()
 * @see #withListeners(IRecordListener...)
 * @see IRecordListener
 * @see Record
 * @since 2.0
 */
public final class Recorder {

  /**
   * Listener to be notified of recording events.
   * 
   * @since 2.0
   */
  public static interface IRecordListener {

    /**
     * Called when a new record is available.
     * 
     * @param record the new record, not null
     */
    void onRecord(Record record);
  }

  /**
   * Represents the result of a recording.
   * 
   * @since 2.0
   */
  public static final class Record extends TimedEvent {

    private final Object data;

    /**
     * Creates a record with the given data.
     * 
     * @param instant the start time of this recording
     * @param duration the duration of this recording
     * @param data the user data of this recording
     * @return a record
     * @throws NullPointerException if start is null, or duration is null
     */
    static Record create(Instant instant, Duration duration, Object data) {
      return new Record(instant, duration, data);
    }

    private Record(Instant instant, Duration duration, Object data) {
      super(instant, duration);
      this.data = data;
    }

    /**
     * The data that was associated with this recording.
     * 
     * @return the user data, or null if there was no data associated with this
     *         recording
     */
    public Object data() {
      return data;
    }

    @Override public String toString() {
      return toStringHelper().add("data", data()).toString();
    }
  }

  /**
   * Creates a recorder.
   * 
   * @return a recorder
   */
  public static Recorder create() {
    return withListeners();
  }

  /**
   * Creates a recorder with some listeners.
   * 
   * @param listeners the listeners
   * @return a new recorder with the specified listeners attached
   * @throws NullPointerException if any listener is null
   */
  public static Recorder withListeners(IRecordListener... listeners) {
    return withClock(SystemClock.INSTANCE, listeners);
  }

  /**
   * Creates a recorder with a specific clock.
   * 
   * @param clock the clock to use
   * @param listeners the listeners
   * @throws NullPointerException if clock is null, or any listener is null
   */
  static Recorder withClock(IClock clock, IRecordListener... listeners) {
    return new Recorder(clock, listeners);
  }

  private final IClock clock;
  private final Set<IRecordListener> listeners;

  private Instant start;
  private Object data;
  private boolean recording;

  private Recorder(IClock clock, IRecordListener... listeners) {
    this.clock = checkNotNull(clock, "clock");
    this.listeners = newCopyOnWriteSet(checkedCopyAsList(listeners));
  }

  /**
   * Adds a new listener to listen to events. Has no affect if an identical
   * listener is already registered.
   * 
   * @param listener the listener to be added
   * @throws NullPointerException if the listener is null
   */
  public void addListener(IRecordListener listener) {
    listeners.add(checkNotNull(listener, "listener"));
  }

  /**
   * Removes a listener from listening to events.
   * 
   * @param listener the listener to be removed
   * @throws NullPointerException if the listener is null
   */
  public void removeListener(IRecordListener listener) {
    listeners.remove(checkNotNull(listener, "listener"));
  }

  /**
   * Starts a recording with the given user data.
   * <p/>
   * If there is currently a recording in progress with an equivalent user data
   * then this call will be ignored and the recording in progress will not be
   * affected. Otherwise if the currently running recording has a different user
   * data, then the recording will be stopped, listeners will be notified, and a
   * new recording will be started with the new user data.
   * 
   * @param userData the user data to associate with this recording, may be null
   */
  public void start(Object userData) {
    boolean sameData = equal(this.data, userData);
    Instant now = clock.now();

    Object data;
    Instant start;
    boolean wasRecording = false;
    synchronized (this) {
      if (this.recording && sameData) {
        return;
      }
      data = this.data;
      start = this.start;
      wasRecording = this.recording;

      this.data = userData;
      this.start = now;
      this.recording = true;
    }

    if (wasRecording) {
      Duration duration = new Duration(start, now);
      notifyListeners(new Record(start, duration, data));
    }
  }

  /**
   * Stops recording.
   * <p/>
   * Has no affect if there is no recording running, otherwise recording will be
   * stopped and listeners will be notified.
   */
  public void stop() {
    Object data;
    Instant start;
    synchronized (this) {
      if (!recording) {
        return;
      }
      data = this.data;
      start = this.start;

      this.recording = false;
      this.data = null;
      this.start = null;
    }

    Duration duration = new Duration(start, clock.now());
    notifyListeners(new Record(start, duration, data));
  }

  private void notifyListeners(Record record) {
    for (IRecordListener listener : listeners) {
      listener.onRecord(record);
    }
  }
}
