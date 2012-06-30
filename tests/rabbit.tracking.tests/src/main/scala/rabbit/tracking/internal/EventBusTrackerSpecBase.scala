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

import org.mockito.Mockito.{ verify, only, inOrder }
import org.scalatest.matchers.MustMatchers
import org.scalatest.mock.MockitoSugar.mock
import org.scalatest.FlatSpec

import com.google.common.eventbus.EventBus

import rabbit.tracking.ITracker

trait EventBusTrackerSpecBase extends FlatSpec {

  behavior of "Tracker listening on an event bus"

  it must "register to the event bus when starting" in {
    val eventBus = mock[EventBus]
    val tracker = create(eventBus)
    try {
      tracker.start
      verify(eventBus, only).register(tracker)

    } finally {
      tracker.stop
    }
  }

  it must "unregister from the event bus when stopping" in {
    val eventBus = mock[EventBus]
    val tracker = create(eventBus)
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

  protected def create(eventBus: EventBus): ITracker
}