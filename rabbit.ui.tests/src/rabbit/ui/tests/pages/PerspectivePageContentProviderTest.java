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

import rabbit.data.access.model.PerspectiveDataDescriptor;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.pages.PerspectivePageContentProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

import com.google.common.collect.Sets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 * @see PerspectivePageContentProvider
 */
@SuppressWarnings("restriction")
public class PerspectivePageContentProviderTest {

  private static Shell shell;
  private static PerspectivePageContentProvider provider;
  private final IPerspectiveDescriptor perspective = new UndefinedPerspectiveDescriptor(System.nanoTime() + "");


  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    TreeViewer viewer = new TreeViewer(shell);
    provider = new PerspectivePageContentProvider(viewer);
    viewer.setContentProvider(provider);
  }

  @Test
  public void testHasChildren() throws Exception {
    PerspectiveDataDescriptor des = new PerspectiveDataDescriptor(new LocalDate(), 1, "id");
    provider.getViewer().setInput(Arrays.asList(des));

    TreeNode root = provider.getRoot();
    provider.setSelectedCategories(Category.DATE);
    assertFalse(provider.hasChildren(root.getChildren()[0]));

    provider.setSelectedCategories(Category.PERSPECTIVE, Category.DATE);
    assertTrue(provider.hasChildren(root.getChildren()[0]));
    assertFalse(provider.hasChildren(root.getChildren()[0].getChildren()[0]));
  }

  @Test
  public void testGetChildren() throws Exception {
    // Two data descriptor of different dates, same id, different value:
    PerspectiveDataDescriptor d1 = new PerspectiveDataDescriptor(new LocalDate(), 1, "id");
    PerspectiveDataDescriptor d2 = new PerspectiveDataDescriptor(d1.getDate().minusDays(1), 2, d1.getPerspectiveId());

    provider.getViewer().setInput(Arrays.asList(d1, d2));

    TreeNode root = provider.getRoot();
    // Set the data to categorize by perspective, then by dates:
    provider.setSelectedCategories(Category.PERSPECTIVE, Category.DATE);
    assertEquals(1, root.getChildren().length);
    TreeNode perspectiveNode = root.getChildren()[0];
    assertTrue(perspectiveNode.getValue() instanceof IPerspectiveDescriptor);

    TreeNode[] dateNodes = (TreeNode[]) provider.getChildren(perspectiveNode);
    assertEquals(2, dateNodes.length);
    assertTrue(dateNodes[0].getValue() instanceof LocalDate);
    assertTrue(dateNodes[1].getValue() instanceof LocalDate);
    Set<Object> set = Sets.newHashSet(dateNodes[0].getValue(), dateNodes[1]
        .getValue());
    assertTrue(set.contains(d1.getDate()));
    assertTrue(set.contains(d2.getDate()));
  }

  @Test
  public void testGetElement() throws Exception {
    // Two data descriptor of different dates, same id, different value:
    PerspectiveDataDescriptor d1 = new PerspectiveDataDescriptor(new LocalDate(), 1, perspective.getId());
    PerspectiveDataDescriptor d2 = new PerspectiveDataDescriptor(d1.getDate().minusDays(1), 2, d1.getPerspectiveId());

    provider.getViewer().setInput(Arrays.asList(d1, d2));

    provider.setSelectedCategories(Category.DATE);
    // Passing null is OK, the provider should return the children of its "root"
    // Size is two, because we defined two data descriptors of different dates:
    assertEquals(2, provider.getElements(null).length);
    TreeNode[] nodes = (TreeNode[]) provider.getElements(null);
    assertTrue(nodes[0].getValue() instanceof LocalDate);
    assertTrue(nodes[1].getValue() instanceof LocalDate);
    Set<LocalDate> dates = Sets.newTreeSet();
    dates.add((LocalDate) nodes[0].getValue());
    dates.add((LocalDate) nodes[1].getValue());
    assertTrue(dates.contains(d1.getDate()));
    assertTrue(dates.contains(d2.getDate()));

    provider.setSelectedCategories(Category.PERSPECTIVE);
    assertEquals(1, provider.getElements(null).length);
    assertEquals(new TreeNode(perspective), provider.getElements(null)[0]);
  }

  @Test
  public void testGetMaxValue() {
    // Two data descriptor of different dates, same id, different value:
    PerspectiveDataDescriptor d1 = new PerspectiveDataDescriptor(new LocalDate(), 100, "id");
    PerspectiveDataDescriptor d2 = new PerspectiveDataDescriptor(d1.getDate().minusDays(1), 2, d1.getPerspectiveId());

    // Date
    provider.getViewer().setInput(Arrays.asList(d1, d2));
    provider.setSelectedCategories(Category.DATE);
    provider.setPaintCategory(Category.DATE);
    assertEquals(d1.getValue(), provider.getMaxValue());

    // Perspective
    // Set to Category.PERSPECTIVE so that the two data descriptors representing the
    // same perspective will be merged as a single tree node:
    provider.setSelectedCategories(Category.PERSPECTIVE);
    provider.setPaintCategory(Category.PERSPECTIVE);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
    // Separate the data descriptors by dates:
    provider.setSelectedCategories(Category.DATE, Category.PERSPECTIVE);
    assertEquals(d1.getValue(), provider.getMaxValue());
  }

  @Test
  public void testGetSelectedCategories() {
    assertNotNull(provider.getSelectedCategories());
    // Should never be empty, if set to empty or null, defaults should be used:
    assertFalse(provider.getSelectedCategories().length == 0);
    ICategory[] categories = new ICategory[] { Category.DATE, Category.PERSPECTIVE };
    provider.setSelectedCategories(categories);
    assertArrayEquals(categories, provider.getSelectedCategories());

    categories = new ICategory[] { Category.PERSPECTIVE, Category.DATE };
    provider.setSelectedCategories(categories);
    assertArrayEquals(categories, provider.getSelectedCategories());
  }

  @Test
  public void testGetUnselectedCategories() {
    Set<Category> all = Sets.newHashSet(Category.DATE, Category.PERSPECTIVE);
    ICategory[] categories = all.toArray(new ICategory[all.size()]);
    provider.setSelectedCategories(categories);
    assertEquals(0, provider.getUnselectedCategories().length);

    categories = new ICategory[] { Category.DATE };
    provider.setSelectedCategories(categories);

    Set<Category> unselect = Sets.difference(all, Sets.newHashSet(categories));
    assertEquals(unselect.size(), provider.getUnselectedCategories().length);
    assertTrue(unselect.containsAll(Arrays.asList(provider
        .getUnselectedCategories())));
  }

  @Test
  public void testGetValue() throws Exception {
    // Two data descriptor of different dates, same id, different value:
    PerspectiveDataDescriptor d1 = new PerspectiveDataDescriptor(new LocalDate(), 1, "id");
    PerspectiveDataDescriptor d2 = new PerspectiveDataDescriptor(d1.getDate().minusDays(1), 2, d1.getPerspectiveId());

    provider.getViewer().setInput(Arrays.asList(d1, d2));

    TreeNode root = provider.getRoot();
    provider.setSelectedCategories(Category.PERSPECTIVE);
    TreeNode fileNode = root.getChildren()[0];
    assertEquals(d1.getValue() + d2.getValue(), provider.getValue(fileNode));

    provider.setSelectedCategories(Category.DATE);
    TreeNode[] dateNodes = root.getChildren();
    assertEquals(2, dateNodes.length);
    assertEquals(d1.getValue(), provider.getValue(dateNodes[0]));
    assertEquals(d2.getValue(), provider.getValue(dateNodes[1]));
  }

  @Test
  public void testInputChanged_clearsOldData() throws Exception {
    PerspectiveDataDescriptor des = new PerspectiveDataDescriptor(new LocalDate(), 1, "id");
    provider.getViewer().setInput(Arrays.asList(des));
    TreeNode root = provider.getRoot();
    assertFalse(root.getChildren() == null || root.getChildren().length == 0);
    try {
      provider.inputChanged(provider.getViewer(), null, null);
      assertTrue(root.getChildren() == null || root.getChildren().length == 0);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testInputChanged_newInputNull() {
    try {
      provider.inputChanged(provider.getViewer(), null, null);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetSelectedCategories_emptyArray() {
    try {
      ICategory[] cats = new ICategory[] { Category.PERSPECTIVE, Category.DATE };
      provider.setSelectedCategories(cats);
      assertArrayEquals(cats, provider.getSelectedCategories());

      provider.setSelectedCategories(new ICategory[0]);
      // The defaults:
      cats = new ICategory[] { Category.PERSPECTIVE };
      assertArrayEquals(cats, provider.getSelectedCategories());

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetSelectedCategories_emptyVararg() {
    try {
      ICategory[] cats = new ICategory[] { Category.DATE, Category.PERSPECTIVE };
      provider.setSelectedCategories(cats);
      assertArrayEquals(cats, provider.getSelectedCategories());

      provider.setSelectedCategories();
      // The defaults:
      cats = new ICategory[] { Category.PERSPECTIVE };
      assertArrayEquals(cats, provider.getSelectedCategories());

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetPaintCategory() {
    // Two data descriptor of different dates, same id, different value:
    PerspectiveDataDescriptor d1 = new PerspectiveDataDescriptor(new LocalDate(), 1000, "id");
    PerspectiveDataDescriptor d2 = new PerspectiveDataDescriptor(d1.getDate().minusDays(1), 2, d1.getPerspectiveId());

    provider.getViewer().setInput(Arrays.asList(d1, d2));

    provider.setSelectedCategories(Category.DATE);
    provider.setPaintCategory(Category.DATE);
    assertEquals(d1.getValue(), provider.getMaxValue());

    provider.setSelectedCategories(Category.PERSPECTIVE);
    provider.setPaintCategory(Category.PERSPECTIVE);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
  }

  @Test
  public void testSetSelectedCategories() {
    ICategory[] cats = new ICategory[] { Category.PERSPECTIVE, Category.DATE };
    provider.setSelectedCategories(cats);
    assertArrayEquals(cats, provider.getSelectedCategories());

    cats = new ICategory[] { Category.DATE };
    provider.setSelectedCategories(cats);
    assertArrayEquals(cats, provider.getSelectedCategories());
  }

  @Test
  public void testShouldFilter() {
    TreeNode dateNode = new TreeNode(new LocalDate());
    TreeNode perspectiveNode = new TreeNode(perspective);
    
    provider.setSelectedCategories(Category.DATE);
    assertFalse(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(perspectiveNode));
    
    provider.setSelectedCategories(Category.PERSPECTIVE);
    assertTrue(provider.shouldFilter(dateNode));
    assertFalse(provider.shouldFilter(perspectiveNode));
  }

  @Test
  public void testShouldPaint() {
    TreeNode dateNode = new TreeNode(new LocalDate());
    TreeNode perspectiveNode = new TreeNode(perspective);
    
    provider.setPaintCategory(Category.DATE);
    assertTrue(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(perspectiveNode));
    
    provider.setPaintCategory(Category.PERSPECTIVE);
    assertFalse(provider.shouldPaint(dateNode));
    assertTrue(provider.shouldPaint(perspectiveNode));
  }
}
