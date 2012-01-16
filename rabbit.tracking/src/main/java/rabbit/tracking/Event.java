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

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Default implementation of an event.
 * 
 * @since 2.0
 */
public class Event implements IEvent {

  private final Instant instant;
  private final Duration duration;

  /**
   * Constructs a new event.
   * 
   * @param instant the instant of this event
   * @param duration the duration of this event
   * @throws NullPointerException if any argument is null
   */
  public Event(Instant instant, Duration duration) {
    this.instant = checkNotNull(instant, "instant");
    this.duration = checkNotNull(duration, "duration");
  }

  /**
   * Constructs a new event with a duration of zero.
   * 
   * @param instant the instant of this event
   * @throws NullPointerException if argument is null
   */
  public Event(Instant instant) {
    this(instant, Duration.ZERO);
  }

  @Override public final Duration getDuration() {
    return duration;
  }

  @Override public final Instant getInstant() {
    return instant;
  }

  @Override public String toString() {
    return toStringHelper().toString();
  }

  /**
   * Helper method returning a {@link ToStringHelper} already containing known
   * properties of this event.
   * 
   * @return a {@link ToStringHelper} for constructing a string representation
   *         of this event
   */
  protected final ToStringHelper toStringHelper() {
    return Objects.toStringHelper(this)
        .add("instant", getInstant())
        .add("duration", getDuration());
  }
}
