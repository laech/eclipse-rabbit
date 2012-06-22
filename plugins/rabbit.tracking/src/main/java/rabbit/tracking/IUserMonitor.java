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
 * This service can be obtained via an {@link IServiceLocator}.
 * 
 * @see IUserListener
 * @since 2.0
 */
public interface IUserMonitor extends IListenable<IUserListener> {

  /**
   * Checks whether the user is currently considered as active.
   * 
   * @return true if user is active, false otherwise
   */
  boolean isUserActive();
}
