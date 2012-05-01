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
import static org.joda.time.Instant.now;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.IExecutionListener;
import org.eclipse.ui.commands.ICommandService;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.IPersistable;
import rabbit.tracking.util.IPersistableEventListenerSupport;

import com.google.inject.Inject;

public final class CommandTracker
    extends AbstractTracker implements IPersistable {

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

  private final IExecutionListener executionListener = new ExecutionListener() {

    /**
     * The last recorded ExecutionEvent, to be updated every time
     * {@link #preExecute(String, ExecutionEvent)} is called. We need this
     * because {@link #postExecuteSuccess(String, Object)} does not have an
     * {@link ExecutionEvent} parameter. This variable is null from the
     * beginning.
     */
    private ExecutionEvent lastExecution;

    @Override public void preExecute(String cmdId, ExecutionEvent event) {
      super.preExecute(cmdId, event);
      setEvent(event);
    }

    @Override public void postExecuteSuccess(String cmdId, Object returnValue) {
      super.postExecuteSuccess(cmdId, returnValue);
      ExecutionEvent execution = getAndResetEvent();
      if (execution != null) {
        Command cmd = execution.getCommand();
        if (cmd != null && equal(cmdId, cmd.getId())) {
          support.notifyOnEvent(new CommandEvent(now(), execution));
        }
      }
    }

    private void setEvent(ExecutionEvent event) {
      synchronized (this) {
        lastExecution = event;
      }
    }

    private ExecutionEvent getAndResetEvent() {
      ExecutionEvent execution = null;
      synchronized (this) {
        execution = lastExecution;
        lastExecution = null;
      }
      return execution;
    }
  };

  private final IPersistableEventListenerSupport<ICommandEvent> support;
  private final ICommandService service;

  /**
   * @param service the command service to listener on
   * @param support the object to receive notifications of this tracker
   * @throws NullPointerException if any argument is null
   */
  @Inject public CommandTracker(
      ICommandService service,
      IPersistableEventListenerSupport<ICommandEvent> support) {
    this.service = checkNotNull(service, "service");
    this.support = checkNotNull(support, "support");
  }

  @Override protected void onDisable() {
    service.removeExecutionListener(executionListener);
  }

  @Override protected void onEnable() {
    service.addExecutionListener(executionListener);
  }

  @Override public void save() {
    support.notifyOnSave();
  }
}
