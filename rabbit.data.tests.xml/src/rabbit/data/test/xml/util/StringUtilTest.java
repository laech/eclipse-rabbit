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
package rabbit.data.test.xml.util;

import rabbit.data.internal.xml.util.StringUtil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * @see StringUtil
 */
public class StringUtilTest {

  @Test
  public void testAreEqual() {
    assertTrue(StringUtil.areEqual(null, null));
    assertTrue(StringUtil.areEqual("", null));
    assertTrue(StringUtil.areEqual(null, ""));
    assertTrue(StringUtil.areEqual("abc", "abc"));
    assertFalse(StringUtil.areEqual("abc", null));
  }

  @Test
  public void testGetString() {
    assertEquals("", StringUtil.getString(null));
  }

}
