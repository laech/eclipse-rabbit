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
package rabbit.data.xml.access;

import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.merge.FileEventTypeMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.FileEventListType;
import rabbit.data.internal.xml.schema.events.FileEventType;

import org.eclipse.core.runtime.Path;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Accesses file event data.
 */
public class FileDataAccessor
    extends
    AbstractDataNodeAccessor<FileDataDescriptor, FileEventType, FileEventListType> {

  public FileDataAccessor() {
  }

  @Override
  protected FileDataDescriptor createDataNode(LocalDate cal, FileEventType type) {
    try {
      return new FileDataDescriptor(cal, type.getDuration(), new Path(type.getFilePath()));
    } catch (NullPointerException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    } catch (Throwable t) {
      return null; // This one is for the construction of the path.
    }
  }

  @Override
  protected Collection<FileEventType> getElements(FileEventListType list) {
    return list.getFileEvent();
  }

  @Override
  protected Collection<FileEventListType> getCategories(EventListType doc) {
    return doc.getFileEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.FILE_STORE;
  }

  @Override
  protected FileEventTypeMerger createMerger() {
    return new FileEventTypeMerger();
  }

}
