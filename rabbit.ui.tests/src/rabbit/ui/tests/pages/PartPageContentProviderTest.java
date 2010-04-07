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

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.ui.internal.pages.AbstractDateCategoryContentProvider;
import rabbit.ui.internal.pages.AbstractTreeViewerPage;
import rabbit.ui.internal.pages.PartPage;
import rabbit.ui.internal.pages.PartPageContentProvider;

import com.google.common.collect.Sets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.joda.time.LocalDate;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 * @see PartPageContentProvider
 */
@SuppressWarnings("restriction")
public class PartPageContentProviderTest extends AbstractDateCategoryContentProviderTest {

  @Override
  protected AbstractDateCategoryContentProvider createContentProvider(
      AbstractTreeViewerPage page, boolean displayByDate) {
    return new PartPageContentProvider((PartPage) page, displayByDate);
  }

  @Override
  protected PartPage createPage() {
    return new PartPage();
  }

  @Test
  public void testGetChildren() {
    PartDataDescriptor des1 = new PartDataDescriptor(
        new LocalDate(), 8723, "123");
    PartDataDescriptor des2 = new PartDataDescriptor(des1
        .getDate(), 18723, "12343");

    page.getViewer().setInput(Arrays.asList(des1, des2));
    Set<PartDataDescriptor> set = Sets.newHashSet(des1, des2);

    Object[] elements = contentProvider.getChildren(des1.getDate());
    assertEquals(2, elements.length);
    assertTrue(set.containsAll(Arrays.asList(elements)));
  }

  @Test
  public void testGetPart_nonExistId() {
    PartPageContentProvider contents = (PartPageContentProvider) contentProvider;
    PartDataDescriptor des = new PartDataDescriptor(
        new LocalDate(), 8723, System.currentTimeMillis() + "");
    IWorkbenchPartDescriptor part = contents.getPart(des);
    // A custom part descriptor is returned, never null:
    assertNotNull(part);
    assertEquals(des.getPartId(), part.getId());
  }

  @Test
  public void testGetValueOfPart_oneElement() {
    PartPageContentProvider contents = (PartPageContentProvider) contentProvider;
    PartDataDescriptor des = new PartDataDescriptor(
        new LocalDate(), 8723, "24");
    page.getViewer().setInput(Arrays.asList(des));
    assertEquals(des.getValue(), contents.getValueOfPart(contents
        .getPart(des)));
  }

  @Test
  public void testGetValueOfPart_twoElements() {
    PartPageContentProvider contents = (PartPageContentProvider) contentProvider;
    PartDataDescriptor des1 = new PartDataDescriptor(
        new LocalDate(), 8723, "24");
    PartDataDescriptor des2 = new PartDataDescriptor(des1
        .getDate().plusDays(1), 83276723, des1.getPartId());
    page.getViewer().setInput(Arrays.asList(des1, des2));
    assertEquals(des1.getValue() + des2.getValue(), contents
        .getValueOfPart(contents.getPart(des1)));
  }

  @Test
  public void testHasChildren() {
    PartDataDescriptor des = new PartDataDescriptor(
        new LocalDate(), 187, "a.b");
    page.getViewer().setInput(Arrays.asList(des));
    assertTrue(contentProvider.hasChildren(des.getDate()));
    assertFalse(contentProvider.hasChildren(des));
    assertFalse(contentProvider.hasChildren(des.getDate().plusDays(1)));
  }

  @Test
  public void testInputChanged_newInputNull_clearsExistingData() {
    PartDataDescriptor des1 = new PartDataDescriptor(
        new LocalDate(), 8723, "123");
    Set<PartDataDescriptor> set = Sets.newHashSet(des1);
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
    PartDataDescriptor des1 = new PartDataDescriptor(
        new LocalDate(), 8723, "123");
    Set<PartDataDescriptor> set = Sets.newHashSet(des1);
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
    PartDataDescriptor des1 = new PartDataDescriptor(
        new LocalDate(), 8723, "123");
    Set<PartDataDescriptor> set = Sets.newHashSet(des1);
    page.getViewer().setInput(set);

    // Disable for testing:
    contentProvider.setDisplayByDate(false);

    // Null is OK, the content provider is not using it...
    Object[] elements = contentProvider.getElements(null);
    assertEquals(1, elements.length);
    // We disabled displaying by date, so it should return parts as roots
    assertTrue(elements[0] instanceof IWorkbenchPartDescriptor);
  }
}
