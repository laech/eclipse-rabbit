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
package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.eclipse.mylyn.tasks.core.ITask;

import rabbit.core.events.FileEvent;
import rabbit.core.internal.storage.xml.schema.events.FileEventListType;
import rabbit.core.internal.storage.xml.schema.events.FileEventType;

public class FileEventStorerTest
		extends AbstractStorerTest2<FileEvent, FileEventType, FileEventListType> {

	@SuppressWarnings("restriction")
	@Override
	public void testHasSameId_typeAndEvent() {
		String id = "asdfsdf23";
		FileEventType type = objectFactory.createFileEventType();
		type.setFileId(id);
		FileEvent event = new FileEvent(Calendar.getInstance(), 10, id);
		assertTrue(storer.hasSameId(type, event));

		event.setFileId(id + id);
		assertFalse(storer.hasSameId(type, event));

		event.setFileId(id);
		assertTrue(storer.hasSameId(type, event));

		// Test task:
		ITask task = new org.eclipse.mylyn.internal.tasks.core.LocalTask("1", "hi");
		event.setTask(task);
		assertFalse(storer.hasSameId(type, event));

		type.setTaskHandleId(task.getHandleIdentifier());
		assertTrue(storer.hasSameId(type, event));
		
		type.setTaskHandleId(System.currentTimeMillis() + "");
		assertFalse(storer.hasSameId(type, event));
	}

	@Override
	public void testHasSameId_typeAndType() {
		String id = "asdfsdf23";
		FileEventType type1 = objectFactory.createFileEventType();
		type1.setFileId(id);
		FileEventType type2 = objectFactory.createFileEventType();
		type2.setFileId(id);
		assertTrue(storer.hasSameId(type1, type2));

		
		String taskHandle = "aknudy2765652tgdhd";
		type1.setTaskHandleId(taskHandle);
		assertFalse(storer.hasSameId(type1, type2));

		type2.setTaskHandleId(taskHandle);
		assertTrue(storer.hasSameId(type1, type2));

		type1.setTaskHandleId(System.nanoTime() + "");
		assertFalse(storer.hasSameId(type1, type2));
	}

	@Override
	protected FileEventStorer create() {
		return new FileEventStorer();
	}

	@SuppressWarnings("restriction")
	@Override
	protected FileEvent createEvent() {
		FileEvent event = new FileEvent(Calendar.getInstance(), 10, "someId");
		event.setTask(new org.eclipse.mylyn.internal.tasks.core.LocalTask(
				System.currentTimeMillis()+"", System.nanoTime()+""));
		return event;
	}

	@SuppressWarnings("restriction")
	@Override
	protected FileEvent createEvent2() {
		FileEvent event = new FileEvent(Calendar.getInstance(), 110, "blah");
		event.setTask(new org.eclipse.mylyn.internal.tasks.core.LocalTask(
				System.currentTimeMillis()+"", System.nanoTime()+""));
		return event;
	}

	@Override
	protected List<FileEventType> getEventTypes(FileEventListType type) {
		return type.getFileEvent();
	}

	@Override
	protected long getValue(FileEvent event) {
		return event.getDuration();
	}

	@Override
	protected long getValue(FileEventType type) {
		return type.getDuration();
	}

	@Override
	protected boolean isEqual(FileEventType type, FileEvent event) {
		boolean isEqual = type.getFileId().equals(event.getFileId());
		isEqual &= (type.getDuration() == event.getDuration());

		if (type.getTaskHandleId() != null && event.getTask() != null) {
			isEqual &= type.getTaskHandleId().equals(event.getTask().getHandleIdentifier());

		} else if (type.getTaskHandleId() == null && event.getTask() == null) {
			isEqual &= true;

		} else {
			isEqual = false;
		}

		return isEqual;
	}

	@Override
	protected void mergeValue(FileEvent main, FileEvent tmp) {
		main.setDuration(tmp.getDuration() + main.getDuration());
	}

}
