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

package rabbit.workbench.tracking.event;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.joda.time.Duration;
import org.joda.time.Instant;

import rabbit.tracking.event.TimedEvent;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.collect.ImmutableSet;

/**
 * An event representing a launch (run/debug) session.
 * 
 * @since 2.0
 */
public final class LaunchEvent extends TimedEvent {

  private final ILaunch launch;
  private final ILaunchConfiguration config;
  private final ILaunchConfigurationType type;
  private final Set<IPath> filePaths;

  /*
   * Note that ILaunch.getLaunchConfiguration() and
   * ILaunchConfiguration.getType() returns the objects we want but may return
   * null, (we don't want null) therefore we specified them as non null
   * parameters rather than just taking the ILaunch.
   */

  /**
   * @param instant the start time of the event
   * @param duration the duration of the launch
   * @param launch the launch instance
   * @param files the files stepped into during the launch
   * @throws CoreException if unable to get {@link ILaunchConfiguration} or
   *         {@link ILaunchConfigurationType} from the launch
   */
  public LaunchEvent(
      Instant instant,
      Duration duration,
      ILaunch launch,
      Set<? extends IPath> files) throws CoreException {
    super(instant, duration);
    this.launch = checkNotNull(launch, "launch");
    this.config = checkNotNull(launch.getLaunchConfiguration(), "config");
    this.type = checkNotNull(config.getType(), "type");
    this.filePaths = ImmutableSet.copyOf(checkNotNull(files, "files"));
  }

  /**
   * Gets the set of files involved in this launch event. For example, if a
   * launched process is suspended by a break point, the file of the break point
   * will be recorded here.
   * 
   * @return the files, not null, may be empty, and unmodifiable
   */
  public Set<IPath> files() {
    return filePaths;
  }

  /**
   * Gets the launch result.
   * 
   * @return the launch result, not null
   */
  public ILaunch launch() {
    return launch;
  }

  /**
   * Gets the launch configuration type of this launch.
   * 
   * @return the launch configuration type, not null
   */
  public ILaunchConfigurationType launchConfigType() {
    return type;
  }

  /**
   * Gets the launch configuration of this launch.
   * 
   * @return the launch configuration, not null
   */
  public ILaunchConfiguration launchConfig() {
    return config;
  }

  @Override public boolean equals(Object obj) {
    if (obj instanceof LaunchEvent) {
      LaunchEvent that = (LaunchEvent)obj;
      return Objects.equal(instant(), that.instant())
          && Objects.equal(duration(), that.duration())
          && Objects.equal(files(), that.files())
          && Objects.equal(launch(), that.launch())
          && Objects.equal(launchConfig(), that.launchConfig())
          && Objects.equal(launchConfigType(), that.launchConfigType());
    }
    return false;
  }

  @Override public int hashCode() {
    return Objects.hashCode(
        instant(),
        duration(),
        files(),
        launch(),
        launchConfig(),
        launchConfigType());
  }

  @Override protected ToStringHelper toStringHelper() {
    return super.toStringHelper()
        .add("launch", launch())
        .add("launchConfiguration", launchConfig())
        .add("launchConfigurationType", launchConfigType())
        .add("files", files());
  }
}
