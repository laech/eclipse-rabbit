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

import java.util.Set;

import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;

import rabbit.tracking.ITimedEvent;

/**
 * An event representing a debug session and/or one or more system processes.
 * 
 * @since 2.0
 */
public interface ILaunchEvent extends ITimedEvent {

  /**
   * Gets the set of files involved in this launch event. For example, if a
   * launched process is suspended by a break point, the file of the break point
   * will be recorded here.
   * 
   * @return the files, not null, may be empty, and unmodifiable
   */
  Set<IPath> files();

  /**
   * Gets the launch result.
   * 
   * @return the launch result, not null
   */
  ILaunch launch();

  /**
   * Gets the launch configuration type of this launch.
   * 
   * @return the launch configuration type, not null
   */
  ILaunchConfigurationType launchConfigType();

  /**
   * Gets the launch configuration of this launch.
   * 
   * @return the launch configuration, not null
   */
  ILaunchConfiguration launchConfig();

}
