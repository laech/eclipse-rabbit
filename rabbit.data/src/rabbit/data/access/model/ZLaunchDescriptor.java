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
package rabbit.data.access.model;

import java.util.HashSet;
import java.util.Set;

/**
 * Describes launches.
 */
public class ZLaunchDescriptor {

  private int count;
  private long totalDuration;
  private String launchModeId;
  private String launchName;
  private Set<String> fileIds;
  private String launchTypeId;

  /**
   * Constructs a new descriptor.
   */
  public ZLaunchDescriptor() {
    fileIds = new HashSet<String>();
    launchModeId = "";
    launchName = "";
    launchTypeId = "";
    totalDuration = 0;
    count = 0;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this)
      return true;
    if (obj.getClass() != getClass())
      return false;

    ZLaunchDescriptor des = (ZLaunchDescriptor) obj;
    return getTotalDuration() == des.getTotalDuration()
        && getLaunchModeId().equals(des.getLaunchModeId())
        && getLaunchName().equals(des.getLaunchName())
        && getLaunchTypeId().equals(des.getLaunchTypeId())
        // Test to see all fileIds are same:
        && getFileIds().containsAll(des.getFileIds())
        && des.getFileIds().containsAll(getFileIds());
  }

  /**
   * Gets the number of time this launch configuration has been launched.
   * 
   * @return The count.
   */
  public int getCount() {
    return count;
  }

  /**
   * Gets the IDs of the files involved.
   * 
   * @return The IDs of the files involved, a modifiable collection.
   * @see {@link rabbit.core.storage.IFileMapper}
   */
  public Set<String> getFileIds() {
    return fileIds;
  }

  /**
   * Gets the launch mode.
   * 
   * @return The launch mode, or an empty string if not set.
   */
  public String getLaunchModeId() {
    return launchModeId;
  }

  /**
   * Gets the name of this launch.
   * 
   * @return The name of this launch, or an empty string if not set.
   */
  public String getLaunchName() {
    return launchName;
  }

  /**
   * Gets the type ID of this launch.
   * 
   * @return The type ID of this launch, or an empty string if not set.
   */
  public String getLaunchTypeId() {
    return launchTypeId;
  }

  /**
   * Gets the total duration.
   * 
   * @return The duration.
   */
  public long getTotalDuration() {
    return totalDuration;
  }

  @Override
  public int hashCode() {
    return (getLaunchModeId().hashCode() + getLaunchName().hashCode()
        + getLaunchTypeId().hashCode() + getFileIds().hashCode()) % 31;
  }

  /**
   * Sets the number of time this launch configuration has been launched.
   * 
   * @param count The count.
   * @return True if argument is accepted, false if argument is < 0.
   */
  public boolean setCount(int count) {
    if (count < 0) {
      return false;
    }
    this.count = count;
    return true;
  }

  /**
   * Sets the launch mode of this launch.
   * 
   * @param launchModeId The launch mode.
   * @return True if argument is accepted, false if it's null.
   */
  public boolean setLaunchModeId(String launchModeId) {
    if (launchModeId == null) {
      return false;
    }
    this.launchModeId = launchModeId;
    return true;
  }

  /**
   * Sets the name of this launch.
   * 
   * @param launchName The name of this launch.
   * @return True if argument is accepted, false if argument is null.
   */
  public boolean setLaunchName(String launchName) {
    if (launchName == null) {
      return false;
    }
    this.launchName = launchName;
    return true;
  }

  /**
   * Sets the type ID of this launch.
   * 
   * @param launchTypeId The type ID of this launch.
   * @return True if argument is accepted, false if argument is null.
   */
  public boolean setLaunchTypeId(String launchTypeId) {
    if (launchTypeId == null) {
      return false;
    }
    this.launchTypeId = launchTypeId;
    return true;
  }

  /**
   * Sets the total duration.
   * 
   * @param duration The duration.
   * @return True if the argument is >= 0 then the duration is changed, false if
   *         the argument is < 0 then the duration is not changed.
   */
  public boolean setTotalDuration(long duration) {
    if (duration < 0) {
      return false;
    }
    this.totalDuration = duration;
    return true;
  }
}
