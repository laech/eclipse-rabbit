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
package rabbit.mylyn.events;

import rabbit.data.store.model.FileEvent;

import org.eclipse.mylyn.tasks.core.ITask;

import java.util.Calendar;

/**
 * Represents a task event.
 */
public class TaskEvent extends FileEvent {

	private ITask task;

	/**
	 * Constructs a new event.
	 * 
	 * @param time
	 *            The end time of the event.
	 * @param duration
	 *            The duration of the event, in milliseconds.
	 * @param fileId
	 *            The id of the file.
	 * @param task
	 *            The task.
	 * @throws IllegalArgumentException
	 *             If duration is negative, or file Id is an empty string or
	 *             contains whitespace only.
	 * @throws NullPointerException
	 *             If time is null, or file id is null, or task is null.
	 */
	public TaskEvent(Calendar time, long duration, String fileId, ITask task) {
		super(time, duration, fileId);
		setTask(task);
	}

	/**
	 * Gets the task.
	 * 
	 * @return The task.
	 */
	public ITask getTask() {
		return task;
	}

	/**
	 * Sets the task.
	 * 
	 * @param task
	 *            The task.
	 * @throws NullPointerException
	 *             If task is null.
	 */
	public void setTask(ITask task) {
		if (task == null) {
			throw new NullPointerException();
		}
		this.task = task;
	}

}
