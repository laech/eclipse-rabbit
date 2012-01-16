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

/**
 * A listener for listening to state events of a tracker.
 * 
 * @since 2.0
 */
public interface ITrackerListener<E> {

  /**
   * Called after the tracker is enabled.
   */
  void onEnabled();

  /**
   * Called after the tracker is disabled.
   */
  void onDisabled();

  /**
   * Called when the tracker is being asked to save its data.
   */
  void onSaveData();
}
