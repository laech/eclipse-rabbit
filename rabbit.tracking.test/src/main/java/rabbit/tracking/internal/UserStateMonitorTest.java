/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
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
import static org.eclipse.swt.SWT.KeyDown;
import static org.eclipse.swt.SWT.MouseDown;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.concurrent.ScheduledExecutorService;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.IUserStateListener;

public final class UserStateMonitorTest {

  private static class CountingListener implements IUserStateListener {
    private int activeCount;
    private int inactiveCount;

    @Override public void onActive() {
      activeCount++;
    }

    @Override public void onInactive() {
      inactiveCount++;
    }
  }

  private static final Display DISPLAY = getWorkbench().getDisplay();
  private static Shell shell;

  @After public void after() {
    shell.dispose();
  }

  @Before public void before() {
    shell = new Shell(DISPLAY);
  }

  @Test public void shouldNotifyObserversWhenUserReturnsToActiveByClickingTheMouse()
      throws Exception {
    long idleInterval = 500;
    long runDelay = 10;
    UserStateMonitor monitor = create(DISPLAY, idleInterval, runDelay);

    CountingListener listener = new CountingListener();
    monitor.addListener(listener);
    monitor.setRunning(true);

    sleep(idleInterval + (runDelay * 2));
    sleep(idleInterval + (runDelay * 2));
    assertFalse(monitor.isUserActive());

    shell.notifyListeners(MouseDown, new Event());
    shell.notifyListeners(MouseDown, new Event());
    shell.notifyListeners(MouseDown, new Event());

    assertEquals(1, listener.inactiveCount);
    assertEquals(1, listener.activeCount);
  }

  @Test public void shouldNotifyObserversWhenUserReturnsToActiveByPressingAKey()
      throws Exception {
    long idleInterval = 100;
    long runDelay = 10;
    UserStateMonitor monitor = create(DISPLAY, idleInterval, runDelay);

    CountingListener listener = new CountingListener();
    monitor.addListener(listener);
    monitor.setRunning(true);

    sleep(idleInterval + (runDelay * 2));
    sleep(idleInterval + (runDelay * 2));
    assertFalse(monitor.isUserActive());

    shell.notifyListeners(KeyDown, new Event());
    shell.notifyListeners(KeyDown, new Event());

    assertEquals(1, listener.inactiveCount);
    assertEquals(1, listener.activeCount);
  }

  @Test public void shouldNotNotifyObserversWhenUserIsActive()
      throws Exception {
    long idleInterval = 50;
    long runDelay = 10;
    UserStateMonitor monitor = create(DISPLAY, idleInterval, runDelay);

    CountingListener listener = new CountingListener();
    monitor.addListener(listener);
    monitor.setRunning(true);

    sleep(idleInterval / 2);
    assertTrue(monitor.isUserActive());
    shell.notifyListeners(MouseDown, new Event());

    sleep(idleInterval / 2);
    assertTrue(monitor.isUserActive());
    shell.notifyListeners(MouseDown, new Event());

    sleep(idleInterval / 2);
    assertTrue(monitor.isUserActive());
    shell.notifyListeners(MouseDown, new Event());

    assertEquals(0, listener.inactiveCount);
    assertEquals(0, listener.activeCount);
  }

  @Test public void shouldDetectUserHasReturnedToActiveByClickingTheMouse()
      throws Exception {
    long idleInterval = 500;
    long runDelay = 10;
    UserStateMonitor monitor = create(DISPLAY, idleInterval, runDelay);
    monitor.setRunning(true);

    sleep(idleInterval + (runDelay * 2));
    assertFalse(monitor.isUserActive());

    shell.notifyListeners(MouseDown, new Event());
    assertTrue(monitor.isUserActive());
  }

  @Test public void shouldDetectUserHasReturnedToActiveByPressingAKey()
      throws Exception {
    long idleInterval = 50;
    long runDelay = 10;
    UserStateMonitor monitor = create(DISPLAY, idleInterval, runDelay);
    monitor.setRunning(true);

    sleep((idleInterval + runDelay) * 2);
    assertThat(monitor.isUserActive(), is(false));

    shell.notifyListeners(KeyDown, new Event());
    assertThat(monitor.isUserActive(), is(true));
  }

  @Test public void shouldDoNothingAfterTheDisplayHasBeenDisposed() {
    Display display = mock(Display.class);
    given(display.isDisposed()).willReturn(false);
    IdleDetector d = new IdleDetector(display, 10, 10);
    given(display.isDisposed()).willReturn(true);

    try {
      d.setRunning(true);
      d.setRunning(false);
    } catch (Exception e) {
      fail();
    }
  }

  @Test public void shouldNotBeRunningWhenFirstConstructed() {
    assertThat(create(DISPLAY, 10, 10).isRunning(), is(false));
  }

  @Test public void shouldNotNotifyObserversIfNotEnabled() throws Exception {
    long idleInterval = 500;
    long runDelay = 10;
    UserStateMonitor monitor = create(DISPLAY, idleInterval, runDelay);

    CountingListener listener = new CountingListener();
    monitor.addListener(listener);
    monitor.setRunning(false);

    sleep(idleInterval + (runDelay * 2));
    sleep(idleInterval + (runDelay * 2));

    shell.notifyListeners(KeyDown, new Event());
    shell.notifyListeners(MouseDown, new Event());

    assertEquals(0, listener.inactiveCount);
    assertEquals(0, listener.activeCount);
  }

  @Test(expected = IllegalArgumentException.class)//
  public void shouldThrowExceptionIfConstructedWithANegativeDelay() {
    create(DISPLAY, 10, -1);
  }

  @Test(expected = IllegalArgumentException.class)//
  public void shouldThrowExceptionIfConstructedWithANegativeInterval() {
    create(DISPLAY, -1, 10);
  }

  @Test(expected = NullPointerException.class)//
  public void shouldThrowExceptionIfConstructedWithoutADisplay() {
    create(null, 10, 10);
  }

  @Test public void setRunningShouldHandleDuplicateCalls() {
    UserStateMonitor monitor = create(DISPLAY, 100, 100);
    assertFalse(monitor.isRunning());

    try {
      monitor.setRunning(false);
      monitor.setRunning(false);
    } catch (Exception e) {
      fail();
    }

    try {
      monitor.setRunning(true);
      monitor.setRunning(true);
    } catch (Exception e) {
      fail();
    }

    monitor.setRunning(true);
    assertTrue(monitor.isRunning());
    try {
      assertFalse(getService(monitor).isShutdown());
    } catch (Exception e) {
      fail();
    }

    monitor.setRunning(false);
    assertFalse(monitor.isRunning());
    try {
      assertTrue(getService(monitor).isShutdown());
    } catch (Exception e) {
      fail();
    }
  }

  @Test public void userShouldBeAssumedActiveWhenFirstConstructed() {
    assertTrue(create(DISPLAY, 10, 10).isUserActive());
  }

  private UserStateMonitor create(Display display, long idleTime, long delay) {
    return new UserStateMonitor(display, idleTime, delay);
  }

  private ScheduledExecutorService getService(UserStateMonitor monitor)
      throws Exception {
    Field field = monitor.getClass().getDeclaredField("service");
    field.setAccessible(true);
    return (ScheduledExecutorService) field.get(monitor);
  }
}
