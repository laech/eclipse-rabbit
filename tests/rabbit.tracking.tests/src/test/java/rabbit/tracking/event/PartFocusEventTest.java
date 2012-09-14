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
import static org.joda.time.Instant.now;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Instant;
import org.junit.Test;

public final class PartFocusEventTest extends EventTest {

  static PartFocusEvent eventWith(Instant instant) {
    return eventWith(instant, mock(IWorkbenchPart.class), false);
  }

  static PartFocusEvent eventWith(IWorkbenchPart part) {
    return eventWith(now(), part, false);
  }

  static PartFocusEvent eventWith(boolean focused) {
    return eventWith(now(), mock(IWorkbenchPart.class), focused);
  }

  static PartFocusEvent eventWith(
      Instant instant, IWorkbenchPart part, boolean focused) {
    return new PartFocusEvent(instant, part, focused);
  }

  @Test public void returnsThePart() {
    IWorkbenchPart part = mock(IWorkbenchPart.class);
    assertThat(eventWith(part).part(), is(part));
  }

  @Test public void returnsTheFocus() {
    assertThat(eventWith(true).isFocused(), is(true));
  }

  @Override protected PartFocusEvent newEvent(Instant instant) {
    return eventWith(instant);
  }
}
