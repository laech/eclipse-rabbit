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
import org.scalatest.matchers.MustMatchers
import org.scalatest.{ FunSpec, BeforeAndAfter }
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CountDownLatch
import rabbit.tracking.tests.TestImplicits.funToRunnable
import rabbit.tracking.tests.TestImplicits.nTimes
import rabbit.tracking.tests.TestUtils.doInNewThreads

@RunWith(classOf[JUnitRunner])
final class AbstractTrackerSpec extends AbstractTrackerSpecBase {

  private[AbstractTrackerSpec] class Tester extends AbstractTracker {
    private var _startCount = new AtomicInteger
    private var _stopCount = new AtomicInteger

    def startCount = _startCount.get()
    def stopCount = _stopCount.get()

    override protected def onStart() { _startCount.incrementAndGet() }
    override protected def onStop() { _stopCount.incrementAndGet() }
  }

  protected type Tracker = Tester

  behavior of "AbstractTracker"

  it must "notify start when starting" in {
    tracker.stop
    tracker.start
    tracker.startCount must be(1)
  }

  it must "notify start only if previously stopped" in {
    tracker.stop
    tracker.start
    tracker.start
    tracker.startCount must be(1)
  }

  it must "notify stop when stopping" in {
    tracker.start
    tracker.stop
    tracker.stopCount must be(1)
  }

  it must "notify stop only if previously started" in {
    tracker.start
    tracker.stop
    tracker.stop
    tracker.stopCount must be(1)
  }

  it must "behave correctly when starting from multiple threads" in {
    100 times {
      val tracker = create()
      tracker.stop
      doInNewThreads(20, 100 times tracker.start)
      tracker.startCount must be(1)
      tracker.isStarted must be(true)
    }
  }

  it must "behave correctly when stopping from multiple threads" in {
    100 times {
      val tracker = create()
      tracker.start
      doInNewThreads(20, 100 times tracker.stop)
      tracker.stopCount must be(1)
      tracker.isStarted must be(false)
    }
  }

  it must "behave correctly when starting/stopping from multiple threads" in {
    100 times {
      val tracker = create()
      doInNewThreads(20, 100 times {
        tracker.start
        tracker.stop
      })

      val diff = tracker.startCount - tracker.stopCount
      diff must be(0 plusOrMinus 1)
      tracker.isStarted must be(diff > 0)
    }
  }

  override protected def create() = new Tester
}