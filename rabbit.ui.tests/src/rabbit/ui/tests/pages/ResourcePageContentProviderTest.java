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

import rabbit.data.IFileStore;
import rabbit.data.access.model.FileDataDescriptor;
import rabbit.data.handler.DataHandler;
import rabbit.ui.internal.pages.ResourcePage;
import rabbit.ui.internal.pages.ResourcePageContentProvider;
import rabbit.ui.internal.pages.ResourcePageContentProvider.Category;
import rabbit.ui.internal.util.ICategory;

import com.google.common.collect.Sets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

/**
 * @see ResourcePageContentProvider
 */
@SuppressWarnings("restriction")
public class ResourcePageContentProviderTest {

  private static Shell shell;
  private static ResourcePage page;
  private static IFileStore mapper;
  private static ResourcePageContentProvider provider;

  private static IProject project;
  private static IFolder folder;
  private static IFile file;

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new ResourcePage();
    page.createContents(shell);
    provider = new ResourcePageContentProvider(page.getViewer());
    page.getViewer().setContentProvider(provider);
    
    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    project = root.getProject("p");
    folder = project.getFolder("f");
    file = folder.getFile("a");
    
    mapper = DataHandler.getFileMapper();
  }

  @Test
  public void testHasChildren() throws Exception {
    FileDataDescriptor des = new FileDataDescriptor(new LocalDate(), 1009823,
        mapper.insert(file));
    page.getViewer().setInput(Arrays.asList(des));

    TreeNode root = provider.getRoot();
    provider.setSelectedCategories(Category.PROJECT);
    assertFalse(provider.hasChildren(root.getChildren()[0]));

    provider.setSelectedCategories(Category.PROJECT, Category.DATE);
    assertTrue(provider.hasChildren(root.getChildren()[0]));
    assertFalse(provider.hasChildren(root.getChildren()[0].getChildren()[0]));
  }

  @Test
  public void testGetChildren() throws Exception {
    // Two data descriptor of different dates, same file, different value:
    FileDataDescriptor d1 = new FileDataDescriptor(new LocalDate(), 1009823,
        mapper.insert(file));
    FileDataDescriptor d2 = new FileDataDescriptor(d1.getDate().minusDays(1),
        123, mapper.insert(file));

    page.getViewer().setInput(Arrays.asList(d1, d2));

    TreeNode root = provider.getRoot();
    // Set the data to categorize by file, then by dates:
    provider.setSelectedCategories(Category.FILE, Category.DATE);
    assertEquals(1, root.getChildren().length);
    TreeNode fileNode = root.getChildren()[0];
    assertTrue(fileNode.getValue() instanceof IFile);

    TreeNode[] dateNodes = (TreeNode[]) provider.getChildren(fileNode);
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
    // Two data descriptor of different dates, same file, different value:
    FileDataDescriptor d1 = new FileDataDescriptor(new LocalDate(), 1009823,
        mapper.insert(file));
    FileDataDescriptor d2 = new FileDataDescriptor(d1.getDate().minusDays(1),
        123, mapper.insert(file));

    page.getViewer().setInput(Arrays.asList(d1, d2));

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

    provider.setSelectedCategories(Category.FILE);
    assertEquals(1, provider.getElements(null).length);
    assertEquals(new TreeNode(file), provider.getElements(null)[0]);
  }

  @Test
  public void testGetMaxValue() {
    // Two data descriptor of different dates, same file, different value:
    FileDataDescriptor d1 = new FileDataDescriptor(new LocalDate(), 1009823,
        mapper.insert(file));
    FileDataDescriptor d2 = new FileDataDescriptor(d1.getDate().minusDays(1),
        123, mapper.insert(file));

    // Date
    page.getViewer().setInput(Arrays.asList(d1, d2));
    provider.setSelectedCategories(Category.DATE);
    provider.setPaintCategory(Category.DATE);
    assertEquals(d1.getValue(), provider.getMaxValue());

    // File
    // Set to Category.FILE so that the two data descriptors representing the
    // same file will be merged as a single tree node:
    provider.setSelectedCategories(Category.FILE);
    provider.setPaintCategory(Category.FILE);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
    // Separate the data descriptors by dates:
    provider.setSelectedCategories(Category.DATE, Category.FILE);
    assertEquals(d1.getValue(), provider.getMaxValue());

    // Folder
    provider.setSelectedCategories(Category.FOLDER);
    provider.setPaintCategory(Category.FOLDER);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
    provider.setSelectedCategories(Category.DATE, Category.FOLDER);
    assertEquals(d1.getValue(), provider.getMaxValue());

    // Project
    provider.setSelectedCategories(Category.PROJECT);
    provider.setPaintCategory(Category.PROJECT);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
    provider.setSelectedCategories(Category.DATE, Category.PROJECT);
    assertEquals(d1.getValue(), provider.getMaxValue());
  }

  @Test
  public void testGetSelectedCategories() {
    assertNotNull(provider.getSelectedCategories());
    // Should never be empty, if set to empty or null, defaults should be used:
    assertFalse(provider.getSelectedCategories().length == 0);
    ICategory[] categories = new ICategory[] { Category.DATE, Category.FILE };
    provider.setSelectedCategories(categories);
    assertArrayEquals(categories, provider.getSelectedCategories());

    categories = new ICategory[] { Category.PROJECT, Category.FOLDER };
    provider.setSelectedCategories(categories);
    assertArrayEquals(categories, provider.getSelectedCategories());
  }

  @Test
  public void testGetUnselectedCategories() {
    Set<Category> all = Sets.newHashSet(Category.DATE, Category.FILE,
        Category.FOLDER, Category.PROJECT);
    ICategory[] categories = all.toArray(new ICategory[all.size()]);
    provider.setSelectedCategories(categories);
    assertEquals(0, provider.getUnselectedCategories().length);

    categories = new ICategory[] { Category.DATE, Category.FILE };
    provider.setSelectedCategories(categories);

    Set<Category> unselect = Sets.difference(all, Sets.newHashSet(categories));
    assertEquals(unselect.size(), provider.getUnselectedCategories().length);
    assertTrue(unselect.containsAll(Arrays.asList(provider
        .getUnselectedCategories())));
  }

  @Test
  public void testGetValue() throws Exception {
    // Two data descriptor of different dates, same file, different value:
    FileDataDescriptor d1 = new FileDataDescriptor(new LocalDate(), 1009823,
        mapper.insert(file));
    FileDataDescriptor d2 = new FileDataDescriptor(d1.getDate().minusDays(1),
        123, mapper.insert(file));

    page.getViewer().setInput(Arrays.asList(d1, d2));

    TreeNode root = provider.getRoot();
    provider.setSelectedCategories(Category.FILE);
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
    FileDataDescriptor des = new FileDataDescriptor(new LocalDate(), 1009823,
        mapper.insert(file));
    page.getViewer().setInput(Arrays.asList(des));
    TreeNode root = provider.getRoot();
    assertFalse(root.getChildren() == null || root.getChildren().length == 0);
    try {
      provider.inputChanged(page.getViewer(), null, null);
      assertTrue(root.getChildren() == null || root.getChildren().length == 0);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testInputChanged_newInputNull() {
    try {
      provider.inputChanged(page.getViewer(), null, null);
    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetSelectedCategories_emptyArray() {
    try {
      ICategory[] cats = new ICategory[] { Category.FILE, Category.FOLDER };
      provider.setSelectedCategories(cats);
      assertArrayEquals(cats, provider.getSelectedCategories());

      provider.setSelectedCategories(new ICategory[0]);
      // The defaults:
      cats = new ICategory[] { Category.PROJECT, Category.FOLDER, Category.FILE };
      assertArrayEquals(cats, provider.getSelectedCategories());

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetSelectedCategories_emptyVararg() {
    try {
      ICategory[] cats = new ICategory[] { Category.FILE, Category.FOLDER };
      provider.setSelectedCategories(cats);
      assertArrayEquals(cats, provider.getSelectedCategories());

      provider.setSelectedCategories();
      // The defaults:
      cats = new ICategory[] { Category.PROJECT, Category.FOLDER, Category.FILE };
      assertArrayEquals(cats, provider.getSelectedCategories());

    } catch (Exception e) {
      fail();
    }
  }

  @Test
  public void testSetPaintCategory() {
    // Two data descriptor of different dates, same file, different value:
    FileDataDescriptor d1 = new FileDataDescriptor(new LocalDate(), 1009823,
        mapper.insert(file));
    FileDataDescriptor d2 = new FileDataDescriptor(d1.getDate().minusDays(1),
        123, mapper.insert(file));

    page.getViewer().setInput(Arrays.asList(d1, d2));

    provider.setSelectedCategories(Category.DATE);
    provider.setPaintCategory(Category.DATE);
    assertEquals(d1.getValue(), provider.getMaxValue());

    provider.setSelectedCategories(Category.FILE);
    provider.setPaintCategory(Category.FILE);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());

    provider.setSelectedCategories(Category.FOLDER);
    provider.setPaintCategory(Category.FOLDER);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());

    provider.setSelectedCategories(Category.PROJECT);
    provider.setPaintCategory(Category.PROJECT);
    assertEquals(d1.getValue() + d2.getValue(), provider.getMaxValue());
  }

  @Test
  public void testSetSelectedCategories() {
    ICategory[] cats = new ICategory[] { Category.FILE, Category.FOLDER };
    provider.setSelectedCategories(cats);
    assertArrayEquals(cats, provider.getSelectedCategories());

    cats = new ICategory[] { Category.FILE, Category.DATE, Category.PROJECT };
    provider.setSelectedCategories(cats);
    assertArrayEquals(cats, provider.getSelectedCategories());
  }

  @Test
  public void testShouldFilter() {
    TreeNode dateNode = new TreeNode(new LocalDate());
    TreeNode projectNode = new TreeNode(file.getProject());
    TreeNode folderNode = new TreeNode((IFolder) file.getParent());
    TreeNode fileNode = new TreeNode(file);
    
    provider.setSelectedCategories(Category.DATE);
    assertFalse(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(projectNode));
    assertTrue(provider.shouldFilter(folderNode));
    assertTrue(provider.shouldFilter(fileNode));
    
    provider.setSelectedCategories(Category.FILE);
    assertTrue(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(projectNode));
    assertTrue(provider.shouldFilter(folderNode));
    assertFalse(provider.shouldFilter(fileNode));

    provider.setSelectedCategories(Category.FOLDER);
    assertTrue(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(projectNode));
    assertFalse(provider.shouldFilter(folderNode));
    assertTrue(provider.shouldFilter(fileNode));

    provider.setSelectedCategories(Category.PROJECT);
    assertTrue(provider.shouldFilter(dateNode));
    assertFalse(provider.shouldFilter(projectNode));
    assertTrue(provider.shouldFilter(folderNode));
    assertTrue(provider.shouldFilter(fileNode));
  }

  @Test
  public void testShouldPaint() {
    TreeNode dateNode = new TreeNode(new LocalDate());
    TreeNode projectNode = new TreeNode(file.getProject());
    TreeNode folderNode = new TreeNode((IFolder) file.getParent());
    TreeNode fileNode = new TreeNode(file);
    
    provider.setPaintCategory(Category.DATE);
    assertTrue(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(projectNode));
    assertFalse(provider.shouldPaint(folderNode));
    assertFalse(provider.shouldPaint(fileNode));
    
    provider.setPaintCategory(Category.FILE);
    assertFalse(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(projectNode));
    assertFalse(provider.shouldPaint(folderNode));
    assertTrue(provider.shouldPaint(fileNode));

    provider.setPaintCategory(Category.FOLDER);
    assertFalse(provider.shouldPaint(dateNode));
    assertFalse(provider.shouldPaint(projectNode));
    assertTrue(provider.shouldPaint(folderNode));
    assertFalse(provider.shouldPaint(fileNode));

    provider.setPaintCategory(Category.PROJECT);
    assertFalse(provider.shouldPaint(dateNode));
    assertTrue(provider.shouldPaint(projectNode));
    assertFalse(provider.shouldPaint(folderNode));
    assertFalse(provider.shouldPaint(fileNode));
  }
}
