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

import org.eclipse.ui.IWorkbenchPart;

/**
 * Represents a workbench part event.
 */
public class PartEvent extends ContinuousEvent {

	private IWorkbenchPart workbenchPart;

	/**
	 * Constructs a new event.
	 * 
	 * @param time
	 *            The end time of the event.
	 * @param duration
	 *            The duration of the event, in milliseconds.
	 * @param part
	 *            The workbench part.
	 */
	public PartEvent(Calendar time, long duration, IWorkbenchPart part) {
		super(time, duration);
		setWorkbenchPart(part);
	}

	/**
	 * Gets the workbench part.
	 * 
	 * @return The workbench part.
	 */
	public IWorkbenchPart getWorkbenchPart() {
		return workbenchPart;
	}

	/**
	 * Sets the workbench part.
	 * 
	 * @param workbenchPart
	 *            The workbench part.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public void setWorkbenchPart(IWorkbenchPart workbenchPart) {
		if (workbenchPart == null) {
			throw new NullPointerException();
		}
		this.workbenchPart = workbenchPart;
	}
}
