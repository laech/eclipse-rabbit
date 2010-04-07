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

import rabbit.data.internal.xml.AbstractStorer;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.convert.IConverter;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.TaskEventListType;
import rabbit.data.internal.xml.schema.events.TaskEventType;
import rabbit.mylyn.TaskEventConverter;
import rabbit.mylyn.TaskEventTypeMerger;
import rabbit.mylyn.events.TaskEvent;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link TaskEvent}.
 */
public final class TaskEventStorer extends
    AbstractStorer<TaskEvent, TaskEventType, TaskEventListType> {

  private static final TaskEventStorer INSTANCE = new TaskEventStorer();

  /**
   * Gets the shared instance of this class.
   * 
   * @return The shared instance.
   */
  public static TaskEventStorer getInstance() {
    return INSTANCE;
  }
  
  @Nonnull
  private final IMerger<TaskEventType> merger;
  @Nonnull
  private final IConverter<TaskEvent, TaskEventType> converter;

  private TaskEventStorer() {
    merger = new TaskEventTypeMerger();
    converter = new TaskEventConverter();
  }

  @Override
  protected List<TaskEventListType> getCategories(EventListType events) {
    return events.getTaskEvents();
  }

  @Override
  protected IConverter<TaskEvent, TaskEventType> getConverter() {
    return converter;
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.TASK_STORE;
  }

  @Override
  protected List<TaskEventType> getElements(TaskEventListType list) {
    return list.getTaskEvent();
  }

  @Override
  protected IMerger<TaskEventType> getMerger() {
    return merger;
  }

  @Override
  protected TaskEventListType newCategory(XMLGregorianCalendar date) {
    TaskEventListType type = objectFactory.createTaskEventListType();
    type.setDate(date);
    return type;
  }

}
