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

import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.Instant;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A perspective focus/unfocus event.
 * <p/>
 * A perspective is consider focused if it's parent window is focused. Therefore
 * there will be at most one focused perspective at any given time regardless of
 * the number of workbench windows opened.
 * 
 * @since 2.0
 */
public final class PerspectiveFocusEvent extends Event {

  private final boolean focused;
  private final IPerspectiveDescriptor perspective;

  /**
   * Constructs a new event.
   * 
   * @param instant the instant of this event
   * @param perspective the perspective of this event
   * @param focused true if the perspective became focused, false if the
   *        perspective became unfocused
   * @throws NullPointerException if any argument is null
   */
  public PerspectiveFocusEvent(
      Instant instant, IPerspectiveDescriptor perspective, boolean focused) {
    super(instant);
    this.perspective = checkNotNull(perspective, "perspective");
    this.focused = focused;
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof PerspectiveFocusEvent) {
      PerspectiveFocusEvent that = (PerspectiveFocusEvent)obj;
      return Objects.equal(instant(), that.instant())
          & Objects.equal(perspective(), that.perspective())
          & Objects.equal(isFocused(), that.isFocused());
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(instant(), perspective(), isFocused());
  }

  /**
   * The focus state of the perspective of this event.
   * 
   * @return true if the perspective was focused, false if it was unfocused
   */
  public boolean isFocused() {
    return focused;
  }

  /**
   * The perspective of this event
   * 
   * @return the perspective, not null
   */
  public IPerspectiveDescriptor perspective() {
    return perspective;
  }

  @Override protected ToStringHelper toStringHelper() {
    return super.toStringHelper()
        .add("perspective", perspective())
        .add("focused", isFocused());
  }

}
