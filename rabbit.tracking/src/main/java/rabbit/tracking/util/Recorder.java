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
import static rabbit.tracking.internal.util.Arrays.checkedCopy;

import java.util.Set;

import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.internal.util.ListenableSupport;

import com.google.common.annotations.VisibleForTesting;
import com.google.inject.Inject;

/**
 * A helper class for recording durations with associated data.
 * <p/>
 * Recording is done sequentially, the currently recording will be stopped
 * before a new one is started.
 * <p/>
 * This class is thread safe.
 * 
 * @see #create(IRecordListener...)
 * @see #withClock(IClock, IRecordListener...)
 * @see IRecordListener
 * @see Record
 * @since 2.0
 */
public final class Recorder implements IRecorder {

  // TODO review static factory methods and constructor

  /**
   * Creates a recorder.
   * 
   * @param listeners the listeners
   * @return a new recorder with the specified listeners attached
   * @throws NullPointerException if any listener is null
   */
  public static Recorder create(IRecordListener... listeners) {
    return withClock(SystemClock.INSTANCE, listeners);
  }

  /**
   * Creates a recorder with a specific clock.
   * 
   * @param clock the clock to use
   * @param listeners the listeners
   * @throws NullPointerException if clock is null, or any listener is null
   */
  public static Recorder withClock(IClock clock, IRecordListener... listeners) {
    Recorder recorder = new Recorder(clock);
    for (IRecordListener listener : checkedCopy(listeners)) {
      recorder.addListener(listener);
    }
    return recorder;
  }

  private final IClock clock;
  private final ListenableSupport<IRecordListener> listenable;

  private Instant start;
  private Object data;
  private boolean recording;

  @Inject Recorder(IClock clock) {
    this.clock = checkNotNull(clock, "clock");
    this.listenable = ListenableSupport.create();
  }

  @Override public void addListener(IRecordListener listener) {
    listenable.addListener(listener);
  }

  @Override public void removeListener(IRecordListener listener) {
    listenable.removeListener(listener);
  }

  @Override public void start(Object userData) {
    Object dataSnapshot;
    synchronized (this) {
      dataSnapshot = data;
    }

    boolean notify = false;
    boolean sameData = equal(dataSnapshot, userData);
    Instant now = clock.now();
    Object myData;
    Instant myStart;
    Duration myDuration = null;

    synchronized (this) {
      if (dataSnapshot != data) {
        // If data is already changed by the time we got here, then the duration
        // will be zero as we didn't actually start recording with the userData,
        // but we still need to notify as the result of someone calling start()
        myData = userData;
        myStart = now;
        myDuration = Duration.ZERO;
        notify = true;

      } else {
        if (recording && sameData) {
          return;
        }
        myData = data;
        myStart = start;
        notify = recording;
        data = userData;
        start = now;
        recording = true;
      }
    }

    if (notify) {
      if (myDuration == null) {
        myDuration = new Duration(myStart, now);
      }
      notifyListeners(new Record(myStart, myDuration, myData));
    }
  }

  @Override public void stop() {
    Object myData;
    Instant myStart;
    synchronized (this) {
      if (!recording) {
        return;
      }
      myData = data;
      myStart = start;

      recording = false;
      data = null;
      start = null;
    }

    Duration duration = new Duration(myStart, clock.now());
    notifyListeners(new Record(myStart, duration, myData));
  }

  @VisibleForTesting Set<IRecordListener> getListeners() {
    return listenable.getListeners();
  }

  private void notifyListeners(Record record) {
    for (IRecordListener listener : listenable.getListeners()) {
      listener.onRecord(record);
    }
  }
}
