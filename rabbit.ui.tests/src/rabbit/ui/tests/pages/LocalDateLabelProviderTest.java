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
package rabbit.ui.tests.pages;

import rabbit.ui.internal.pages.LocalDateLabelProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * @see LocalDateLabelProvider
 */
@SuppressWarnings("restriction")
public class LocalDateLabelProviderTest {

  private static final LocalDateLabelProvider labels = new LocalDateLabelProvider();

  @AfterClass
  public static void afterClass() {
    labels.dispose();
  }

  @Test
  public void testGetText() {
    LocalDate date = new LocalDate();
    assertEquals(date.toString(), labels.getText(date));
  }

  @Test
  public void testGetImage() {
    assertNotNull(labels.getImage(new LocalDate()));
  }
}
