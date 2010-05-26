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
package rabbit.data.internal.xml.store;

import rabbit.data.internal.xml.AbstractStorer;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.convert.IConverter;
import rabbit.data.internal.xml.convert.PartEventConverter;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.PartEventTypeMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PartEventListType;
import rabbit.data.internal.xml.schema.events.PartEventType;
import rabbit.data.store.model.PartEvent;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link PartEvent}
 */
public final class PartEventStorer extends
    AbstractStorer<PartEvent, PartEventType, PartEventListType> {

  private static final PartEventStorer INSTANCE = new PartEventStorer();

  /**
   * Gets the shared instance of this class.
   * 
   * @return The shared instance of this class.
   */
  public static PartEventStorer getInstance() {
    return INSTANCE;
  }

  @Nonnull
  private final PartEventConverter converter;
  @Nonnull
  private final PartEventTypeMerger merger;

  private PartEventStorer() {
    converter = new PartEventConverter();
    merger = new PartEventTypeMerger();
  }

  @Override
  protected List<PartEventListType> getCategories(EventListType events) {
    return events.getPartEvents();
  }

  @Override
  protected IConverter<PartEvent, PartEventType> getConverter() {
    return converter;
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.PART_STORE;
  }

  @Override
  protected List<PartEventType> getElements(PartEventListType list) {
    return list.getPartEvent();
  }

  @Override
  protected IMerger<PartEventType> getMerger() {
    return merger;
  }

  @Override
  protected PartEventListType newCategory(XMLGregorianCalendar date) {
    PartEventListType type = objectFactory.createPartEventListType();
    type.setDate(date);
    return type;
  }
}
