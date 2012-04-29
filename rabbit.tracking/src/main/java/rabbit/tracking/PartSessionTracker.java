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
import static rabbit.tracking.internal.util.Workbenches.getFocusedPart;

import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.util.IRecorder;

import com.google.inject.Inject;

/**
 * Tracks how long a workbench part has been in focus. See
 * {@link IPartSessionListener} for the behaviors of this tracker.
 */
final class PartSessionTracker
    extends AbstractSessionTracker<IWorkbenchPart, IPartSessionListener> {

  private final IListenableTracker<IPartFocusListener> tracker;
  private final IPartFocusListener listener;
  private final IWorkbench workbench;

  /**
   * @param workbench the workbench to track for
   * @param partTracker the part track to use for tracking part focus events
   * @param recorder the recorder to use for calculating elapsed time
   * @param monitor the optional user monitor for taking into account the user's
   *        activeness
   * @throws NullPointerException if workbench, partTracker, or recorder is null
   */
  @Inject PartSessionTracker(
      IRecorder<IWorkbenchPart> recorder,
      IUserMonitor monitor,
      IWorkbench workbench,
      IListenableTracker<IPartFocusListener> partTracker) {
    super(recorder, monitor);

    this.workbench = checkNotNull(workbench, "workbench");
    this.tracker = checkNotNull(partTracker, "partTracker");
    this.listener = createPartFocusListener();
  }

  @Override protected IWorkbenchPart findTarget() {
    return getFocusedPart(workbench);
  }

  @Override protected void onDisable() {
    super.onDisable();
    tracker.disable();
    tracker.removeListener(listener);
  }

  @Override protected void onEnable() {
    super.onEnable();
    tracker.addListener(listener);
    tracker.enable();
  }

  @Override protected void onSession(
      Instant instant, Duration duration, IWorkbenchPart part) {
    for (IPartSessionListener listener : getListeners()) {
      listener.onPartSession(instant, duration, part);
    }
  }

  private IPartFocusListener createPartFocusListener() {
    return new IPartFocusListener() {
      @Override public void onPartFocused(IWorkbenchPart part) {
        recorder().start(part);
      }

      @Override public void onPartUnfocused(IWorkbenchPart part) {
        recorder().stop();
      }
    };
  }
}
