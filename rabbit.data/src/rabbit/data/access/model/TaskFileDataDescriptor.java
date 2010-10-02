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
package rabbit.data.access.model;

import rabbit.data.common.TaskId;

import static com.google.common.base.Preconditions.checkNotNull;

import org.eclipse.core.runtime.IPath;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.joda.time.Duration;
import org.joda.time.LocalDate;

/**
 * Data descriptor for a task file event.
 */
public class TaskFileDataDescriptor extends FileDataDescriptor {
  
  private final TaskId taskId;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param duration The duration.
   * @param filePath The path of the file.
   * @param taskId The ID of the task.
   * @throws NullPointerException If any of the arguments are null.
   */
  public TaskFileDataDescriptor(LocalDate date, 
                                Duration duration, 
                                IPath filePath, 
                                TaskId taskId) {
    super(date, duration, filePath);
    this.taskId = checkNotNull(taskId);
  }
  
  /**
   * Finds the task that has the same handle identifier as
   * {@link TaskId#getHandleIdentifier()} and has the same creation date as
   * {@link TaskId#getCreationDate()}.
   * 
   * @return The task, or null if not found.
   * @see #getTaskId()
   */
  public final ITask findTask() {
    ITask task = TasksUi.getRepositoryModel().getTask(getTaskId().getHandleIdentifier());
    if (task != null && !getTaskId().getCreationDate().equals(task.getCreationDate())) {
      task = null;
    }
    return task;
  }

  /**
   * Gets the ID of the task.
   * @return The task ID.
   */
  public final TaskId getTaskId() {
    return taskId;
  }
}
