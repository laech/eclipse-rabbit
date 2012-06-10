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
import org.eclipse.ui.{ IWorkbenchPart, IWorkbench }
import org.joda.time.Instant.now
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Mockito.{ verify, only, inOrder }
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import com.google.common.eventbus.{ EventBus }

import rabbit.tracking.event.{ PartSessionEvent, PartFocusEvent }
import rabbit.tracking.tests.Workbenches.{ openRandomPart, closeAllParts }
import rabbit.tracking.util.SystemClock
import rabbit.tracking.AbstractTrackerSpecBase

@RunWith(classOf[JUnitRunner])
final class PartSessionTrackerSpec extends AbstractTrackerSpecBase {

  behavior of classOf[PartSessionTracker].getSimpleName

  it must "register to the event bus when starting" in {
    val eventBus = mock[EventBus]
    val tracker = create(eventBus, workbench)
    try {
      tracker.start
      verify(eventBus, only).register(tracker)

    } finally {
      tracker.stop
    }
  }

  it must "unregister from the event bus when stopping" in {
    val eventBus = mock[EventBus]
    val tracker = create(eventBus, workbench)
    try {
      tracker.start
      tracker.stop

      val order = inOrder(eventBus)
      order.verify(eventBus).unregister(tracker)
      order.verifyNoMoreInteractions

    } finally {
      tracker.stop
    }
  }

  it must "record part session duration using events from event bus" in {
    val part = mock[IWorkbenchPart]
    val start = new PartFocusEvent(new Instant(10), part, true)
    val end = new PartFocusEvent(new Instant(100), part, false)

    tracker.start
    eventBus.post(start)
    eventBus.post(end)

    eventBus.events.size must be(1)
    val event = eventBus.events(0)
    event.part must be(part)
    event.instant must be(start.instant)
    event.duration must be(new Duration(start.instant, end.instant))
  }

  it must "record part session duration on part switch" in {
    val helper = new PartFocusTracker(eventBus, SystemClock.INSTANCE, workbench)
    helper.start
    try {

      tracker.start
      val preStart = currentTimeMillis
      val part = openRandomPart
      val postStart = currentTimeMillis

      sleep(10)

      val preEnd = currentTimeMillis
      openRandomPart
      val postEnd = currentTimeMillis

      eventBus.events.size must be(1)
      verifyEvent(eventBus.events(0), part, preStart, postStart, preEnd, postEnd)

    } finally {
      helper.stop
    }
  }

  it must "record part session duration on start if there is a focused part" in {
    val part = openRandomPart

    val preStart = currentTimeMillis
    tracker.start
    val postStart = currentTimeMillis

    sleep(10)

    val preEnd = currentTimeMillis
    tracker.stop
    val postEnd = currentTimeMillis

    eventBus.events.size must be(1)
    verifyEvent(eventBus.events(0), part, preStart, postStart, preEnd, postEnd)
  }

  it must "record part session duration on stop if there is a session in progress" in {
    tracker.start
    eventBus.post(new PartFocusEvent(now, mock[IWorkbenchPart], true))
    tracker.stop
    eventBus.events.size must be(1)
  }

  it must "record nothing on start if there is no focused part" in {
    closeAllParts
    tracker.start
    tracker.stop
    eventBus.events must be(Seq.empty)
  }

  it must "throw NullPointerException if constructing without a workbench" in {
    intercept[NullPointerException] {
      create(eventBus, null)
    }
  }

  it must "throw NullPointerException if constructing without an event bus" in {
    intercept[NullPointerException] {
      create(null, workbench)
    }
  }

  private var workbench = getWorkbench
  private var eventBus = mockEventBus

  override def beforeEach {
    workbench = getWorkbench
    eventBus = mockEventBus
    super.beforeEach
    closeAllParts
  }

  override protected type Tracker = PartSessionTracker

  override protected def create() = create(eventBus, workbench)

  private def create(eventBus: EventBus, workbench: IWorkbench) =
    new PartSessionTracker(eventBus, workbench)

  private def mockEventBus() = new EventBus {
    var events = Seq.empty[PartSessionEvent]
    override def post(event: Object) {
      super.post(event)
      if (event.isInstanceOf[PartSessionEvent])
        events :+= event.asInstanceOf[PartSessionEvent]
    }
  }

  private def verifyEvent(
    event: PartSessionEvent,
    part: IWorkbenchPart,
    preStart: Long,
    postStart: Long,
    preEnd: Long,
    postEnd: Long) {

    event.part must be(part)

    val start = event.instant.getMillis
    val end = event.instant.plus(event.duration).getMillis
    start must be >= preStart
    start must be <= postStart
    end must be >= preEnd
    end must be <= postEnd
  }
}