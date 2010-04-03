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
package rabbit.data.xml.store;

import rabbit.data.internal.xml.AbstractContinuousEventStorer;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.FileEventListType;
import rabbit.data.internal.xml.schema.events.FileEventType;
import rabbit.data.store.model.FileEvent;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

public final class FileEventStorer extends
    AbstractContinuousEventStorer<FileEvent, FileEventType, FileEventListType> {

  private static final FileEventStorer INSTANCE = new FileEventStorer();

  /**
   * Gets the shared instance of this class.
   * 
   * @return The shared instance of this class.
   */
  public static FileEventStorer getInstance() {
    return INSTANCE;
  }

  private FileEventStorer() {
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.FILE_STORE;
  }

  @Override
  protected List<FileEventListType> getXmlTypeCategories(EventListType events) {
    return events.getFileEvents();
  }

  @Override
  protected List<FileEventType> getXmlTypes(FileEventListType list) {
    return list.getFileEvent();
  }

  @Override
  protected boolean hasSameId(FileEventType x1, FileEventType x2) {
    return x1.getFileId().equals(x2.getFileId());
  }

  @Override
  protected FileEventType newXmlType(FileEvent e) {
    FileEventType type = objectFactory.createFileEventType();
    type.setDuration(e.getDuration());
    type.setFileId(e.getFileId());
    return type;
  }

  @Override
  protected FileEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
    FileEventListType type = objectFactory.createFileEventListType();
    type.setDate(date);
    return type;
  }
}
