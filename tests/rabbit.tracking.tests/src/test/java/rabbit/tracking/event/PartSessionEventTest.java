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
import static org.joda.time.Instant.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

public final class PartSessionEventTest extends TimedEventTest {

  static PartSessionEvent eventWith(Instant instant) {
    return new PartSessionEvent(instant, ZERO, mock(IWorkbenchPart.class));
  }

  static PartSessionEvent eventWith(Duration duration) {
    return new PartSessionEvent(now(), duration, mock(IWorkbenchPart.class));
  }

  static PartSessionEvent eventWith(IWorkbenchPart part) {
    return new PartSessionEvent(now(), ZERO, part);
  }

  static PartSessionEvent eventWith(
      Instant instant, Duration duration, IWorkbenchPart part) {
    return new PartSessionEvent(instant, duration, part);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutPart() {
    eventWith(now(), ZERO, null);
  }

  @Test public void returnsThePart() {
    IWorkbenchPart part = mock(IWorkbenchPart.class);
    assertThat(eventWith(part).part(), is(part));
  }

  @Override protected TimedEvent newEvent(Instant instant, Duration duration) {
    return new PartSessionEvent(instant, duration, mock(IWorkbenchPart.class));
  }
}
