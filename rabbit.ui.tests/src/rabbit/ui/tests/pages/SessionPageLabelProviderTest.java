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
package rabbit.ui.tests.pages;

import static rabbit.ui.internal.util.DurationFormat.format;

import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.ui.internal.pages.DateLabelProvider;
import rabbit.ui.internal.pages.SessionPageLabelProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Test for {@link SessionPageLabelProvider}
 */
@SuppressWarnings("restriction")
public class SessionPageLabelProviderTest {

  private static final SessionPageLabelProvider provider;
  private static final DateLabelProvider dateLabels;

  static {
    provider = new SessionPageLabelProvider();
    dateLabels = new DateLabelProvider();
  }

  @AfterClass
  public static void afterClass() {
    provider.dispose();
    dateLabels.dispose();
  }
  
  @Test
  public void testGetText() {
    LocalDate today = new LocalDate();
    SessionDataDescriptor des = new SessionDataDescriptor(today, 101);
    assertEquals(dateLabels.getText(des), provider.getText(today));

    LocalDate yesterday = today.minusDays(1);
    des = new SessionDataDescriptor(yesterday, 1);
    assertEquals(dateLabels.getText(des), provider.getText(yesterday));

    LocalDate someday = yesterday.minusDays(1);
    des = new SessionDataDescriptor(someday, 0);
    assertEquals(dateLabels.getText(des), provider.getText(someday));
  }
  
  @Test
  public void testGetImage() {
    SessionDataDescriptor d = new SessionDataDescriptor(new LocalDate(), 101);
    assertNotNull(provider.getImage(d));
  }

  @Test
  public void testGetColumnText_0() {
    LocalDate today = new LocalDate();
    SessionDataDescriptor des = new SessionDataDescriptor(today, 101);
    assertEquals(dateLabels.getText(des), provider.getColumnText(today, 0));

    LocalDate yesterday = today.minusDays(1);
    des = new SessionDataDescriptor(yesterday, 1);
    assertEquals(dateLabels.getText(des), provider.getColumnText(yesterday, 0));

    LocalDate someday = yesterday.minusDays(1);
    des = new SessionDataDescriptor(someday, 0);
    assertEquals(dateLabels.getText(des), provider.getColumnText(someday, 0));
  }

  @Test
  public void testGetColumnText_1() {
    SessionDataDescriptor d = new SessionDataDescriptor(new LocalDate(), 101);
    assertEquals(format(d.getValue()), provider.getColumnText(d, 1));
  }

  @Test
  public void testGetColumnImage() {
    SessionDataDescriptor d = new SessionDataDescriptor(new LocalDate(), 101);
    assertNotNull(provider.getColumnImage(d, 0));
    assertNull(provider.getColumnImage(d, 1));
  }
}
