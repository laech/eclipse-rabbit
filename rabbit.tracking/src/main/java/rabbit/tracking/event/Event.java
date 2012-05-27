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

import org.joda.time.Instant;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * An event happened at a particular time.
 * 
 * @since 2.0
 */
public class Event {

  private final Instant instant;

  /**
   * Constructs a new event.
   * 
   * @param instant the instant of this event
   * @throws NullPointerException if instant is null
   */
  public Event(Instant instant) {
    this.instant = checkNotNull(instant, "instant");
  }

  /**
   * Compares two events for equality.
   * <p/>
   * Two events are only equal if they are of the same <strong>concrete</strong>
   * type and all properties are equal.
   * 
   * @see Object#equals(Object)
   */
  @Override public boolean equals(Object obj) {
    if (obj != null) {
      return Objects.equal(getClass(), obj.getClass())
          && Objects.equal(instant(), ((Event)obj).instant());
    }
    return false;
  }

  /**
   * @see Object#hashCode()
   */
  @Override public int hashCode() {
    return Objects.hashCode(instant());
  }

  /**
   * The time which this event occurred.
   * 
   * @return the time of this event, not null
   */
  public final Instant instant() {
    return instant;
  }

  @Override public String toString() {
    return toStringHelper().toString();
  }

  /**
   * Helper method returning a {@link ToStringHelper} already containing known
   * properties of this event. {@code toString()} by default calls this method,
   * subclass can override this to add more properties if wish to customize the
   * default output. For example:
   * 
   * <pre>
   * &#064;Overide protected ToStringHelper toStringHelper() {
   *   return super.toStringHelper().add(&quot;myProperty&quot;, myProperty());
   * }
   * </pre>
   * 
   * @return a {@link ToStringHelper} for constructing a string representation
   *         of this event
   */
  protected ToStringHelper toStringHelper() {
    return Objects.toStringHelper(this).add("instant", instant());
  }
}
