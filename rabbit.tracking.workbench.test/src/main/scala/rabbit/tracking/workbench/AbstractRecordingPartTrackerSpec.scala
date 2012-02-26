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

package rabbit.tracking.workbench
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import rabbit.tracking.AbstractUserTrackerSpecBase
import rabbit.tracking.IUserMonitorService
import rabbit.tracking.workbench.test.WorkbenchTestUtil._
import org.joda.time.Instant
import org.joda.time.Instant.now
import org.joda.time.Duration
import org.eclipse.ui.IWorkbenchPart
import java.lang.Thread.sleep

@RunWith(classOf[JUnitRunner])
final class AbstractRecordingPartTrackerSpec extends AbstractUserTrackerSpecBase {

  private[AbstractRecordingPartTrackerSpec] class Tester(service: IUserMonitorService)
    extends AbstractRecordingPartTracker(service) {

    private var _instant: Instant = _
    private var _duration: Duration = _
    private var _part: IWorkbenchPart = _

    def startTime = synchronized { _instant }
    def duration = synchronized { _duration }
    def part = synchronized { _part }

    override def onPartEvent(instant: Instant, d: Duration, part: IWorkbenchPart) {
      synchronized {
        _instant = instant
        _duration = d
        _part = part
      }
    }

    override def saveData() {}
  }

  type Tracker = Tester

  override def beforeEach() {
    super.beforeEach()
    closeAllParts()
  }

  behavior of "AbstractRecordingPartTracker"

  it must "record part focused duration" in {
    tracker.enable()
    val preStart = now()
    val part = openRandomPart()
    val postStart = now()

    sleep(5)

    val preEnd = now()
    openRandomPart()
    val postEnd = now()
    verifyEvent(part, preStart, postStart, preEnd, postEnd)
  }

  it must "stop recording on user inactive" in {
    tracker.enable()
    val preStart = now()
    val part = openRandomPart()
    val postStart = now()

    sleep(5)

    val preEnd = now()
    getMockService(tracker).notifyInactive()
    val postEnd = now()
    verifyEvent(part, preStart, postStart, preEnd, postEnd)
  }

  it must "start recording if there is an active part on user active" in {
    val part = openRandomPart()
    tracker.enable()
    sleep(5)
    tracker.disable()
    tracker.part must be(part)
  }

  it must "not start recording if there is no active part on user active" in {
    closeAllParts()
    tracker.enable()
    sleep(5)
    tracker.disable()
    tracker.part must be(null)
  }

  it must "stop recording on disable" in {
    tracker.enable()
    val preStart = now()
    val part = openRandomPart()
    val postStart = now()

    sleep(5)

    val preEnd = now()
    tracker.disable()
    val postEnd = now()
    verifyEvent(part, preStart, postStart, preEnd, postEnd)
  }

  override protected def create(service: IUserMonitorService) = new Tester(service)

  private def verifyEvent(
    part: IWorkbenchPart,
    preStart: Instant,
    postStart: Instant,
    preEnd: Instant,
    postEnd: Instant) {

    tracker.part must be(part)

    val start = tracker.startTime.getMillis
    start must be >= preStart.getMillis
    start must be <= postStart.getMillis

    val end = start + tracker.duration.getMillis
    end must be >= preEnd.getMillis
    end must be <= postEnd.getMillis;
  }
}