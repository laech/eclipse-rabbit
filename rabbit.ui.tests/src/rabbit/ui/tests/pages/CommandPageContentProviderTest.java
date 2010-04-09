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

import rabbit.data.access.model.CommandDataDescriptor;
import rabbit.ui.internal.pages.AbstractDateCategoryContentProvider;
import rabbit.ui.internal.pages.AbstractTreeViewerPage;
import rabbit.ui.internal.pages.CommandPage;
import rabbit.ui.internal.pages.CommandPageContentProvider;
import rabbit.ui.internal.pages.PerspectivePageContentProvider;

import com.google.common.collect.Sets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.commands.Command;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 * @see PerspectivePageContentProvider
 */
@SuppressWarnings("restriction")
public class CommandPageContentProviderTest extends
    AbstractDateCategoryContentProviderTest {

  @Test
  public void testGetChildren() {
    CommandDataDescriptor des1 = new CommandDataDescriptor(new LocalDate(),
        8723, "123");
    CommandDataDescriptor des2 = new CommandDataDescriptor(des1.getDate(),
        18723, "12343");

    page.getViewer().setInput(Arrays.asList(des1, des2));
    Set<CommandDataDescriptor> set = Sets.newHashSet(des1, des2);

    Object[] elements = contentProvider.getChildren(des1.getDate());
    assertEquals(2, elements.length);
    assertTrue(set.containsAll(Arrays.asList(elements)));
  }

  @Test
  public void testGetPerspective_nonExistId() {
    CommandPageContentProvider contents = (CommandPageContentProvider) contentProvider;
    CommandDataDescriptor des = new CommandDataDescriptor(new LocalDate(),
        8723, System.currentTimeMillis() + "");
    Command cmd = contents.getCommand(des);
    // A custom perspective descriptor is returned, never null:
    assertNotNull(cmd);
    assertEquals(des.getCommandId(), cmd.getId());
  }

  @Test
  public void testGetValueOfPerspective_oneElement() {
    CommandPageContentProvider contents = (CommandPageContentProvider) contentProvider;
    CommandDataDescriptor des = new CommandDataDescriptor(new LocalDate(),
        8723, "24");
    page.getViewer().setInput(Arrays.asList(des));
    assertEquals(des.getValue(), contents.getValueOfCommand(contents
        .getCommand(des)));
  }

  @Test
  public void testGetValueOfPerspective_twoElements() {
    CommandPageContentProvider contents = (CommandPageContentProvider) contentProvider;
    CommandDataDescriptor des1 = new CommandDataDescriptor(new LocalDate(),
        8723, "24");
    CommandDataDescriptor des2 = new CommandDataDescriptor(des1.getDate()
        .plusDays(1), 83276723, des1.getCommandId());
    page.getViewer().setInput(Arrays.asList(des1, des2));
    assertEquals(des1.getValue() + des2.getValue(), contents
        .getValueOfCommand(contents.getCommand(des1)));
  }

  @Test
  public void testHasChildren() {
    CommandDataDescriptor des = new CommandDataDescriptor(new LocalDate(), 187,
        "a.b");
    page.getViewer().setInput(Arrays.asList(des));
    assertTrue(contentProvider.hasChildren(des.getDate()));
    assertFalse(contentProvider.hasChildren(des));
    assertFalse(contentProvider.hasChildren(des.getDate().plusDays(1)));
  }

  @Test
  public void testInputChanged_newInputNull_clearsExistingData() {
    CommandDataDescriptor des1 = new CommandDataDescriptor(new LocalDate(),
        8723, "123");
    Set<CommandDataDescriptor> set = Sets.newHashSet(des1);
    page.getViewer().setInput(set);
    assertTrue(contentProvider.hasChildren(des1.getDate()));

    contentProvider.inputChanged(page.getViewer(), set, null);
    assertFalse(contentProvider.hasChildren(des1.getDate()));
  }

  @Test
  public void testInputChanged_newInputNull_noException() {
    try {
      contentProvider.inputChanged(page.getViewer(), null, null);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testGetElement_isDisplayingByDateTrue() {
    CommandDataDescriptor des1 = new CommandDataDescriptor(new LocalDate(),
        8723, "123");
    Set<CommandDataDescriptor> set = Sets.newHashSet(des1);
    page.getViewer().setInput(set);

    // Enable for testing:
    contentProvider.setDisplayByDate(true);

    // Null is OK, the content provider is not using it...
    Object[] elements = contentProvider.getElements(null);
    assertEquals(1, elements.length);
    // We enabled displaying by date, so it should return dates as roots:
    assertTrue(elements[0] instanceof LocalDate);
  }

  @Test
  public void testGetElement_isDisplayingByDateFalse() {
    CommandDataDescriptor des1 = new CommandDataDescriptor(new LocalDate(),
        8723, "123");
    Set<CommandDataDescriptor> set = Sets.newHashSet(des1);
    page.getViewer().setInput(set);

    // Disable for testing:
    contentProvider.setDisplayByDate(false);

    // Null is OK, the content provider is not using it...
    Object[] elements = contentProvider.getElements(null);
    assertEquals(1, elements.length);
    // We disabled displaying by date, so it should return commands as roots
    assertTrue(elements[0] instanceof Command);
  }

  @Override
  protected AbstractDateCategoryContentProvider createContentProvider(
      AbstractTreeViewerPage page, boolean displayByDate) {
    return new CommandPageContentProvider((CommandPage) page, displayByDate);
  }

  @Override
  protected CommandPage createPage() {
    return new CommandPage();
  }
}
