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

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Perspective focused duration event.
 * <p/>
 * If a perspective is focused, then when it becomes unfocused (or by some other
 * means such as the user becomes inactive), an event is generated.
 * <p/>
 * A perspective is consider focused if its parent window has the focus.
 * 
 * @since 2.0
 */
public final class PerspectiveSessionEvent extends TimedEvent {

  private final IPerspectiveDescriptor perspective;

  /**
   * Constructs a new event.
   * 
   * @param instant the instant (start time) of this event
   * @param duration the duration of this event
   * @param perspective the perspective of this event
   * @throws NullPointerException if any argument is null
   */
  public PerspectiveSessionEvent(
      Instant instant, Duration duration, IPerspectiveDescriptor perspective) {
    super(instant, duration);
    this.perspective = checkNotNull(perspective, "perspective");
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof PerspectiveSessionEvent) {
      PerspectiveSessionEvent that = (PerspectiveSessionEvent)obj;
      return equal(instant(), that.instant())
          && equal(duration(), that.duration())
          && equal(perspective(), that.perspective());
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(instant(), duration(), perspective());
  }

  /**
   * The perspective of this event.
   * 
   * @return the perspective, not null
   */
  public IPerspectiveDescriptor perspective() {
    return perspective;
  }

  @Override protected ToStringHelper toStringHelper() {
    return super.toStringHelper().add("perspective", perspective());
  }
}
