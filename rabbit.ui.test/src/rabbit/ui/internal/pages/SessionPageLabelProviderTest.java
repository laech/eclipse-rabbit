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
package rabbit.ui.internal.pages;

import static rabbit.ui.internal.util.DurationFormat.format;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.jface.viewers.TreeNode;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * Test for {@link SessionPageLabelProvider}
 */
@Deprecated
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
  public void testGetColumnImage_0() {
    TreeNode node = new TreeNode(new LocalDate());
    assertNotNull(provider.getColumnImage(node, 0));
    assertNull(provider.getColumnImage(node, 1));
  }
  
  @Test
  public void testGetColumnText_0() {
    LocalDate today = new LocalDate();
    TreeNode node = new TreeNode(today);
    assertNotNull(dateLabels.getText(today));
    assertEquals(dateLabels.getText(today), provider.getColumnText(node, 0));

    LocalDate yesterday = today.minusDays(1);
    node = new TreeNode(yesterday);
    assertNotNull(dateLabels.getText(yesterday));
    assertEquals(dateLabels.getText(yesterday), provider.getColumnText(node, 0));

    LocalDate someday = yesterday.minusDays(1);
    node = new TreeNode(someday);
    assertNotNull(dateLabels.getText(someday));
    assertEquals(dateLabels.getText(someday), provider.getColumnText(node, 0));
  }

  @Test
  public void testGetColumnText_1() {
    long duration = 100;
    TreeNode node = new TreeNode(duration);
    assertEquals(format(duration), provider.getColumnText(node, 1));
  }

  @Test
  public void testGetImage() {
    assertNotNull(provider.getImage(new TreeNode(new LocalDate())));
  }

  @Test
  public void testGetText() {
    LocalDate today = new LocalDate();
    TreeNode node = new TreeNode(today);
    assertNotNull(dateLabels.getText(today));
    assertEquals(dateLabels.getText(today), provider.getText(node));

    LocalDate yesterday = today.minusDays(1);
    node = new TreeNode(yesterday);
    assertNotNull(dateLabels.getText(yesterday));
    assertEquals(dateLabels.getText(yesterday), provider.getText(node));

    LocalDate someday = yesterday.minusDays(1);
    node = new TreeNode(someday);
    assertNotNull(dateLabels.getText(someday));
    assertEquals(dateLabels.getText(someday), provider.getText(node));
  }
}
