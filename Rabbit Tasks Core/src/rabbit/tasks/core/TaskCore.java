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
package rabbit.tasks.core;

import java.util.Map;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.IStorer;
import rabbit.tasks.core.events.TaskEvent;
import rabbit.tasks.core.internal.storage.xml.TaskDataAccessor;
import rabbit.tasks.core.internal.storage.xml.TaskEventStorer;

/**
 */
public class TaskCore {

	private static TaskDataAccessor accessor;

	static {
		accessor = new TaskDataAccessor();
	}

	/**
	 * Gets an accessor to get the data stored.
	 * 
	 * @return An accessor to get the data stored.
	 */
	public static IAccessor<Map<TaskId, Map<String, Long>>> getTaskDataAccessor() {
		return accessor;
	}

	/**
	 * Gets an {@code IStorer} that stores {@linkplain TaskEvent}.
	 * 
	 * @return The storer.
	 */
	public static IStorer<TaskEvent> getTaskEventStorer() {
		return TaskEventStorer.getInstance();
	}

	private TaskCore() {
	}
}
