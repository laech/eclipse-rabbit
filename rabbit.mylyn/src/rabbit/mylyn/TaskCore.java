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
package rabbit.mylyn;

import rabbit.data.access.IAccessor;
import rabbit.data.store.IStorer;
import rabbit.mylyn.events.TaskFileEvent;
import rabbit.mylyn.internal.storage.xml.TaskDataAccessor;
import rabbit.mylyn.internal.storage.xml.TaskEventStorer;

/**TODO
 */
public class TaskCore {

  private static TaskDataAccessor accessor;

  static {
    accessor = new TaskDataAccessor();
  }

  /**
   * Gets an accessor to get the data stored.
   * 
   * @return An accessor to get the data stored.
   */
  public static IAccessor<TaskFileDataDescriptor> getTaskDataAccessor() {
    return accessor;
  }

  /**
   * Gets an {@code IStorer} that stores {@linkplain TaskFileEvent}.
   * 
   * @return The storer.
   */
  public static IStorer<TaskFileEvent> getTaskEventStorer() {
    return TaskEventStorer.getInstance();
  }

  private TaskCore() {
  }
}
