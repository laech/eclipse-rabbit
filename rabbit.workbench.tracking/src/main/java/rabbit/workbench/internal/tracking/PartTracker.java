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

package rabbit.workbench.internal.tracking;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.IListenableTracker;
import rabbit.tracking.IPartSessionListener;
import rabbit.tracking.IPersistable;
import rabbit.tracking.util.IPersistableEventListenerSupport;

import com.google.inject.Inject;

public final class PartTracker extends AbstractPartTracker
    implements IPersistable {

  private final IPersistableEventListenerSupport<IPartEvent> support;

  /**
   * @param tracker the tracker to use for listening to part session events
   * @param support the listener support for sending events
   * @throws NullPointerException if any argument is null
   */
  @Inject public PartTracker(
      IListenableTracker<IPartSessionListener> tracker,
      IPersistableEventListenerSupport<IPartEvent> support) {
    super(tracker);
    this.support = checkNotNull(support, "support");
  }

  @Override protected void onPartSession(
      Instant start, Duration duration, IWorkbenchPart part) {
    support.notifyOnEvent(new PartEvent(start, duration, part));
  }

  @Override public void save() {
    support.notifyOnSave();
  }

}
