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
package rabbit.tasks.ui.internal.pages;

import org.eclipse.core.resources.IResource;
import org.eclipse.mylyn.tasks.core.ITask;

/**
 * Represents a resource associated with a task.
 */
public final class TaskResource {

	public final ITask task;
	public final IResource resource;

	/**
	 * Constructs a new task resource association.
	 * 
	 * @param task
	 *            The task.
	 * @param resource
	 *            The resource.
	 */
	public TaskResource(ITask task, IResource resource) {
		if (task == null || resource == null) {
			throw new NullPointerException();
		}
		this.task = task;
		this.resource = resource;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TaskResource) {
			TaskResource element = (TaskResource) obj;
			return resource.equals(element.resource)
					&& task.equals(element.task);
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (task.getHandleIdentifier() + resource.getFullPath().toString())
				.hashCode();
	}

	@Override
	public String toString() {
		return "Task: " + task.getSummary()
				+ ", resource: " + resource.getFullPath().toString();
	}
}
