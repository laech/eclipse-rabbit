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
package rabbit.data.internal.xml.access;

import static rabbit.data.access.model.IFileData.DATE;
import static rabbit.data.access.model.IFileData.DURATION;
import static rabbit.data.access.model.IFileData.FILE;
import static rabbit.data.access.model.IFileData.TIME;
import static rabbit.data.access.model.IFileData.WORKSPACE;

import rabbit.data.access.model.IFileData;
import rabbit.data.access.model.WorkspaceStorage;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.FileEventListType;
import rabbit.data.internal.xml.schema.events.FileEventType;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.nullValue;
import static org.junit.Assert.assertThat;

import org.eclipse.core.runtime.Path;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.junit.Test;

import java.util.List;

/**
 * @see FileDataAccessor
 */
public class FileDataAccessorTest extends
    AbstractAccessorTest2<IFileData, FileEventType, FileEventListType> {

  @Test
  public void localTimeIsNullIfStartTimeIs0() throws Exception {
    final LocalDate date = new LocalDate();
    final WorkspaceStorage workspace = new WorkspaceStorage(new Path("/"), null);
    final FileEventType type = new FileEventType();
    type.setStartTime(0); // <- time is 0
    type.setDuration(19834);
    type.setFilePath("/a/b");
    final IFileData data = accessor.createDataNode(date, workspace, type);
    assertThat(data.get(IFileData.TIME), is(nullValue()));
  }

  @Test
  public void localTimeIsNullIfStartTimeIsNegative() throws Exception {
    final LocalDate date = new LocalDate();
    final WorkspaceStorage workspace = new WorkspaceStorage(new Path("/"), null);
    final FileEventType type = new FileEventType();
    type.setStartTime(-1); // <- time is negative
    type.setDuration(19834);
    type.setFilePath("/a/b");
    final IFileData data = accessor.createDataNode(date, workspace, type);
    assertThat(data.get(IFileData.TIME), is(nullValue()));
  }

  @Override
  protected void assertValues(FileEventType expected, LocalDate expectedDate,
      WorkspaceStorage expectedWs, IFileData actual) {
    assertThat(actual.get(DATE), is(expectedDate));
    assertThat(actual.get(DURATION).getMillis(), is(expected.getDuration()));
    assertThat(actual.get(WORKSPACE), is(expectedWs));
    assertThat(actual.get(FILE).getFullPath(),
        is(Path.fromPortableString(expected.getFilePath())));
    assertThat(actual.get(TIME), is(new LocalTime(expected.getStartTime())));
  }

  @Override
  protected FileDataAccessor create() {
    return new FileDataAccessor(DataStore.FILE_STORE);
  }

  @Override
  protected FileEventListType createCategory() {
    return new FileEventListType();
  }

  @Override
  protected FileEventType createElement() {
    final FileEventType type = new FileEventType();
    type.setDuration(1000);
    type.setFilePath("/project/file.txt");
    type.setStartTime(1010);
    return type;
  }

  @Override
  protected List<FileEventListType> getCategories(EventListType events) {
    return events.getFileEvents();
  }

  @Override
  protected List<FileEventType> getElements(FileEventListType list) {
    return list.getFileEvent();
  }
}
