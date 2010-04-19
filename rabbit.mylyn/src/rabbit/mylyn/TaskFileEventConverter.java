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
package rabbit.mylyn;

import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.convert.AbstractConverter;
import rabbit.data.internal.xml.schema.events.TaskFileEventType;
import rabbit.data.internal.xml.schema.events.TaskIdType;
import rabbit.mylyn.events.TaskFileEvent;

import java.util.GregorianCalendar;

/**
 * Converter for {@link TaskFileEvent}
 */
public class TaskFileEventConverter extends
    AbstractConverter<TaskFileEvent, TaskFileEventType> {

  @Override
  protected TaskFileEventType doConvert(TaskFileEvent event) {
    GregorianCalendar creationDate = new GregorianCalendar();
    creationDate.setTime(event.getTask().getCreationDate());

    TaskIdType id = new TaskIdType();
    id.setCreationDate(DatatypeUtil.toXmlDateTime(creationDate));
    id.setHandleId(event.getTask().getHandleIdentifier());

    TaskFileEventType type = new TaskFileEventType();
    type.setDuration(event.getDuration());
    type.setFileId(event.getFileId());
    type.setTaskId(id);
    return type;
  }

}
