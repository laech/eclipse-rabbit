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
package rabbit.data.internal.xml.convert;

import rabbit.data.internal.xml.convert.TaskFileEventConverter;
import rabbit.data.internal.xml.schema.events.TaskFileEventType;
import rabbit.data.store.model.TaskFileEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.joda.time.Interval;

import java.util.Date;

/**
 * @see TaskFileEventConverter
 */
@SuppressWarnings("restriction")
public class TaskFileEventConverterTest extends 
    AbstractConverterTest<TaskFileEvent, TaskFileEventType> {

  @Override
  protected TaskFileEventConverter createConverter() {
    return new TaskFileEventConverter();
  }

  @Override
  public void testConvert() throws Exception {
    ITask task = new LocalTask("abc", "a task");
    task.setCreationDate(new Date());
    TaskFileEvent event = new TaskFileEvent(new Interval(0, 1), new Path("/a/b"), task);
    TaskFileEventType type = converter.convert(event);
    
    assertEquals(task.getHandleIdentifier(), type.getTaskId().getHandleId());
    assertEquals(task.getCreationDate(), type.getTaskId().getCreationDate().toGregorianCalendar().getTime());
    assertEquals(event.getInterval().toDurationMillis(), type.getDuration());
    assertEquals(event.getFilePath().toString(), type.getFilePath());
  }

}
