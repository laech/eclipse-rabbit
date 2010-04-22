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

import com.google.common.base.Objects;

import org.joda.time.LocalDate;

import javax.annotation.Nonnull;

/**
 * Data descriptor for a task file event.
 */
public class TaskFileDataDescriptor extends FileDataDescriptor {
  
  @Nonnull
  private final TaskId taskId;

  /**
   * Constructs a new data descriptor.
   * 
   * @param date The date of the data.
   * @param value The duration of the event, in milliseconds.
   * @param fileId The ID of the file.
   * @param taskId The ID of the task.
   * @throws NullPointerException If date is null, or fileId is null, or taskId
   *           is null.
   * @throws IllegalArgumentException If value < 0;
   */
  public TaskFileDataDescriptor(@Nonnull LocalDate date, 
                                         long value, 
                                @Nonnull String fileId, 
                                @Nonnull TaskId taskId) {
    super(date, value, fileId);
    checkNotNull(taskId, "Task ID cannot be null");
    this.taskId = taskId;
  }
  
  @Override
  public int hashCode() {
    return Objects.hashCode(getDate(), getFileId(), getTaskId());
  }
  
  @Override
  public boolean equals(Object obj) {
    if (obj == null) return false;
    if (obj == this) return true;
    if (obj.getClass() != getClass()) return false;
    
    TaskFileDataDescriptor des = (TaskFileDataDescriptor) obj;
    return des.getDate().equals(getDate())
        && des.getFileId().equals(getFileId())
        && des.getTaskId().equals(getTaskId())
        && des.getValue() == getValue();
  }

  /**
   * Gets the ID of the task.
   * @return The task ID.
   */
  @Nonnull
  public TaskId getTaskId() {
    return taskId;
  }
}
