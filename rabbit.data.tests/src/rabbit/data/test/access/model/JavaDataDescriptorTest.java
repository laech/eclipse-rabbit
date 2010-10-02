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
package rabbit.data.test.access.model;

import rabbit.data.access.model.JavaDataDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * @see JavaDataDescriptor
 */
public class JavaDataDescriptorTest extends ValueDescriptorTest {

  @Test(expected = NullPointerException.class)
  public void testConstructor_handleIdNull() {
    createDescriptor(new LocalDate(), new Duration(1), null);
  }

  @Test
  public void testFindElement() {
    // A valid handle identifier:
    String handleId = "=J/src<java.something{InterfaceA.java";
    assertEquals(handleId, createDescriptor(new LocalDate(), new Duration(1),
        handleId).findElement().getHandleIdentifier());

    // An invalid handle identifier:
    handleId = "abc";
    assertNull(createDescriptor(new LocalDate(), new Duration(1), handleId).findElement());
  }

  @Test
  public void testGetHandleIdentifier() {
    String handleId = "abc";
    assertEquals(handleId, createDescriptor(new LocalDate(), new Duration(1),
        handleId).getHandleIdentifier());
  }

  @Override
  protected final JavaDataDescriptor createDescriptor(LocalDate date,
      Duration value) {
    return createDescriptor(date, value, "someHandle");
  }

  /**
   * @see JavaDataDescriptor#JavaDataDescriptor(LocalDate, Duration, String)
   */
  protected JavaDataDescriptor createDescriptor(LocalDate date, Duration value,
      String handleId) {
    return new JavaDataDescriptor(date, value, handleId);
  }

}
