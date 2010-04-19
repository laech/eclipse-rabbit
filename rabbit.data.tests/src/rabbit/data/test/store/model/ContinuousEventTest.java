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
package rabbit.data.test.store.model;

import rabbit.data.store.model.ContinuousEvent;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Test for {@link ContinuousEvent}
 */
public class ContinuousEventTest extends DiscreteEventTest {

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_withNegativeDuration() {
   createEvent(new DateTime(), -1);
  }

  @Test
  public void testGetDuration() {
    long duration = 348723;
    assertEquals(duration, createEvent(new DateTime(), duration).getDuration());
  }

  @Override
  protected final ContinuousEvent createEvent(DateTime time) {
    return createEvent(time, 10);
  }

  /**
   * @see ContinuousEvent#ContinuousEvent(DateTime, long)
   */
  protected ContinuousEvent createEvent(DateTime time, long duration) {
    return new ContinuousEvent(time, duration);
  }
}
