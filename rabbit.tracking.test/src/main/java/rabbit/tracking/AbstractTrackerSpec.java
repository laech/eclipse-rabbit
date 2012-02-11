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

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTrackerSpec {

  private AbstractTracker tracker;

  @Before public void setup() throws Exception {
    tracker = create();
  }

  @After public void teardown() throws Exception {
    tracker.setEnabled(false);
  }

  @Test public void isDisabledByDefault() {
    assertThat(tracker.isEnabled(), is(false));
  }

  @Test public void isEnabledWhenSetToEnable() {
    tracker.setEnabled(true);
    assertThat(tracker.isEnabled(), is(true));
  }

  @Test public void isDisabledWhenSetToDisable() {
    tracker.setEnabled(false);
    assertThat(tracker.isEnabled(), is(false));
  }

  protected abstract AbstractTracker create();
}
