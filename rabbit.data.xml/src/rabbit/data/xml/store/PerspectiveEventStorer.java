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
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.store.model.PerspectiveEvent;

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

public final class PerspectiveEventStorer
    extends
    AbstractContinuousEventStorer<PerspectiveEvent, PerspectiveEventType, PerspectiveEventListType> {

  private static final PerspectiveEventStorer INSTANCE = new PerspectiveEventStorer();

  /**
   * Gets the shared instance of this class.
   * 
   * @return The shared instance of this class.
   */
  public static PerspectiveEventStorer getInstance() {
    return INSTANCE;
  }

  private PerspectiveEventStorer() {
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.PERSPECTIVE_STORE;
  }

  @Override
  protected List<PerspectiveEventListType> getXmlTypeCategories(
      EventListType events) {
    return events.getPerspectiveEvents();
  }

  @Override
  protected List<PerspectiveEventType> getXmlTypes(PerspectiveEventListType list) {
    return list.getPerspectiveEvent();
  }

  @Override
  protected boolean hasSameId(PerspectiveEventType x1, PerspectiveEventType x2) {
    return x1.getPerspectiveId().equals(x2.getPerspectiveId());
  }

  @Override
  protected PerspectiveEventType newXmlType(PerspectiveEvent e) {
    PerspectiveEventType type = objectFactory.createPerspectiveEventType();
    type.setDuration(e.getDuration());
    type.setPerspectiveId(e.getPerspective().getId());
    return type;
  }

  @Override
  protected PerspectiveEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
    PerspectiveEventListType type = objectFactory
        .createPerspectiveEventListType();
    type.setDate(date);
    return type;
  }

}
