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

import org.eclipse.ui.IPerspectiveDescriptor;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.AbstractTracker;
import rabbit.tracking.IEventListener;
import rabbit.tracking.IListenableTracker;
import rabbit.tracking.IPerspectiveSessionListener;

import com.google.inject.Inject;

public final class PerspectiveTracker extends AbstractTracker {

  private final IEventListener<IPerspectiveEvent> eventListener;
  private final IListenableTracker<IPerspectiveSessionListener> tracker;
  private final IPerspectiveSessionListener perspectiveListener;

  /**
   * @param tracker the tracker to use for listening to perspective session
   *        events
   * @param listener the listener to receive events
   * @throws NullPointerException if any argument is null
   */
  @Inject public PerspectiveTracker(
      IListenableTracker<IPerspectiveSessionListener> tracker,
      IEventListener<IPerspectiveEvent> listener) {
    this.tracker = checkNotNull(tracker, "tracker");
    this.eventListener = checkNotNull(listener, "listener");
    this.perspectiveListener = createPerspectiveSessionListener();
  }

  @Override protected void onEnable() {
    tracker.addListener(perspectiveListener);
    tracker.enable();
  }

  @Override protected void onDisable() {
    tracker.disable();
    tracker.removeListener(perspectiveListener);
  }

  private IPerspectiveSessionListener createPerspectiveSessionListener() {
    return new IPerspectiveSessionListener() {
      @Override public void onPerspectiveSession(
          Instant start, Duration duration, IPerspectiveDescriptor persp) {
        eventListener.onEvent(new PerspectiveEvent(start, duration, persp));
      }
    };
  }
}