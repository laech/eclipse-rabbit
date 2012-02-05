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

package rabbit.tracking.internal.workbench;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import rabbit.tracking.workbench.AbstractUserTracker;

public final class AbstractUserTrackerTest extends AbstractUserTrackerSpec {

  private static class AbstractUserTrackerTester extends AbstractUserTracker {
    int activeCount;
    int inactiveCount;

    @Override public void saveData() {
    }

    @Override protected void onUserActive() {
      activeCount++;
    }

    @Override protected void onUserInactive() {
      inactiveCount++;
    }
  }

  @Test public void notifiesWhenUserBecomesActive() {
    AbstractUserTrackerTester tracker = create();
    tracker.setEnabled(true);
    getMockService().notifyActive();
    assertThat(tracker.activeCount, is(1));
    assertThat(tracker.inactiveCount, is(0));
  }

  @Test public void notifiesWhenUserBecomesInactive() {
    AbstractUserTrackerTester tracker = create();
    tracker.setEnabled(true);
    getMockService().notifyInactive();
    assertThat(tracker.inactiveCount, is(1));
    assertThat(tracker.activeCount, is(0));
  }

  @Override protected AbstractUserTrackerTester create() {
    return new AbstractUserTrackerTester();
  }
}
