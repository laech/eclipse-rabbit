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

import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Test;

public final class PerspectiveSessionEventTest extends TimedEventTest {

  static IPerspectiveDescriptor newPerspective() {
    return mock(IPerspectiveDescriptor.class);
  }

  static PerspectiveSessionEvent eventWith(Instant instant) {
    return eventWith(instant, ZERO, newPerspective());
  }

  static PerspectiveSessionEvent eventWith(Duration duration) {
    return eventWith(now(), duration, newPerspective());
  }

  static PerspectiveSessionEvent eventWith(IPerspectiveDescriptor perspective) {
    return eventWith(now(), ZERO, perspective);
  }

  static PerspectiveSessionEvent eventWith(
      Instant instant, Duration duration, IPerspectiveDescriptor perspective) {
    return new PerspectiveSessionEvent(instant, duration, perspective);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutPerspective() {
    eventWith(now(), ZERO, null);
  }

  @Test public void returnsThePerspective() {
    IPerspectiveDescriptor p = newPerspective();
    assertThat(eventWith(p).perspective(), is(p));
  }

  @Override protected PerspectiveSessionEvent newEvent(
      Instant instant, Duration duration) {
    return eventWith(instant, duration, newPerspective());
  }
}
