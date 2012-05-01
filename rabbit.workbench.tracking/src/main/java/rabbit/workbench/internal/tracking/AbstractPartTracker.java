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

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.IListenableTracker;
import rabbit.tracking.IPartSessionListener;

/**
 * Abstract base class for tracking part events.
 * <p/>
 * Subclass needs to provide a part session tracker for this class to listen on,
 * then when an event occur,
 * {@link #onPartSession(Instant, Duration, IWorkbenchPart)} will be called with
 * the event.
 */
abstract class AbstractPartTracker extends AbstractTracker {

  private final IListenableTracker<IPartSessionListener> tracker;
  private final IPartSessionListener listener;

  /**
   * @param tracker the part session tracker to use for listening to events
   * @throws NullPointerException if tracker is null
   */
  AbstractPartTracker(IListenableTracker<IPartSessionListener> tracker) {
    this.tracker = checkNotNull(tracker, "tracker");
    this.listener = createListener();
  }

  private IPartSessionListener createListener() {
    return new IPartSessionListener() {
      @Override public void onPartSession(
          Instant start, Duration duration, IWorkbenchPart part) {
        AbstractPartTracker.this.onPartSession(start, duration, part);
      }
    };
  }

  @Override protected void onEnable() {
    tracker.addListener(listener);
    tracker.enable();
  }

  @Override protected void onDisable() {
    tracker.disable();
    tracker.removeListener(listener);
  }

  /**
   * Called when a new event is captured.
   * 
   * @see IPartSessionListener
   */
  protected abstract void onPartSession(
      Instant start, Duration duration, IWorkbenchPart part);
}
