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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.TimedEvent;
import rabbit.tracking.workbench.IPerspectiveEvent;

final class PerspectiveEvent extends TimedEvent implements IPerspectiveEvent {

  private final IPerspectiveDescriptor perspective;

  PerspectiveEvent(Instant instant, Duration duraion,
      IPerspectiveDescriptor perspective) {
    super(instant, duraion);
    this.perspective = checkNotNull(perspective, "perspective");
  }

  @Override public final IPerspectiveDescriptor perspective() {
    return perspective;
  }

  @Override public String toString() {
    return toStringHelper().add("perspective", perspective()).toString();
  }
}
