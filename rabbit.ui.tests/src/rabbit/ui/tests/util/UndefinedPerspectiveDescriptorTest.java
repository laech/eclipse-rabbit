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
package rabbit.ui.tests.util;

import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test for {@link UndefinedPerspectiveDescriptor}
 */
public class UndefinedPerspectiveDescriptorTest {

  String id = "sansijiuqehnsdfjh22wiur";
  private UndefinedPerspectiveDescriptor per = new UndefinedPerspectiveDescriptor(
      id);

  @Test
  public void testGetId() {
    assertEquals(id, per.getId());
  }

  @Test
  public void testGetLabel() {
    assertEquals(id, per.getId());
  }

}
