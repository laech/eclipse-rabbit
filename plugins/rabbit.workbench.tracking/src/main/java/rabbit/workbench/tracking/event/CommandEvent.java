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

package rabbit.workbench.tracking.event;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.commands.ExecutionEvent;
import org.joda.time.Instant;

import rabbit.tracking.event.Event;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

/**
 * A command execution event.
 * 
 * @since 2.0
 */
public final class CommandEvent extends Event {

  private final ExecutionEvent event;

  /**
   * @param instant the time of execution
   * @param execution the execution event of the command
   * @throws NullPointerException if any argument is null
   */
  public CommandEvent(Instant instant, ExecutionEvent execution) {
    super(instant);
    this.event = checkNotNull(execution, "execution");
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof CommandEvent) {
      CommandEvent that = (CommandEvent)obj;
      return Objects.equal(instant(), that.instant())
          && Objects.equal(execution(), that.execution());
    }
    return false;
  }

  /**
   * Gets the execution event.
   * 
   * @return the execution, not null
   */
  public ExecutionEvent execution() {
    return event;
  }

  @Override public int hashCode() {
    return Objects.hashCode(instant(), execution());
  }

  @Override protected ToStringHelper toStringHelper() {
    return super.toStringHelper().add("execution", execution());
  }
}
