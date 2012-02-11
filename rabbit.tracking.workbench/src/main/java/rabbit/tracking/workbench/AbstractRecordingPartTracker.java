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

package rabbit.tracking.workbench;

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.util.Recorder;
import rabbit.tracking.util.Recorder.IRecorderListener;
import rabbit.tracking.util.Recorder.Record;

/**
 * Tracks how long a workbench part has been in focus, taking into account the
 * user's state.
 * <p/>
 * If a part is focused, then when it becomes unfocused or the user becomes
 * inactive, {@link #onPartEvent(Instant, Duration, IWorkbenchPart)} will be
 * called with the captured event. When the user becomes active or when a part
 * becomes focused, a new tracking session will be started if appropriate.
 * 
 * @since 2.0
 */
public abstract class AbstractRecordingPartTracker extends AbstractPartTracker {

  private final Recorder<IWorkbenchPart> recorder = Recorder.create();

  public AbstractRecordingPartTracker() {
    super();
    recorder.addListener(new IRecorderListener<IWorkbenchPart>() {
      @Override public void onRecord(Record<IWorkbenchPart> record) {
        onPartEvent(record.getStart(), record.getDuration(), record.getData());
      }
    });
  }

  @Override protected void onDisable() {
    super.onDisable();
    recorder.stop();
  }

  @Override protected void onUserActive() {
    IWorkbenchPart part = getFocusedPart();
    if (part != null) {
      recorder.start(part);
    }
  }

  @Override protected void onUserInactive() {
    recorder.stop();
  }

  @Override protected void onPartFocused(IWorkbenchPart part) {
    recorder.start(part);
  }

  @Override protected void onPartUnfocused(IWorkbenchPart part) {
    recorder.stop();
  }

  /**
   * Called when a new event is captured.
   * 
   * @param start the start time of this event, not null
   * @param duration the duration of this event, not null
   * @param part the workbench part of this event, not null
   */
  protected abstract void onPartEvent(Instant start, Duration duration, IWorkbenchPart part);
}
