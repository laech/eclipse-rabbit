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

import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.TimedEvent;

/**
 * Represents the result of a recording.
 * 
 * @param <T> the type of data this record holds
 * @see Recorder
 * @see IRecordListener
 * @since 2.0
 */
public final class Record<T> extends TimedEvent {

  /**
   * Creates a record with the given data.
   * 
   * @param instant the start time of this recording
   * @param duration the duration of this recording
   * @param data the user data of this recording
   * @return a record
   * @throws NullPointerException if start is null, or duration is null
   */
  public static <T> Record<T> create(Instant instant, Duration duration, T data) {
    return new Record<T>(instant, duration, data);
  }

  private final T data;

  private Record(Instant instant, Duration duration, T data) {
    super(instant, duration);
    this.data = data;
  }

  /**
   * The data that was associated with this recording.
   * 
   * @return the user data, or null if there was no data associated with this
   *         recording
   */
  public T data() {
    return data;
  }

  @Override public String toString() {
    return toStringHelper().add("data", data()).toString();
  }
}