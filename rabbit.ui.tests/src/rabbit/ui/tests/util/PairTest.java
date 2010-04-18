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
package rabbit.ui.tests.util;

import rabbit.ui.internal.util.Pair;

import com.google.common.base.Objects;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

/**
 * @see Pair
 */
@SuppressWarnings("restriction")
public class PairTest {

  @Test
  public void testNullArguments() {
    try {
      Pair.create(null, null);
    } catch (Exception e) {
      fail();
    }
  }
  
  @Test
  public void testGetFirst() {
    String first = "helloWorld";
    Pair<String, String> pair = Pair.create(first, null);
    assertEquals(first, pair.getFirst());
    assertNull(pair.getSecond());
  }
  
  @Test
  public void testGetSecond() {
    int second = 101;
    Pair<Integer, Integer> pair = Pair.create(null, second);
    assertEquals(second, pair.getSecond().intValue());
    assertNull(pair.getFirst());
  }
  
  @Test
  public void testHashCode() {
    String first = "hello";
    String second = "world";
    assertEquals(Objects.hashCode(first, second), Pair.create(first, second).hashCode());
  }
  
  @Test
  public void testEquals() {
    Pair<Integer, Integer> p1 = Pair.create(1, 2);
    Pair<Integer, Integer> p2 = Pair.create(p1.getFirst(), p1.getSecond());
    assertTrue(p1.equals(p2));
    assertTrue(p1.equals(p1));
    assertFalse(p1.equals(null));
    
    p2 = Pair.create(p1.getFirst(), p1.getSecond() + 1);
    assertFalse(p1.equals(p2));
    
    p2 = Pair.create(p1.getFirst() + 1, p1.getSecond());
    assertFalse(p1.equals(p2));
  }
}
