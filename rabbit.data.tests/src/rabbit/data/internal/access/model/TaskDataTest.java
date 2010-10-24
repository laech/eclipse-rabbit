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
package rabbit.data.internal.access.model;

import rabbit.data.access.model.ITaskData;
import rabbit.data.access.model.WorkspaceStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

/**
 * @see TaskData
 */
@SuppressWarnings("restriction")
public class TaskDataTest {

  private ITask task;
  private IFile file;
  private LocalDate date;
  private Duration duration;
  private WorkspaceStorage workspace;

  @Before
  public void prepare() {
    task = new LocalTask("id", "summary");
    file = workspaceRoot().getFile(new Path("/project/file.txt"));
    date = new LocalDate();
    duration = new Duration(10);
    workspace = new WorkspaceStorage(new Path(""), new Path(""));
  }

  @Test
  public void shouldReturnTheDate() {
    assertThat(
        create(date, workspace, duration, file, task).get(ITaskData.DATE),
        is(date));
  }

  @Test
  public void shouldReturnTheDuration() {
    assertThat(
        create(date, workspace, duration, file, task).get(ITaskData.DURATION),
        is(duration));
  }

  @Test
  public void shouldReturnTheFile() {
    assertThat(
        create(date, workspace, duration, file, task).get(ITaskData.FILE),
        is(file));
  }

  @Test
  public void shouldReturnTheWorkspace() {
    assertThat(
        create(date, workspace, duration, file, task).get(ITaskData.WORKSPACE),
        is(workspace));
  }
  
  @Test
  public void shouldReturnTheTask() {
    assertThat(
        create(date, workspace, duration, file, task).get(ITaskData.TASK),
        is(task));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, duration, file, task);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    create(date, workspace, null, file, task);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAFile() {
    create(date, workspace, duration, null, task);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, duration, file, task);
  }
  
  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutATask() {
    create(date, workspace, duration, file, null);
  }

  /**
   * @see TaskData#TaskData(LocalDate, WorkspaceStorage, Duration, IFile, ITask)
   */
  private TaskData create(LocalDate date, WorkspaceStorage workspace,
      Duration duration, IFile file, ITask task) {
    return new TaskData(date, workspace, duration, file, task);
  }
  
  /**
   * @return The workspace root.
   */
  private IWorkspaceRoot workspaceRoot() {
    return ResourcesPlugin.getWorkspace().getRoot();
  }
}
