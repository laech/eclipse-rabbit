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

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.TimedEvent;

import com.google.common.base.Objects.ToStringHelper;

final class PerspectiveEvent extends TimedEvent implements IPerspectiveEvent {

  private final IPerspectiveDescriptor perspective;

  PerspectiveEvent(Instant instant, Duration duration,
      IPerspectiveDescriptor perspective) {
    super(instant, duration);
    this.perspective = checkNotNull(perspective, "perspective");
  }

  @Override public final IPerspectiveDescriptor perspective() {
    return perspective;
  }

  @Override protected ToStringHelper toStringHelper() {
    return super.toStringHelper().add("perspective", perspective());
  }
}
