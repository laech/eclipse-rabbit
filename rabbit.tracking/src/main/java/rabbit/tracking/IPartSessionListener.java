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

import org.eclipse.ui.IWorkbenchPart;
import org.joda.time.Duration;
import org.joda.time.Instant;

/**
 * Listens to part focused duration events.
 * <p/>
 * If a part is focused, then when it becomes unfocused (or by some other means
 * such as the user becomes inactive, or the track gets disabled), this listener
 * will be notified with the captured session.
 * <p/>
 * A part is consider focused if it's the active part and its parent window has
 * the focus.
 * 
 * @since 2.0
 */
public interface IPartSessionListener {

  /**
   * Called when a new event is captured.
   * 
   * @param start the start time of this event, not null
   * @param duration the duration of this event, not null
   * @param part the workbench part of this event, not null
   */
  void onPartSession(Instant start, Duration duration, IWorkbenchPart part);
}