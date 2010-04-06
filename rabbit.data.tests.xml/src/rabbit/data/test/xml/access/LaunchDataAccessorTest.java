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

import static rabbit.data.internal.xml.util.StringUtil.getString;

import rabbit.data.access.model.ZLaunchDescriptor;
import rabbit.data.internal.xml.DatatypeUtil;
import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventListType;
import rabbit.data.internal.xml.schema.events.LaunchEventType;
import rabbit.data.test.xml.AbstractAccessorTest;
import rabbit.data.xml.access.LaunchDataAccessor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.eclipse.debug.core.ILaunchManager;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @see LaunchDataAccessor
 */
public class LaunchDataAccessorTest
    extends
    AbstractAccessorTest<Set<ZLaunchDescriptor>, LaunchEventType, LaunchEventListType> {

  @Override
  protected void assertValues(Set<ZLaunchDescriptor> data, EventListType events) {
    Set<ZLaunchDescriptor> myData = new HashSet<ZLaunchDescriptor>();
    for (LaunchEventListType list : events.getLaunchEvents()) {
      for (LaunchEventType type : list.getLaunchEvent()) {

        boolean done = false;

        for (ZLaunchDescriptor des : myData) {
          if (getString(type.getName()).equals(des.getLaunchName())
              && getString(type.getLaunchModeId())
                  .equals(des.getLaunchModeId())
              && getString(type.getLaunchTypeId())
                  .equals(des.getLaunchTypeId())) {

            des.setCount(des.getCount() + type.getCount());
            des.setTotalDuration(des.getTotalDuration()
                + type.getTotalDuration());
            des.getFileIds().addAll(type.getFileId());

            done = true;
            break;
          }
        }

        if (!done) {
          ZLaunchDescriptor des = new ZLaunchDescriptor();
          des.setCount(type.getCount());
          des.setTotalDuration(type.getTotalDuration());
          des.getFileIds().addAll(type.getFileId());
          des.setLaunchName(type.getName());
          des.setLaunchTypeId(type.getLaunchTypeId());
          des.setLaunchModeId(type.getLaunchModeId());

          myData.add(des);
        }
      }
    }

    assertEquals(myData.size(), data.size());
    myData.removeAll(data);
    assertTrue(myData.isEmpty());
  }

  @Override
  protected LaunchDataAccessor create() {
    return new LaunchDataAccessor();
  }

  @Override
  protected LaunchEventListType createListType() {
    LaunchEventListType type = objectFactory.createLaunchEventListType();
    type.setDate(DatatypeUtil
        .toXmlDateTime(new GregorianCalendar()));
    return type;
  }

  @Override
  protected LaunchEventType createXmlType() {
    LaunchEventType type = objectFactory.createLaunchEventType();
    type.setTotalDuration(10);
    type.setLaunchModeId(ILaunchManager.RUN_MODE);
    type.setName("name");
    type.setLaunchTypeId("type");
    type.setCount(1);
    return type;
  }

  @Override
  protected List<LaunchEventType> getXmlTypes(LaunchEventListType list) {
    return list.getLaunchEvent();
  }

  @Override
  protected void setId(LaunchEventType type, String id) {
    type.setLaunchModeId(id);
    type.setLaunchTypeId(id);
    type.setName(id);
  }

  @Override
  protected void setUsage(LaunchEventType type, long usage) {
    type.setTotalDuration(usage);
  }
}
