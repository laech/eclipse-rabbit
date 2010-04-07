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

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.merge.PartEventTypeMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PartEventListType;
import rabbit.data.internal.xml.schema.events.PartEventType;

import org.joda.time.LocalDate;

import java.util.Collection;

/**
 * Accesses workbench part event data.
 */
public class PartDataAccessor2
    extends
    AbstractDataNodeAccessor<PartDataDescriptor, PartEventType, PartEventListType> {

  /**
   * Constructor.
   */
  public PartDataAccessor2() {
  }

  @Override
  protected Collection<PartEventListType> getCategories(EventListType doc) {
    return doc.getPartEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.PART_STORE;
  }

  @Override
  protected PartDataDescriptor createDataNode(LocalDate cal, PartEventType type) {
    try {
      return new PartDataDescriptor(cal, type.getDuration(), type.getPartId());
    } catch (NullPointerException e) {
      return null;
    } catch (IllegalArgumentException e) {
      return null;
    }
  }

  @Override
  protected Collection<PartEventType> getElements(PartEventListType list) {
    return list.getPartEvent();
  }

  @Override
  protected PartEventTypeMerger createMerger() {
    return new PartEventTypeMerger();
  }
}
