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

import org.joda.time.Instant;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Event indicating the active state of a user. When the active state of the
 * user changes, this event gets fired. For example, if the user is currently
 * inactive, when he/she becomes active, an event is fired with
 * {@link #isUserActive()} == true.
 * 
 * @since 2.0
 */
public final class UserStateEvent extends Event {

  private final boolean userActive;

  /**
   * Constructs a new event.
   * 
   * @param instant the instant of this event
   * @throws NullPointerException if instant is null
   */
  public UserStateEvent(Instant instant, boolean userActive) {
    super(instant);
    this.userActive = userActive;
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof UserStateEvent) {
      UserStateEvent that = (UserStateEvent)obj;
      return Objects.equal(instant(), that.instant())
          && Objects.equal(isUserActive(), that.isUserActive());
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(instant(), isUserActive());
  }

  /**
   * Indicates whether the user is active.
   * 
   * @return true if the user state of this event is active, false otherwise
   */
  public boolean isUserActive() {
    return userActive;
  }

  @Override protected ToStringHelper toStringHelper() {
    return super.toStringHelper().add("userActive", isUserActive());
  }
}
