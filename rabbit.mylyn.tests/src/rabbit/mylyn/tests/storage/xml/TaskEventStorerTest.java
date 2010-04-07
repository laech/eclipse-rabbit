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
package rabbit.mylyn.tests.storage.xml;

import rabbit.data.internal.xml.schema.events.TaskEventListType;
import rabbit.data.internal.xml.schema.events.TaskEventType;
import rabbit.data.test.xml.AbstractStorerTest;
import rabbit.mylyn.events.TaskEvent;
import rabbit.mylyn.internal.storage.xml.TaskEventStorer;

import com.google.common.base.Objects;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.joda.time.DateTime;

/**
 * @see TaskEventStorer
 */
@SuppressWarnings("restriction")
public class TaskEventStorerTest extends
    AbstractStorerTest<TaskEvent, TaskEventType, TaskEventListType> {

  @Override
  protected TaskEventStorer createStorer() {
    return TaskEventStorer.getInstance();
  }

  @Override
  protected TaskEvent createEventDiff(DateTime dateTime) {
    LocalTask task = new LocalTask("taskId", "what?");
    task.setCreationDate(dateTime.toDate());
    return new TaskEvent(dateTime, 187, "fileId", task);
  }

  @Override
  protected TaskEvent createEvent(DateTime dateTime) {
    LocalTask task = new LocalTask("tas1kId", "what?1");
    task.setCreationDate(dateTime.toDate());
    return new TaskEvent(dateTime, 1187, "fileId", task);
  }

  @Override
  protected boolean equal(TaskEventType t1, TaskEventType t2) {
    return t1.getDuration() == t2.getDuration()
        && Objects.equal(t1.getFileId(), t2.getFileId())
        && Objects.equal(t1.getTaskId().getHandleId(), t2.getTaskId()
            .getHandleId())
        && Objects.equal(t1.getTaskId().getCreationDate(), t2.getTaskId()
            .getCreationDate());
  }
}
