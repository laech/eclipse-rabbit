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
package rabbit.data.internal.xml.access;

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.merge.PartEventTypeMerger;
import rabbit.data.internal.xml.schema.events.PartEventListType;
import rabbit.data.internal.xml.schema.events.PartEventType;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * Test for {@link PartDataAccessor}
 */
public class PartDataAccessorTest
    extends
    AbstractNodeAccessorTest<PartDataDescriptor, PartEventType, PartEventListType> {

  @Override
  protected PartDataAccessor create() {
    return new PartDataAccessor(
        DataStore.PART_STORE, new PartEventTypeMerger());
  }

  @Override
  protected PartEventListType createCategory() {
    return objectFactory.createPartEventListType();
  }

  @Override
  protected PartEventType createElement() {
    PartEventType type = objectFactory.createPartEventType();
    type.setDuration(11);
    type.setPartId("am.an.id");
    return type;
  }

  @Override
  protected List<PartEventType> getElements(PartEventListType list) {
    return list.getPartEvent();
  }

  @Override
  protected void setId(PartEventType type, String id) {
    type.setPartId(id);
  }

  @Override
  protected void setValue(PartEventType type, long usage) {
    type.setDuration(usage);
  }

  @Override
  public void testCreateDataNode() throws Exception {
    LocalDate date = new LocalDate();
    PartEventType type = createElement();
    PartDataDescriptor des = accessor.createDataNode(date, type);
    
    assertEquals(date, des.getDate());
    assertEquals(type.getPartId(), des.getPartId());
    assertEquals(type.getDuration(), des.getDuration().getMillis());
  }

  @Override
  protected boolean areEqual(PartDataDescriptor expected,
      PartDataDescriptor actual) {
    return expected.getDate().equals(actual.getDate())
        && expected.getDuration().equals(actual.getDuration())
        && expected.getPartId().equals(actual.getPartId());
  }
}
