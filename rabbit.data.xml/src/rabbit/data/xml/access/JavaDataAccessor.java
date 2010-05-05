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
package rabbit.data.xml.access;

import rabbit.data.access.model.JavaDataDescriptor;
import rabbit.data.internal.xml.AbstractDataNodeAccessor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.merge.IMerger;
import rabbit.data.internal.xml.merge.JavaEventTypeMerger;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.JavaEventListType;
import rabbit.data.internal.xml.schema.events.JavaEventType;

import org.joda.time.LocalDate;

import java.util.Collection;

// TODO
public class JavaDataAccessor extends 
    AbstractDataNodeAccessor<JavaDataDescriptor, JavaEventType, JavaEventListType> {

  @Override
  protected JavaDataDescriptor createDataNode(LocalDate cal, JavaEventType type) {
    return new JavaDataDescriptor(cal, type.getDuration(), type.getHandleIdentifier());
  }

  @Override
  protected IMerger<JavaEventType> createMerger() {
    return new JavaEventTypeMerger();
  }

  @Override
  protected Collection<JavaEventType> getElements(JavaEventListType category) {
    return category.getJavaEvent();
  }

  @Override
  protected Collection<JavaEventListType> getCategories(EventListType doc) {
    return doc.getJavaEvents();
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.JAVA_STORE;
  }

}
