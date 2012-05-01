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

import org.eclipse.ui.{ IWorkbenchPart, part }
import org.joda.time.Duration.ZERO
import org.joda.time.Instant.now
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verify, only, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.util.IPersistableEventListenerSupport
import rabbit.tracking.{ IPartSessionListener, IListenableTracker, AbstractTrackerSpecBase }

@RunWith(classOf[JUnitRunner])
final class PartTrackerSpec extends AbstractTrackerSpecBase {

  private var support: IPersistableEventListenerSupport[IPartEvent] = _

  override def beforeEach {
    support = mock[IPersistableEventListenerSupport[IPartEvent]]
    super.beforeEach
  }

  behavior of classOf[PartTrackerSpec].getSimpleName

  it must "notify support on event" in {
    val instant = now
    val duration = ZERO
    val part = mock[IWorkbenchPart]

    tracker.onPartSession(instant, duration, part)

    doAnswer({ i: InvocationOnMock =>
      val event = i.getArguments()(0).asInstanceOf[IPartEvent]
      event.instant must be(instant)
      event.duration must be(duration)
      event.part must be(part)
    }).when(support).notifyOnEvent(any[IPartEvent])

    verify(support, only).notifyOnEvent(any[IPartEvent])
  }

  it must "notify support on save" in {
    tracker.save
    verify(support, only).notifyOnSave
  }

  override protected type Tracker = PartTracker

  override def create() = new PartTracker(mock[IListenableTracker[IPartSessionListener]], support)
}