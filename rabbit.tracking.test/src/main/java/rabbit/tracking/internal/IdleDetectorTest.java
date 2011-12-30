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

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import java.lang.reflect.Field;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @see IdleDetector
 */
public class IdleDetectorTest {

  /**
   * Helper observer for testing.
   */
  private static class ObserverTester implements Observer {
    private int activeCount = 0;
    private int inactiveCount = 0;

    @Override
    public synchronized void update(Observable o, Object arg) {
      IdleDetector detect = (IdleDetector) o;
      if (detect.isUserActive()) {
        activeCount++;
      } else {
        inactiveCount++;
      }
    }
  }

  private static final Display DISPLAY = PlatformUI.getWorkbench().getDisplay();
  private static Shell shell;

  @After
  public void after() {
    shell.dispose();
  }

  @Before
  public void before() {
    shell = new Shell(DISPLAY);
  }

  @Test
  public void shouldBeAbleToDetectThatTheUserHasReturnedToActiveByPressingAKey() throws Exception {
    long idleInterval = 50;
    long runDelay = 10;
    IdleDetector d = create(DISPLAY, idleInterval, runDelay);
    d.setRunning(true);

    Thread.sleep((idleInterval + runDelay) * 2);
    assertThat(d.isUserActive(), is(false));

    shell.notifyListeners(SWT.KeyDown, new Event());
    assertThat(d.isUserActive(), is(true));
  }

  @Test
  public void observersShouldBeNotifiedWhenTheUserReturnsToActiveByPressingAKey() throws Exception {
    long idleInterval = 100;
    long runDelay = 10;
    IdleDetector d = new IdleDetector(DISPLAY, idleInterval, runDelay);

    ObserverTester ob = new ObserverTester();
    d.addObserver(ob);
    d.setRunning(true);

    Thread.sleep(idleInterval + (runDelay * 2));
    Thread.sleep(idleInterval + (runDelay * 2));
    assertFalse(d.isUserActive());

    shell.notifyListeners(SWT.KeyDown, new Event());
    shell.notifyListeners(SWT.KeyDown, new Event());

    assertEquals(1, ob.inactiveCount);
    assertEquals(1, ob.activeCount);
  }

  @Test
  public void shouldBeAbleToDetectThatTheUserHasReturnedToActiveByClickingTheMouse() throws Exception {
    long idleInterval = 500;
    long runDelay = 10;
    IdleDetector d = new IdleDetector(DISPLAY, idleInterval, runDelay);
    d.setRunning(true);

    Thread.sleep(idleInterval + (runDelay * 2));
    assertFalse(d.isUserActive());

    shell.notifyListeners(SWT.MouseDown, new Event());
    assertTrue(d.isUserActive());
  }

  @Test
  public void observersShouldBeNotifiedWhenTheUserReturnsToActiveByClickingTheMouse() throws Exception {
    long idleInterval = 500;
    long runDelay = 10;
    IdleDetector d = new IdleDetector(DISPLAY, idleInterval, runDelay);

    ObserverTester ob = new ObserverTester();
    d.addObserver(ob);
    d.setRunning(true);

    Thread.sleep(idleInterval + (runDelay * 2));
    Thread.sleep(idleInterval + (runDelay * 2));
    assertFalse(d.isUserActive());

    shell.notifyListeners(SWT.MouseDown, new Event());
    shell.notifyListeners(SWT.MouseDown, new Event());
    shell.notifyListeners(SWT.MouseDown, new Event());

    assertEquals(1, ob.inactiveCount);
    assertEquals(1, ob.activeCount);
  }
  
