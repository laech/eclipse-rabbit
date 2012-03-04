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

package rabbit.tracking.internal.workbench;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.joda.time.Duration.millis;
import static org.mockito.Mockito.mock;

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.TimedEventTest;

public final class PartEventTest extends TimedEventTest {

  private Duration duration;
  private Instant instant;
  private IWorkbenchPart part;

  private PartEvent event;

  @Before public void init() {
    instant = Instant.now();
    duration = millis(10);
    part = mock(IWorkbenchPart.class);
    event = create(instant, duration, part);
  }

  @Test(expected = NullPointerException.class)//
  public void constructorThrowsExceptionIfPartisNull() {
    create(instant, duration, null);
  }

  @Test public void returnsTheWorkbenchPart() {
    assertThat(event.part(), is(part));
  }

  @Override protected PartEvent create(Instant instant, Duration duration) {
    return create(instant, duration, part);
  }

  private PartEvent create(
      Instant instant, Duration duration, IWorkbenchPart part) {
    return new PartEvent(instant, duration, part);
  }
}
