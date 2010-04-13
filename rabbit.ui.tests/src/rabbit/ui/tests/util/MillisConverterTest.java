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

import rabbit.ui.internal.util.MillisConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Test for {@link MillisConverter}
 */
@SuppressWarnings("restriction")
public class MillisConverterTest {

  @Test
  public void testToDefaultString() {
    long millis = 1000;
    System.out.println(MillisConverter.toDefaultString(millis));
    assertEquals("1 s", MillisConverter.toDefaultString(millis));

    millis = 60000;
    System.out.println(MillisConverter.toDefaultString(millis));
    assertEquals("1 min 00 s", MillisConverter.toDefaultString(millis));

    millis = 3600000;
    System.out.println(MillisConverter.toDefaultString(millis));
    assertEquals("1 hr 00 min 00 s", MillisConverter.toDefaultString(millis));

    millis = 36061000;
    System.out.println(MillisConverter.toDefaultString(millis));
    assertEquals("10 hr 01 min 01 s", MillisConverter.toDefaultString(millis));
  }

  @Test
  public void testToHours() {
    long millis = 127468458;
    double hours = (double) millis / 1000 / 60 / 60;
    assertTrue(Double.compare(hours, MillisConverter.toHours(millis)) == 0);
  }

  @Test
  public void testToMinutes() {
    long millis = 945736236;
    double minutes = (double) millis / 1000 / 60;
    assertTrue(Double.compare(minutes, MillisConverter.toMinutes(millis)) == 0);
  }

  @Test
  public void testToSeconds() {
    long millis = 1000098712;
    double seconds = (double) millis / 1000;
    assertTrue(Double.compare(seconds, MillisConverter.toSeconds(millis)) == 0);
  }

}
