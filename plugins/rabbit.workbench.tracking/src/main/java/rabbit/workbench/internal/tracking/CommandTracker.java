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
package rabbit.workbench.internal.tracking;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.ui.commands.ICommandService;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.util.IClock;
import rabbit.workbench.tracking.event.CommandEvent;

import com.google.common.eventbus.EventBus;

public final class CommandTracker extends AbstractTracker {

  /*
   * We only record commands that have been successfully executed, therefore we
   * only use postExecuteSuccess(String, Object) to record the event.
   * 
   * Note that preExecute(String, ExecutionEvent) will always be called when a
   * command is called to be execute, even if the command is non-executable at
   * that moment. For example, when there is nothing to undo in an editor, the
   * "Undo" menu is disabled, but if the user uses Ctrl+Z, the undo command will
   * still be called. Therefore we don't use preExecute(String, ExecutionEvent).
   */

  private final IExecutionListener executionListener = new IExecutionListener() {

    private final AtomicReference<ExecutionEvent> startRef =
        new AtomicReference<ExecutionEvent>(null);

    @Override public void notHandled(
        String commandId, NotHandledException exception) {
    }

    @Override public void postExecuteFailure(
        String commandId, ExecutionException exception) {
    }

    @Override public void postExecuteSuccess(String cmdId, Object returnValue) {
      ExecutionEvent startEvent = startRef.getAndSet(null);
      if (startEvent == null)
        return;

      if (equal(cmdId, startEvent.getCommand().getId()))
        bus.post(new CommandEvent(clock.now(), startEvent));
    }

    @Override public void preExecute(String cmdId, ExecutionEvent event) {
      startRef.set(event);
    }
  };

  private final EventBus bus;
  private final IClock clock;
  private final ICommandService service;

  public CommandTracker(EventBus bus, IClock clock, ICommandService service) {
    this.bus = checkNotNull(bus, "bus");
    this.clock = checkNotNull(clock, "clock");
    this.service = checkNotNull(service, "service");
  }

  @Override protected void onStart() {
    service.addExecutionListener(executionListener);
  }

  @Override protected void onStop() {
    service.removeExecutionListener(executionListener);
  }
}
