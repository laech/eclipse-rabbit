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
package rabbit.workbench.internal.tracking;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.Duration.millis;
import static org.joda.time.Instant.now;
import static org.mockito.Mockito.mock;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.TimedEventTest;
import rabbit.workbench.internal.tracking.PerspectiveEvent;

public final class PerspectiveEventTest extends TimedEventTest {

  private Duration duration;
  private Instant instant;
  private IPerspectiveDescriptor perspective;

  private PerspectiveEvent event;

  @Before public void init() {
    instant = now();
    duration = millis(10);
    perspective = mock(IPerspectiveDescriptor.class);
    event = create(instant, duration, perspective);
  }

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfPerspectiveIsNull() {
    create(instant, duration, null);
  }

  @Test public void returnsThePerspective() {
    assertThat(event.perspective(), is(perspective));
  }

  @Override protected PerspectiveEvent create(Instant instant, Duration duration) {
    return create(instant, duration, perspective);
  }

  private PerspectiveEvent create(Instant instant, Duration duration,
      IPerspectiveDescriptor perspective) {
    return new PerspectiveEvent(instant, duration, perspective);
  }
}
