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
package rabbit.core.internal;

import rabbit.core.ITracker;

/**
 * An object of this type will created when reading the extension point.
 */
public class TrackerObject {

	private String id;
	private String name;
	private String description;
	private ITracker<?> tracker;

	/**
	 * Constructs a new tracker object.
	 * 
	 * @param id
	 *            The id of the tracker.
	 * @param name
	 *            The name of the tracker.
	 * @param description
	 *            The description of the tracker.
	 * @param t
	 *            The tracker.
	 */
	public TrackerObject(String id, String name, String description, ITracker<?> t) {
		this.id = id;
		this.name = name;
		this.description = description;
		tracker = t;
	}

	/**
	 * Gets the description of the tracker.
	 * 
	 * @return The description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Gets the id of the tracker.
	 * 
	 * @return The id.
	 */
	public String getId() {
		return id;
	}

	/**
	 * Gets the name of the tracker.
	 * 
	 * @return The name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the tracker.
	 * 
	 * @return The tracker.
	 */
	public ITracker<?> getTracker() {
		return tracker;
	}

	/**
	 * Sets the description of the tracker.
	 * 
	 * @param description
	 *            The new description.
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * Sets the id of the tracker.
	 * 
	 * @param id
	 *            The new id.
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * Sets the name of the tracker.
	 * 
	 * @param name
	 *            The new name.
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Sets the tracker.
	 * 
	 * @param tracker
	 *            The new tracker.
	 */
	public void setTracker(ITracker<?> tracker) {
		this.tracker = tracker;
	}
}
