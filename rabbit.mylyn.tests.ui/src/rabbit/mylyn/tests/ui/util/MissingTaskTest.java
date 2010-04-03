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
package rabbit.mylyn.tests.ui.util;

import rabbit.mylyn.TaskId;
import rabbit.mylyn.internal.ui.util.MissingTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.util.Date;

/**
 * @see MissingTask
 */
public class MissingTaskTest {

  @Test
  public void testEquals() {
    TaskId id1 = new TaskId("id", new Date());
    MissingTask task1 = new MissingTask(id1);
    assertFalse(task1.equals(null));
    assertTrue(task1.equals(task1));

    TaskId id2 = new TaskId("abc", new Date());
    MissingTask task2 = new MissingTask(id2);
    assertFalse(task1.equals(task2));

    id2 = new TaskId(id1.getHandleIdentifier(), new Date(id1.getCreationDate()
        .getTime() - 1000)); // 1 second difference
    task2 = new MissingTask(id2);
    assertFalse(task1.equals(task2));

    id2 = new TaskId("133444459", id1.getCreationDate());
    task2 = new MissingTask(id2);
    assertFalse(task1.equals(task2));

    id2 = new TaskId(id1.getHandleIdentifier(), id1.getCreationDate());
    task2 = new MissingTask(id2);
    assertTrue(task1.equals(task2));
  }

  @Test
  public void testGetCreationDate() {
    TaskId id = new TaskId("id", new Date());
    MissingTask task = new MissingTask(id);
    assertEquals(id.getCreationDate(), task.getCreationDate());
  }

  @Test
  public void testGetHandleIdentifier() {
    TaskId id = new TaskId("id", new Date());
    MissingTask task = new MissingTask(id);
    assertEquals(id.getHandleIdentifier(), task.getHandleIdentifier());
  }

  @Test
  public void testHashCode() {
    TaskId id = new TaskId("id", new Date());
    MissingTask task = new MissingTask(id);
    assertEquals(id.hashCode(), task.hashCode());
  }

}
