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
import rabbit.data.internal.xml.schema.events.PartEventListType;
import rabbit.data.internal.xml.schema.events.PartEventType;
import rabbit.data.store.model.PartEvent;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

public final class PartEventStorer extends
    AbstractContinuousEventStorer<PartEvent, PartEventType, PartEventListType> {

  private static final PartEventStorer INSTANCE = new PartEventStorer();

  /**
   * Gets the shared instance of this class.
   * 
   * @return The shared instance of this class.
   */
  public static PartEventStorer getInstance() {
    return INSTANCE;
  }

  private PartEventStorer() {
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.PART_STORE;
  }

  @Override
  protected List<PartEventListType> getXmlTypeCategories(EventListType events) {
    return events.getPartEvents();
  }

  @Override
  protected List<PartEventType> getXmlTypes(PartEventListType list) {
    return list.getPartEvent();
  }

  @Override
  protected boolean hasSameId(PartEventType x1, PartEventType x2) {
    return x1.getPartId().equals(x2.getPartId());
  }

  @Override
  protected PartEventType newXmlType(PartEvent e) {

    PartEventType type = objectFactory.createPartEventType();
    type.setDuration(e.getDuration());
    type.setPartId(e.getWorkbenchPart().getSite().getId());

    return type;
  }

  @Override
  protected PartEventListType newXmlTypeHolder(XMLGregorianCalendar date) {

    PartEventListType type = objectFactory.createPartEventListType();
    type.setDate(date);

    return type;
  }
}
