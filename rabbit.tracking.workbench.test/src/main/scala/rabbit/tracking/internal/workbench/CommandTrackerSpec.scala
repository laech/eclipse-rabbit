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

package rabbit.tracking.internal.workbench

import java.lang.System.{ nanoTime, currentTimeMillis }

import org.eclipse.core.commands.common.CommandException
import org.eclipse.core.commands.IExecutionListener
import org.eclipse.core.commands.{ ExecutionEvent, AbstractHandler }
import org.eclipse.ui.PlatformUI.getWorkbench
import org.eclipse.ui.commands.ICommandService
import org.eclipse.ui.handlers.IHandlerService
import org.junit.runner.RunWith
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verifyZeroInteractions, verifyNoMoreInteractions, verify, inOrder, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.test.Tests.funToAnswer
import rabbit.tracking.util.IPersistableEventListenerSupport
import rabbit.tracking.workbench.ICommandEvent
import rabbit.tracking.AbstractTrackerSpecBase

@RunWith(classOf[JUnitRunner])
final class CommandTrackerSpec extends AbstractTrackerSpecBase {

  type Tracker = CommandTracker

  private var support: IPersistableEventListenerSupport[ICommandEvent] = _
  private var event: ICommandEvent = _

  override def beforeEach() {
    // Initialize before super to resolve dependencies of create()
    support = mock[IPersistableEventListenerSupport[ICommandEvent]]
    doAnswer({ invocation: InvocationOnMock =>
      event = invocation.getArguments()(0).asInstanceOf[ICommandEvent]
    }).when(support).notifyOnEvent(any[ICommandEvent])

    super.beforeEach()
  }

  behavior of "CommandTracker"

  it must "attach to command service when enabling" in {
    val (tracker, service) = createWithMockService()
    tracker.enable()
    verify(service).addExecutionListener(notNull(classOf[IExecutionListener]))
    verifyNoMoreInteractions(service)
  }

  it must "detach from command service when disabling" in {
    val (tracker, service) = createWithMockService()
    tracker.enable()
    tracker.disable()
    val order = inOrder(service)
    order.verify(service).removeExecutionListener(notNull(classOf[IExecutionListener]))
    order.verifyNoMoreInteractions()
  }

  it must "notify on save" in {
    tracker.save()
    verify(support).notifyOnSave()
    verifyNoMoreInteractions(support)
  }

  behavior of "CommandTracker when disabled"

  it must "not track command executions" in {
    tracker.disable()
    executeCommand()
    verifyZeroInteractions(support)
  }

  behavior of "CommandTracker when enabled"

  it must "track successful command executions" in {
    tracker.enable()

    val start = currentTimeMillis
    val command = executeCommand()
    val end = currentTimeMillis

    verify(support).notifyOnEvent(event)
    event.execution.getCommand must be(command)
    event.instant.getMillis must be >= start
    event.instant.getMillis must be <= end
  }

  it must "not track failed command executions" in {
    tracker.enable()

    try {
      handlerService.executeCommand("noSuchCommand", null)
    } catch {
      case e: CommandException =>
    }

    verifyZeroInteractions(support)
  }

  // Create with real command service by default
  override protected def create() = new CommandTracker(commandService, support)

  private def createWithMockService() = {
    val service = mock[ICommandService]
    val tracker = create(service, support)
    (tracker, service)
  }

  private def create(
    service: ICommandService,
    support: IPersistableEventListenerSupport[ICommandEvent]) =
    new CommandTracker(service, support)

  private def executeCommand() = {
    val commandId = "test." + nanoTime
    val command = commandService.getCommand(commandId)
    command.define("name", "description", commandService.getDefinedCategories()(0))
    handlerService.activateHandler(commandId, new AbstractHandler {
      override def execute(event: ExecutionEvent) = null
    })
    handlerService.executeCommand(commandId, null)
    command
  }

  private def commandService = getService[ICommandService]

  private def handlerService = getService[IHandlerService]

  private def getService[T: Manifest] =
    getWorkbench.getService(manifest[T].erasure).asInstanceOf[T]
}