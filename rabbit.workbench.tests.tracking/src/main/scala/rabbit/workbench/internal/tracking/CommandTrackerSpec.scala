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

package rabbit.workbench.internal.tracking

import java.lang.System.{ nanoTime, currentTimeMillis }

import org.eclipse.core.commands.common.CommandException
import org.eclipse.core.commands.{ ExecutionEvent, AbstractHandler, IExecutionListener }
import org.eclipse.ui.PlatformUI.getWorkbench
import org.eclipse.ui.commands.ICommandService
import org.eclipse.ui.handlers.IHandlerService
import org.junit.runner.RunWith
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verifyZeroInteractions, verifyNoMoreInteractions, verify, inOrder, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.{ IEventListener, AbstractTrackerSpecBase }

@RunWith(classOf[JUnitRunner])
final class CommandTrackerSpec extends AbstractTrackerSpecBase {

  type Tracker = CommandTracker

  private var listener: IEventListener[ICommandEvent] = _
  private var event: ICommandEvent = _

  override def beforeEach() {
    // Initialize before super to resolve dependencies of create()
    listener = mock[IEventListener[ICommandEvent]]
    doAnswer({ invocation: InvocationOnMock =>
      event = invocation.getArguments()(0).asInstanceOf[ICommandEvent]
    }).when(listener).onEvent(any[ICommandEvent])

    super.beforeEach
  }

  behavior of "CommandTracker"

  it must "attach to command service when starting" in {
    val (tracker, service) = createWithMockService()
    tracker.start
    verify(service).addExecutionListener(notNull(classOf[IExecutionListener]))
    verifyNoMoreInteractions(service)
  }

  it must "detach from command service when stopping" in {
    val (tracker, service) = createWithMockService()
    tracker.start
    tracker.stop
    val order = inOrder(service)
    order.verify(service).removeExecutionListener(notNull(classOf[IExecutionListener]))
    order.verifyNoMoreInteractions()
  }

  behavior of "CommandTracker when stopped"

  it must "not track command executions" in {
    tracker.stop
    executeCommand()
    verifyZeroInteractions(listener)
  }

  behavior of "CommandTracker when started"

  it must "track successful command executions" in {
    tracker.start

    val start = currentTimeMillis
    val command = executeCommand()
    val end = currentTimeMillis

    verify(listener).onEvent(event)
    event.execution.getCommand must be(command)
    event.instant.getMillis must be >= start
    event.instant.getMillis must be <= end
  }

  it must "not track failed command executions" in {
    tracker.start

    try {
      handlerService.executeCommand("noSuchCommand", null)
    } catch {
      case e: CommandException =>
    }

    verifyZeroInteractions(listener)
  }

  // Create with real command service by default
  override protected def create() = new CommandTracker(commandService, listener)

  private def createWithMockService() = {
    val service = mock[ICommandService]
    val tracker = create(service, listener)
    (tracker, service)
  }

  private def create(service: ICommandService, listener: IEventListener[ICommandEvent]) =
    new CommandTracker(service, listener)

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