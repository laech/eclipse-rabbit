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

import rabbit.data.access.model.IFileData;
import rabbit.data.access.model.WorkspaceStorage;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Path;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

/**
 * @see FileData
 */
public class FileDataTest {

  private final IFile file = mock(IFile.class);
  private final LocalDate date = new LocalDate();
  private final Duration duration = new Duration(11);
  private final LocalTime time = new LocalTime();
  private final WorkspaceStorage workspace = new WorkspaceStorage(
      new Path("/a"), new Path("/b"));

  private final FileData data = create(date, workspace, duration, file, time);

  @Test
  public void shouldAllowToBeConstructedWithANullTime() throws Exception {
    /*
     * Time is allowed to be null because data don't have "time" before 1.3
     */
    create(date, workspace, duration, file, null);
    // No exception
  }

  @Test
  public void shouldReturnNullIfKeyIsNull() {
    assertThat(data.get(null), is(nullValue()));
  }

  @Test
  public void shouldReturnTheDate() {
    assertThat(data.get(IFileData.DATE), is(date));
  }

  @Test
  public void shouldReturnTheDuration() {
    assertThat(data.get(IFileData.DURATION), is(duration));
  }

  @Test
  public void shouldReturnTheFile() {
    assertThat(data.get(IFileData.FILE), is(file));
  }

  @Test
  public void shouldReturnTheTime() throws Exception {
    assertThat(data.get(IFileData.TIME), is(time));
  }

  @Test
  public void shouldReturnTheWorkspace() {
    assertThat(data.get(IFileData.WORKSPACE), is(workspace));
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADate() {
    create(null, workspace, duration, file, time);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutADuration() {
    create(date, workspace, null, file, time);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAFile() {
    create(date, workspace, duration, null, time);
  }

  @Test(expected = NullPointerException.class)
  public void shouldThrowNullPointerExceptionIfConstructedWithoutAWorkspace() {
    create(date, null, duration, file, time);
  }

  /**
   * @see FileData#FileData(LocalDate, WorkspaceStorage, Duration, IFile,
   *      LocalTime)
   */
  private FileData create(
      LocalDate date,
      WorkspaceStorage workspace,
      Duration duration,
      IFile file,
      LocalTime time) {
    return new FileData(date, workspace, duration, file, time);
  }
}
