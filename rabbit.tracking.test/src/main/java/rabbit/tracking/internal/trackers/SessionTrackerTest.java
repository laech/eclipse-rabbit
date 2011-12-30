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
package rabbit.tracking.internal.trackers;

import static java.lang.Thread.sleep;
import static org.eclipse.ui.PlatformUI.getWorkbench;
import static org.hamcrest.collection.IsArrayWithSize.emptyArray;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Observable;

import org.eclipse.swt.widgets.Shell;
import org.joda.time.Interval;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import rabbit.data.store.model.SessionEvent;
import rabbit.tracking.internal.IdleDetector;
import rabbit.tracking.internal.TrackingPlugin;

public class SessionTrackerTest extends AbstractTrackerTest<SessionEvent> {

  private Shell activeShell;

  @Before public void setup() {
    activeShell = getWorkbench().getDisplay().getActiveShell();
  }

  @After public void teardown() {
    if (activeShell != null) {
      activeShell.setMinimized(false);
    }
  }

  @Test public void shouldNotTrackIfDisabled() throws Exception {
    tracker.setEnabled(false);
    sleep(10);
    minimizeAllShells();
    assertThat(tracker.getData().toArray(), is(emptyArray()));
  }

  @Test public void shouldNotTrackIfNoShellIsActive() throws Exception {
    minimizeAllShells();
    tracker.setEnabled(true);
    sleep(10);
    tracker.setEnabled(false);
    assertThat(tracker.getData().toArray(), is(emptyArray()));
  }

  @Test public void shouldTrackWithCorrectTime() throws Exception {
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    sleep(10);

    long preEnd = System.currentTimeMillis();
    tracker.setEnabled(false);
    long postEnd = System.currentTimeMillis();

    Collection<SessionEvent> data = tracker.getData();
    assertThat(data, hasSize(1));

    SessionEvent event = data.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test public void shouldNotTrackIfNoShellIsActiveWhenEnablingThenDisabling()
      throws Exception {
    minimizeAllShells();
    tracker.setEnabled(true);
    tracker.flushData();
    sleep(10);
    tracker.setEnabled(false);
    assertThat(tracker.getData().toArray(), is(emptyArray()));
  }

  @Test public void shouldIgnoreIdleDetectorNotificationsIfNoShellIsActive()
      throws Exception {
    minimizeAllShells();
    tracker.setEnabled(true);
    tracker.flushData();
    sleep(10);
    callIdleDetectorToNotify();
    assertThat(tracker.getData().toArray(), is(emptyArray()));
  }

  @Test public void shouldIgnoreIdleDetectorNotificationsIfDisabled()
      throws Exception {
    tracker.setEnabled(false);
    tracker.flushData();
    sleep(10);
    callIdleDetectorToNotify();
    assertThat(tracker.getData().toArray(), is(emptyArray()));
  }

  @Test public void shouldTrackIdleDetectorNotificationsIfEnabled()
      throws Exception {
    long preStart = System.currentTimeMillis();
    tracker.setEnabled(true);
    long postStart = System.currentTimeMillis();

    assertEquals(0, tracker.getData().size());
    sleep(20);

    long preEnd = System.currentTimeMillis();
    callIdleDetectorToNotify();
    long postEnd = System.currentTimeMillis();

    Collection<SessionEvent> data = tracker.getData();
    assertThat(data, hasSize(1));

    SessionEvent event = data.iterator().next();
    long start = event.getInterval().getStartMillis();
    long end = event.getInterval().getEndMillis();
    checkTime(preStart, start, postStart, preEnd, end, postEnd);
  }

  @Test public void shouldStartObservingOnIdleDetectorWhenEnabled() {
    tracker.setEnabled(false); // It should remove itself from the observable
    int count = TrackingPlugin.getDefault().getIdleDetector().countObservers();
    tracker.setEnabled(true); // It should add itself to the observable
    assertThat(
        TrackingPlugin.getDefault().getIdleDetector().countObservers(),
        is(count + 1));
  }

  @Test public void shouldStopObservingOnIdleDetectorWhenEnabled() {
    tracker.setEnabled(true); // It should add itself to the observable
    int count = TrackingPlugin.getDefault().getIdleDetector().countObservers();
    tracker.setEnabled(false); // It should remove itself from the observable
    assertThat(
        TrackingPlugin.getDefault().getIdleDetector().countObservers(),
        is(count - 1));
  }

  protected void callIdleDetectorToNotify() throws Exception {
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

  @Override protected SessionEvent createEvent() {
    return new SessionEvent(new Interval(10, 2000));
  }

  @Override protected AbstractTracker<SessionEvent> createTracker() {
    return new SessionTracker();
  }

  private void minimizeAllShells() {
    for (Shell shell : getWorkbench().getDisplay().getShells()) {
      shell.setMinimized(true);
    }
  }
}
