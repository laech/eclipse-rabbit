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

import java.lang.System.nanoTime
import java.lang.Thread.sleep
import org.eclipse.ui.{ IWorkbenchWindow, IWorkbenchPart }
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.matchers.MustMatchers
import org.scalatest.{ FunSpec, BeforeAndAfter }
import rabbit.tracking.workbench.test.WorkbenchTestUtil._
import rabbit.tracking.{ IUserMonitorService, AbstractUserTrackerSpecBase }
import rabbit.tracking.IUserListener

@RunWith(classOf[JUnitRunner])
final class AbstractPartTrackerSpec extends AbstractUserTrackerSpecBase {

  private[AbstractPartTrackerSpec] class Tester(service: IUserMonitorService)
    extends AbstractPartTracker(service) {

    private[this] var _focusedCount = 0
    private[this] var _focusedTime = 0L
    private[this] var _focusedPart: IWorkbenchPart = _

    private[this] var _unfocusedCount = 0
    private[this] var _unfocusedTime = 0L
    private[this] var _unfocusedPart: IWorkbenchPart = _

    override def onPartFocused(part: IWorkbenchPart) {
      synchronized {
        _focusedPart = part;
        _focusedCount += 1
        _focusedTime = nanoTime();
      }
    }

    override def onPartUnfocused(part: IWorkbenchPart) {
      synchronized {
        _unfocusedPart = part
        _unfocusedCount += 1
        _unfocusedTime = nanoTime();
      }
    }

    def reset() {
      synchronized {
        _focusedPart = null
        _focusedCount = 0
        _focusedTime = 0
        _unfocusedPart = null
        _unfocusedCount = 0
        _unfocusedTime = 0
      }
    }

    def focusedCount = synchronized { _focusedCount }
    def focusedTime = synchronized { _focusedTime }
    def focusedPart = synchronized { _focusedPart }

    def unfocusedCount = synchronized { _unfocusedCount }
    def unfocusedTime = synchronized { _unfocusedTime }
    def unfocusedPart = synchronized { _unfocusedPart }

    override protected def onUserActive() {}
    override protected def onUserInactive() {}
    override protected def saveData() {}
  }

  private[this] var window: IWorkbenchWindow = _

  override def beforeEach() {
    super.beforeEach()
    closeAllParts();
  }

  override def afterEach() {
    super.afterEach()
    if (window != null) {
      close(window)
      window = null
    }
  }

  type Tracker = Tester

  behavior of "AbstractPartTracker"

  it must "notify part focused when a part is already focused when enabled" in {
    val part = activate(openRandomPart())
    tracker.enable()
    tracker.focusedPart must be(part)
    tracker.focusedCount must be(1)
    tracker.unfocusedCount must be(0)
  }

  it must "notify part focused due to part opened" in {
    tracker.enable()
    val part = openRandomPart()
    tracker.focusedCount must be(1)
    tracker.focusedPart must be(part)
  }

  it must "notify part focused due to part selected" in {
    val part1 = openRandomPart()
    val part2 = openRandomPart()
    activate(part1)
    tracker.enable()
    tracker.reset()

    activate(part2)
    tracker.focusedCount must be(1)
    tracker.focusedPart must be(part2)
  }

  it must "notify part unfocused due to new part selected" in {
    val part1 = openRandomPart()
    val part2 = openRandomPart()
    activate(part1)
    tracker.enable()
    tracker.reset()

    activate(part2)
    tracker.unfocusedCount must be(1)
    tracker.unfocusedPart must be(part1)
  }

  it must "notify part unfocused due to new part opened" in {
    tracker.enable()
    val part = openRandomPart()

    openRandomPart()
    tracker.unfocusedCount must be(1)
    tracker.unfocusedPart must be(part)
  }

  it must "notify part unfocused due to window unfocused" in {
    tracker.enable()
    val part = openRandomPart()

    window = openWindow()
    tracker.unfocusedPart must be(part)
    tracker.unfocusedCount must be(1)
  }

  it must "notify part unfocused due to part closed" in {
    val part = openRandomPart();
    tracker.enable()

    hide(part)
    tracker.unfocusedPart must be(part)
    tracker.unfocusedCount must be(1)
  }

  it must "notify part unfocused due to window closed" in {
    val window = openWindow()
    var part: IWorkbenchPart = null
    try {
      part = openRandomPart(window)
      tracker.enable()
      tracker.unfocusedCount must be(0)
    } finally {
      close(window)
    }
    tracker.unfocusedPart must be(part)
    tracker.unfocusedCount must be(1)
  }

  it must "notify part unforcused on old part before notifying part focused on new part" in {
    tracker.enable()
    openRandomPart()
    openRandomPart()
    tracker.unfocusedTime must be < tracker.focusedTime
  }

  it must "not notify when disabled" in {
    tracker.disable()
    openRandomPart()
    openRandomPart()
    tracker.focusedCount must be(0)
    tracker.unfocusedCount must be(0)
  }

  it must "track newly opened window" in {
    tracker.enable()
    window = openWindow()
    tracker.reset()

    openRandomPart(window)
    tracker.focusedCount must be > 0
  }

  it must "return the active part of focused window when getFocusedPart is called" in {
    val part = openRandomPart()
    tracker.getFocusedPart must be(part)
  }

  it must "returns null if no part is focused when getFocusedPart is called" in {
    openRandomPart()
    window = openWindow()

    closeAllParts(window)
    tracker.getFocusedPart must be(null)
  }

  override protected def create(service: IUserMonitorService) = new Tester(service)
}
