/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package rabbit.mylyn.tests.internal.storage.xml;

import static rabbit.data.internal.xml.DatatypeUtil.*;
import rabbit.data.internal.xml.schema.events.TaskEventListType;
import rabbit.data.internal.xml.schema.events.TaskEventType;
import rabbit.data.internal.xml.schema.events.TaskIdType;
import rabbit.data.test.xml.AbstractContinuousEventStorerTest;
import rabbit.mylyn.events.TaskEvent;
import rabbit.mylyn.internal.storage.xml.TaskEventStorer;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @see TaskEventStorer
 */
@SuppressWarnings("restriction")
public class TaskEventStorerTest
    extends
    AbstractContinuousEventStorerTest<TaskEvent, TaskEventType, TaskEventListType> {

  @Override
  public void testHasSameId_typeAndType() throws Exception {
    TaskEventStorer storer = create();

    TaskEvent event = createEvent();
    TaskEventType type1 = createFrom(event);
    TaskEventType type2 = createFrom(event);
    assertTrue(hasSameId(storer, type1, type2));

    type1.setFileId("adfnckuhq397y398rhfsadf");
    assertFalse(hasSameId(storer, type1, type2));

    // /
    type1 = createFrom(event);
    type2 = createFrom(event);
    assertTrue(hasSameId(storer, type1, type2));
    type1.getTaskId().setHandleId("ajnvhe2");
    assertFalse(hasSameId(storer, type1, type2));

    // /
    type1 = createFrom(event);
    type2 = createFrom(event);
    assertTrue(hasSameId(storer, type1, type2));
    type1.getTaskId().setCreationDate(
        toXMLGregorianCalendarDateTime(new GregorianCalendar(1999, 1, 1)));
    assertFalse(hasSameId(storer, type1, type2));
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
  protected TaskEvent createEvent(Calendar eventTime) {
    LocalTask task = new LocalTask("tas1kId", "what?1");
    task.setCreationDate(new Date());
    return new TaskEvent(eventTime, 1187, "fileId", task);
  }

  @Override
  protected TaskEvent createEvent2() {
    LocalTask task = new LocalTask("tttttt", "22222222");
    task.setCreationDate(new Date());
    return new TaskEvent(Calendar.getInstance(), 233, "bbbbbbb", task);
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
  protected List<TaskEventType> getEventTypes(TaskEventListType type) {
    return type.getTaskEvent();
  }

  @Override
  protected boolean hasSameId(TaskEventType xml, TaskEvent e) {
    return xml.getFileId().equals(e.getFileId())
        && xml.getTaskId().getHandleId().equals(
            e.getTask().getHandleIdentifier())
        && xml.getTaskId().getCreationDate().toGregorianCalendar().getTime()
            .equals(e.getTask().getCreationDate());
  }

  @Override
  protected boolean isEqual(TaskEventType type, TaskEvent event) {
    GregorianCalendar creationDate = new GregorianCalendar();
    creationDate.setTime(event.getTask().getCreationDate());
    return type.getDuration() == event.getDuration()
        && type.getFileId().equals(event.getFileId())
        && type.getTaskId().getHandleId().equals(
            event.getTask().getHandleIdentifier())
        && type.getTaskId().getCreationDate().equals(
            toXMLGregorianCalendarDateTime(creationDate));
  }

  @Override
  protected TaskEvent mergeValue(TaskEvent main, TaskEvent tmp) {
    return new TaskEvent(main.getTime(),
        main.getDuration() + tmp.getDuration(), main.getFileId(), main
            .getTask());
  }

}
