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

package rabbit.tracking.event;

import static org.hamcrest.Matchers.is;
import static org.joda.time.Duration.ZERO;
import static org.joda.time.Duration.millis;
import static org.joda.time.Instant.now;
import static org.junit.Assert.assertThat;

import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

public class TimedEventTest extends EventTest {

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutDuration() {
    newEvent(now(), null);
  }

  @Test public void returnsTheDuration() {
    Duration d = millis(11);
    assertThat(newEvent(now(), d).duration(), is(d));
  }

  @Override protected final TimedEvent newEvent(Instant instant) {
    return newEvent(instant, ZERO);
  }

  protected TimedEvent newEvent(Instant instant, Duration duration) {
    return new TimedEvent(instant, duration);
  }
}
