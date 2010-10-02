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

import static rabbit.data.internal.xml.DatatypeUtil.toXmlDate;

import rabbit.data.access.model.JavaDataDescriptor;
import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.merge.JavaEventTypeMerger;
import rabbit.data.internal.xml.schema.events.JavaEventListType;
import rabbit.data.internal.xml.schema.events.JavaEventType;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;

import java.util.List;

/**
 * @see JavaDataAccessor
 */
public class JavaDataAccessorTest extends 
    AbstractDataNodeAccessorTest<JavaDataDescriptor, JavaEventType, JavaEventListType> {

  @Override
  protected JavaDataAccessor create() {
    return new JavaDataAccessor(
        DataStore.JAVA_STORE, new JavaEventTypeMerger());
  }

  @Override
  public void testCreateDataNode() throws Exception {
    LocalDate date = new LocalDate(1999, 1, 1);
    String id = "abc";
    long value = 1983;
    
    JavaEventType type = new JavaEventType();
    type.setDuration(value);
    type.setHandleIdentifier(id);
    
    JavaDataDescriptor des = accessor.createDataNode(date, type);
    assertEquals(date, des.getDate());
    assertEquals(value, des.getDuration().getMillis());
    assertEquals(id, des.getHandleIdentifier());
  }

  @Override
  protected JavaEventListType createCategory() {
    JavaEventListType type = new JavaEventListType();
    type.setDate(toXmlDate(new LocalDate()));
    return type;
  }

  @Override
  protected JavaEventType createElement() {
    JavaEventType type = new JavaEventType();
    type.setDuration(10);
    type.setHandleIdentifier("abc");
    return type;
  }

  @Override
  protected List<JavaEventType> getElements(JavaEventListType list) {
    return list.getJavaEvent();
  }

  @Override
  protected void setId(JavaEventType type, String id) {
    type.setHandleIdentifier(id);
  }

  @Override
  protected void setValue(JavaEventType type, long usage) {
    type.setDuration(usage);
  }

  @Override
  protected boolean areEqual(JavaDataDescriptor expected,
      JavaDataDescriptor actual) {
    return expected.getDate().equals(actual.getDate())
        && expected.getDuration().equals(actual.getDuration())
        && expected.getHandleIdentifier().equals(actual.getHandleIdentifier());
  }

}
