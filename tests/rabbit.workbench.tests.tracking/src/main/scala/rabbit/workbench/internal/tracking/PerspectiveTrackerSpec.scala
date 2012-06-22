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

import org.eclipse.ui.IPerspectiveDescriptor
import org.joda.time.Duration.ZERO
import org.joda.time.Instant.now
import org.junit.runner.RunWith
import org.mockito.Matchers.{ notNull, any }
import org.mockito.Mockito.{ verifyZeroInteractions, verify, only, never, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.{ IPerspectiveSessionListener, IListenableTracker, IEventListener, AbstractTrackerSpecBase }

@RunWith(classOf[JUnitRunner])
final class PerspectiveTrackerSpec extends AbstractTrackerSpecBase {

  var listener: IEventListener[IPerspectiveEvent] = _
  var perspTracker: IListenableTracker[IPerspectiveSessionListener] = _
  var perspListeners: List[IPerspectiveSessionListener] = _

  override def beforeEach {
    listener = mock[IEventListener[IPerspectiveEvent]]
    perspTracker = mock[IListenableTracker[IPerspectiveSessionListener]]
    perspListeners = List.empty

    doAnswer { i: InvocationOnMock =>
      perspListeners :+= i.getArguments()(0).asInstanceOf[IPerspectiveSessionListener]
    } when perspTracker addListener any[IPerspectiveSessionListener]

    doAnswer { i: InvocationOnMock =>
      perspListeners -= i.getArguments()(0).asInstanceOf[IPerspectiveSessionListener]
    } when perspTracker removeListener any[IPerspectiveSessionListener]

    super.beforeEach
  }

  behavior of classOf[PerspectiveTracker].getSimpleName

  it must "notify of captured event" in {
    tracker.start
    val instant = now
    val duration = ZERO
    val perspective = mock[IPerspectiveDescriptor]

    doAnswer { i: InvocationOnMock =>
      val event = i.getArguments()(0).asInstanceOf[IPerspectiveEvent]
      event.instant must be(instant)
      event.duration must be(duration)
      event.perspective must be(perspective)
    } when listener onEvent any[IPerspectiveEvent]

    perspListeners foreach (_.onPerspectiveSession(instant, duration, perspective))

    verify(listener, only) onEvent any[IPerspectiveEvent]
  }

  it must "start perspective tracker on start" in {
    tracker.start
    verify(perspTracker).start
    verify(perspTracker, never).stop
  }

  it must "stop perspective tracker on stop" in {
    tracker.start
    tracker.stop
    verify(perspTracker).stop
  }

  it must "notify of captured events on stop" in {
    tracker.start
    doAnswer { i: InvocationOnMock =>
      perspListeners foreach (_.onPerspectiveSession(now, ZERO, mock[IPerspectiveDescriptor]))
    } when perspTracker stop

    tracker.stop
    verify(listener) onEvent notNull.asInstanceOf[IPerspectiveEvent]
  }

  it must "not notify event if stopped" in {
    tracker.start
    tracker.stop
    perspListeners foreach (_.onPerspectiveSession(now, ZERO, mock[IPerspectiveDescriptor]))
    verifyZeroInteractions(listener)
  }

  it must "throw NullPointerException if constructing without a perspective tracker" in {
    intercept[NullPointerException] {
      create(perspectiveTracker = null)
    }
  }

  it must "throw NullPointerException if constructing without a support" in {
    intercept[NullPointerException] {
      create(listener = null)
    }
  }

  override protected type Tracker = PerspectiveTracker

  override def create() = create(perspTracker, listener)

  private def create(
    perspectiveTracker: IListenableTracker[IPerspectiveSessionListener] = perspTracker,
    listener: IEventListener[IPerspectiveEvent] = listener) = {
    new PerspectiveTracker(perspectiveTracker, listener)
  }
}