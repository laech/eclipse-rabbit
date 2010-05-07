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
import rabbit.data.internal.xml.convert.JavaEventConverter;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.JavaEventTypeMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.JavaEventListType;
import rabbit.data.internal.xml.schema.events.JavaEventType;
import rabbit.data.store.model.JavaEvent;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link JavaEvent}s.
 */
public class JavaEventStorer 
    extends AbstractStorer<JavaEvent, JavaEventType, JavaEventListType> {
  
  private static JavaEventStorer INSTANCE = new JavaEventStorer();
  
  /**
   * Gets the shared instance of this class.
   * @return The shared instance.
   */
  public static JavaEventStorer getInstance() {
    return INSTANCE;
  }
  
  private final IMerger<JavaEventType> merger;
  private final IConverter<JavaEvent, JavaEventType> converter;
  
  /**
   * Private constructor.
   */
  private JavaEventStorer() {
    merger = new JavaEventTypeMerger();
    converter = new JavaEventConverter();
  }

  @Override
  protected List<JavaEventListType> getCategories(EventListType events) {
    return events.getJavaEvents();
  }

  @Override
  protected IConverter<JavaEvent, JavaEventType> getConverter() {
    return converter;
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.JAVA_STORE;
  }

  @Override
  protected List<JavaEventType> getElements(JavaEventListType list) {
    return list.getJavaEvent();
  }

  @Override
  protected IMerger<JavaEventType> getMerger() {
    return merger;
  }

  @Override
  protected JavaEventListType newCategory(XMLGregorianCalendar date) {
    JavaEventListType type = new JavaEventListType();
    type.setDate(date);
    return type;
  }

}