  @Test
  public void observersShouldNotBeNotifiedWhenTheUserIsActive() throws Exception {
    long idleInterval = 50;
    long runDelay = 10;
    IdleDetector d = new IdleDetector(DISPLAY, idleInterval, runDelay);

    ObserverTester ob = new ObserverTester();
    d.addObserver(ob);
    d.setRunning(true);

    Thread.sleep(idleInterval / 2);
    assertTrue(d.isUserActive());
    shell.notifyListeners(SWT.MouseDown, new Event());

    Thread.sleep(idleInterval / 2);
    assertTrue(d.isUserActive());
    shell.notifyListeners(SWT.MouseDown, new Event());

    Thread.sleep(idleInterval / 2);
    assertTrue(d.isUserActive());
    shell.notifyListeners(SWT.MouseDown, new Event());

    assertEquals(0, ob.inactiveCount);
    assertEquals(0, ob.activeCount);
  }
  

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADisplay() {
    new IdleDetector(null, 10, 10);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfConstructedWithANegativeDelay() {
    new IdleDetector(DISPLAY, 10, -1);
  }

  @Test(expected = IllegalArgumentException.class)
  public void shouldThrowIllegalArgumentExceptionIfConstructedWithANegativeInterval() {
    new IdleDetector(DISPLAY, -1, 10);
  }

  @Test
  public void shouldDoNothingAfterTheDisplayHasBeenDisposed() {
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

  @Test
  public void shouldReturnTheDisplay() {
    Display actualDisplay = create(DISPLAY, 1, 1).getDisplay();
    assertThat(actualDisplay, sameInstance(DISPLAY));
  }

  @Test
  public void shouldReturnTheIdleInterval() {
    long expected = 1936l;
    long actual =  create(DISPLAY, expected, 1).getIdleInterval();
    assertThat(actual, is(expected));
  }

  @Test
  public void shouldReturnTheDelay() {
    long expectedDelay = 1231;
    long actualDelay = create(DISPLAY, 101010, expectedDelay).getRunDelay();
    assertThat(actualDelay, is(expectedDelay));
  }

  @Test
  public void shouldNotBeRunningWhenFirstConstructed() {
    assertThat(create(DISPLAY, 10, 10).isRunning(), is(false));
  }
  
  private IdleDetector create(Display display, long idleTime, long delay) {
    return new IdleDetector(display, idleTime, delay);
  }

  @Test
  public void theUserShouldBeActiveWhenTheIdleDetectorIsFirstConstructed() {
    assertTrue(new IdleDetector(DISPLAY, 10, 10).isUserActive());
  }

  @Test
  public void shouldNotNotifyAnyObserversIfNotEnabled() throws Exception {
    // IdleDetector is not running, so no observers should be notified
    long idleInterval = 500;
    long runDelay = 10;
    IdleDetector d = new IdleDetector(DISPLAY, idleInterval, runDelay);

    ObserverTester ob = new ObserverTester();
    d.addObserver(ob);
    d.setRunning(false);

    Thread.sleep(idleInterval + (runDelay * 2));
    Thread.sleep(idleInterval + (runDelay * 2));

    shell.notifyListeners(SWT.KeyDown, new Event());
    shell.notifyListeners(SWT.MouseDown, new Event());

    assertEquals(0, ob.inactiveCount);
    assertEquals(0, ob.activeCount);
  }

  @Test
  public void testSetRunning() {
    IdleDetector d = new IdleDetector(DISPLAY, 100, 100);
    assertFalse(d.isRunning());

    try {
      d.setRunning(false);
      d.setRunning(false);
    } catch (Exception e) {
      fail();
    }

    try {
      d.setRunning(true);
      d.setRunning(true);
    } catch (Exception e) {
      fail();
    }

    d.setRunning(true);
    assertTrue(d.isRunning());
    try {
      assertFalse(getTimer(d).isShutdown());
    } catch (Exception e) {
      fail();
    }

    d.setRunning(false);
    assertFalse(d.isRunning());
    try {
      assertTrue(getTimer(d).isShutdown());
    } catch (Exception e) {
      fail();
    }
  }

  private ScheduledThreadPoolExecutor getTimer(IdleDetector d) throws Exception {
    Field field = d.getClass().getDeclaredField("timer");
    field.setAccessible(true);
    return (ScheduledThreadPoolExecutor) field.get(d);
  }
}
