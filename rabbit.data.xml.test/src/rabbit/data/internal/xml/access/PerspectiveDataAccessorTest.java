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

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.merge.PerspectiveEventTypeMerger;
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;

import java.util.List;

public class PerspectiveDataAccessorTest
    extends
    AbstractNodeAccessorTest<PerspectiveDataDescriptor, PerspectiveEventType, PerspectiveEventListType> {

  @Override
  protected PerspectiveDataAccessor create() {
    return new PerspectiveDataAccessor(
        DataStore.PERSPECTIVE_STORE, new PerspectiveEventTypeMerger());
  }

  @Override
  protected PerspectiveEventListType createCategory() {
    return objectFactory.createPerspectiveEventListType();
  }

  @Override
  protected PerspectiveEventType createElement() {
    PerspectiveEventType type = objectFactory.createPerspectiveEventType();
    type.setDuration(11);
    type.setPerspectiveId("abc");
    return type;
  }

  @Override
  protected List<PerspectiveEventType> getElements(PerspectiveEventListType list) {
    return list.getPerspectiveEvent();
  }

  @Override
  protected void setId(PerspectiveEventType type, String id) {
    type.setPerspectiveId(id);
  }

  @Override
  protected void setValue(PerspectiveEventType type, long usage) {
    type.setDuration(usage);
  }

  @Override
  public void testCreateDataNode() throws Exception {
    LocalDate date = new LocalDate();
    PerspectiveEventType e = createElement();
    PerspectiveDataDescriptor des = accessor.createDataNode(date, e);

    assertEquals(date, des.getDate());
    assertEquals(e.getDuration(), des.getDuration().getMillis());
    assertEquals(e.getPerspectiveId(), des.getPerspectiveId());
  }

  @Override
  protected boolean areEqual(PerspectiveDataDescriptor expected,
      PerspectiveDataDescriptor actual) {
    return expected.getDate().equals(actual.getDate())
        && expected.getDuration().equals(actual.getDuration())
        && expected.getPerspectiveId().equals(actual.getPerspectiveId());
  }

}
