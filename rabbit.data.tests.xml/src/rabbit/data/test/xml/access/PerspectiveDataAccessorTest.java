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

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.test.xml.AbstractDataNodeAccessorTest;
import rabbit.data.xml.access.PerspectiveDataAccessor;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;

import java.util.List;

@SuppressWarnings("restriction")
public class PerspectiveDataAccessorTest
    extends
    AbstractDataNodeAccessorTest<PerspectiveDataDescriptor, PerspectiveEventType, PerspectiveEventListType> {

  @Override
  protected PerspectiveDataAccessor create() {
    return new PerspectiveDataAccessor();
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
    PerspectiveDataDescriptor des = createDataNode(accessor, date, e);

    assertEquals(date, des.getDate());
    assertEquals(e.getDuration(), des.getValue());
    assertEquals(e.getPerspectiveId(), des.getPerspectiveId());
  }

}
