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

package rabbit.tracking;

import static com.google.common.base.Preconditions.checkNotNull;
import static rabbit.tracking.internal.util.Workbenches.getFocusedPerspective;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbench;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.util.IRecorder;

import com.google.inject.Inject;

/**
 * Tracks how long a perspective has been in focus. See
 * {@link IPerspectiveSessionListener} for the behaviors of this tracker.
 */
final class PerspectiveSessionTracker extends
    AbstractSessionTracker<IPerspectiveDescriptor, IPerspectiveSessionListener> {

  private final IWorkbench workbench;
  private final IListenableTracker<IPerspectiveFocusListener> tracker;
  private final IPerspectiveFocusListener listener;

  @Inject PerspectiveSessionTracker(
      IRecorder<IPerspectiveDescriptor> recorder,
      IUserMonitor monitor,
      IWorkbench workbench,
      IListenableTracker<IPerspectiveFocusListener> perspectiveTracker) {
    super(recorder, monitor);
    this.workbench = checkNotNull(workbench, "workbench");
    this.tracker = checkNotNull(perspectiveTracker, "perspectiveTracker");
    this.listener = createPerspectiveFocusListener();
  }

  @Override protected IPerspectiveDescriptor findTarget() {
    return getFocusedPerspective(workbench);
  }

  @Override protected void onStop() {
    super.onStop();
    tracker.stop();
    tracker.removeListener(listener);
  }

  @Override protected void onStart() {
    super.onStart();
    tracker.addListener(listener);
    tracker.start();
  }

  @Override protected void onSession(Instant instant, Duration duration,
      IPerspectiveDescriptor perspective) {
    for (IPerspectiveSessionListener listener : getListeners()) {
      listener.onPerspectiveSession(instant, duration, perspective);
    }
  }

  private IPerspectiveFocusListener createPerspectiveFocusListener() {
    return new IPerspectiveFocusListener() {
      @Override public void onPerspectiveFocused(IPerspectiveDescriptor p) {
        recorder().start(p);
      }

      @Override public void onPerspectiveUnfocused(IPerspectiveDescriptor p) {
        recorder().stop();
      }
    };
  }
}
