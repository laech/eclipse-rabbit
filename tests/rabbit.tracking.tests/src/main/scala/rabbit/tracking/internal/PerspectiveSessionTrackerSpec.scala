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

package rabbit.tracking.internal

import java.lang.System.currentTimeMillis
import java.lang.Thread.sleep

import org.eclipse.ui.PlatformUI.getWorkbench
import org.eclipse.ui.{ IWorkbench, IPerspectiveDescriptor }
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.mockito.Mockito.{ verify, only, inOrder }
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import com.google.common.eventbus.EventBus

import rabbit.tracking.event.{ PerspectiveSessionEvent, PerspectiveFocusEvent }
import rabbit.tracking.tests.Workbenches.{ openRandomPerspective, closeAllPerspectives }
import rabbit.tracking.util.IClock
import rabbit.tracking.AbstractTrackerSpecBase

@RunWith(classOf[JUnitRunner])
final class PerspectiveSessionTrackerSpec extends AbstractTrackerSpecBase
  with EventBusTrackerSpecBase {

  behavior of classOf[PerspectiveSessionTracker].getSimpleName

  it must "record perspective session duration using events from event bus" in {
    val perspective = mock[IPerspectiveDescriptor]
    val start = new PerspectiveFocusEvent(new Instant(10), perspective, true)
    val end = new PerspectiveFocusEvent(new Instant(100), perspective, false)

    tracker.start
    eventBus.post(start)
    eventBus.post(end)

    eventBus.events.size must be(1)
    val event = eventBus.events(0)
    event.perspective must be(perspective)
    event.instant must be(start.instant)
    event.duration must be(new Duration(start.instant, end.instant))
  }

  it must "record perspective session duration on perspective switch" in {
    val helper = new PerspectiveFocusTracker(eventBus, clock, workbench)
    helper.start
    try {

      tracker.start
      val start = new Instant(0)
      given(clock.now).willReturn(start)
      val perspective = openRandomPerspective

      sleep(10)

      val end = new Instant(100)
      given(clock.now).willReturn(end)
      openRandomPerspective

      eventBus.events.size must be(1)
      verifyEvent(eventBus.events(0), perspective, start, end)

    } finally {
      helper.stop
    }
  }

  it must "record perspective session duration on start if there is a focused perspective" in {
    val perspective = openRandomPerspective

    val start = new Instant(1)
    given(clock.now).willReturn(start)
    tracker.start

    sleep(10)

    val end = new Instant(1010)
    given(clock.now).willReturn(end)
    tracker.stop

    eventBus.events.size must be(1)
    verifyEvent(eventBus.events(0), perspective, start, end)
  }

  it must "record perspective session duration on stop if there is a session in progress" in {
    tracker.start
    eventBus.post(new PerspectiveFocusEvent(now, mock[IPerspectiveDescriptor], true))
    tracker.stop
    eventBus.events.size must be(1)
  }

  it must "record nothing on start if there is no focused perspective" in {
    closeAllPerspectives
    tracker.start
    tracker.stop
    eventBus.events must be(Seq.empty)
  }

  it must "throw NullPointerException if constructing without a workbench" in {
    intercept[NullPointerException] {
      create(eventBus, clock, null)
    }
  }

  it must "throw NullPointerException if constructing without an event bus" in {
    intercept[NullPointerException] {
      create(null, clock, workbench)
    }
  }

  it must "throw NullPointerException if constructing without a clock" in {
    intercept[NullPointerException] {
      create(eventBus, null, workbench)
    }
  }

  private var workbench: IWorkbench = _
  private var clock: IClock = _
  private var eventBus = mockEventBus

  override protected type Tracker = PerspectiveSessionTracker

  override def beforeEach {
    workbench = getWorkbench
    eventBus = mockEventBus
    clock = mock[IClock]
    given(clock.now).willReturn(now)

    super.beforeEach

    closeAllPerspectives
  }

  override protected def create() = create(eventBus)

  override protected def create(eventBus: EventBus) =
    create(eventBus, clock, workbench)

  private def create(eventBus: EventBus, clock: IClock, workbench: IWorkbench) =
    new PerspectiveSessionTracker(eventBus, clock, workbench)

  private def mockEventBus() = new EventBus {
    var events = Seq.empty[PerspectiveSessionEvent]
    override def post(event: Object) {
      super.post(event)
      if (event.isInstanceOf[PerspectiveSessionEvent])
        events :+= event.asInstanceOf[PerspectiveSessionEvent]
    }
  }

  private def verifyEvent(
    event: PerspectiveSessionEvent,
    perspective: IPerspectiveDescriptor,
    start: Instant,
    end: Instant) {

    event.perspective must be(perspective)
    event.instant must be(start)
    event.duration must be(new Duration(start, end))
  }
}