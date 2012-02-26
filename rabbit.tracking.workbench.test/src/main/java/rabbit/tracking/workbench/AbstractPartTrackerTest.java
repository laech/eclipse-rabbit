///*
// * Copyright 2012 The Rabbit Eclipse Plug-in Project
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
//
//package rabbit.tracking.workbench;
//
//import static java.lang.System.nanoTime;
//import static java.lang.Thread.sleep;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.hamcrest.core.Is.is;
//import static org.hamcrest.core.IsNull.nullValue;
//import static org.hamcrest.number.OrderingComparison.greaterThan;
//import static org.hamcrest.number.OrderingComparison.lessThan;
//import static rabbit.tracking.workbench.test.WorkbenchTestUtil.activate;
//import static rabbit.tracking.workbench.test.WorkbenchTestUtil.close;
//import static rabbit.tracking.workbench.test.WorkbenchTestUtil.closeAllParts;
//import static rabbit.tracking.workbench.test.WorkbenchTestUtil.hide;
//import static rabbit.tracking.workbench.test.WorkbenchTestUtil.openRandomPart;
//import static rabbit.tracking.workbench.test.WorkbenchTestUtil.openWindow;
//
//import org.eclipse.ui.IViewPart;
//import org.eclipse.ui.IWorkbenchPart;
//import org.eclipse.ui.IWorkbenchWindow;
//import org.junit.Test;
//
//import rabbit.tracking.AbstractUserTrackerSpec;
//import rabbit.tracking.IUserMonitorService;
//
//public final class AbstractPartTrackerTest extends AbstractUserTrackerSpec {
//
//  private static class AbstractPartTrackerTester extends AbstractPartTracker {
//    int focusCount;
//    int unfocusCount;
//    long focusedTime;
//    long unfocusedTime;
//    IWorkbenchPart focusedPart;
//    IWorkbenchPart unfocusedPart;
//
//    AbstractPartTrackerTester(IUserMonitorService service) {
//      super(service);
//    }
//
//    @Override protected synchronized void onPartFocused(IWorkbenchPart part) {
//      focusedPart = part;
//      focusCount++;
//      try {
//        sleep(1);
//      } catch (InterruptedException e) {
//        throw new RuntimeException(e);
//      }
//      focusedTime = nanoTime();
//    }
//
//    @Override protected synchronized void onPartUnfocused(IWorkbenchPart part) {
//      unfocusedPart = part;
//      unfocusCount++;
//      try {
//        sleep(1);
//      } catch (InterruptedException e) {
//        throw new RuntimeException(e);
//      }
//      unfocusedTime = nanoTime();
//    }
//
//    synchronized void reset() {
//      focusCount = 0;
//      focusedTime = 0;
//      focusedPart = null;
//      unfocusCount = 0;
//      unfocusedTime = 0;
//      unfocusedPart = null;
//    }
//
//    synchronized int focusCount() {
//      return focusCount;
//    }
//
//    synchronized IWorkbenchPart focusedPart() {
//      return focusedPart;
//    }
//
//    synchronized long focusedTime() {
//      return focusedTime;
//    }
//
//    synchronized int unfocusCount() {
//      return unfocusCount;
//    }
//
//    synchronized IWorkbenchPart unfocusedPart() {
//      return unfocusedPart;
//    }
//
//    synchronized long unfocusedTime() {
//      return unfocusedTime;
//    }
//
//    @Override protected void onUserActive() {
//    }
//
//    @Override protected void onUserInactive() {
//    }
//
//    @Override public void saveData() {
//    }
//  }
//
//  private AbstractPartTrackerTester tracker;
//
//  @Override public void setup() throws Exception {
//    super.setup();
//    closeAllParts();
//    tracker = create(getMockService());
//  }
//
//  @Override public void teardown() throws Exception {
//    super.teardown();
//    tracker.disable();
//  }
//
//  @Test public void notifiesPartFocusedWhenAPartIsAlreadyFocusedWhenEnabled() {
//    IWorkbenchPart part = openRandomPart();
//    activate(part);
//
//    tracker.enable();
//    assertThat(tracker.focusCount(), is(1));
//    assertThat(tracker.focusedPart(), is(part));
//    assertThat(tracker.unfocusCount(), is(0));
//  }
//
//  @Test public void notifiesPartFocusedDueToPartOpened() {
//    tracker.enable();
//    IWorkbenchPart part = openRandomPart();
//    assertThat(tracker.focusCount(), is(1));
//    assertThat(tracker.focusedPart(), is(part));
//  }
//
//  @Test public void notifiesPartFocusedDueToPartSelected() {
//    IWorkbenchPart part1 = openRandomPart();
//    IWorkbenchPart part2 = openRandomPart();
//    activate(part1);
//    tracker.enable();
//    tracker.reset();
//
//    activate(part2);
//    assertThat(tracker.focusCount(), is(1));
//    assertThat(tracker.focusedPart(), is(part2));
//  }
//
//  @Test public void notifiesPartUnfocusedDueToNewPartSelected() {
//    IWorkbenchPart part1 = openRandomPart();
//    IWorkbenchPart part2 = openRandomPart();
//    activate(part1);
//    tracker.enable();
//    tracker.reset();
//
//    activate(part2);
//    assertThat(tracker.unfocusCount(), is(1));
//    assertThat(tracker.unfocusedPart(), is(part1));
//  }
//
//  @Test public void notifiesPartUnfocusedDueToNewPartOpened() {
//    tracker.enable();
//    IWorkbenchPart part = openRandomPart();
//
//    openRandomPart();
//    assertThat(tracker.unfocusCount(), is(1));
//    assertThat(tracker.unfocusedPart(), is(part));
//  }
//
//  @Test public void notifiesPartUnfocusedDueToWindowUnfocused() {
//    tracker.enable();
//    IWorkbenchPart part = openRandomPart();
//
//    IWorkbenchWindow window = openWindow();
//    try {
//      assertThat(tracker.unfocusCount(), is(1));
//      assertThat(tracker.unfocusedPart(), is(part));
//    } finally {
//      close(window);
//    }
//  }
//
//  @Test public void notifiesPartUnfocusedDueToPartClosed() {
//    IViewPart part = openRandomPart();
//    tracker.enable();
//
//    hide(part);
//    assertThat(tracker.unfocusCount(), is(1));
//    assertThat(tracker.unfocusedPart(), is((IWorkbenchPart)part));
//  }
//
//  @Test public void notifiesPartUnfocusedDueToWindowClosed() {
//    IWorkbenchWindow window = openWindow();
//    IWorkbenchPart part = openRandomPart(window);
//    tracker.enable();
//    assertThat(tracker.unfocusCount(), is(0));
//
//    close(window);
//    assertThat(tracker.unfocusCount(), is(1));
//    assertThat(tracker.unfocusedPart(), is(part));
//  }
//
//  @Test public void notifiesPartUnforcusedOnOldPartBeforeNotifyingPartFocusedOnNewPart() {
//    tracker.enable();
//    openRandomPart();
//    openRandomPart();
//    assertThat(tracker.unfocusedTime(), lessThan(tracker.focusedTime()));
//  }
//
//  @Test public void ifDisabledThenWontNotify() {
//    tracker.disable();
//    openRandomPart();
//    openRandomPart();
//    assertThat(tracker.focusCount(), is(0));
//    assertThat(tracker.unfocusCount(), is(0));
//  }
//
//  @Test public void tracksNewlyOpenedWindow() {
//    tracker.enable();
//    IWorkbenchWindow window = openWindow();
//    try {
//      tracker.reset();
//      openRandomPart(window);
//      assertThat(tracker.focusCount(), greaterThan(0));
//    } finally {
//      close(window);
//    }
//  }
//
//  @Test public void getFocusedPartReturnsTheActivePartOfFocusedWindow() {
//    IWorkbenchPart part = openRandomPart();
//    assertThat(tracker.getFocusedPart(), is(part));
//  }
//
//  @Test public void getFocusedPartReturnsNullIfNone() {
//    openRandomPart();
//    IWorkbenchWindow window = openWindow();
//    try {
//      closeAllParts(window);
//      assertThat(tracker.getFocusedPart(), is(nullValue()));
//    } finally {
//      close(window);
//    }
//  }
//
//  @Override protected AbstractPartTrackerTester create(
//      IUserMonitorService service) {
//    return new AbstractPartTrackerTester(service);
//  }
// }
