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

package rabbit.tracking
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import java.util.concurrent.atomic.AtomicInteger

@RunWith(classOf[JUnitRunner])
final class AbstractUserTrackerSpec extends AbstractUserTrackerSpecBase {

  private[AbstractUserTrackerSpec] class Tester(service: IUserMonitorService)
    extends AbstractUserTracker(service) {

    private var _onActiveCount = new AtomicInteger
    private var _onInactiveCount = new AtomicInteger

    def onActiveCount = _onActiveCount.get()
    def onInactiveCount = _onInactiveCount.get()

    override def onUserActive() { _onActiveCount.incrementAndGet() }
    override def onUserInactive() { _onInactiveCount.incrementAndGet() }
  }

  type Tracker = Tester

  behavior of "AbstractUserTracker"

  it must "notify active when user becomes active" in {
    tracker.enable()
    getMockService(tracker).notifyActive()
    tracker.onActiveCount must be(1)
    tracker.onInactiveCount must be(0)
  }

  it must "notify inactive when user becomes inactive" in {
    tracker.enable()
    getMockService(tracker).notifyInactive()
    tracker.onInactiveCount must be(1)
    tracker.onActiveCount must be(0)
  }

  it must "throw NullPointerException if service is null" in {
    intercept[NullPointerException] {
      create(null)
    }
  }

  override protected def create(service: IUserMonitorService) = new Tester(service)
}