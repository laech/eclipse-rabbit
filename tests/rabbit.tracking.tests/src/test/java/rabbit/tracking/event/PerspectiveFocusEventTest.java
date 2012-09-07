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
import static org.mockito.Mockito.mock;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.Instant;
import org.junit.Test;

public final class PerspectiveFocusEventTest extends EventTest {

  static IPerspectiveDescriptor newPerspective() {
    return mock(IPerspectiveDescriptor.class);
  }

  static PerspectiveFocusEvent create(Instant instant) {
    return create(instant, newPerspective(), false);
  }

  static PerspectiveFocusEvent create(IPerspectiveDescriptor perspective) {
    return create(now(), perspective, false);
  }

  static PerspectiveFocusEvent create(boolean focused) {
    return create(now(), newPerspective(), focused);
  }

  static PerspectiveFocusEvent create(
      Instant instant, IPerspectiveDescriptor perspective, boolean focused) {
    return new PerspectiveFocusEvent(instant, perspective, focused);
  }

  @Test(expected = NullPointerException.class)//
  public void throwsNpeOnConstructWithoutPerspective() {
    create(now(), null, false);
  }

  @Test public void returnsThePerspective() {
    IPerspectiveDescriptor p = newPerspective();
    assertThat(create(p).perspective(), is(p));
  }

  @Test public void returnsTheFocus() {
    assertThat(create(true).isFocused(), is(true));
  }

  @Override protected PerspectiveFocusEvent newEvent(Instant instant) {
    return new PerspectiveFocusEvent(
        instant, newPerspective(), false);
  }
}
