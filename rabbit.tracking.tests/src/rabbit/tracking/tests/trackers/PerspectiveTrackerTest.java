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
package rabbit.tracking.tests.trackers;

import rabbit.data.store.model.PerspectiveEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;
import rabbit.tracking.internal.trackers.PerspectiveTracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Observable;
import java.util.concurrent.TimeUnit;

/**
 * Test for {@link PerspectiveTracker}
 */
public class PerspectiveTrackerTest extends
    AbstractTrackerTest<PerspectiveEvent> {

  @BeforeClass
  public static void setUpBeforeClass() {
    // RabbitCore.getDefault().setIdleDetectionEnabled(false);
  }

  private PerspectiveTracker tracker;

  private IWorkbenchWindow activeWindow;

  @Before
  public void setUp() {
    tracker = createTracker();

    final IWorkbench wb = PlatformUI.getWorkbench();
    wb.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        wb.getActiveWorkbenchWindow().getActivePage().setPerspective(
            wb.getPerspectiveRegistry().getPerspectives()[1]);
      }
    });
  }

  /*
   * Old tests, based on calling listener methods.
   */
  @Test
  public void testAccuracy() throws InterruptedException {
    PerspectiveEvent e = null;
    DateTime start = null;
    DateTime end = null;
    long duration = 0;
    int size = 0;

    // Test enable then disable:

    IWorkbenchWindow activeWindow = getActiveWindow();

    tracker.setEnabled(true);
    start = new DateTime();
    TimeUnit.MILLISECONDS.sleep((duration = 30));
    end = new DateTime();
    tracker.setEnabled(false);
    size = 1;
    e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, size, activeWindow
        .getActivePage().getPerspective());

    // Test perspectiveActivated then perspectiveDeactivated:

    tracker.flushData();
    tracker.perspectiveActivated(activeWindow.getActivePage(), activeWindow
        .getActivePage().getPerspective());
    start = new DateTime();
    TimeUnit.MILLISECONDS.sleep((duration = 25));
    end = new DateTime();
    tracker.perspectiveDeactivated(activeWindow.getActivePage(), activeWindow
        .getActivePage().getPerspective());
    size = 1;
    e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, size, activeWindow
        .getActivePage().getPerspective());

    // Test perspectiveActivated then windowClosed:

    tracker.flushData();
    tracker.perspectiveActivated(activeWindow.getActivePage(), activeWindow
        .getActivePage().getPerspective());
    start = new DateTime();
    TimeUnit.MILLISECONDS.sleep((duration = 12));
    end = new DateTime();
    tracker.windowClosed(activeWindow);
    size = 1;
    e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, size, activeWindow
        .getActivePage().getPerspective());

    // Test windowOpened then perspectiveDeactivated:

    tracker.flushData();
    tracker.windowOpened(activeWindow);
    start = new DateTime();
    TimeUnit.MILLISECONDS.sleep((duration = 21));
    end = new DateTime();
    tracker.perspectiveDeactivated(activeWindow.getActivePage(), activeWindow
        .getActivePage().getPerspective());
    size = 1;
    e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, size, activeWindow
        .getActivePage().getPerspective());

    // Test windowOpened then windowClosed:

    tracker.flushData();
    tracker.windowOpened(activeWindow);
    start = new DateTime();
    TimeUnit.MILLISECONDS.sleep((duration = 10));
    end = new DateTime();
    tracker.windowClosed(activeWindow);
    size = 1;
    e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, size, activeWindow
        .getActivePage().getPerspective());

    // Test windowOpened then windowDeactivated:

    tracker.flushData();
    tracker.windowOpened(activeWindow);
    start = new DateTime();
    TimeUnit.MILLISECONDS.sleep((duration = 49));
    end = new DateTime();
    tracker.windowDeactivated(activeWindow);
    size = 1;
    e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, size, activeWindow
        .getActivePage().getPerspective());

    // Test windowActivated then windowDeactivated:

    tracker.flushData();
    tracker.windowActivated(activeWindow);
    start = new DateTime();
    TimeUnit.MILLISECONDS.sleep((duration = 25));
    end = new DateTime();
    tracker.windowDeactivated(activeWindow);
    size = 1;
    e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, size, activeWindow
        .getActivePage().getPerspective());

    // Test windowActivated then windowClosed:

    tracker.flushData();
    tracker.windowActivated(activeWindow);
    start = new DateTime();
    TimeUnit.MILLISECONDS.sleep((duration = 9));
    end = new DateTime();
    tracker.windowClosed(activeWindow);
    size = 1;
    e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, size, activeWindow
        .getActivePage().getPerspective());

    // Test windowActivated then perspectiveDeactivated:

    tracker.flushData();
    tracker.windowActivated(activeWindow);
    start = new DateTime();
    TimeUnit.MILLISECONDS.sleep((duration = 6));
    end = new DateTime();
    tracker.perspectiveDeactivated(activeWindow.getActivePage(), activeWindow
        .getActivePage().getPerspective());
    size = 1;
    e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, size, activeWindow
        .getActivePage().getPerspective());
  }

  @Test
  public void testChangePerspective() throws InterruptedException {
    IWorkbenchWindow win = getActiveWindow();
    IPerspectiveDescriptor oldPers = win.getActivePage().getPerspective();
    IPerspectiveDescriptor newPers = null;

    for (IPerspectiveDescriptor p : PlatformUI.getWorkbench()
        .getPerspectiveRegistry().getPerspectives()) {
      if (!p.equals(oldPers)) {
        newPers = p;
        break;
      }
    }

    long duration = 30;
    DateTime start = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(duration);
    win.getActivePage().setPerspective(newPers);
    DateTime end = new DateTime();

    assertEquals(1, tracker.getData().size());
    PerspectiveEvent event = tracker.getData().iterator().next();
    assertEquals(oldPers, event.getPerspective());
    assertTrue(start.compareTo(event.getTime()) <= 0);
    assertTrue(end.compareTo(event.getTime()) >= 0);
    assertTrue(end.getMillis() - start.getMillis() >= event
        .getDuration());
    assertTrue(duration <= event.getDuration());
  }

  @Test
  public void testClosePerspectives() throws InterruptedException {
    final IWorkbenchPage page = getActiveWindow().getActivePage();
    IPerspectiveDescriptor oldPers = page.getPerspective();
    long duration = 20;
    DateTime start = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(duration);
    page.getWorkbenchWindow().getShell().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        page.closeAllPerspectives(false, false);
      }
    });
    DateTime end = new DateTime();
    PerspectiveEvent e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, 1, oldPers);
  }

  @Test
  public void testCloseWindow() throws Exception {
    final IWorkbenchWindow win = PlatformUI.getWorkbench().openWorkbenchWindow(
        null);
    IPerspectiveDescriptor perspective = win.getActivePage().getPerspective();

    long duration = 10;
    DateTime start = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(duration);
    win.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        win.close();
      }
    });
    DateTime end = new DateTime();
    PerspectiveEvent e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, 1, perspective);
  }

  @Test
  public void testDisabled() throws Exception {
    tracker.setEnabled(false);

    // Test IPerspectiveListener.
    TimeUnit.MILLISECONDS.sleep(30);
    getActiveWindow().getShell().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        getActiveWindow().getActivePage().setPerspective(
            PlatformUI.getWorkbench().getPerspectiveRegistry()
                .getPerspectives()[1]);
      }
    });

    assertTrue(tracker.getData().isEmpty());

    // Test IWindowListener.
    TimeUnit.MILLISECONDS.sleep(25);
    getActiveWindow().getWorkbench().openWorkbenchWindow(null);
    assertTrue(tracker.getData().isEmpty());

    // Test IdleDetector
    TimeUnit.MILLISECONDS.sleep(20);
    callIdleDetectorToNotify();
    assertTrue(tracker.getData().isEmpty());
  }

  @Test
  public void testEnableThenDisable() throws InterruptedException {
    long duration = 20;
    DateTime start = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(duration);
    tracker.setEnabled(false);
    DateTime end = new DateTime();
    PerspectiveEvent e = tracker.getData().iterator().next();
    internalAssertAccuracy(e, start, end, duration, 1, getActiveWindow()
        .getActivePage().getPerspective());
  }

  @Test
  public void testIdleDetector() throws Exception {
    IPerspectiveDescriptor perspective = getActiveWindow().getActivePage()
        .getPerspective();

    long duration = 30;
    DateTime start = new DateTime();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(duration);
    callIdleDetectorToNotify();
    DateTime end = new DateTime();

    PerspectiveEvent event = tracker.getData().iterator().next();
    internalAssertAccuracy(event, start, end, duration, 1, perspective);
  }

  @Test
  public void testNewWindow() throws Exception {
    long sleepDuration = 15;
    long start = System.currentTimeMillis();
    tracker.setEnabled(true);

    final IWorkbenchWindow window = getActiveWindow().getWorkbench()
        .openWorkbenchWindow(null);
    IPerspectiveDescriptor perspective2 = window.getActivePage()
        .getPerspective();

    TimeUnit.MILLISECONDS.sleep(sleepDuration);

    window.getWorkbench().getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        window.getActivePage()
            .setPerspective(
                window.getWorkbench().getPerspectiveRegistry()
                    .getPerspectives()[1]);
      }
    });
    long end = System.currentTimeMillis();

    // One for the original window, one for the newly opened window.
    assertEquals(2, tracker.getData().size());

    Iterator<PerspectiveEvent> it = tracker.getData().iterator();
    PerspectiveEvent event = it.next();
    if (!event.getPerspective().equals(perspective2)) {
      event = it.next();
    }

    assertEquals(perspective2, event.getPerspective());
    assertTrue(start <= event.getTime().getMillis());
    assertTrue(end >= event.getTime().getMillis());
    assertTrue(sleepDuration <= event.getDuration());
    assertTrue((end - start) >= event.getDuration());
  }

  @Test
  public void testWindowDeactivated() throws Exception {
    IWorkbenchPage page = getActiveWindow().getActivePage();

    long sleepDuration = 10;
    long start = System.currentTimeMillis();
    tracker.setEnabled(true);
    TimeUnit.MILLISECONDS.sleep(sleepDuration);
    // Open new window to cause the current window to loose focus
    page.getWorkbenchWindow().getWorkbench().openWorkbenchWindow(null);
    long end = System.currentTimeMillis();

    assertEquals(1, tracker.getData().size());
    PerspectiveEvent e = tracker.getData().iterator().next();
    assertEquals(page.getPerspective(), e.getPerspective());
    assertTrue(start <= e.getTime().getMillis());
    assertTrue(end >= e.getTime().getMillis());
    assertTrue(sleepDuration <= e.getDuration());
    assertTrue((end - start) >= e.getDuration());
  }

  @Override
  protected PerspectiveEvent createEvent() {
    return new PerspectiveEvent(new DateTime(), 101, getActiveWindow()
        .getActivePage().getPerspective());
  }

  @Override
  protected PerspectiveTracker createTracker() {
    return new PerspectiveTracker();
  }

  private void callIdleDetectorToNotify() throws Exception {
    Field isActive = IdleDetector.class.getDeclaredField("isActive");
    isActive.setAccessible(true);

    Method setChanged = Observable.class.getDeclaredMethod("setChanged");
    setChanged.setAccessible(true);

    Method notifyObservers = Observable.class
        .getDeclaredMethod("notifyObservers");
    notifyObservers.setAccessible(true);

    IdleDetector detector = TrackingPlugin.getDefault().getIdleDetector();
    detector.setRunning(true);
    isActive.set(detector, false);
    setChanged.invoke(detector);
    notifyObservers.invoke(detector);
    detector.setRunning(false);
  }

  private IWorkbenchWindow getActiveWindow() {
    final IWorkbench wb = PlatformUI.getWorkbench();
    wb.getDisplay().syncExec(new Runnable() {
      @Override
      public void run() {
        activeWindow = wb.getActiveWorkbenchWindow();
      }
    });
    return activeWindow;
  }

  private void internalAssertAccuracy(PerspectiveEvent e, DateTime start,
      DateTime end, long duration, int size, IPerspectiveDescriptor per) {

    assertEquals(per, e.getPerspective());
    assertEquals(size, tracker.getData().size());
    assertTrue(start.compareTo(e.getTime()) <= 0);
    assertTrue(end.compareTo(e.getTime()) >= 0);
    assertTrue(duration - 100 <= e.getDuration());
    assertTrue(duration + 100 >= e.getDuration());
  }

}