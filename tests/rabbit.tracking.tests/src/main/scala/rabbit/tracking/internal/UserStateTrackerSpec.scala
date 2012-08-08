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

import java.lang.Thread.sleep
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.CountDownLatch

import org.eclipse.swt.SWT.{ MouseDown, KeyDown }
import org.eclipse.swt.widgets.{ Shell, Event, Display }
import org.eclipse.ui.PlatformUI.getWorkbench
import org.joda.time.Instant.now
import org.junit.runner.RunWith
import org.mockito.BDDMockito.given
import org.scalatest.concurrent.Timeouts
import org.scalatest.junit.JUnitRunner
import org.scalatest.mock.MockitoSugar.mock
import org.scalatest.time.SpanSugar.convertIntToGrainOfTime

import com.google.common.eventbus.{ EventBus, Subscribe }

import rabbit.tracking.event.UserStateEvent
import rabbit.tracking.tests.TestImplicits.{ nTimes, funToRunnable }
import rabbit.tracking.util.{ SystemClock, IClock }
import rabbit.tracking.AbstractTrackerSpecBase

@RunWith(classOf[JUnitRunner])
final class UserStateTrackerSpec extends AbstractTrackerSpecBase with Timeouts {

  behavior of classOf[UserStateTracker].getSimpleName

  it must "notify when user returns to active by clicking the mouse" in {
    testUserInputShouldChangeStateToActive(MouseDown)
  }

  it must "notify when user returns to active by pressing a key" in {
    testUserInputShouldChangeStateToActive(KeyDown)
  }

  it must "not notify when user is already active" in {
    val listener = registerNewListener
    simulateUserInputBeforeTimeout(MouseDown)
    simulateUserInputBeforeTimeout(KeyDown)
    simulateUserInputBeforeTimeout(MouseDown)
    listener must have('activeCount(0), 'inactiveCount(0))
  }

  it must "do nothing after the display has been disposed" in {
    val display = mock[Display]
    given(display.isDisposed).willReturn(true)
    val tracker = create(display)
    try {
      tracker.start
    } catch {
      case e => fail(e)
    } finally {
      tracker.stop
    }
  }

  it must "do nothing when stopped" in {
    tracker.start
    tracker.stop
    val listener = registerNewListener
    sleep(tracker.getTimeoutMillis * 2)
    simulateUserInput(1, KeyDown)
    simulateUserInput(1, MouseDown)
    listener must have('activeCount(0), 'inactiveCount(0))
  }

  it must "use provide clock to set time for events" in {
    val instant = now
    val clock = mock[IClock]
    given(clock.now).willReturn(instant)
    val tracker = create(clock)
    try {
      val listener = registerNewListener
      tracker.start
      waitUntilUserIsInactive(listener, tracker)
      listener.event.instant must be(instant)
    } finally {
      tracker.stop
    }
  }

  it must "throw NullPointerException if constructing without a display" in {
    intercept[NullPointerException] {
      create(null.asInstanceOf[Display])
    }
  }

  it must "throw IllegalArgumentException if constructing with a negative timeout" in {
    intercept[IllegalArgumentException] {
      create(-1)
    }
  }

  it must "throw IllegalStateException on isUserActive if not started" in {
    intercept[IllegalStateException] {
      tracker.stop
      tracker.isUserActive
    }
  }

  private class CountingListener(
    val activeLatch: CountDownLatch,
    val inactiveLatch: CountDownLatch) {

    @volatile var event: UserStateEvent = _

    private val _activeCount = new AtomicInteger
    private val _inactiveCount = new AtomicInteger

    def this() = this(new CountDownLatch(1), new CountDownLatch(1))

    def activeCount = _activeCount.get
    def inactiveCount = _inactiveCount.get

    @Subscribe def handle(event: UserStateEvent) {
      this.event = event
      if (event.isUserActive) onActive else onInactive
    }

    private def onActive() {
      _activeCount.incrementAndGet
      if (activeLatch != null) activeLatch.countDown
    }

    private def onInactive() {
      _inactiveCount.incrementAndGet
      if (inactiveLatch != null) inactiveLatch.countDown
    }
  }

  private val defaultTimeoutMillis = 10L
  private val defaultInstant = now

  private var eventBus: EventBus = _
  private var clock: IClock = _
  private var display: Display = _
  private var shell: Shell = _

  override protected type Tracker = UserStateTracker

  override def beforeEach() {
    eventBus = new EventBus
    clock = SystemClock.INSTANCE
    display = getWorkbench.getDisplay
    display.syncExec(() => { shell = new Shell(display) })
    super.beforeEach
  }

  override def afterEach() {
    super.afterEach
    display.asyncExec(() => { shell.dispose })
  }

  override protected def create() = create(display)

  private def create(clock: IClock) =
    new UserStateTracker(eventBus, clock, display, defaultTimeoutMillis, MILLISECONDS)

  private def create(timeout: Long) =
    new UserStateTracker(eventBus, clock, display, timeout, MILLISECONDS)

  private def create(display: Display) =
    new UserStateTracker(eventBus, clock, display, defaultTimeoutMillis, MILLISECONDS)

  private def registerNewListener() = {
    val listener = new CountingListener
    eventBus.register(listener)
    listener
  }

  private def waitUntilUserIsInactive(listener: CountingListener, tracker: Tracker = this.tracker) {
    failAfter(1 second) { listener.inactiveLatch.await }
    tracker.isUserActive must be(false)
  }

  private def waitUntilUserIsActive(listener: CountingListener) {
    failAfter(1 second) { listener.activeLatch.await }
    tracker.isUserActive must be(true)
  }

  private def simulateUserInput(n: Int, event: Int) {
    display.syncExec(() => { n times shell.notifyListeners(event, new Event) })
  }

  private def simulateUserInputBeforeTimeout(event: Int) {
    sleep(tracker.getTimeoutMillis / 2)
    simulateUserInput(1, event)
  }

  private def testUserInputShouldChangeStateToActive(eventType: Int) {
    val listener = registerNewListener
    tracker.start

    waitUntilUserIsInactive(listener)
    listener must have('activeCount(0), 'inactiveCount(1))

    simulateUserInput(2, eventType)
    waitUntilUserIsActive(listener)
    listener must have('activeCount(1), 'inactiveCount(1))
  }
}