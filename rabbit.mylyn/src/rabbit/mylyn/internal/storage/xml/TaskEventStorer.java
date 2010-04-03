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
package rabbit.mylyn.internal.storage.xml;

import rabbit.data.internal.xml.AbstractContinuousEventStorer;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.TaskEventListType;
import rabbit.data.internal.xml.schema.events.TaskEventType;
import rabbit.data.internal.xml.schema.events.TaskIdType;
import rabbit.mylyn.events.TaskEvent;

import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link TaskEvent}.
 */
public final class TaskEventStorer extends
    AbstractContinuousEventStorer<TaskEvent, TaskEventType, TaskEventListType> {

  private static final TaskEventStorer INSTANCE = new TaskEventStorer();

  /**
   * Gets the shared instance of this class.
   * 
   * @return The shared instance.
   */
  public static TaskEventStorer getInstance() {
    return INSTANCE;
  }

  private TaskEventStorer() {
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.TASK_STORE;
  }

  @Override
  protected List<TaskEventListType> getXmlTypeCategories(EventListType events) {
    return events.getTaskEvents();
  }

  @Override
  protected List<TaskEventType> getXmlTypes(TaskEventListType list) {
    return list.getTaskEvent();
  }

  @Override
  protected boolean hasSameId(TaskEventType x1, TaskEventType x2) {
    return x1.getFileId().equals(x2.getFileId())
        && x1.getTaskId().getHandleId().equals(x2.getTaskId().getHandleId())
        && x1.getTaskId().getCreationDate().equals(
            x2.getTaskId().getCreationDate());
  }

  @Override
  protected TaskEventType newXmlType(TaskEvent e) {
    GregorianCalendar creationDate = new GregorianCalendar();
    creationDate.setTime(e.getTask().getCreationDate());

    TaskIdType id = objectFactory.createTaskIdType();
    id.setCreationDate(DatatypeUtil
        .toXMLGregorianCalendarDateTime(creationDate));
    id.setHandleId(e.getTask().getHandleIdentifier());

    TaskEventType type = objectFactory.createTaskEventType();
    type.setDuration(e.getDuration());
    type.setFileId(e.getFileId());
    type.setTaskId(id);
    return type;
  }

  @Override
  protected TaskEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
    TaskEventListType type = objectFactory.createTaskEventListType();
    type.setDate(date);
    return type;
  }

}
