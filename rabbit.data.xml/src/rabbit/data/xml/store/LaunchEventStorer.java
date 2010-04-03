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

import static rabbit.data.internal.xml.util.StringUtil.areEqual;

import rabbit.data.internal.xml.AbstractDiscreteEventStorer;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.IDataStore;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.store.model.LaunchEvent;

import org.eclipse.core.runtime.CoreException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

/**
 * Stores {@link LaunchEvent}
 */
public class LaunchEventStorer
    extends
    AbstractDiscreteEventStorer<LaunchEvent, LaunchEventType, LaunchEventListType> {

  private static LaunchEventStorer INSTANCE = new LaunchEventStorer();

  /**
   * Gets the shared instance of this class.
   * 
   * @return The shared instance.
   */
  public static LaunchEventStorer getInstance() {
    return INSTANCE;
  }

  private LaunchEventStorer() {
  }

  @Override
  protected IDataStore getDataStore() {
    return DataStore.LAUNCH_STORE;
  }

  @Override
  protected List<LaunchEventListType> getXmlTypeCategories(EventListType events) {
    return events.getLaunchEvents();
  }

  @Override
  protected List<LaunchEventType> getXmlTypes(LaunchEventListType list) {
    return list.getLaunchEvent();
  }

  @Override
  protected boolean hasSameId(LaunchEventType type1, LaunchEventType type2) {
    return areEqual(type1.getName(), type2.getName())
        && areEqual(type1.getLaunchModeId(), type2.getLaunchModeId())
        && areEqual(type1.getLaunchTypeId(), type2.getLaunchTypeId());
  }

  @Override
  protected void merge(LaunchEventType main, LaunchEventType x) {
    main.setCount(main.getCount() + x.getCount());
    main.setTotalDuration(main.getTotalDuration() + x.getTotalDuration());

    if (!x.getFileId().isEmpty()) {
      Set<String> ids = new HashSet<String>(main.getFileId());
      ids.addAll(x.getFileId());

      main.getFileId().clear();
      main.getFileId().addAll(ids);
    }
  }

  @Override
  protected LaunchEventType newXmlType(LaunchEvent event) {
    LaunchEventType type = objectFactory.createLaunchEventType();
    type.getFileId().addAll(event.getFileIds());
    type.setTotalDuration(event.getDuration());
    type.setName(event.getLaunchConfiguration().getName());
    try {
      type.setLaunchTypeId(event.getLaunchConfiguration().getType()
          .getIdentifier());
    } catch (CoreException ex) {
      ex.printStackTrace();
      type.setLaunchTypeId(null);
    }
    type.setLaunchModeId(event.getLaunch().getLaunchMode());
    type.setCount(1);

    return type;
  }

  @Override
  protected LaunchEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
    LaunchEventListType type = objectFactory.createLaunchEventListType();
    type.setDate(date);
    return type;
  }
}
