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
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.eclipse.swt.SWT.KeyDown;
import static org.eclipse.swt.SWT.MouseDown;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.IUserListener;
import scala.actors.threadpool.AtomicInteger;

public final class UserMonitorServiceTest {

  private static class CountingListener implements IUserListener {
    private final CountDownLatch activeLatch;
    private final CountDownLatch inactiveLatch;
    private final AtomicInteger activeCount;
    private final AtomicInteger inactiveCount;

    CountingListener() {
      this(null, null);
    }

    CountingListener(CountDownLatch activeLatch, CountDownLatch inactiveLatch) {
      this.activeLatch = activeLatch;
      this.inactiveLatch = inactiveLatch;
      this.activeCount = new AtomicInteger();
      this.inactiveCount = new AtomicInteger();
    }

    @Override public void onActive() {
      activeCount.incrementAndGet();
      if (activeLatch != null) {
        activeLatch.countDown();
      }
    }

    @Override public void onInactive() {
      inactiveCount.incrementAndGet();
      if (inactiveLatch != null) {
        inactiveLatch.countDown();
      }
    }
  }

  private Display display;
  private Shell shell;
  private UserMonitorService service;

  @After public void after() {
    service.dispose();
    display.asyncExec(new Runnable() {
      @Override public void run() {
        shell.dispose();
      }
    });
  }

  @Before public void before() {
    display = getWorkbench().getDisplay();
    display.syncExec(new Runnable() {
      @Override public void run() {
        shell = new Shell(display);
      }
    });
    service = create(10L);
  }

  @Test(timeout = 1000)//
  public void shouldNotifyObserversWhenUserReturnsToActiveByClickingTheMouse()
      throws Exception {

    CountDownLatch active = new CountDownLatch(1);
    CountDownLatch inactive = new CountDownLatch(1);
    CountingListener listener = new CountingListener(active, inactive);
    service.addListener(listener);
    service.start();

    inactive.await();
    assertThat(service.isUserActive(), is(false));
    assertThat(listener.activeCount.get(), is(0));
    assertThat(listener.inactiveCount.get(), is(1));

    display.syncExec(new Runnable() {
      @Override public void run() {
        shell.notifyListeners(MouseDown, new Event());
        shell.notifyListeners(MouseDown, new Event());
        shell.notifyListeners(MouseDown, new Event());
      }
    });

    active.await();
    assertThat(service.isUserActive(), is(true));
    assertThat(listener.inactiveCount.get(), is(1));
    assertThat(listener.activeCount.get(), is(1));
  }

  @Test(timeout = 1000)//
  public void shouldNotifyObserversWhenUserReturnsToActiveByPressingAKey()
      throws Exception {

    CountDownLatch active = new CountDownLatch(1);
    CountDownLatch inactive = new CountDownLatch(1);
    CountingListener listener = new CountingListener(active, inactive);
    service.addListener(listener);
    service.start();

    inactive.await();
    assertThat(service.isUserActive(), is(false));
    assertThat(listener.activeCount.get(), is(0));
    assertThat(listener.inactiveCount.get(), is(1));

    display.syncExec(new Runnable() {
      @Override public void run() {
        shell.notifyListeners(KeyDown, new Event());
        shell.notifyListeners(KeyDown, new Event());
      }
    });

    active.await();
    assertThat(service.isUserActive(), is(true));
    assertThat(listener.activeCount.get(), is(1));
    assertThat(listener.inactiveCount.get(), is(1));
  }

  @Test public void shouldNotNotifyObserversWhenUserIsActive() throws Exception {

    long timeout = 100;
    UserMonitorService service = create(timeout);
    CountingListener listener = new CountingListener();
    service.addListener(listener);
    service.start();

    try {
      sleep(timeout / 2);
      assertThat(service.isUserActive(), is(true));
      display.syncExec(new Runnable() {
        @Override public void run() {
          shell.notifyListeners(MouseDown, new Event());
        }
      });

      sleep(timeout / 2);
      assertThat(service.isUserActive(), is(true));
      display.syncExec(new Runnable() {
        @Override public void run() {
          shell.notifyListeners(MouseDown, new Event());
        }
      });

      sleep(timeout / 2);
      assertThat(service.isUserActive(), is(true));
      display.syncExec(new Runnable() {
        @Override public void run() {
          shell.notifyListeners(MouseDown, new Event());
        }
      });

      assertThat(listener.inactiveCount.get(), is(0));
      assertThat(listener.activeCount.get(), is(0));

    } finally {
      service.dispose();
    }
  }

