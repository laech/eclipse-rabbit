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

import static java.lang.Thread.sleep;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.eclipse.swt.SWT.KeyDown;
import static org.eclipse.swt.SWT.MouseDown;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static rabbit.tracking.tests.Instants.epoch;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.Test;

import rabbit.tracking.AbstractTrackerTestBase;
import rabbit.tracking.event.UserStateEvent;
import rabbit.tracking.util.IClock;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

public final class UserStateTrackerTest
    extends AbstractTrackerTestBase<UserStateTracker> {

  private static class CountingListener {
    final AtomicInteger activeCount;
    final CountDownLatch activeLatch;
    final AtomicInteger inactiveCount;
    final CountDownLatch inactiveLatch;

    private volatile UserStateEvent event;

    CountingListener() {
      this(new CountDownLatch(1), new CountDownLatch(1));
    }

    CountingListener(CountDownLatch start, CountDownLatch stop) {
      this.activeLatch = start;
      this.inactiveLatch = stop;
      this.activeCount = new AtomicInteger();
      this.inactiveCount = new AtomicInteger();
    }

    @Subscribe public void handle(UserStateEvent event) {
      this.event = event;
      if (event.isUserActive())
        onActive();
      else
        onInactive();
    }

    int activeCount() {
      return activeCount.get();
    }

    int inactiveCount() {
      return inactiveCount.get();
    }

    private void onActive() {
      activeCount.incrementAndGet();
      if (activeLatch != null)
        activeLatch.countDown();
    }

    private void onInactive() {
      inactiveCount.incrementAndGet();
      if (inactiveLatch != null)
        inactiveLatch.countDown();
    }
  }

  private EventBus bus;

  private IClock clock;
  private final long defaultTimeoutMillis = 10L;
  private Display display;
  private Shell shell;

  @Override public void after() throws Exception {
    super.after();
    display.asyncExec(new Runnable() {
      @Override public void run() {
        shell.dispose();
      }
    });
  }

  @Test public void notifiesOnKeyPress() throws Exception {
    testUserInputChangesStateToActive(KeyDown);
  }

  @Test public void notifiesOnMouseClick() throws Exception {
    testUserInputChangesStateToActive(MouseDown);
  }

  @Test public void notNotifyIfDisplayIsDisposed() {
    UserStateTracker tracker = newTracker(newDisposedDisplay());
    try {
      tracker.start();
      tracker.stop();
      // No error
    } finally {
      tracker.stop();
    }
  }

  @Test public void notNotifyIfStopped() throws Exception {
    tracker().start();
    tracker().stop();

    CountingListener listener = registerNewListener();
    sleep(tracker().getTimeoutMillis() * 2);
    simulateUserInput(1, KeyDown);
    simulateUserInput(1, MouseDown);

    assertThat(listener.activeCount(), is(0));
    assertThat(listener.inactiveCount(), is(0));
  }

  @Test public void notNotifyIfUserAlreadyActive() throws Exception {
    CountingListener listener = registerNewListener();
    simulateUserInputBeforeTimeout(MouseDown);
    simulateUserInputBeforeTimeout(KeyDown);
    simulateUserInputBeforeTimeout(MouseDown);
    assertThat(listener.activeCount(), is(0));
    assertThat(listener.inactiveCount(), is(0));
  }

  @Test(expected = IllegalArgumentException.class)//
  public void throwsIllegalArgumentOnConstructWithNegativeTimeout() {
    newTracker(bus, clock, display, -1, MILLISECONDS);
  }

  @Test(expected = IllegalStateException.class)//
  public void throwsIllegalStateOnAskOfUserStateIfNotStarted() {
    tracker().stop();
    tracker().isUserActive();
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutDisplay() {
    newTracker(bus, clock, null, defaultTimeoutMillis, MILLISECONDS);
  }

  @Test public void usesProvidedClockToSetEventTime() throws Exception {
    given(clock.now()).willReturn(epoch());
    CountingListener listener = registerNewListener();
    tracker().start();

    waitUntilUserIsInactive(listener);
    assertThat(listener.event.instant(), is(epoch()));
  }

  @Override protected void init() throws Exception {
    super.init();
    bus = new EventBus();

    clock = mock(IClock.class);
    given(clock.now()).willReturn(epoch());

    display = getWorkbench().getDisplay();
    display.syncExec(new Runnable() {
      @Override public void run() {
        shell = new Shell(display);
      }
    });
  }

  @Override protected UserStateTracker newTracker() {
    return newTracker(display);
  }

  private Display newDisposedDisplay() {
    Display display = mock(Display.class);
    given(display.isDisposed()).willReturn(true);
    return display;
  }

  private UserStateTracker newTracker(Display display) {
    return newTracker(bus, clock, display, defaultTimeoutMillis, MILLISECONDS);
  }

  private UserStateTracker newTracker(
      EventBus bus, IClock clock, Display display, long timeout, TimeUnit unit) {
    return new UserStateTracker(bus, clock, display, timeout, unit);
  }

  private CountingListener registerNewListener() {
    CountingListener listener = new CountingListener();
    bus.register(listener);
    return listener;
  }

  private void simulateUserInput(final int times, final int eventType) {
    shell.getDisplay().syncExec(new Runnable() {
      @Override public void run() {
        for (int i = 0; i < times; ++i)
          shell.notifyListeners(eventType, new Event());
      }
    });
  }

  private void simulateUserInputBeforeTimeout(int eventType)
      throws InterruptedException {
    sleep(tracker().getTimeoutMillis() / 2);
    simulateUserInput(1, eventType);
  }

  private void testUserInputChangesStateToActive(int eventType)
      throws InterruptedException {
    CountingListener listener = registerNewListener();
    tracker().start();

    waitUntilUserIsInactive(listener);
    assertThat(listener.activeCount(), is(0));
    assertThat(listener.inactiveCount(), is(1));

    simulateUserInput(2, eventType);
    waitUntilUserIsActive(listener);
    assertThat(listener.activeCount(), is(1));
    assertThat(listener.inactiveCount(), is(1));
  }

  private void waitUntilUserIsActive(CountingListener l)
      throws InterruptedException {
    l.activeLatch.await(1, SECONDS);
    assertThat(tracker().isUserActive(), is(true));
  }

  private void waitUntilUserIsInactive(CountingListener l)
      throws InterruptedException {
    waitUntilUserIsInactive(l, tracker());
  }

  private void waitUntilUserIsInactive(
      CountingListener l, UserStateTracker tracker) throws InterruptedException {
    l.inactiveLatch.await(1, SECONDS);
    assertThat(tracker.isUserActive(), is(false));
  }
}
