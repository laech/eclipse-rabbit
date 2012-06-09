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

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * Part focused duration event.
 * <p/>
 * If a part is focused, then when it becomes unfocused (or by some other means
 * such as the user becomes inactive), an event is generated.
 * <p/>
 * A part is consider focused if it's the active part and its parent window has
 * the focus.
 * 
 * @since 2.0
 */
public final class PartSessionEvent extends TimedEvent {

  private final IWorkbenchPart part;

  /**
   * Constructs a new event.
   * 
   * @param instant the instant (start time) of this event
   * @param duration the duration of this event
   * @param part the workbench part of this event
   * @throws NullPointerException if any argument is null
   */
  public PartSessionEvent(
      Instant instant, Duration duration, IWorkbenchPart part) {
    super(instant, duration);
    this.part = checkNotNull(part, "part");
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof PartSessionEvent) {
      PartSessionEvent that = (PartSessionEvent)obj;
      return equal(instant(), that.instant())
          && equal(duration(), that.duration())
          && equal(part(), that.part());
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(instant(), duration(), part());
  }

  /**
   * The workbench part of this event.
   * 
   * @return the workbench part, not null
   */
  public IWorkbenchPart part() {
    return part;
  }

  @Override protected ToStringHelper toStringHelper() {
    return super.toStringHelper().add("part", part());
  }
}
