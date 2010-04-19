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

import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.TaskFileEventListType;
import rabbit.data.internal.xml.schema.events.TaskFileEventType;
import rabbit.mylyn.TaskFileDataDescriptor;
import rabbit.mylyn.TaskFileEventTypeMerger;
import rabbit.mylyn.TaskId;

import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Gets task data.
 */
public class TaskDataAccessor extends
    AbstractDataNodeAccessor<TaskFileDataDescriptor, TaskFileEventType, TaskFileEventListType> {

  @Override
  protected Collection<TaskFileEventListType> getCategories(EventListType doc) {
    return doc.getTaskFileEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.TASK_STORE;
  }

  @Override
  protected TaskFileDataDescriptor createDataNode(LocalDate cal, TaskFileEventType type) {
    try {
      TaskId id = new TaskId(type.getTaskId().getHandleId(), 
          type.getTaskId().getCreationDate().toGregorianCalendar().getTime());
      return new TaskFileDataDescriptor(cal, type.getDuration(), type.getFileId(), id);
      
    } catch (NullPointerException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  protected IMerger<TaskFileEventType> createMerger() {
    return new TaskFileEventTypeMerger();
  }

  @Override
  protected Collection<TaskFileEventType> getElements(TaskFileEventListType category) {
    return category.getTaskFileEvent();
  }
}
