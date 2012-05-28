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

package rabbit.tracking.event;

import static com.google.common.base.Preconditions.checkNotNull;

import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * An event with duration.
 * <p/>
 * {@link #instant()} returns the start time of this event.
 * 
 * @since 2.0
 */
public class TimedEvent extends Event {

  private final Duration duration;

  /**
   * Constructs a new event.
   * 
   * @param instant the instant (start time) of this event
   * @param duration the duration of this event
   * @throws NullPointerException if any argument is null
   */
  public TimedEvent(Instant instant, Duration duration) {
    super(instant);
    this.duration = checkNotNull(duration, "duration");
  }

  /**
   * The duration of this event.
   * 
   * @return the duration of this event, not null
   */
  public final Duration duration() {
    return duration;
  }

  @Override public boolean equals(Object obj) {
    if (obj != null && Objects.equal(getClass(), obj.getClass())) {
      TimedEvent that = (TimedEvent)obj;
      return Objects.equal(instant(), that.instant())
          & Objects.equal(duration(), that.duration());
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(instant(), duration());
  }

  @Override protected ToStringHelper toStringHelper() {
    return super.toStringHelper().add("duration", duration());
  }
}
