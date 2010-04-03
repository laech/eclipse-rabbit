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

import java.util.Calendar;

/**
 * An event that has a duration.
 */
public class ContinuousEvent extends DiscreteEvent {

  private long duration;

  /**
   * Constructs a new event.
   * 
   * @param time The end time of the event.
   * @param duration The duration in milliseconds.
   * @throws IllegalArgumentException If duration is negative.
   * @throws NullPointerException If time is null.
   */
  public ContinuousEvent(Calendar time, long duration) {
    super(time);

    if (duration < 0) {
      throw new IllegalArgumentException();
    }

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
