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
package rabbit.data.store.model;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableSet;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.joda.time.DateTime;

import java.util.Set;

import javax.annotation.Nonnull;

/**
 * Represents a launch event such as a debug launch.
 */
public class LaunchEvent extends ContinuousEvent {

  @Nonnull
  private final ILaunch launch;
  @Nonnull
  private final ILaunchConfiguration config;

  /** Unmodifiable set of file paths. */
  @Nonnull
  private final ImmutableSet<IPath> filePaths;

  /**
   * Constructs a new event.
   * 
   * @param endTime The end time of the event.
   * @param duration The duration of the event, in milliseconds.
   * @param config The launch configuration.
   * @param filePaths The paths of the files associated with the launch, or an
   *          empty collection.
   * @throws IllegalArgumentException If duration is negative.
   * @throws NullPointerException If startTime, or launch, or config, or
   *           filePaths is null.
   * 
   * @see IResource#getFullPath()
   */
  public LaunchEvent(@Nonnull DateTime endTime, long duration,
      @Nonnull ILaunch launch, @Nonnull ILaunchConfiguration config,
      @Nonnull Set<IPath> filePaths) {

    super(endTime, duration);

    checkNotNull(launch, "Launch cannot be null");
    checkNotNull(config, "Launch configuration cannot be null");
    checkNotNull(filePaths, "File paths cannot be null");

    this.config = config;
    this.launch = launch;
    this.filePaths = ImmutableSet.copyOf(filePaths);
  }

  /**
   * Gets the IDs of the files involved.
   * 
   * @return An unmodifiable collection of IDs of the files involved, or an
   *         empty collection.
   */
  @Nonnull
  public Set<IPath> getFilePaths() {
    return filePaths;
  }

  /**
   * Gets the launch.
   * 
   * @return The launch.
   */
  @Nonnull
  public ILaunch getLaunch() {
    return launch;
  }

  /**
   * Gets the launch configuration.
   * 
   * @return The launch configuration.
   */
  @Nonnull
  public ILaunchConfiguration getLaunchConfiguration() {
    return config;
  }
}
