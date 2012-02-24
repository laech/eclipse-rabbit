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

package rabbit.tracking.workbench;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.hamcrest.number.OrderingComparison.greaterThanOrEqualTo;
import static org.hamcrest.number.OrderingComparison.lessThanOrEqualTo;
import static org.joda.time.Instant.now;
import static rabbit.tracking.workbench.test.WorkbenchTestUtil.closeAllParts;
import static rabbit.tracking.workbench.test.WorkbenchTestUtil.openRandomPart;

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.AbstractUserTrackerSpec;
import rabbit.tracking.IUserMonitorService;

public final class AbstractRecordingPartTrackerTest
    extends AbstractUserTrackerSpec {

  private static class AbstractRecordingPartTrackerTester
      extends AbstractRecordingPartTracker {

    private Instant start;
    private Duration duration;
    private IWorkbenchPart part;

    AbstractRecordingPartTrackerTester(IUserMonitorService service) {
      super(service);
    }

    @Override protected synchronized void onPartEvent(
        Instant start, Duration d, IWorkbenchPart part) {
      this.start = start;
      this.duration = d;
      this.part = part;
    }

    synchronized Instant startTime() {
      return start;
    }

    synchronized Duration duration() {
      return duration;
    }

    synchronized IWorkbenchPart part() {
      return part;
    }

    @Override public void saveData() {
    }
  }

  private AbstractRecordingPartTrackerTester tracker;

  @Override @Before public void setup() throws Exception {
    super.setup();
    closeAllParts();
    tracker = create(getMockService());
  }

  @Override public void teardown() throws Exception {
    super.teardown();
    tracker.setEnabled(false);
  }

  @Test public void recordsPartFocusedDuration() {
    tracker.setEnabled(true);
    Instant preStart = now();
    IWorkbenchPart part = openRandomPart();
    Instant postStart = now();

    sleep(5);

    Instant preEnd = now();
    openRandomPart();
    Instant postEnd = now();
    verifyEvent(part, preStart, postStart, preEnd, postEnd);
  }

  @Test public void onUserInactiveWillStopRecording() {
    tracker.setEnabled(true);
    Instant preStart = now();
    IWorkbenchPart part = openRandomPart();
    Instant postStart = now();

    sleep(5);

    Instant preEnd = now();
    getMockService().notifyInactive();
    Instant postEnd = now();
    verifyEvent(part, preStart, postStart, preEnd, postEnd);
  }

  @Test public void onUserActiveWillStartRecordingIfThereIsActivePart() {
    IWorkbenchPart part = openRandomPart();
    tracker.setEnabled(true);
    sleep(5);
    tracker.setEnabled(false);
    assertThat(tracker.part(), is(part));
  }

  @Test public void onUserActiveWillNotStartRecordingIfThereIsNoActivePart() {
    closeAllParts();
    tracker.setEnabled(true);
    sleep(5);
    tracker.setEnabled(false);
    assertThat(tracker.part(), is(nullValue()));
  }

  @Test public void onDisableWillStopRecording() {
    tracker.setEnabled(true);
    Instant preStart = now();
    IWorkbenchPart part = openRandomPart();
    Instant postStart = now();

    sleep(5);

    Instant preEnd = now();
    tracker.setEnabled(false);
    Instant postEnd = now();
    verifyEvent(part, preStart, postStart, preEnd, postEnd);
  }

  @Override protected AbstractRecordingPartTrackerTester create(
      IUserMonitorService service) {
    return new AbstractRecordingPartTrackerTester(service);
  }

  private void verifyEvent(
      IWorkbenchPart part,
      Instant preStart,
      Instant postStart,
      Instant preEnd,
      Instant postEnd) {
    assertThat(tracker.part(), is(part));

    long start = tracker.startTime().getMillis();
    assertThat(start, greaterThanOrEqualTo(preStart.getMillis()));
    assertThat(start, lessThanOrEqualTo(postStart.getMillis()));

    long end = start + tracker.duration().getMillis();
    assertThat(end, greaterThanOrEqualTo(preEnd.getMillis()));
    assertThat(end, lessThanOrEqualTo(postEnd.getMillis()));
  }

  private void sleep(long millis) {
    try {
      Thread.sleep(millis);
    } catch (InterruptedException e) {
      throw new RuntimeException(e);
    }
  }
}
