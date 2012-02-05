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

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public final class AbstractTrackerTest extends AbstractTrackerSpec {

  public AbstractTrackerTest() {
  }

  @Test public void shouldCallOnEnableWhenEnabling() {
    AbstractTrackerTester tracker = create();
    tracker.setEnabled(false);
    tracker.setEnabled(true);
    assertThat(tracker.enableCount, is(1));
  }

  @Test public void shouldCallOnEnableOnlyIfPreviouslyDisabled() {
    AbstractTrackerTester tracker = create();
    tracker.setEnabled(false);
    tracker.setEnabled(true);
    tracker.setEnabled(true);
    assertThat(tracker.enableCount, is(1));
  }

  @Test public void shouldCallOnDisableWhenDisabling() {
    AbstractTrackerTester tracker = create();
    tracker.setEnabled(true);
    tracker.setEnabled(false);
    assertThat(tracker.disableCount, is(1));
  }

  @Test public void shouldCallOnDisableOnlyIfPreviouslyEnabled() {
    AbstractTrackerTester tracker = create();
    tracker.setEnabled(true);
    tracker.setEnabled(false);
    tracker.setEnabled(false);
    assertThat(tracker.disableCount, is(1));
  }

  @Override protected AbstractTrackerTester create() {
    return new AbstractTrackerTester();
  }

  private static class AbstractTrackerTester extends AbstractTracker {
    int enableCount = 0;
    int disableCount = 0;

    @Override public void saveData() {
    }

    @Override protected void onEnable() {
      enableCount++;
    }

    @Override protected void onDisable() {
      disableCount++;
    }
  }
}
