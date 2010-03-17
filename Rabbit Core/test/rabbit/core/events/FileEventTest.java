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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;

import java.util.Calendar;

import org.eclipse.mylyn.tasks.core.ITask;
import org.junit.Test;

/**
 * @see FileEvent
 */
public class FileEventTest extends ContinuousEventTest {

	@Test(expected = NullPointerException.class)
	public void testContructor_fileIdNull() {
		new FileEvent(Calendar.getInstance(), 10, null);
	}

	@Test
	public void testGetFileId() {
		String fileId = System.currentTimeMillis() + "";
		FileEvent event = new FileEvent(Calendar.getInstance(), 10, fileId);
		assertEquals(fileId, event.getFileId());
	}

	@Test
	public void testGetTask() {
		FileEvent event = new FileEvent(Calendar.getInstance(), 1, "");
		assertNull(event.getTask());
	}

	@Test
	public void testSetFileId() {
		String fileId = System.nanoTime() + "";
		FileEvent event = new FileEvent(Calendar.getInstance(), 10, fileId);
		assertEquals(fileId, event.getFileId());

		fileId = "helloWorld";
		event.setFileId(fileId);
		assertEquals(fileId, event.getFileId());
	}

	@Test(expected = NullPointerException.class)
	public void testSetFileId_null() {
		FileEvent event = new FileEvent(Calendar.getInstance(), 1, "abc");
		event.setFileId(null);
	}

	@SuppressWarnings("restriction")
	@Test
	public void testSetTask() {
		FileEvent event = new FileEvent(Calendar.getInstance(), 2, "1");

		ITask task = new org.eclipse.mylyn.internal.tasks.core
				.LocalTask("repo", "task");
		event.setTask(task);
		assertSame(task, event.getTask());
	}

	@Override
	protected FileEvent createEvent(Calendar time, long duration) {
		return new FileEvent(time, duration, System.currentTimeMillis() + "");
	}
}
