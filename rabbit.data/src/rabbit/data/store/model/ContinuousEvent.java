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
package rabbit.data.store.model;

import static com.google.common.base.Preconditions.checkArgument;

import org.joda.time.DateTime;

import javax.annotation.Nonnull;

/**
 * An event that has a duration.
 */
public class ContinuousEvent extends DiscreteEvent {

  private final long duration;

  /**
   * Constructs a new event.
   * 
   * @param endTime The end time of the event.
   * @param duration The duration in milliseconds.
   * @throws IllegalArgumentException If duration is negative.
   * @throws NullPointerException If time is null.
   */
  public ContinuousEvent(@Nonnull DateTime endTime, long duration) {
    super(endTime);
    checkArgument(duration >= 0, "Duration cannot be negative");
    this.duration = duration;
  }

  /**
   * Gets the duration.
   * 
   * @return The duration in milliseconds.
   */
  public long getDuration() {
    return duration;
  }
}
