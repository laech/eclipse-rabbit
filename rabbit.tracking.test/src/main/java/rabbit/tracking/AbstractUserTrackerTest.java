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

package rabbit.tracking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.junit.Test;

public final class AbstractUserTrackerTest extends AbstractUserTrackerSpec {

  private static class AbstractUserTrackerTester extends AbstractUserTracker {
    int onActiveCount;
    int onInactiveCount;

    AbstractUserTrackerTester(IUserMonitorService service) {
      super(service);
    }

    @Override public void saveData() {
    }

    @Override protected void onUserActive() {
      onActiveCount++;
    }

    @Override protected void onUserInactive() {
      onInactiveCount++;
    }
  }

  private AbstractUserTrackerTester tracker;

  @Override public void setup() throws Exception {
    super.setup();
    tracker = create(getMockService());
  }

  @Override public void teardown() throws Exception {
    super.teardown();
    tracker.disable();
  }

  @Test public void notifiesWhenUserBecomesActive() {
    tracker.enable();
    getMockService().notifyActive();
    assertThat(tracker.onActiveCount, is(1));
    assertThat(tracker.onInactiveCount, is(0));
  }

  @Test public void notifiesWhenUserBecomesInactive() {
    tracker.enable();
    getMockService().notifyInactive();
    assertThat(tracker.onInactiveCount, is(1));
    assertThat(tracker.onActiveCount, is(0));
  }

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfServiceIsNull() {
    create(null);
  }

  @Override protected AbstractUserTrackerTester create(
      IUserMonitorService service) {
    return new AbstractUserTrackerTester(service);
  }
}
