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

import org.eclipse.ui.IPerspectiveDescriptor;

/**
 * Represents a perspective event.
 */
public class PerspectiveEvent extends ContinuousEvent {

	private IPerspectiveDescriptor perspective;

	/**
	 * Constructs a perspective event.
	 * 
	 * @param time
	 *            The end time of the event.
	 * @param duration
	 *            The duration in milliseconds.
	 * @param perspective
	 *            The perspective.
	 * @throws NullPointerException
	 *             If time is null or perspective is null.
	 * @throws IllegalArgumentException
	 *             If duration is negative.
	 */
	public PerspectiveEvent(Calendar time, long duration, IPerspectiveDescriptor perspective) {
		super(time, duration);

		if (perspective == null) {
			throw new NullPointerException();
		}
		this.perspective = perspective;
	}

	/**
	 * Gets the perspective.
	 * 
	 * @return The perspective.
	 */
	public IPerspectiveDescriptor getPerspective() {
		return perspective;
	}
}
