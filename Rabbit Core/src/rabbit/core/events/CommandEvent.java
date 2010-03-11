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

import org.eclipse.core.commands.ExecutionEvent;

/**
 * A command execution event.
 */
public class CommandEvent extends DiscreteEvent {

	private ExecutionEvent event;

	/**
	 * Constructs a new event.
	 * 
	 * @param time
	 *            The time of the event.
	 * @param e
	 *            The execution event.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public CommandEvent(Calendar time, ExecutionEvent e) {
		super(time);
		setExecutionEvent(e);
	}

	/**
	 * Gets the execution event.
	 * 
	 * @return The execution event.
	 */
	public ExecutionEvent getExecutionEvent() {
		return event;
	}

	/**
	 * Sets the execution event.
	 * 
	 * @param event
	 *            The execution event.
	 * @throws NullPointerException
	 *             If argument is null.
	 */
	public void setExecutionEvent(ExecutionEvent event) {
		if (event == null) {
			throw new NullPointerException();
		}
		this.event = event;
	}

}
