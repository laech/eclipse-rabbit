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

import org.eclipse.ui.services.IServiceLocator;

/**
 * Monitors the state of the user and notifies listeners when user becomes
 * active/inactive.
 * <p/>
 * This service can be obtained via a {@link IServiceLocator}.
 * 
 * @since 2.0
 * @noimplement this interface is not intended to be implemented by clients
 */
public interface IUserMonitorService {

  /**
   * Adds the listener to be notified of user state changes. Has no effect if an
   * identical listener has already been added.
   * 
   * @param listener the listener to be notified
   * @throws NullPointerException if listener is null
   */
  void addListener(IUserListener listener);

  /**
   * Removes the listener from listening to user state change events.
   * 
   * @param listener the listener to be removed
   * @throws NullPointerException if listener is null
   */
  void removeListener(IUserListener listener);

  /**
   * Checks whether the user is currently considered as active.
   * 
   * @return true if user is active, false otherwise
   */
  boolean isUserActive();
}
