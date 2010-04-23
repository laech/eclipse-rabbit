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

import rabbit.data.access.model.SessionDataDescriptor;
import rabbit.ui.internal.pages.AbstractTableViewerPage;
import rabbit.ui.internal.pages.SessionPage;

import static org.junit.Assert.assertEquals;

import org.joda.time.LocalDate;
import org.junit.Test;

/**
 * Test for {@link SessionPage}
 */
@SuppressWarnings("restriction")
public class SessionPageTest extends AbstractTableViewerPageTest {

  @Test
  public void testGetValue() throws Exception {
    SessionDataDescriptor des = new SessionDataDescriptor(new LocalDate(), 10);
    assertEquals(des.getValue(), page.getValue(des));
  }

  @Override
  protected AbstractTableViewerPage createPage() {
    return new SessionPage();
  }
}
