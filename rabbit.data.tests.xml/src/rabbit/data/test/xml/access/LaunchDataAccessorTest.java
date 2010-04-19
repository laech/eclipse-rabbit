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
package rabbit.data.test.xml.access;

import rabbit.data.access.model.LaunchDataDescriptor;
import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.test.xml.AbstractDataNodeAccessorTest;
import rabbit.data.xml.access.LaunchDataAccessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.debug.core.ILaunchManager;
import org.joda.time.LocalDate;

import java.util.GregorianCalendar;
import java.util.List;

/**
 * @see LaunchDataAccessor
 */
@SuppressWarnings("restriction")
public class LaunchDataAccessorTest
    extends
    AbstractDataNodeAccessorTest<LaunchDataDescriptor, LaunchEventType, LaunchEventListType> {


  @Override
  protected LaunchDataAccessor create() {
    return new LaunchDataAccessor();
  }

  @Override
  protected LaunchEventListType createCategory() {
    LaunchEventListType type = objectFactory.createLaunchEventListType();
    type.setDate(DatatypeUtil
        .toXmlDateTime(new GregorianCalendar()));
    return type;
  }

  @Override
  protected LaunchEventType createElement() {
    LaunchEventType type = objectFactory.createLaunchEventType();
    type.setTotalDuration(10);
    type.setLaunchModeId(ILaunchManager.RUN_MODE);
    type.setName("name");
    type.setLaunchTypeId("type");
    type.setCount(1);
    return type;
  }

  @Override
  protected List<LaunchEventType> getElements(LaunchEventListType list) {
    return list.getLaunchEvent();
  }

  @Override
  protected void setId(LaunchEventType type, String id) {
    type.setLaunchModeId(id);
    type.setLaunchTypeId(id);
    type.setName(id);
  }

  @Override
  protected void setValue(LaunchEventType type, long usage) {
    type.setTotalDuration(usage);
  }

  @Override
  public void testCreateDataNode() throws Exception {
    LocalDate date = new LocalDate();
    LaunchEventType e = createElement();
    LaunchDataDescriptor des = createDataNode(accessor, date, e);
    
    assertEquals(date, des.getDate());
    assertEquals(e.getCount(), des.getLaunchCount());
    assertEquals(e.getTotalDuration(), des.getTotalDuration());
    assertEquals(e.getLaunchModeId(), des.getLaunchDescriptor().getLaunchModeId());
    assertEquals(e.getLaunchTypeId(), des.getLaunchDescriptor().getLaunchTypeId());
    assertEquals(e.getName(), des.getLaunchDescriptor().getLaunchName());
    assertEquals(e.getFileId().size(), des.getFileIds().size());
    assertTrue(e.getFileId().containsAll(des.getFileIds()));
  }
}
