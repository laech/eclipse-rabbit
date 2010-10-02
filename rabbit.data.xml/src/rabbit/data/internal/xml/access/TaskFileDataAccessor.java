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
package rabbit.data.internal.xml.access;

import rabbit.data.access.model.TaskFileDataDescriptor;
import rabbit.data.common.TaskId;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.TaskFileEventListType;
import rabbit.data.internal.xml.schema.events.TaskFileEventType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Gets task data.
 */
public class TaskFileDataAccessor extends
    AbstractDataNodeAccessor<TaskFileDataDescriptor, 
                             TaskFileEventType, 
                             TaskFileEventListType> {

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @param merger The merger for merging XML data nodes.
   * @throws NullPointerException If any arguments are null.
   */
  @Inject
  TaskFileDataAccessor(
      @Named(StoreNames.TASK_STORE) IDataStore store,
      IMerger<TaskFileEventType> merger) {
    super(store, merger);
  }

  @Override
  protected Collection<TaskFileEventListType> getCategories(EventListType doc) {
    return doc.getTaskFileEvents();
  }

  @Override
  protected TaskFileDataDescriptor createDataNode(LocalDate cal,
      TaskFileEventType type) {
    try {
      TaskId id = new TaskId(type.getTaskId().getHandleId(),
          type.getTaskId().getCreationDate().toGregorianCalendar().getTime());
      return new TaskFileDataDescriptor(cal,new Duration(type.getDuration()), 
          new Path(type.getFilePath()), id);

    } catch (Exception e) {
      return null;
    }
  }

  @Override
  protected Collection<TaskFileEventType> getElements(
      TaskFileEventListType category) {
    return category.getTaskFileEvent();
  }
}
