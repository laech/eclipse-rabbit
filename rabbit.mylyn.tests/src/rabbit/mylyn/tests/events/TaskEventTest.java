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
package rabbit.mylyn.tests.events;

import rabbit.mylyn.events.TaskEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.junit.Test;

import java.util.Calendar;

/**
 * @see TaskEvent
 */
@SuppressWarnings("restriction")
public class TaskEventTest {

	@Test(expected = NullPointerException.class)
	public void testConstructor_taskNull() {
		new TaskEvent(Calendar.getInstance(), 1, "Abc", null);
	}

	@Test
	public void testGetTask() {
		ITask task = new LocalTask("abc", "def");
		assertEquals(task, new TaskEvent(
				Calendar.getInstance(), 1, "abcd", task).getTask());
	}

	@Test
	public void testSetTask() {
		ITask task = new LocalTask("abc", "def");
		TaskEvent event = new TaskEvent(Calendar.getInstance(), 1, "abcd", task);

		task = new LocalTask("124", "567");
		event.setTask(task);
		assertEquals(task, event.getTask());
	}

	@Test(expected = NullPointerException.class)
	public void testSetTask_null() {
		ITask task = new LocalTask("abc", "def");
		new TaskEvent(Calendar.getInstance(), 1, "abcd", task).setTask(null);
	}
}
