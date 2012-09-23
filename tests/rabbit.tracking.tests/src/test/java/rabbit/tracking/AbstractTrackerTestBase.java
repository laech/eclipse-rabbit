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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public abstract class AbstractTrackerTestBase<T extends AbstractTracker> {

  private T tracker;

  @Before public void before() throws Exception {
    init();
    tracker = newTracker();
  }

  @After public void after() throws Exception {
    tracker.stop();
  }

  @Test public void beStoppedByDefault() {
    assertThat(tracker.isStarted(), is(false));
  }

  @Test public void canBeStarted() {
    tracker.start();
    assertThat(tracker.isStarted(), is(true));
  }

  @Test public void canBeStopped() {
    tracker.start();
    tracker.stop();
    assertThat(tracker.isStarted(), is(false));
  }

  protected final T tracker() {
    return tracker;
  }

  protected abstract T newTracker();

  protected void init() throws Exception {
  }
}
