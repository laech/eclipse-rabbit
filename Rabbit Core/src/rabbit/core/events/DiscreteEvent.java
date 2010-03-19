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
package rabbit.core.events;

import java.util.Calendar;

/**
 * Represents an event with no duration.
 */
public class DiscreteEvent {

	private Calendar time;

	/**
	 * Constructs a new event.
	 * 
	 * @param time
	 *            The event time.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public DiscreteEvent(Calendar time) {
		setTime(time);
	}

	/**
	 * Gets the time of the event.
	 * 
	 * @return The event time.
	 */
	public Calendar getTime() {
		return (Calendar) time.clone();
	}

	/**
	 * Sets the time of the event.
	 * 
	 * @param time
	 *            The time of the event.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public void setTime(Calendar time) {
		if (time == null) {
			throw new NullPointerException();
		}

		this.time = (Calendar) time.clone();
	}
}
