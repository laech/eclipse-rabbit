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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.joda.time.Instant.now;

import org.joda.time.Instant;
import org.junit.Test;

public class EventTest {

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutInstant() {
    newEvent(null);
  }

  @Test public void returnsTheInstant() {
    Instant i = now();
    assertThat(newEvent(i).instant(), is(i));
  }

  protected Event newEvent(Instant instant) {
    return new Event(instant);
  }
}
