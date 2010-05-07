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
package rabbit.data.test.access.model;

import rabbit.data.access.model.JavaDataDescriptor;

import com.google.common.base.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see JavaDataDescriptor
 */
public class JavaDataDescriptorTest extends ValueDescriptorTest {
  
  @Test(expected = NullPointerException.class)
  public void testConstructor_handleIdNull() {
    createDescriptor(new LocalDate(), 1, null);
  }
  
  @Test
  public void testGetHandleIdentifier() {
    String handleId = "abc";
    assertEquals(handleId, createDescriptor(new LocalDate(), 1, handleId).getHandleIdentifier());
  }
  
  @Override
  protected final JavaDataDescriptor createDescriptor(LocalDate date, long value) {
    return createDescriptor(date, value, "someHandle");
  }
  
  /**
   * @see JavaDataDescriptor#JavaDataDescriptor(LocalDate, long, String)
   */
  protected JavaDataDescriptor createDescriptor(LocalDate date, long value, String handleId) {
    return new JavaDataDescriptor(date, value, handleId);
  }

  @Override
  public void testHashCode() {
    JavaDataDescriptor des = createDescriptor(new LocalDate(), 1);
    assertEquals(Objects.hashCode(des.getDate(), des.getHandleIdentifier()), des.hashCode());
  }
  
  @Override
  public void testEquals() {
    JavaDataDescriptor d1 = createDescriptor(new LocalDate(), 1);
    JavaDataDescriptor d2 = createDescriptor(d1.getDate(), d1.getValue(), d1.getHandleIdentifier());
    assertTrue(d1.equals(d2));
    assertTrue(d1.equals(d1));
    assertFalse(d1.equals(null));
    
    d2 = createDescriptor(d1.getDate().minusDays(1), d1.getValue(), d1.getHandleIdentifier());
    assertFalse(d1.equals(d2));
    
    d2 = createDescriptor(d1.getDate(), d1.getValue() + 1, d1.getHandleIdentifier());
    assertFalse(d1.equals(d2));
    
    d2 = createDescriptor(d1.getDate(), d1.getValue(), d1.getHandleIdentifier() + "1");
    assertFalse(d1.equals(d2));
  }
}