  @Test(timeout = 1000)//
  public void shouldDetectUserHasReturnedToActiveByClickingTheMouse()
      throws Exception {

    CountDownLatch inactive = new CountDownLatch(1);
    service.addListener(new CountingListener(null, inactive));
    service.start();

    inactive.await();
    assertThat(service.isUserActive(), is(false));

    display.syncExec(new Runnable() {
      @Override public void run() {
        shell.notifyListeners(MouseDown, new Event());
      }
    });
    assertThat(service.isUserActive(), is(true));

  }

  @Test(timeout = 1000)//
  public void shouldDetectUserHasReturnedToActiveByPressingAKey()
      throws Exception {

    CountDownLatch active = new CountDownLatch(1);
    CountDownLatch inactive = new CountDownLatch(1);
    service.addListener(new CountingListener(active, inactive));
    service.start();

    inactive.await();
    assertThat(service.isUserActive(), is(false));

    display.syncExec(new Runnable() {
      @Override public void run() {
        shell.notifyListeners(KeyDown, new Event());
      }
    });
    assertThat(service.isUserActive(), is(true));
  }

  @Test public void shouldDoNothingAfterTheDisplayHasBeenDisposed() {
    Display display = mock(Display.class);
    given(display.isDisposed()).willReturn(false);
    given(display.isDisposed()).willReturn(true);
    UserMonitorService service = create(10, display);
    try {
      service.start();
    } catch (Exception e) {
      fail();
    } finally {
      try {
        service.dispose();
      } catch (Exception e) {
        fail();
      }
    }
  }

  @Test public void shouldNotNotifyObserversIfNotEnabled() throws Exception {
    long timeout = 10;
    UserMonitorService service = create(timeout);
    CountingListener listener = new CountingListener();
    service.addListener(listener);

    sleep(timeout);

    display.syncExec(new Runnable() {
      @Override public void run() {
        shell.notifyListeners(KeyDown, new Event());
        shell.notifyListeners(MouseDown, new Event());
      }
    });

    sleep(timeout);
    assertThat(listener.inactiveCount.get(), is(0));
    assertThat(listener.activeCount.get(), is(0));

    service.dispose();
  }

  @Test public void shouldTerminateWorkerThreadWhenDispose() throws Exception {
    UserMonitorService monitor = create(10);
    monitor.start();
    assertTrue(getWorker(monitor).isAlive());
    monitor.dispose();
    sleep(10);
    assertFalse(getWorker(monitor).isAlive());
  }

  @Test(expected = IllegalArgumentException.class)//
  public void shouldThrowExceptionIfConstructedWithANegativeTimeout() {
    create(-1);
  }

  @Test(expected = NullPointerException.class)//
  public void shouldThrowExceptionIfConstructedWithoutADisplay() {
    create(10, null);
  }

  @Test public void userShouldBeAssumedActiveWhenFirstConstructed() {
    assertTrue(create(10).isUserActive());
  }

  @Test(expected = NullPointerException.class)//
  public void shouldThrowExceptionIfAddingNullListener() {
    service.addListener(null);
  }

  @Test(expected = NullPointerException.class)//
  public void shouldThrowExceptionIfRemovingNullListener() {
    service.removeListener(null);
  }

  @Test public void shouldNotNotifyListenerIfRemoved() throws Exception {
    long timeout = 10;
    UserMonitorService service = create(timeout);
    CountingListener listener = new CountingListener();
    service.addListener(listener);
    service.start();

    service.removeListener(listener);
    sleep(timeout);
    assertThat(listener.inactiveCount.get(), is(0));
  }

  @Test(timeout = 1000)//
  public void shouldIgnoreIdenticalListener() throws Exception {
    CountDownLatch inactive = new CountDownLatch(1);
    CountingListener listener = new CountingListener(null, inactive);
    service.addListener(listener); // 1
    service.addListener(listener); // 2
    service.start();

    inactive.await();
    sleep(10);
    assertThat(listener.inactiveCount.get(), is(1));
  }

  private UserMonitorService create(long timeout) {
    return create(timeout, display);
  }

  private UserMonitorService create(long timeout, Display display) {
    return new UserMonitorService(display, timeout, MILLISECONDS);
  }

  private Thread getWorker(UserMonitorService monitor) throws Exception {
    Field field = monitor.getClass().getDeclaredField("worker");
    field.setAccessible(true);
    return (Thread) field.get(monitor);
  }
}
