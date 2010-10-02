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

import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.StoreNames;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.FileEventListType;
import rabbit.data.internal.xml.schema.events.FileEventType;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import org.eclipse.core.runtime.Path;
import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Accesses file event data.
 */
public class FileDataAccessor
    extends
    AbstractDataNodeAccessor<FileDataDescriptor, FileEventType, FileEventListType> {

  /**
   * Constructor.
   * 
   * @param store The data store to get the data from.
   * @param merger The merger for merging XML data nodes.
   * @throws NullPointerException If any arguments are null.
   */
  @Inject
  FileDataAccessor(
      @Named(StoreNames.FILE_STORE) IDataStore store,
      IMerger<FileEventType> merger) {
    super(store, merger);
  }

  @Override
  protected FileDataDescriptor createDataNode(LocalDate cal, FileEventType type) {
    try {
      return new FileDataDescriptor(cal, type.getDuration(), new Path(
          type.getFilePath()));
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
}
