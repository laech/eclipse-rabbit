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

import rabbit.data.access.model.PartDataDescriptor;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.pages.PartPageContentProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

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
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.IViewDescriptor;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 * @see PartPageContentProvider
 */
public class PartPageContentProviderTest {

  private static Shell shell;
  private static PartPageContentProvider provider;

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    TreeViewer viewer = new TreeViewer(shell);
    provider = new PartPageContentProvider(viewer);
    viewer.setContentProvider(provider);
  }

  private final IWorkbenchPartDescriptor part = new UndefinedWorkbenchPartDescriptor(
      System.nanoTime() + "");

  @Test
  public void testCategories() {
    Set<ICategory> selected = Sets.newHashSet(provider.getSelectedCategories());
    Set<ICategory> unselected = Sets.newHashSet(provider.getUnselectedCategories());
    assertEquals(0, Sets.intersection(selected, unselected).size());

    Set<ICategory> all = Sets.union(selected, unselected);
    Set<Category> set = Sets.newHashSet(Category.DATE, Category.WORKBENCH_TOOL);
    assertEquals(set.size(), all.size());
    assertEquals(0, Sets.difference(all, set).size());
  }

  @Test
  public void testGetChildren() throws Exception {
    // Two data descriptor of different dates, same id, different value:
    PartDataDescriptor d1 = new PartDataDescriptor(new LocalDate(),
        new Duration(1), "id");
    PartDataDescriptor d2 = new PartDataDescriptor(d1.getDate().minusDays(1),
        new Duration(2), d1.getPartId());

    provider.getViewer().setInput(Arrays.asList(d1, d2));

    TreeNode root = provider.getRoot();
    // Set the data to categorize by part, then by dates:
    provider.setSelectedCategories(Category.WORKBENCH_TOOL, Category.DATE);
    assertEquals(1, root.getChildren().length);
    TreeNode partNode = root.getChildren()[0];
    assertTrue(partNode.getValue() instanceof IWorkbenchPartDescriptor);

    TreeNode[] dateNodes = (TreeNode[]) provider.getChildren(partNode);
    assertEquals(2, dateNodes.length);
    assertTrue(dateNodes[0].getValue() instanceof LocalDate);
    assertTrue(dateNodes[1].getValue() instanceof LocalDate);
    Set<Object> set = Sets.newHashSet(dateNodes[0].getValue(),
        dateNodes[1].getValue());
    assertTrue(set.contains(d1.getDate()));
    assertTrue(set.contains(d2.getDate()));
  }

  @Test
  public void testGetElement() throws Exception {
    // Two data descriptor of different dates, same id, different value:
    PartDataDescriptor d1 = new PartDataDescriptor(new LocalDate(),
        new Duration(1), part.getId());
    PartDataDescriptor d2 = new PartDataDescriptor(d1.getDate().minusDays(1),
        new Duration(2), d1.getPartId());

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

    provider.setSelectedCategories(Category.WORKBENCH_TOOL);
    assertEquals(1, provider.getElements(null).length);
    assertEquals(new TreeNode(part), provider.getElements(null)[0]);
  }

  @Test
  public void testGetMaxValue() {
    // Two data descriptor of different dates, same id, different value:
    PartDataDescriptor d1 = new PartDataDescriptor(new LocalDate(),
        new Duration(100), "id");
    PartDataDescriptor d2 = new PartDataDescriptor(d1.getDate().minusDays(1),
        new Duration(2), d1.getPartId());

    // Date
    provider.getViewer().setInput(Arrays.asList(d1, d2));
    provider.setSelectedCategories(Category.DATE);
    provider.setPaintCategory(Category.DATE);
    assertEquals(d1.getDuration().getMillis(), provider.getMaxValue());

    // Part
    // Set to Category.WORKBENCH_TOOL so that the two data descriptors
    // representing the
    // same part will be merged as a single tree node:
    provider.setSelectedCategories(Category.WORKBENCH_TOOL);
    provider.setPaintCategory(Category.WORKBENCH_TOOL);
    assertEquals(d1.getDuration().getMillis() + d2.getDuration().getMillis(),
        provider.getMaxValue());
    // Separate the data descriptors by dates:
    provider.setSelectedCategories(Category.DATE, Category.WORKBENCH_TOOL);
    assertEquals(d1.getDuration().getMillis(), provider.getMaxValue());
  }

  @Test
  public void testGetSelectedCategories() {
    assertNotNull(provider.getSelectedCategories());
    // Should never be empty, if set to empty or null, defaults should be used:
    assertFalse(provider.getSelectedCategories().length == 0);
    ICategory[] categories = new ICategory[]{
        Category.DATE, Category.WORKBENCH_TOOL};
    provider.setSelectedCategories(categories);
    assertArrayEquals(categories, provider.getSelectedCategories());

    categories = new ICategory[]{Category.WORKBENCH_TOOL, Category.DATE};
    provider.setSelectedCategories(categories);
    assertArrayEquals(categories, provider.getSelectedCategories());
  }

  @Test
  public void testGetUnselectedCategories() {
    Set<Category> all = Sets.newHashSet(Category.DATE, Category.WORKBENCH_TOOL);
    ICategory[] categories = all.toArray(new ICategory[all.size()]);
    provider.setSelectedCategories(categories);
    assertEquals(0, provider.getUnselectedCategories().length);

    categories = new ICategory[]{Category.DATE};
    provider.setSelectedCategories(categories);

    Set<Category> unselect = Sets.difference(all, Sets.newHashSet(categories));
    assertEquals(unselect.size(), provider.getUnselectedCategories().length);
    assertTrue(unselect.containsAll(Arrays.asList(provider.getUnselectedCategories())));
  }

  @Test
  public void testGetValue() throws Exception {
    // Two data descriptor of different dates, same id, different value:
    PartDataDescriptor d1 = new PartDataDescriptor(new LocalDate(),
        new Duration(1), "id");
    PartDataDescriptor d2 = new PartDataDescriptor(d1.getDate().minusDays(1),
        new Duration(2), d1.getPartId());

    provider.getViewer().setInput(Arrays.asList(d1, d2));

    TreeNode root = provider.getRoot();
    provider.setSelectedCategories(Category.WORKBENCH_TOOL);
    TreeNode fileNode = root.getChildren()[0];
    assertEquals(d1.getDuration().getMillis() + d2.getDuration().getMillis(),
        provider.getValue(fileNode));

    provider.setSelectedCategories(Category.DATE);
    TreeNode[] dateNodes = root.getChildren();
    assertEquals(2, dateNodes.length);
    assertEquals(d1.getDuration().getMillis(), provider.getValue(dateNodes[0]));
    assertEquals(d2.getDuration().getMillis(), provider.getValue(dateNodes[1]));
  }

  @Test
  public void testHasChildren() throws Exception {
    PartDataDescriptor des = new PartDataDescriptor(new LocalDate(),
        new Duration(1), "id");
    provider.getViewer().setInput(Arrays.asList(des));

    TreeNode root = provider.getRoot();
    provider.setSelectedCategories(Category.DATE);
    assertFalse(provider.hasChildren(root.getChildren()[0]));

    provider.setSelectedCategories(Category.WORKBENCH_TOOL, Category.DATE);
    assertTrue(provider.hasChildren(root.getChildren()[0]));
    assertFalse(provider.hasChildren(root.getChildren()[0].getChildren()[0]));
  }

  @Test
  public void testInputChanged_clearsOldData() throws Exception {
    PartDataDescriptor des = new PartDataDescriptor(new LocalDate(),
        new Duration(1), "id");
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
  public void testInputChanged_invalidInput() {
    try {
      provider.inputChanged(provider.getViewer(), new Object(), new Object());
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testIsHidingEditors() {
    provider.setHideEditors(true);
    assertTrue(provider.isHidingEditors());
    provider.setHideEditors(false);
    assertFalse(provider.isHidingEditors());
  }

  @Test
  public void testIsHidingEditors_defaultFalse() {
    assertFalse(new PartPageContentProvider(new TreeViewer(shell)).isHidingEditors());
  }

  @Test
  public void testIsHidingViews() {
    provider.setHideViews(true);
    assertTrue(provider.isHidingViews());
    provider.setHideViews(false);
    assertFalse(provider.isHidingViews());
  }

  @Test
  public void testIsHidingViews_defaultFalse() {
    assertFalse(new PartPageContentProvider(new TreeViewer(shell)).isHidingViews());
  }

  @Test
  public void testSetHideEditors() {
    IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(
        "org.eclipse.ui.DefaultTextEditor");
    assertNotNull(editor);
    provider.setHideEditors(true);
    assertTrue(provider.shouldFilter(new TreeNode(editor)));
    provider.setHideEditors(false);
    assertFalse(provider.shouldFilter(new TreeNode(editor)));
  }

  @Test
  public void testSetHideEditors_maxValueUpdated() {
    IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(
        "org.eclipse.ui.DefaultTextEditor");
    assertNotNull(editor);
    IViewDescriptor view = PlatformUI.getWorkbench().getViewRegistry().getViews()[0];

    PartDataDescriptor editorDes = new PartDataDescriptor(new LocalDate(),
        new Duration(1), editor.getId());
    PartDataDescriptor viewDes = new PartDataDescriptor(new LocalDate(),
        new Duration(editorDes.getDuration().getMillis() - 1), view.getId());
    provider.getViewer().setInput(Arrays.asList(editorDes, viewDes));

    provider.setHideEditors(true);
    assertEquals(viewDes.getDuration().getMillis(), provider.getMaxValue());
    provider.setHideEditors(false);
    assertEquals(editorDes.getDuration().getMillis(), provider.getMaxValue());
  }

  @Test
  public void testSetHideEditors_undefinedPart() {
    IWorkbenchPartDescriptor undefined = new UndefinedWorkbenchPartDescriptor(
        "1");
    provider.setHideEditors(true);
    assertFalse(provider.shouldFilter(new TreeNode(undefined)));
    provider.setHideEditors(false);
    assertFalse(provider.shouldFilter(new TreeNode(undefined)));
  }

  @Test
  public void testSetHideViews() {
    IViewDescriptor view = PlatformUI.getWorkbench().getViewRegistry().getViews()[0];
    provider.setHideViews(true);
    assertTrue(provider.shouldFilter(new TreeNode(view)));
    provider.setHideViews(false);
    assertFalse(provider.shouldFilter(new TreeNode(view)));
  }

  @Test
  public void testSetHideViews_maxValueUpdated() {
    IEditorDescriptor editor = PlatformUI.getWorkbench().getEditorRegistry().findEditor(
        "org.eclipse.ui.DefaultTextEditor");
    assertNotNull(editor);
    IViewDescriptor view = PlatformUI.getWorkbench().getViewRegistry().getViews()[0];

    PartDataDescriptor viewDes = new PartDataDescriptor(new LocalDate(),
        new Duration(10), view.getId());
    PartDataDescriptor editorDes = new PartDataDescriptor(new LocalDate(),
        new Duration(viewDes.getDuration().getMillis() - 1), editor.getId());
    provider.getViewer().setInput(Arrays.asList(editorDes, viewDes));

    provider.setHideViews(true);
    assertEquals(editorDes.getDuration().getMillis(), provider.getMaxValue());
    provider.setHideViews(false);
    assertEquals(viewDes.getDuration().getMillis(), provider.getMaxValue());
  }

  @Test
  public void testSetHideViews_undefinedPart() {
    IWorkbenchPartDescriptor undefined = new UndefinedWorkbenchPartDescriptor(
        "1");
    provider.setHideViews(true);
    assertFalse(provider.shouldFilter(new TreeNode(undefined)));
    provider.setHideViews(false);
    assertFalse(provider.shouldFilter(new TreeNode(undefined)));
  }

  @Test
  public void testSetPaintCategory() {
    // Two data descriptor of different dates, same id, different value:
    PartDataDescriptor d1 = new PartDataDescriptor(new LocalDate(),
        new Duration(100), "id");
    PartDataDescriptor d2 = new PartDataDescriptor(d1.getDate().minusDays(1),
        new Duration(2), d1.getPartId());

    provider.getViewer().setInput(Arrays.asList(d1, d2));

    provider.setSelectedCategories(Category.DATE);
    provider.setPaintCategory(Category.DATE);
    assertEquals(d1.getDuration().getMillis(), provider.getMaxValue());

    provider.setSelectedCategories(Category.WORKBENCH_TOOL);
    provider.setPaintCategory(Category.WORKBENCH_TOOL);
    assertEquals(d1.getDuration().getMillis() + d2.getDuration().getMillis(),
        provider.getMaxValue());
  }

  @Test
  public void testSetSelectedCategories() {
    ICategory[] cats = new ICategory[]{Category.WORKBENCH_TOOL, Category.DATE};
    provider.setSelectedCategories(cats);
    assertArrayEquals(cats, provider.getSelectedCategories());

    cats = new ICategory[]{Category.DATE};
    provider.setSelectedCategories(cats);
    assertArrayEquals(cats, provider.getSelectedCategories());
  }

  @Test
  public void testSetSelectedCategories_emptyArray() {
    try {
      ICategory[] cats = new ICategory[]{Category.WORKBENCH_TOOL, Category.DATE};
      provider.setSelectedCategories(cats);
      assertArrayEquals(cats, provider.getSelectedCategories());

      provider.setSelectedCategories(new ICategory[0]);
      // The defaults:
      cats = new ICategory[]{Category.WORKBENCH_TOOL};
      assertArrayEquals(cats, provider.getSelectedCategories());

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetSelectedCategories_emptyVararg() {
    try {
      ICategory[] cats = new ICategory[]{Category.DATE, Category.WORKBENCH_TOOL};
      provider.setSelectedCategories(cats);
      assertArrayEquals(cats, provider.getSelectedCategories());

      provider.setSelectedCategories();
      // The defaults:
      cats = new ICategory[]{Category.WORKBENCH_TOOL};
      assertArrayEquals(cats, provider.getSelectedCategories());

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testShouldFilter() {
    TreeNode dateNode = new TreeNode(new LocalDate());
    TreeNode partNode = new TreeNode(part);

    provider.setSelectedCategories(Category.DATE);
    assertFalse(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(partNode));

    provider.setSelectedCategories(Category.WORKBENCH_TOOL);
    assertTrue(provider.shouldFilter(dateNode));
    assertFalse(provider.shouldFilter(partNode));
  }

  @Test
  public void testShouldPaint() {
    TreeNode dateNode = new TreeNode(new LocalDate());
    TreeNode partNode = new TreeNode(part);

    provider.setPaintCategory(Category.DATE);
    assertTrue(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(partNode));

    provider.setPaintCategory(Category.WORKBENCH_TOOL);
    assertFalse(provider.shouldPaint(dateNode));
    assertTrue(provider.shouldPaint(partNode));
  }
}
