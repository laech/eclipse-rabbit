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
import rabbit.data.internal.xml.convert.LaunchEventConverter;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.LaunchEventTypeMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.store.model.LaunchEvent;

import java.util.List;

import javax.annotation.Nonnull;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link LaunchEvent}
 */
public class LaunchEventStorer extends
    AbstractStorer<LaunchEvent, LaunchEventType, LaunchEventListType> {

  private static LaunchEventStorer INSTANCE = new LaunchEventStorer();

  /**
   * Gets the shared instance of this class.
   * 
   * @return The shared instance.
   */
  public static LaunchEventStorer getInstance() {
    return INSTANCE;
  }

  @Nonnull
  private final LaunchEventConverter converter;
  @Nonnull
  private final LaunchEventTypeMerger merger;

  private LaunchEventStorer() {
    converter = new LaunchEventConverter();
    merger = new LaunchEventTypeMerger();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.LAUNCH_STORE;
  }

  @Override
  protected List<LaunchEventListType> getCategories(EventListType events) {
    return events.getLaunchEvents();
  }

  @Override
  protected List<LaunchEventType> getElements(LaunchEventListType list) {
    return list.getLaunchEvent();
  }

  @Override
  protected LaunchEventListType newCategory(XMLGregorianCalendar date) {
    LaunchEventListType type = objectFactory.createLaunchEventListType();
    type.setDate(date);
    return type;
  }

  @Override
  protected IConverter<LaunchEvent, LaunchEventType> getConverter() {
    return converter;
  }

  @Override
  protected IMerger<LaunchEventType> getMerger() {
    return merger;
  }
}
