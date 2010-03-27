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
package rabbit.tasks.core.internal.storage.xml;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static rabbit.core.internal.storage.xml.DatatypeUtil.toXMLGregorianCalendarDateTime;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;

import rabbit.core.internal.storage.xml.AbstractStorerTest2;
import rabbit.core.internal.storage.xml.schema.events.TaskEventListType;
import rabbit.core.internal.storage.xml.schema.events.TaskEventType;
import rabbit.core.internal.storage.xml.schema.events.TaskIdType;
import rabbit.tasks.core.events.TaskEvent;

/**
 * @see TaskEventStorer
 */
@SuppressWarnings("restriction")
public class TaskEventStorerTest extends
		AbstractStorerTest2<TaskEvent, TaskEventType, TaskEventListType> {

	@Override
	protected List<TaskEventType> getEventTypes(TaskEventListType type) {
		return type.getTaskEvent();
	}

	@Override
	protected long getValue(TaskEvent event) {
		return event.getDuration();
	}

	@Override
	protected long getValue(TaskEventType type) {
		return type.getDuration();
	}

	@Override
	protected boolean isEqual(TaskEventType type, TaskEvent event) {
		GregorianCalendar creationDate = new GregorianCalendar();
		creationDate.setTime(event.getTask().getCreationDate());
		return type.getDuration() == event.getDuration()
				&& type.getFileId().equals(event.getFileId())
				&& type.getTaskId().getHandleId().equals(event.getTask().getHandleIdentifier())
				&& type.getTaskId().getCreationDate().equals(
						toXMLGregorianCalendarDateTime(creationDate));
	}

	@Override
	protected void mergeValue(TaskEvent main, TaskEvent tmp) {
		main.setDuration(main.getDuration() + tmp.getDuration());
	}

	@Override
	protected TaskEventStorer create() {
		return TaskEventStorer.getInstance();
	}

	@Override
	protected TaskEvent createEvent() {
		LocalTask task = new LocalTask("taskId", "what?");
		task.setCreationDate(new Date());
		return new TaskEvent(Calendar.getInstance(), 187, "fileId", task);
	}

	@Override
	protected TaskEvent createEvent2() {
		LocalTask task = new LocalTask("tttttt", "22222222");
		task.setCreationDate(new Date());
		return new TaskEvent(Calendar.getInstance(), 233, "bbbbbbb", task);
	}

	@Override
	public void testHasSameId_typeAndEvent() {
		TaskEventStorer storer = create();

		TaskEvent event = createEvent();
		TaskEventType type = createFrom(event);
		assertTrue(storer.hasSameId(type, event));

		type.setFileId("adfnckuhq397y398rhfsadf");
		assertFalse(storer.hasSameId(type, event));

		// /
		type = createFrom(event);
		assertTrue(storer.hasSameId(type, event));
		type.getTaskId().setCreationDate(
				toXMLGregorianCalendarDateTime(new GregorianCalendar(1839, 1, 2)));
		assertFalse(storer.hasSameId(type, event));

		// /
		type = createFrom(event);
		assertTrue(storer.hasSameId(type, event));
		type.getTaskId().setHandleId("nvkuh398fnkxdhdskfhafaf");
		assertFalse(storer.hasSameId(type, event));
	}

	protected TaskEventType createFrom(TaskEvent event) {
		GregorianCalendar creationDate = new GregorianCalendar();
		creationDate.setTimeInMillis(event.getTask().getCreationDate().getTime());
		TaskIdType id = objectFactory.createTaskIdType();
		id.setCreationDate(toXMLGregorianCalendarDateTime(creationDate));
		id.setHandleId(event.getTask().getHandleIdentifier());

		TaskEventType type = objectFactory.createTaskEventType();
		type.setDuration(event.getDuration());
		type.setFileId(event.getFileId());
		type.setTaskId(id);
		return type;
	}

	@Override
	public void testHasSameId_typeAndType() {
		TaskEventStorer storer = create();

		TaskEvent event = createEvent();
		TaskEventType type1 = createFrom(event);
		TaskEventType type2 = createFrom(event);
		assertTrue(storer.hasSameId(type1, type2));

		type1.setFileId("adfnckuhq397y398rhfsadf");
		assertFalse(storer.hasSameId(type1, type2));

		// /
		type1 = createFrom(event);
		type2 = createFrom(event);
		assertTrue(storer.hasSameId(type1, type2));
		type1.getTaskId().setHandleId("ajnvhe2");
		assertFalse(storer.hasSameId(type1, type2));

		// /
		type1 = createFrom(event);
		type2 = createFrom(event);
		assertTrue(storer.hasSameId(type1, type2));
		type1.getTaskId().setCreationDate(
				toXMLGregorianCalendarDateTime(new GregorianCalendar(1999, 1, 1)));
		assertFalse(storer.hasSameId(type1, type2));
	}

}
