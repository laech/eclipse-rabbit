/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.data.xml.store;

import rabbit.data.internal.xml.AbstractStorer;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.convert.IConverter;
import rabbit.data.internal.xml.convert.SessionEventConverter;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.SessionEventTypeMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.SessionEventListType;
import rabbit.data.internal.xml.schema.events.SessionEventType;
import rabbit.data.store.model.SessionEvent;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link SessionEvent}
 */
public class SessionEventStorer extends 
    AbstractStorer<SessionEvent, SessionEventType, SessionEventListType> {
  
  private static SessionEventStorer INSTANCE = new SessionEventStorer();
  
  /**
   * Gets the shared instance of this class.
   * @return The shared instance;
   */
  public static SessionEventStorer getInstance() {
    return INSTANCE;
  }
  
  private final IMerger<SessionEventType> merger;
  private final IConverter<SessionEvent, SessionEventType> converter;
  
  /**
   * Private constructor.
   */
  private SessionEventStorer() {
    merger = new SessionEventTypeMerger();
    converter = new SessionEventConverter();
  }

  @Override
  protected List<SessionEventListType> getCategories(EventListType events) {
    return events.getSessionEvents();
  }

  @Override
  protected IConverter<SessionEvent, SessionEventType> getConverter() {
    return converter;
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.SESSION_STORE;
  }

  @Override
  protected List<SessionEventType> getElements(SessionEventListType list) {
    return list.getSessionEvent();
  }

  @Override
  protected IMerger<SessionEventType> getMerger() {
    return merger;
  }

  @Override
  protected SessionEventListType newCategory(XMLGregorianCalendar date) {
    SessionEventListType list = new SessionEventListType();
    list.setDate(date);
    return list;
  }

}
