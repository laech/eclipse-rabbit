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

import com.google.common.collect.ImmutableList
import com.google.common.eventbus.EventBus
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference
import org.eclipse.swt.widgets.Display
import org.eclipse.swt.widgets.Event
import org.eclipse.swt.widgets.Listener
import rabbit.tracking.AbstractTracker
import rabbit.tracking.event.UserStateEvent
import rabbit.tracking.util.IClock

import static java.lang.Thread.*
import static org.eclipse.swt.SWT.*
import static rabbit.tracking.internal.UserStateTracker.*

import static extension com.google.common.base.Preconditions.*

/**
 * Tracks {@link UserStateEvent}s
 */
class UserStateTracker extends AbstractTracker {

  /** The type of events we are filtering on the display */
  static val EVENTS = ImmutableList::<Integer>of(KeyDown, MouseDown)

  val EventBus eventBus
  val IClock clock
  val Display display
  val long timeoutMillis

  val helperRef = new AtomicReference<UserStateChecker>

  new(
      EventBus eventBus,
      IClock clock,
      Display display,
      long timeout,
      TimeUnit unit) {
    
    this.eventBus = eventBus.checkNotNull("eventBus")
    this.clock = clock.checkNotNull("clock")
    this.display = display.checkNotNull("display")
  
    checkArgument(timeout >= 0, "timeout = " + timeout)
    this.timeoutMillis = unit.checkNotNull("unit").toMillis(timeout)
  }

  def getTimeoutMillis() { timeoutMillis }

  def isUserActive() {
    if (!started) throw new IllegalStateException("Tracker is not started.")
    
    helperRef.get.isUserActive
  }

  override protected onStart() { starthelper }
  override protected onStop() { stophelper }

  def private starthelper() {
    val helper = obtainNewhelper
    display.asyncExec(| EVENTS.forEach(evt | display.addFilter(evt, helper)))
    new Thread(helper).start
  }

  def private stophelper() {
    val helper = helperRef.getAndSet(null)
    display.asyncExec(| EVENTS.forEach(evt | display.removeFilter(evt, helper)))
    helper.terminate
  }
  
  def private obtainNewhelper() {
    val helper = new UserStateChecker(eventBus, clock, display, timeoutMillis)
    helperRef.set(helper)
    helper
  }
}

class UserStateChecker implements Runnable, Listener {
  
  val terminated = new AtomicBoolean
  val userActive = new AtomicBoolean
  val lastEventId = new AtomicInteger
  val backgroundThread = new AtomicReference<Thread>
  
  val long timeoutMillis
  val Display display
  val EventBus eventBus
  val IClock clock
  
  new(EventBus eventBus, IClock clock, Display display, long timeoutMillis) {
    this.eventBus = eventBus
    this.clock = clock
    this.display = display
    this.timeoutMillis = timeoutMillis
  }

  override run() {
    userActive.set(true) // Assume user is active initially 
    backgroundThread.set(currentThread)
    monitorUserStateInBackground
  }

  def private monitorUserStateInBackground() {
    while (!isTerminated) handleNextUserStateChange
  }

  def private void handleNextUserStateChange() {
    try {
      waitUntilUserIsInactive
      handleUserInactiveState
      waitForNextUserInput
    } catch (InterruptedException userIsActiveOrTerminated) {
    }
  }

  def private handleUserInactiveState() {
    userActive.set(false)
    notifyUserIsInactive
  }

  def private notifyUserIsInactive() {
    val snapshotId = lastEventId.get
    display.asyncExec[ |
        if (snapshotId == lastEventId.get) 
          eventBus.post(new UserStateEvent(clock.now(), false))
    ]
  }

  /**
   * Waits until timeout period is reached, then returns normally,
   * or throws {@link InterruptedException} if user input occurs while waiting.
   */
  def private waitUntilUserIsInactive() { sleep(timeoutMillis) }

  /**
   * Waits until a user input occurs, then an {@link InterruptedException}
   * will be thrown.
   */
  def private waitForNextUserInput() { while (true) sleep(Long::MAX_VALUE) }

  def isTerminated() { terminated.get }
  
  def terminate() {
    terminated.set(true)
    wakeupBackgrondThread
  }

  override handleEvent(Event event) { responseToUserInput }

  def private responseToUserInput() {
    lastEventId.incrementAndGet
    notifyUserIsActiveIf(userWasInactive)
    wakeupBackgrondThread
  }
  
  def private wakeupBackgrondThread() { backgroundThread.get.interrupt }

  def private userWasInactive() { !userActive.getAndSet(true) }

  def private notifyUserIsActiveIf(boolean doIt) {
    if (doIt)
      eventBus.post(new UserStateEvent(clock.now, true))
  }
  
  def isUserActive() { userActive.get }
}