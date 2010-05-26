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
import rabbit.data.internal.xml.convert.PerspectiveEventConverter;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.PerspectiveEventTypeMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.store.model.PerspectiveEvent;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link PerspectiveEvent}.
 */
public final class PerspectiveEventStorer
    extends
    AbstractStorer<PerspectiveEvent, PerspectiveEventType, PerspectiveEventListType> {

  private static final PerspectiveEventStorer INSTANCE = new PerspectiveEventStorer();

  /**
   * Gets the shared instance of this class.
   * 
   * @return The shared instance of this class.
   */
  public static PerspectiveEventStorer getInstance() {
    return INSTANCE;
  }

  @Nonnull
  private final PerspectiveEventConverter converter;
  @Nonnull
  private final PerspectiveEventTypeMerger merger;

  private PerspectiveEventStorer() {
    converter = new PerspectiveEventConverter();
    merger = new PerspectiveEventTypeMerger();
  }

  @Override
  protected List<PerspectiveEventListType> getCategories(EventListType events) {
    return events.getPerspectiveEvents();
  }

  @Override
  protected IConverter<PerspectiveEvent, PerspectiveEventType> getConverter() {
    return converter;
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.PERSPECTIVE_STORE;
  }

  @Override
  protected List<PerspectiveEventType> getElements(PerspectiveEventListType list) {
    return list.getPerspectiveEvent();
  }

  @Override
  protected IMerger<PerspectiveEventType> getMerger() {
    return merger;
  }

  @Override
  protected PerspectiveEventListType newCategory(XMLGregorianCalendar date) {
    PerspectiveEventListType t = objectFactory.createPerspectiveEventListType();
    t.setDate(date);
    return t;
  }
}
