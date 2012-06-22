package rabbit.workbench.internal.tracking;
///*
// * Copyright 2010 The Rabbit Eclipse Plug-in Project
// * 
// * Licensed under the Apache License, Version 2.0 (the "License"); you may not
// * use this file except in compliance with the License. You may obtain a copy of
// * the License at
// * 
// * http://www.apache.org/licenses/LICENSE-2.0
// * 
// * Unless required by applicable law or agreed to in writing, software
// * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
// * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
// * License for the specific language governing permissions and limitations under
// * the License.
// */
//package rabbit.tracking.internal.workbench;
//
//import static java.lang.System.currentTimeMillis;
//import static java.lang.Thread.sleep;
//import static org.eclipse.ui.PlatformUI.getWorkbench;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
//import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
//import static org.hamcrest.core.Is.is;
//import static org.hamcrest.core.IsNull.notNullValue;
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//import java.lang.reflect.Field;
//import java.lang.reflect.Method;
//import java.util.Observable;
//import java.util.Random;
//
//import org.eclipse.ui.IPerspectiveDescriptor;
//import org.eclipse.ui.IWorkbench;
//import org.eclipse.ui.IWorkbenchPage;
//import org.eclipse.ui.IWorkbenchWindow;
//import org.eclipse.ui.WorkbenchException;
//import org.joda.time.Interval;
//import org.junit.Before;
//import org.junit.Test;
//
//import rabbit.tracking.AbstractTrackerTest;
//import rabbit.tracking.internal.TrackingPlugin;
//
//public class PerspectiveTrackerTest extends
//    AbstractTrackerTest<PerspectiveEvent> {
//
//  private PerspectiveTracker tracker;
//
//  @Before public void setup() {
//    tracker = createTracker();
//
//    IWorkbench workbench = getWorkbench();
//    workbench.getActiveWorkbenchWindow().getActivePage().setPerspective(
//        workbench.getPerspectiveRegistry().getPerspectives()[1]);
//  }
//
//  @Test public void shouldTrackOnPerspectiveChange() throws Exception {
//    IWorkbenchWindow win = getActiveWindow();
//    IPerspectiveDescriptor oldPers = win.getActivePage().getPerspective();
//    IPerspectiveDescriptor newPers = null;
//
//    for (IPerspectiveDescriptor p : getWorkbench()
//        .getPerspectiveRegistry().getPerspectives()) {
//      if (!p.equals(oldPers)) {
//        newPers = p;
//        break;
//      }
//    }
//
//    long preStart = currentTimeMillis();
//    tracker.setEnabled(true);
//    long postStart = currentTimeMillis();
//
//    sleep(20);
//
//    long preEnd = currentTimeMillis();
//    win.getActivePage().closeAllPerspectives(false, false);
//    win.getActivePage().setPerspective(newPers);
//    long postEnd = currentTimeMillis();
//
//    assertThat(tracker.getData(), hasSize(1));
//    PerspectiveEvent event = tracker.getData().iterator().next();
//    assertThat(event.perspective(), is(oldPers));
//
//    long start = event.getInterval().getStartMillis();
//    long end = event.getInterval().getEndMillis();
//    checkTime(preStart, start, postStart, preEnd, end, postEnd);
//  }
//
//  @Test public void shouldTrackOnPerspectiveClose() throws Exception {
//    IWorkbenchPage page = getActiveWindow().getActivePage();
//    IPerspectiveDescriptor perspective = page.getPerspective();
//
//    long preStart = currentTimeMillis();
//    tracker.setEnabled(true);
//    long postStart = currentTimeMillis();
//
//    sleep(20);
//
//    long preEnd = currentTimeMillis();
//    page.closeAllPerspectives(false, false);
//    long postEnd = currentTimeMillis();
//
//    assertEquals(1, tracker.getData().size());
//    PerspectiveEvent e = tracker.getData().iterator().next();
//    assertEquals(perspective, e.perspective());
//
//    long start = e.getInterval().getStartMillis();
//    long end = e.getInterval().getEndMillis();
//    checkTime(preStart, start, postStart, preEnd, end, postEnd);
//  }
//
//  @Test public void shouldTrackOnWindowClose() throws Exception {
//    IWorkbenchWindow win = openWindow();
//    IPerspectiveDescriptor perspective = win.getActivePage().getPerspective();
//
//    long preStart = currentTimeMillis();
//    tracker.setEnabled(true);
//    long postStart = currentTimeMillis();
//    sleep(15);
//    long preEnd = currentTimeMillis();
//    assertTrue(win.close());
//    long postEnd = currentTimeMillis();
//
//    assertThat(tracker.getData(), hasSize(1));
//    PerspectiveEvent e = tracker.getData().iterator().next();
//    assertThat(e.perspective(), is(perspective));
//
//    long start = e.getInterval().getStartMillis();
//    long end = e.getInterval().getEndMillis();
//    checkTime(preStart, start, postStart, preEnd, end, postEnd);
//  }
//
//  @Test public void shouldNotTrackIfDisabled() throws Exception {
//    tracker.setEnabled(false);
//
//    // Test IPerspectiveListener.
//    IWorkbenchPage page = getActiveWindow().getActivePage();
//    page.closeAllPerspectives(false, false);
//    page.setPerspective(getRandomPerspective());
//    assertThat(tracker.getData().toArray(), is(emptyArray()));
//
//    // Test IWindowListener.
//    IWorkbenchWindow window = getWorkbench().openWorkbenchWindow(null);
//    try {
//      assertThat(tracker.getData().toArray(), is(emptyArray()));
//    } finally {
//      window.close();
//    }
//
//    // Test IdleDetector
//    callIdleDetectorToNotify();
//    assertThat(tracker.getData().toArray(), is(emptyArray()));
//  }
//
//  @Test public void shouldNotTrackIfNoWindowIsActive() throws Exception {
//    for (IWorkbenchWindow win : getWorkbench().getWorkbenchWindows()) {
//      win.getShell().setMinimized(true);
//    }
//
//    try {
//      tracker.setEnabled(true);
//      sleep(5);
//      tracker.setEnabled(false);
//      assertThat(tracker.getData().toArray(), is(emptyArray()));
//
//    } finally {
//      for (IWorkbenchWindow win : getWorkbench().getWorkbenchWindows()) {
//        win.getShell().setMinimized(false);
//      }
//    }
//  }
//
//  @Test public void shouldTrackWithTheCorrectTime() throws Exception {
//    long preStart = System.currentTimeMillis();
//    tracker.setEnabled(true);
//    long postStart = System.currentTimeMillis();
//
//    sleep(20);
//
//    long preEnd = System.currentTimeMillis();
//    tracker.setEnabled(false);
//    long postEnd = System.currentTimeMillis();
//
//    assertEquals(1, tracker.getData().size());
//    PerspectiveEvent e = tracker.getData().iterator().next();
//    assertThat(e.perspective(), is(notNullValue()));
//
//    long start = e.getInterval().getStartMillis();
//    long end = e.getInterval().getEndMillis();
//    checkTime(preStart, start, postStart, preEnd, end, postEnd);
//  }
//
//  @Test public void shouldTrackIdleDetectNotifications() throws Exception {
//    IPerspectiveDescriptor perspective = getActiveWindow().getActivePage()
//        .getPerspective();
//
//    long preStart = System.currentTimeMillis();
//    tracker.setEnabled(true);
//    long postStart = System.currentTimeMillis();
//
//    sleep(20);
//
//    long preEnd = System.currentTimeMillis();
//    callIdleDetectorToNotify();
//    long postEnd = System.currentTimeMillis();
//
//    assertThat(tracker.getData(), hasSize(1));
//    PerspectiveEvent e = tracker.getData().iterator().next();
//    assertThat(e.perspective(), is(perspective));
//
//    long start = e.getInterval().getStartMillis();
//    long end = e.getInterval().getEndMillis();
//    checkTime(preStart, start, postStart, preEnd, end, postEnd);
//  }
//
//  @Test public void shouldTrackOnNewlyOpenedWindow() throws Exception {
//    tracker.setEnabled(true);
//
//    long preStart = System.currentTimeMillis();
//    IWorkbenchWindow window = openWindow(); // Opens a second window
//    try {
//      long postStart = System.currentTimeMillis();
//
//      IPerspectiveDescriptor persp = window.getActivePage().getPerspective();
//      tracker.flushData(); // Removes data from the first window
//
//      sleep(10);
//
//      long preEnd = System.currentTimeMillis();
//      IWorkbenchPage page = window.getActivePage();
//      page.closeAllPerspectives(false, false);
//      page.setPerspective(getRandomPerspective());
//      long postEnd = System.currentTimeMillis();
//
//      assertThat(tracker.getData(), hasSize(1));
//      PerspectiveEvent event = tracker.getData().iterator().next();
//      assertThat(event.perspective(), is(persp));
//
//      long start = event.getInterval().getStartMillis();
//      long end = event.getInterval().getEndMillis();
//      checkTime(preStart, start, postStart, preEnd, end, postEnd);
//    } finally {
//      window.close();
//    }
//  }
//
//  @Test public void shouldStartObservingOnIdleDetectorWhenEnabled() {
//    IdleDetector dt = TrackingPlugin.getDefault().getIdleDetector();
//    tracker.setEnabled(false);
//    int count = dt.countObservers();
//    tracker.setEnabled(true);
//    assertThat(dt.countObservers(), is(count + 1));
//  }
//
//  @Test public void shouldStopObservingOnIdleDetectorWhenEnabled() {
//    IdleDetector dt = TrackingPlugin.getDefault().getIdleDetector();
//    tracker.setEnabled(true);
//    int count = dt.countObservers();
//    tracker.setEnabled(false);
//    assertThat(dt.countObservers(), is(count - 1));
//  }
//
//  @Test public void shouldTrackAnEventWhenWindowDeactivates() throws Exception {
//    IWorkbenchPage page = getActiveWindow().getActivePage();
//
//    long preStart = currentTimeMillis();
//    tracker.setEnabled(true);
//    long postStart = currentTimeMillis();
//
//    sleep(10);
//
//    long preEnd = currentTimeMillis();
//    // Open new window to cause the current window to loose focus
//    IWorkbenchWindow win = getWorkbench().openWorkbenchWindow(null);
//    try {
//      long postEnd = currentTimeMillis();
//
//      assertThat(tracker.getData(), hasSize(1));
//      PerspectiveEvent e = tracker.getData().iterator().next();
//      assertThat(e.perspective(), is(page.getPerspective()));
//
//      long start = e.getInterval().getStartMillis();
//      long end = e.getInterval().getEndMillis();
//      checkTime(preStart, start, postStart, preEnd, end, postEnd);
//    } finally {
//      win.close();
//    }
//  }
//
//  @Override protected PerspectiveEvent createEvent() {
//    return new PerspectiveEvent(new Interval(0, 1), getActiveWindow()
//        .getActivePage().getPerspective());
//  }
//
//  @Override protected PerspectiveTracker createTracker() {
//    return new PerspectiveTracker();
//  }
//
//  private void callIdleDetectorToNotify() throws Exception {
//    Field isActive = IdleDetector.class.getDeclaredField("isActive");
//    isActive.setAccessible(true);
//
//    Method setChanged = Observable.class.getDeclaredMethod("setChanged");
//    setChanged.setAccessible(true);
//
//    Method notifyObservers = Observable.class
//        .getDeclaredMethod("notifyObservers");
//    notifyObservers.setAccessible(true);
//
//    IdleDetector detector = TrackingPlugin.getDefault().getIdleDetector();
//    detector.setRunning(true);
//    isActive.set(detector, false);
//    setChanged.invoke(detector);
//    notifyObservers.invoke(detector);
//    detector.setRunning(false);
//  }
//
//  private IWorkbenchWindow getActiveWindow() {
//    return getWorkbench().getActiveWorkbenchWindow();
//  }
//
//  private IPerspectiveDescriptor getRandomPerspective() {
//    IPerspectiveDescriptor[] ps = getWorkbench().getPerspectiveRegistry()
//        .getPerspectives();
//    return ps[new Random().nextInt(ps.length)];
//  }
//
//  private IWorkbenchWindow openWindow() throws WorkbenchException {
//    return getWorkbench().openWorkbenchWindow(null);
//  }
// }
