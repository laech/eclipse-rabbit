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

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Instant;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A workbench part focus/unfocus event. A part is consider focused if all of
 * the followings are true:
 * <ul>
 * <li>it's the active part in the parent window</li>
 * <li>its parent window is focused</li>
 * </ul>
 * therefore there will be at most one focused part at any time regardless of
 * how many workbench windows are opened.
 * 
 * @since 2.0
 */
public final class PartFocusEvent extends Event {

  private final boolean focused;
  private final IWorkbenchPart part;

  /**
   * Constructs a new event.
   * 
   * @param instant the instant of this event
   * @param part the workbench part of this event
   * @param focused true if the part became focused, false if the part became
   *        unfocused
   * @throws NullPointerException if any argument is null
   */
  public PartFocusEvent(Instant instant, IWorkbenchPart part, boolean focused) {
    super(instant);
    this.part = checkNotNull(part, "part");
    this.focused = focused;
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof PartFocusEvent) {
      PartFocusEvent that = (PartFocusEvent)obj;
      return Objects.equal(instant(), that.instant())
          & Objects.equal(part(), that.part())
          & Objects.equal(isFocused(), that.isFocused());
    }
    return super.equals(obj);
  }

  @Override public int hashCode() {
    return Objects.hashCode(instant(), part(), isFocused());
  }

  /**
   * The focus state of the workbench part of this event.
   * 
   * @return true if the part was focused, false if it was unfocused
   */
  public boolean isFocused() {
    return focused;
  }

  /**
   * The workbench part of this event
   * 
   * @return the workbench part, not null
   */
  public IWorkbenchPart part() {
    return part;
  }

  @Override protected ToStringHelper toStringHelper() {
    return super.toStringHelper().add("part", part).add("focused", isFocused());
  }

}
