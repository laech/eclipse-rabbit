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

import rabbit.data.internal.xml.AbstractAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.TaskEventListType;
import rabbit.data.internal.xml.schema.events.TaskEventType;
import rabbit.mylyn.TaskId;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TaskDataAccessor
    extends
    AbstractAccessor<Map<TaskId, Map<String, Long>>, TaskEventType, TaskEventListType> {

  @Override
  protected Map<TaskId, Map<String, Long>> filter(List<TaskEventListType> data) {
    Map<TaskId, Map<String, Long>> result = new HashMap<TaskId, Map<String, Long>>();
    for (TaskEventListType list : data) {
      for (TaskEventType type : list.getTaskEvent()) {

        String handleId = type.getTaskId().getHandleId();
        Date creationDate = type.getTaskId().getCreationDate()
            .toGregorianCalendar().getTime();

        TaskId id = null;
        try {
          id = new TaskId(handleId, creationDate);
        } catch (IllegalArgumentException e) {
          continue; // Ignore, due to invalid XML entries?
        } catch (NullPointerException e) {
          continue; // Ignore, due to invalid XML entries?
        }

        Map<String, Long> map = result.get(id);
        if (map == null) {
          map = new HashMap<String, Long>();
          result.put(id, map);
        }

        Long value = map.get(type.getFileId());
        if (value == null) {
          value = 0L;
        }
        map.put(type.getFileId(), type.getDuration() + value);
      }
    }
    return result;
  }

  @Override
  protected Collection<TaskEventListType> getCategories(EventListType doc) {
    return doc.getTaskEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.TASK_STORE;
  }
}
