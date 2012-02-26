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

@RunWith(classOf[JUnitRunner])
final class AbstractTrackerSpec extends AbstractTrackerSpecBase {

  private[AbstractTrackerSpec] class Tester extends AbstractTracker {
    private var _enableCount = new AtomicInteger
    private var _disableCount = new AtomicInteger

    def enableCount = _enableCount.get()
    def disableCount = _disableCount.get()

    override def onEnable() { _enableCount.incrementAndGet() }
    override def onDisable() { _disableCount.incrementAndGet() }
  }

  protected type Tracker = Tester

  behavior of "AbstractTracker"

  it must "notify enable when enabling" in {
    tracker.disable()
    tracker.enable()
    tracker.enableCount must be(1)
  }

  it must "notify enable only if previously disabled" in {
    tracker.disable()
    tracker.enable()
    tracker.enable()
    tracker.enableCount must be(1)
  }

  it must "notify disable when disabling" in {
    tracker.enable()
    tracker.disable()
    tracker.disableCount must be(1)
  }

  it must "notify disable only if previously enabled" in {
    tracker.enable()
    tracker.disable()
    tracker.disable()
    tracker.disableCount must be(1)
  }

  override protected def create() = new Tester
}