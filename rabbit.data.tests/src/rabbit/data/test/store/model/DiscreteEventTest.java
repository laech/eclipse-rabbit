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

import rabbit.data.store.model.DiscreteEvent;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;
import org.junit.Test;

/**
 * Test for {@link DiscreteEvent}
 */
public class DiscreteEventTest {

  private DateTime time = new DateTime();

  private DiscreteEvent event = createEvent(time);

  @Test(expected = NullPointerException.class)
  public void testConstructor_null() {
    new DiscreteEvent(null);
  }

  @Test
  public void testEvent() {
    assertNotNull(event);
  }

  @Test
  public void testGetTime() {
    assertEquals(time, event.getTime());
  }

  /** Creates an event for testing. */
  protected DiscreteEvent createEvent(DateTime time) {
    return new DiscreteEvent(time);
  }
}
