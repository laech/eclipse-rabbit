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
import org.joda.time.{ Instant, Duration }
import org.junit.runner.RunWith
import org.mockito.Matchers.any
import org.mockito.Mockito.{ verify, never, doAnswer }
import org.mockito.invocation.InvocationOnMock
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock

import rabbit.tracking.tests.TestImplicits.funToAnswer
import rabbit.tracking.{ IPartSessionListener, IListenableTracker, AbstractTrackerSpecBase }

@RunWith(classOf[JUnitRunner])
final class AbstractPartTrackerSpec extends AbstractTrackerSpecBase {

  class Tester(tracker: IListenableTracker[IPartSessionListener])
    extends AbstractPartTracker(tracker) {

    var sessions: Seq[(Instant, Duration, IWorkbenchPart)] = Seq.empty

    override protected def onPartSession(instant: Instant, duration: Duration, part: IWorkbenchPart) {
      sessions :+= (instant, duration, part)
    }
  }

  private var partTracker: IListenableTracker[IPartSessionListener] = _
  private var partTrackerListeners: List[IPartSessionListener] = _

  override def beforeEach {
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

  behavior of classOf[AbstractPartTracker].getSimpleName

  it must "enable part tracker on enable" in {
    tracker.enable
    verify(partTracker).enable
    verify(partTracker, never).disable
  }

  it must "disable part tracker on disable" in {
    tracker.enable
    tracker.disable
    verify(partTracker).disable
  }

  it must "notify subclass of captured events on disable" in {
    tracker.enable

    val instant = now
    val duration = ZERO
    val part = mock[IWorkbenchPart]

    doAnswer({ i: InvocationOnMock =>
      partTrackerListeners foreach (_.onPartSession(instant, duration, part))
    }).when(partTracker).disable

    tracker.disable

    tracker.sessions must be(Seq((instant, duration, part)))
  }

  it must "notify subclass of captured events" in {
    tracker.enable

    val instant = now
    val duration = ZERO
    val part = mock[IWorkbenchPart]

    partTrackerListeners foreach (_.onPartSession(instant, duration, part))

    tracker.sessions must be(Seq((instant, duration, part)))
  }

  it must "not notify if disabled" in {
    tracker.enable
    tracker.disable
    partTrackerListeners foreach (_.onPartSession(now, ZERO, mock[IWorkbenchPart]))
    tracker.sessions must be(Seq.empty)
  }

  it must "throw NullPointerException if constructing without a part tracker" in {
    intercept[NullPointerException] {
      create(null)
    }
  }

  override protected type Tracker = Tester

  override protected def create() = create(partTracker)

  private def create(partTracker: IListenableTracker[IPartSessionListener]) = new Tester(partTracker)
}