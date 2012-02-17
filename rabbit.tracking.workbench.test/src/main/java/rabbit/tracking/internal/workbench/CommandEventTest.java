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

package rabbit.tracking.internal.workbench;

import static org.hamcrest.core.Is.is;
import static org.joda.time.Instant.now;
import static org.junit.Assert.assertThat;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.EventTest;

public final class CommandEventTest extends EventTest {

  private ExecutionEvent execution;
  private CommandEvent event;

  @Before public void setup() {
    execution = new ExecutionEvent(getCommandService().getCommand("something"),
        Collections.EMPTY_MAP, null, null);
    event = create(now(), execution);
  }

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfExecutionEventIsNull() {
    create(now(), null);
  }

  @Test public void returnTheExecutionEvent() {
    assertThat(event.getExecutionEvent(), is(execution));
  }

  @Override protected CommandEvent create(Instant instant) {
    return create(instant, execution);
  }

  private CommandEvent create(Instant instant, ExecutionEvent execution) {
    return new CommandEvent(instant, execution);
  }

  private ICommandService getCommandService() {
    return (ICommandService)PlatformUI.getWorkbench().getService(
        ICommandService.class);
  }
}
