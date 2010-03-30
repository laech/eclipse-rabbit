/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.core.storage;

import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Set;

/**
 * Describes a launch.
 */
public class LaunchDescriptor {

	private long duration;
	private String launchModeId;
	private String launchName;
	private Calendar launchTime;
	private Set<String> fileIds;
	private String launchTypeId;

	/**
	 * Constructs a new descriptor.
	 */
	public LaunchDescriptor() {
		launchTime = new GregorianCalendar(0, 0, 0);
		fileIds = Collections.emptySet();
		launchModeId = "";
		launchName = "";
		launchTypeId = "";
		duration = 0;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this)
			return true;
		if (obj.getClass() != getClass())
			return false;

		LaunchDescriptor des = (LaunchDescriptor) obj;
		return getDuration() == des.getDuration()
				&& getLaunchModeId().equals(des.getLaunchModeId())
				&& getLaunchName().equals(des.getLaunchName())
				&& getLaunchTime().getTime().equals(des.getLaunchTime().getTime())
				&& getLaunchTypeId().equals(des.getLaunchTypeId())
				// Test to see all fileIds are same:
				&& getFileIds().containsAll(des.getFileIds())
				&& des.getFileIds().containsAll(getFileIds());
	}

	/**
	 * Gets the duration of the launch.
	 * 
	 * @return The duration.
	 */
	public long getDuration() {
		return duration;
	}

	/**
	 * Gets the IDs of the files involved.
	 * 
	 * @return The IDs of the files involved.
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
	 * Gets the time of this launch.
	 * 
	 * @return The time of this launch, the calendar object can be manipulated
	 *         directly to make changes.
	 */
	public Calendar getLaunchTime() {
		return launchTime;
	}

	/**
	 * Gets the type ID of this launch.
	 * 
	 * @return The type ID of this launch, or an empty string if not set.
	 */
	public String getLaunchTypeId() {
		return launchTypeId;
	}

	@Override
	public int hashCode() {
		return (getLaunchModeId().hashCode()
				+ getLaunchName().hashCode()
				+ getLaunchTime().hashCode()
				+ getLaunchTypeId().hashCode() + getFileIds().hashCode()) % 31;
	}

	/**
	 * Sets the duration of the launch.
	 * 
	 * @param duration
	 *            The duration.
	 * @return True if the argument is >= 0 then the duration is changed, false
	 *         if the argument is < 0 then the duration is not changed.
	 */
	public boolean setDuration(long duration) {
		if (duration < 0) {
			return false;
		}
		this.duration = duration;
		return true;
	}

	/**
	 * Sets the IDs of the files involved in this launch.
	 * 
	 * @param fileIds
	 *            The IDs of the files.
	 * @return True if the argument is accepted, false if argument is null.
	 * @see {@link rabbit.core.storage.IFileMapper}
	 */
	public boolean setFileIds(Collection<String> fileIds) {
		if (fileIds == null) {
			return false;
		}
		this.fileIds = Collections.unmodifiableSet(new HashSet<String>(fileIds));
		return true;
	}

	/**
	 * Sets the launch mode of this launch.
	 * 
	 * @param launchModeId
	 *            The launch mode.
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
	 * @param launchName
	 *            The name of this launch.
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
	 * @param launchTypeId
	 *            The type ID of this launch.
	 * @return True if argument is accepted, false if argument is null.
	 */
	public boolean setLaunchTypeId(String launchTypeId) {
		if (launchTypeId == null) {
			return false;
		}
		this.launchTypeId = launchTypeId;
		return true;
	}
}
