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

package rabbit.tracking.internal;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.lang.Thread.currentThread;
import static java.lang.Thread.sleep;
import static org.eclipse.swt.SWT.KeyDown;
import static org.eclipse.swt.SWT.MouseDown;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.event.UserStateEvent;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;

public final class UserStateTracker extends AbstractTracker {

  private class UserStateChecker implements Runnable, Listener {

    private final AtomicBoolean terminated;
    private final AtomicBoolean userActive;
    private final AtomicInteger lastEventId;
    private final AtomicReference<Thread> backgroundThread;

    UserStateChecker() {
      this.terminated = new AtomicBoolean(false);
      this.userActive = new AtomicBoolean(false);
      this.lastEventId = new AtomicInteger(0);
      this.backgroundThread = new AtomicReference<Thread>(null);
    }

    @Override public void handleEvent(Event event) {
      onUserInputReceived();
    }

    @Override public void run() {
      userActive.set(true); // Assume user is active initially
      backgroundThread.set(currentThread());
      startMonitoringUserStateInBackground();
    }

    boolean isTerminate() {
      return terminated.get();
    }

    boolean isUserActive() {
      return userActive.get();
    }

    void terminate() {
      terminated.set(true);
      wakeupBackgroundThread();
    }

    private void notifyUserIsActiveIf(boolean conditionMet) {
      if (conditionMet)
        bus.post(new UserStateEvent(clock.now(), true));
    }

    private void notifyUserIsInactive() {
      final int snapshot = lastEventId.get();
      display.asyncExec(new Runnable() {
        @Override public void run() {
          if (snapshot == lastEventId.get())
            bus.post(new UserStateEvent(clock.now(), false));
        }
      });
    }

    private void onUserInactiveState() {
      userActive.set(false);
      notifyUserIsInactive();
    }

    private void onUserInputReceived() {
      lastEventId.incrementAndGet();
      notifyUserIsActiveIf(userWasInactive());
      wakeupBackgroundThread();
    }

    private void startMonitoringUserStateInBackground() {
      while (!isTerminate())
        waitForUserStateChangeAndHandleIt();
    }

    private boolean userWasInactive() {
      return !userActive.getAndSet(true);
    }

    private void waitForUserStateChangeAndHandleIt() {
      try {
        waitUntilUserIsInactive();
        onUserInactiveState();
        waitUntilUserIsActive();
      } catch (InterruptedException userIsActiveOrThisTerminate) {
      }
    }

    /**
     * Waits until a user input occurs, then an {@link InterruptedException}
     * will be thrown.
     */
    private void waitUntilUserIsActive() throws InterruptedException {
      while (true)
        sleep(Long.MAX_VALUE);
    }

    /**
     * Waits until timeout period is reached, then returns normally, or throws
     * {@link InterruptedException} if user input occurs while waiting.
     */
    private void waitUntilUserIsInactive() throws InterruptedException {
      sleep(timeoutMillis);
    }

    private void wakeupBackgroundThread() {
      Thread thread = backgroundThread.get();
      if (thread != null)
        thread.interrupt();
    }
  }

  private static final int[] EVENTS = {KeyDown, MouseDown};

  private final EventBus bus;
  private final IClock clock;
  private final Display display;
  private final long timeoutMillis;
  private final AtomicReference<UserStateChecker> helperRef;

  public UserStateTracker(
      EventBus bus, IClock clock, Display display, long timeout, TimeUnit unit) {
    this.bus = checkNotNull(bus, "bus");
    this.clock = checkNotNull(clock, "clock");
    this.display = checkNotNull(display, "display");

    checkArgument(timeout >= 0, "timeout = " + timeout);
    this.timeoutMillis = checkNotNull(unit, "unit").toMillis(timeout);
    this.helperRef = new AtomicReference<UserStateChecker>(null);
  }

  public long getTimeoutMillis() {
    return timeoutMillis;
  }

  /**
   * @throws IllegalStateException if !{@link #isStarted()}
   */
  public boolean isUserActive() {
    checkState(isStarted(), "This tracker is not started.");
    return helperRef.get().isUserActive();
  }

  @Override protected void onStart() {
    startHelper();
  }

  @Override protected void onStop() {
    stopHelper();
  }

  private void addEventFiltersAsync(final Listener listener) {
    display.asyncExec(new Runnable() {
      @Override public void run() {
        for (int event : EVENTS)
          display.addFilter(event, listener);
      }
    });
  }

  private UserStateChecker obtainNewHelper() {
    UserStateChecker helper = new UserStateChecker();
    helperRef.set(helper);
    return helper;
  }

  private void removeEventFiltersAsync(final Listener listener) {
    display.asyncExec(new Runnable() {
      @Override public void run() {
        for (int event : EVENTS)
          display.removeFilter(event, listener);
      }
    });
  }

  private void startHelper() {
    final UserStateChecker helper = obtainNewHelper();
    if (!display.isDisposed()) {
      addEventFiltersAsync(helper);
      new Thread(helper, UserStateChecker.class.getSimpleName()).start();
    }
  }

  private void stopHelper() {
    final UserStateChecker helper = helperRef.getAndSet(null);
    if (!display.isDisposed()) {
      removeEventFiltersAsync(helper);
    }
    helper.terminate();
  }
}
