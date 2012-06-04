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
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verifyZeroInteractions, verify, only, never, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.{ IPartSessionListener, IListenableTracker, IEventListener, AbstractTrackerSpecBase }

@RunWith(classOf[JUnitRunner])
final class PartTrackerSpec extends AbstractTrackerSpecBase {

  private var listener: IEventListener[IPartEvent] = _
  private var partTracker: IListenableTracker[IPartSessionListener] = _
  private var partTrackerListeners: List[IPartSessionListener] = _

  override def beforeEach {
    listener = mock[IEventListener[IPartEvent]]
    partTracker = mock[IListenableTracker[IPartSessionListener]]
    partTrackerListeners = List.empty

    doAnswer({ i: InvocationOnMock =>
      partTrackerListeners :+= i.getArguments()(0).asInstanceOf[IPartSessionListener]
    }).when(partTracker).addListener(any[IPartSessionListener])

    doAnswer({ i: InvocationOnMock =>
      partTrackerListeners -= i.getArguments()(0).asInstanceOf[IPartSessionListener]
    }).when(partTracker).removeListener(any[IPartSessionListener])

    super.beforeEach
  }

  behavior of classOf[PartTracker].getSimpleName

  it must "notify of captured event" in {
    tracker.start
    val instant = now
    val duration = ZERO
    val part = mock[IWorkbenchPart]

    doAnswer({ i: InvocationOnMock =>
      val event = i.getArguments()(0).asInstanceOf[IPartEvent]
      event.instant must be(instant)
      event.duration must be(duration)
      event.part must be(part)
    }).when(listener).onEvent(any[IPartEvent])

    partTrackerListeners foreach (_.onPartSession(instant, duration, part))

    verify(listener, only).onEvent(any[IPartEvent])
  }

  it must "enable part tracker on enable" in {
    tracker.start
    verify(partTracker).start
    verify(partTracker, never).stop
  }

  it must "disable part tracker on disable" in {
    tracker.start
    tracker.stop
    verify(partTracker).stop
  }

  it must "notify of captured events on disable" in {
    tracker.start
    doAnswer({ i: InvocationOnMock =>
      partTrackerListeners foreach (_.onPartSession(now, ZERO, mock[IWorkbenchPart]))
    }).when(partTracker).stop

    tracker.stop
    verify(listener).onEvent(notNull.asInstanceOf[IPartEvent])
  }

  it must "not notify event if disabled" in {
    tracker.start
    tracker.stop
    partTrackerListeners foreach (_.onPartSession(now, ZERO, mock[IWorkbenchPart]))
    verifyZeroInteractions(listener)
  }

  it must "throw NullPointerException if constructing without a part tracker" in {
    intercept[NullPointerException] {
      create(partTracker = null)
    }
  }

  it must "throw NullPointerException if constructing without a support" in {
    intercept[NullPointerException] {
      create(listener = null)
    }
  }

  override protected type Tracker = PartTracker

  override def create() = create(partTracker, listener)

  private def create(
    partTracker: IListenableTracker[IPartSessionListener] = partTracker,
    listener: IEventListener[IPartEvent] = listener) = new PartTracker(partTracker, listener)
}