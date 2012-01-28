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
import static com.google.common.base.Objects.toStringHelper;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.System.currentTimeMillis;
import static org.joda.time.Duration.millis;
import static org.joda.time.Instant.now;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.joda.time.Duration;
import org.joda.time.Instant;

/**
 * A helper class for recording durations with associated data.
 * <p/>
 * Recording is done sequentially, the currently recording will be stopped
 * before a new one is started.
 * <p/>
 * This class is thread safe.
 * 
 * @param <T> the type of data this recorder can associate with a recording
 * @see #create()
 * @see IRecorderListener
 * @see Record
 * @since 2.0
 */
public final class Recorder<T> {

  /**
   * Listener to be notified of recording events.
   * 
   * @since 2.0
   */
  public static interface IRecorderListener<T> {

    /**
     * Called when a new record is available.
     * 
     * @param record the new record, not null
     */
    void onRecord(Record<T> record);
  }

  /**
   * Represents the result of a recording.
   * 
   * @since 2.0
   */
  public static final class Record<T> {

    /**
     * Creates a new record.
     * 
     * @param start the start time of this record
     * @param duration the duration of this record
     * @param data the associated user data of this record
     * @throws NullPointerException if start time or duration is null
     */
    static <T> Record<T> create(Instant start, Duration duration, T data) {
      return new Record<T>(
          checkNotNull(start, "start"),
          checkNotNull(duration, "duration"),
          data);
    }

    private final Instant start;
    private final Duration duration;

    private final T data;

    private Record(Instant start, Duration duration, T data) {
      this.start = start;
      this.duration = duration;
      this.data = data;
    }

    /**
     * Gets the data that was associated with this recording.
     * 
     * @return the user data, or null if there was no data associated with this
     *         recording
     */
    public T getData() {
      return data;
    }

    /**
     * Gets the duration of this recording.
     * 
     * @return the duration, not null
     */
    public Duration getDuration() {
      return duration;
    }

    /**
     * Gets the start time of this recording.
     * 
     * @return the start time, not null
     */
    public Instant getStart() {
      return start;
    }

    @Override public String toString() {
      return toStringHelper(this)
          .add("data", getData())
          .add("start", getStart())
          .add("duration", getDuration())
          .toString();
    }
  }

  /**
   * Creates a new recorder.
   * 
   * @return a new recorder
   */
  public static <T> Recorder<T> create() {
    return new Recorder<T>();
  }

  private Instant start;
  private T data;
  private boolean recording;
  private final Set<IRecorderListener<T>> listeners;

  private Recorder() {
    listeners = new CopyOnWriteArraySet<IRecorderListener<T>>();
  }

  /**
   * Adds a new listener to listen to events. Has no affect if an identical
   * listener is already registered.
   * 
   * @param listener the listener to be added
   * @throws NullPointerException if the listener is null
   */
  public void addListener(IRecorderListener<T> listener) {
    listeners.add(checkNotNull(listener, "listener"));
  }

  /**
   * Removes a listener from listening to events.
   * 
   * @param listener the listener to be removed
   * @throws NullPointerException if the listener is null
   */
  public void removeListener(IRecorderListener<T> listener) {
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
  public void start(T userData) {
    boolean sameData = equal(this.data, userData);
    Instant now = now();

    T data;
    Instant start;
    boolean wasRecording = false;
    synchronized (this) {
      if (recording && sameData) {
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
      Record<T> record = Record.create(start, duration, data);
      notifyListeners(record);
    }
  }

  /**
   * Stops recording.
   * <p/>
   * Has no affect if there is no recording running, otherwise recording will be
   * stopped and listeners will be notified.
   */
  public void stop() {
    T data;
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

    Duration dur = millis(currentTimeMillis() - start.getMillis());
    Record<T> record = Record.create(start, dur, data);
    notifyListeners(record);
  }

  private void notifyListeners(Record<T> record) {
    for (IRecorderListener<T> listener : listeners) {
      listener.onRecord(record);
    }
  }

}
