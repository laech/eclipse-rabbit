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

import static java.util.Collections.EMPTY_MAP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.Instant.now;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.joda.time.Instant;
import org.junit.Test;

import rabbit.tracking.event.Event;
import rabbit.tracking.event.EventTest;

public final class CommandEventTest extends EventTest {

  static CommandEvent eventWith(Instant instant) {
    return new CommandEvent(instant, newExecution());
  }

  static CommandEvent eventWith(Instant instant, ExecutionEvent execution) {
    return new CommandEvent(instant, execution);
  }

  static ExecutionEvent newExecution() {
    return new ExecutionEvent(getCommand("a"), EMPTY_MAP, null, null);
  }

  private static Command getCommand(String id) {
    return getCommandService().getCommand(id);
  }

  private static ICommandService getCommandService() {
    return (ICommandService)PlatformUI.getWorkbench().getService(
        ICommandService.class);
  }

  @Test public void returnsTheExecutionEvent() {
    ExecutionEvent ex = newExecution();
    assertThat(newEvent(now(), ex).execution(), is(ex));
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutExecutionEvent() {
    newEvent(now(), null);
  }

  @Override protected Event newEvent(Instant instant) {
    return newEvent(instant, newExecution());
  }

  private CommandEvent newEvent(Instant instant, ExecutionEvent execution) {
    return eventWith(instant, execution);
  }
}
