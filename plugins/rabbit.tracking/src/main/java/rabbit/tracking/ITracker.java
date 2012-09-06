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

package rabbit.tracking;

/**
 * Represents a tracker that tracks events within the workbench.
 * <p/>
 * This interface is also used for plug-in who wishes to extend the
 * {@code rabbit.tracking.trackers} extension point.
 * <p/>
 * 
 * <h3>Extension Lifecycle</h3>
 * <p/>
 * A tracker by default is stopped, it will be started after it is loaded from
 * the extension point, and will be stopped prior to workbench shutdown so that
 * it may perform clean up operations if need to.
 * 
 * @since 2.0
 */
public interface ITracker {

  /**
   * Checks whether this tracker is currently started.
   * 
   * @return true if this tracker is started, false otherwise
   */
  boolean isStarted();

  /**
   * Starts this tracker. Has no affect if already started.
   */
  void start();

  /**
   * Stops this tracker. Has no affect if already stopped.
   */
  void stop();
}