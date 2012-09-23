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

package rabbit.workbench.internal.tracking;

import static java.lang.System.nanoTime;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static rabbit.tracking.tests.Instants.epoch;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.IHandlerService;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import rabbit.tracking.AbstractTrackerTestBase;
import rabbit.tracking.util.IClock;
import rabbit.workbench.tracking.event.CommandEvent;

import com.google.common.eventbus.EventBus;

public final class CommandTrackerTest
    extends AbstractTrackerTestBase<CommandTracker> {

  private EventBus bus;
  private IClock clock;
  private ICommandService commandService;
  private IHandlerService handlerService;

  @Test public void attachesToCommandServiceOnStart() {
    ICommandService service = mock(ICommandService.class);
    CommandTracker tracker = newTracker(service);
    try {
      tracker.start();
      verify(service).addExecutionListener(anyExecutionListener());
      verifyNoMoreInteractions(service);
    } finally {
      tracker.stop();
    }
  }

  @Test public void detachesFromCommandServiceOnStop() {
    ICommandService service = mock(ICommandService.class);
    CommandTracker tracker = newTracker(service);
    try {
      tracker.start();
      tracker.stop();

      InOrder order = inOrder(service);
      order.verify(service).removeExecutionListener(anyExecutionListener());
      order.verifyNoMoreInteractions();
    } finally {
      tracker.stop();
    }
  }

  @Test public void doesntTrackFailedCommandExecution() throws Exception {
    tracker().start();
    try {
      executeCommand(false);
    } catch (ExecutionException e) {
    }
    verifyZeroInteractions(bus);
  }

  @Test public void doesntTrackWhenStopped() throws Exception {
    tracker().stop();
    executeCommand(true);
    verifyZeroInteractions(bus);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutClock() {
    newTracker(bus, null, commandService);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutCommandService() {
    newTracker(bus, clock, null);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutEventBus() {
    newTracker(null, clock, commandService);
  }

  @Test public void tracksSuccessfulCommandExecution() throws Exception {
    tracker().start();

    given(clock.now()).willReturn(epoch());
    Command command = executeCommand(true);

    ArgumentCaptor<CommandEvent> arg = captureEvent();
    verify(bus).post(arg.capture());
    assertThat(arg.getValue().instant(), is(epoch()));
    assertThat(arg.getValue().execution().getCommand(), is(command));
  }

  @Override protected void init() throws Exception {
    super.init();
    commandService = getService(ICommandService.class);
    handlerService = getService(IHandlerService.class);
    bus = mock(EventBus.class);
    clock = mock(IClock.class);
    given(clock.now()).willReturn(epoch());
  }

  @Override protected CommandTracker newTracker() {
    return newTracker(commandService);
  }

  private IExecutionListener anyExecutionListener() {
    return any(IExecutionListener.class);
  }

  private ArgumentCaptor<CommandEvent> captureEvent() {
    return ArgumentCaptor.forClass(CommandEvent.class);
  }

  private Command executeCommand(final boolean shouldSuccess) throws Exception {
    String commandId = "test." + nanoTime();
    Command command = commandService.getCommand(commandId);
    command.define("name", "desc", commandService.getDefinedCategories()[0]);
    handlerService.activateHandler(commandId, new AbstractHandler() {
      @Override public Object execute(ExecutionEvent event)
          throws ExecutionException {
        if (!shouldSuccess)
          throw new ExecutionException("Testing");
        return null;
      }
    });
    handlerService.executeCommand(commandId, null);
    return command;
  }

  @SuppressWarnings("unchecked")//
  private <T> T getService(Class<T> clazz) {
    return (T)getWorkbench().getService(clazz);
  }

  private CommandTracker newTracker(EventBus bus, IClock c, ICommandService s) {
    return new CommandTracker(bus, c, s);
  }

  private CommandTracker newTracker(ICommandService service) {
    return new CommandTracker(bus, clock, service);
  }

}
