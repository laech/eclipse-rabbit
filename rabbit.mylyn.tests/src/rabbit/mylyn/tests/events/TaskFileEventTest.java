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
package rabbit.mylyn.tests.events;

import rabbit.data.store.model.FileEvent;
import rabbit.data.test.store.model.FileEventTest;
import rabbit.mylyn.events.TaskFileEvent;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.joda.time.DateTime;
import org.junit.Test;

/**
 * @see TaskFileEvent
 */
@SuppressWarnings("restriction")
public class TaskFileEventTest extends FileEventTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_taskNull() {
    new TaskFileEvent(new DateTime(), 1, "Abc", null);
  }

  @Test
  public void testGetTask() {
    ITask task = new LocalTask("abc", "def");
    assertEquals(task, new TaskFileEvent(new DateTime(), 1, "abcd", task).getTask());
  }
  
  @Override
  protected final FileEvent createEvent(DateTime time, long duration, String fileId) {
    return createEvent(time, duration, fileId, new LocalTask("a", "1"));
  }
  
  /**
   * @see TaskFileEvent#TaskEvent(DateTime, long, String, ITask)
   */
  protected TaskFileEvent createEvent(DateTime time, long duration, String fileId, ITask task) {
    return new TaskFileEvent(time, duration, fileId, task);
  }
}
