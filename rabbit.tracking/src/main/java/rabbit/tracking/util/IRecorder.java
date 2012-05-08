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

package rabbit.tracking.util;

import rabbit.tracking.IListenableTracker;

// TODO 
public interface IRecorder<T> extends IListenableTracker<IRecordListener<T>> {

  /**
   * Starts a recording with the given user data.
   * <p/>
   * If there is currently a recording in progress with an equivalent user data
   * then this call will be ignored and the recording in progress will not be
   * affected. Otherwise if the currently running recording has a different user
   * data, then the recording will be stopped, listeners will be notified, and a
   * new recording will be started with the new user data.
   * 
   * @param userData the user data to associate with this recording, may be null
   */
  void start(T userData);

  /**
   * Calling this method has the same effect as {@code start(null)}.
   */
  @Override void start();

  /**
   * Stops recording.
   * <p/>
   * Has no affect if there is no recording running, otherwise recording will be
   * stopped and listeners will be notified.
   */
  @Override void stop();

}