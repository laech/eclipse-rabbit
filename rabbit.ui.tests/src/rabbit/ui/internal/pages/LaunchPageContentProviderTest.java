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

import rabbit.data.access.model.LaunchConfigurationDescriptor;
import rabbit.data.access.model.LaunchDataDescriptor;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.pages.LaunchPageContentProvider;
import rabbit.ui.internal.util.ICategory;
import rabbit.ui.internal.util.Pair;
import rabbit.ui.internal.util.UndefinedLaunchConfigurationType;
import rabbit.ui.internal.util.UndefinedLaunchMode;
import rabbit.ui.internal.viewers.CellPainter.IValueProvider;

import com.google.common.collect.Sets;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;

/**
 * @see LaunchPageContentProvider
 */
public class LaunchPageContentProviderTest {

  private static Shell shell;
  private static LaunchPageContentProvider provider;

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    TreeViewer viewer = new TreeViewer(shell);
    provider = new LaunchPageContentProvider(viewer);
    viewer.setContentProvider(provider);
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_viewerNull() {
    new LaunchPageContentProvider(null);
  }

  @Test
  public void testCategories() {
    Set<ICategory> selected = Sets.newHashSet(provider.getSelectedCategories());
    Set<ICategory> unselected = Sets.newHashSet(provider.getUnselectedCategories());
    assertEquals(0, Sets.intersection(selected, unselected).size());

    Set<ICategory> all = Sets.union(selected, unselected);
    Set<Category> set = Sets.newHashSet(Category.DATE, Category.LAUNCH,
        Category.LAUNCH_MODE, Category.LAUNCH_TYPE);
    assertEquals(set.size(), all.size());
    assertEquals(0, Sets.difference(all, set).size());
  }

  @Test
  public void testGetChildren() {
    /*
     * Build a tree that look like the following, then check the value of each
     * node against the data:
     * 
     * +-- RunMode --- Date | +-- DebugMode --- Date
     */
    LaunchConfigurationDescriptor runMode = new LaunchConfigurationDescriptor(
        "Name", ILaunchManager.RUN_MODE, "org.eclipse.pde.ui.RuntimeWorkbench");
    LaunchConfigurationDescriptor debugMode = new LaunchConfigurationDescriptor(
        runMode.getLaunchName(), ILaunchManager.DEBUG_MODE,
        runMode.getLaunchTypeId());

    LaunchDataDescriptor d1 = new LaunchDataDescriptor(new LocalDate(),
        runMode, 1, new Duration(10), Collections.<IPath> emptySet());
    LaunchDataDescriptor d2 = new LaunchDataDescriptor(d1.getDate(), debugMode,
        19, new Duration(1200), Collections.<IPath> emptySet());

    provider.getViewer().setInput(Arrays.asList(d1, d2));
    provider.setSelectedCategories(Category.LAUNCH_MODE, Category.DATE);

    // We should have two different modes:
    TreeNode[] launchModes = (TreeNode[]) provider.getElements(null);
    assertNotNull(launchModes);
    assertEquals(2, launchModes.length);

    // Each mode should have a date as their child:
    assertNotNull(provider.getChildren(launchModes[0]));
    assertEquals(1, provider.getChildren(launchModes[0]).length);
    TreeNode dateNode = (TreeNode) provider.getChildren(launchModes[0])[0];
    assertEquals(d1.getDate(), dateNode.getValue());
    assertFalse(provider.hasChildren(dateNode));

    assertNotNull(provider.getChildren(launchModes[1]));
    assertEquals(1, provider.getChildren(launchModes[1]).length);
    dateNode = (TreeNode) provider.getChildren(launchModes[1])[0];
    assertEquals(d1.getDate(), dateNode.getValue());
    assertFalse(provider.hasChildren(dateNode));
  }

  @Test
  public void testGetElements() {
    LaunchConfigurationDescriptor config = new LaunchConfigurationDescriptor(
        "Name", ILaunchManager.RUN_MODE, "org.eclipse.pde.ui.RuntimeWorkbench");

    LaunchDataDescriptor d1 = new LaunchDataDescriptor(new LocalDate(), config,
        0, new Duration(10), Collections.<IPath> emptySet());
    LaunchDataDescriptor d2 = new LaunchDataDescriptor(
        d1.getDate().plusDays(1), config, 110, new Duration(1800),
        Collections.<IPath> emptySet());

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

    provider.setSelectedCategories(Category.LAUNCH_MODE);
    assertEquals(1, provider.getElements(null).length);
  }

  @Test
  public void testGetLaunchCountValueProvider() {
    assertNotNull(provider.getLaunchCountValueProvider());
  }

  @Test
  public void testGetLaunchCountValueProvider_getMaxValue() {
    /*
     * Build a tree that look like the following, then check the value of each
     * node against the data:
     * 
     * Date +-- LaunchType +-- Launch +-- RunMode | +-- DebugMode
     */

    LaunchConfigurationDescriptor runMode = new LaunchConfigurationDescriptor(
        "Name", ILaunchManager.RUN_MODE, "org.eclipse.pde.ui.RuntimeWorkbench");
    LaunchConfigurationDescriptor debugMode = new LaunchConfigurationDescriptor(
        runMode.getLaunchName(), ILaunchManager.DEBUG_MODE,
        runMode.getLaunchTypeId());

    LaunchDataDescriptor d1 = new LaunchDataDescriptor(new LocalDate(),
        runMode, 1, new Duration(10), Collections.<IPath> emptySet());
    LaunchDataDescriptor d2 = new LaunchDataDescriptor(d1.getDate(), debugMode,
        19, new Duration(1200), Collections.<IPath> emptySet());

    IValueProvider values = provider.getLaunchCountValueProvider();
    provider.getViewer().setInput(Arrays.asList(d1, d2));
    provider.setSelectedCategories(Category.DATE, Category.LAUNCH_TYPE,
        Category.LAUNCH, Category.LAUNCH_MODE);

    provider.setPaintCategory(Category.DATE);
    assertEquals(d1.getLaunchCount() + d2.getLaunchCount(),
        values.getMaxValue());

    provider.setPaintCategory(Category.LAUNCH_TYPE);
    assertEquals(d1.getLaunchCount() + d2.getLaunchCount(),
        values.getMaxValue());

    provider.setPaintCategory(Category.LAUNCH);
    assertEquals(d1.getLaunchCount() + d2.getLaunchCount(),
        values.getMaxValue());

    provider.setPaintCategory(Category.LAUNCH_MODE);
    assertEquals(d2.getLaunchCount(), values.getMaxValue());
  }

  @Test
  public void testGetLaunchCountValueProvider_getValue() {
    /*
     * Build a tree that look like the following, then check the value of each
     * node against the data:
     * 
     * Date +-- LaunchType +-- Launch +-- RunMode | +-- DebugMode
     */

    LaunchConfigurationDescriptor runMode = new LaunchConfigurationDescriptor(
        "Name", ILaunchManager.RUN_MODE, "org.eclipse.pde.ui.RuntimeWorkbench");
    LaunchConfigurationDescriptor debugMode = new LaunchConfigurationDescriptor(
        runMode.getLaunchName(), ILaunchManager.DEBUG_MODE,
        runMode.getLaunchTypeId());
    LaunchDataDescriptor d1 = new LaunchDataDescriptor(new LocalDate(),
        runMode, 1, new Duration(10), Collections.<IPath> emptySet());
    LaunchDataDescriptor d2 = new LaunchDataDescriptor(d1.getDate(), debugMode,
        19, new Duration(1200), Collections.<IPath> emptySet());

    provider.getViewer().setInput(Arrays.asList(d1, d2));
    provider.setSelectedCategories(Category.DATE, Category.LAUNCH_TYPE,
        Category.LAUNCH, Category.LAUNCH_MODE);
    IValueProvider values = provider.getLaunchCountValueProvider();
    TreeNode root = provider.getRoot();

    provider.setPaintCategory(Category.DATE);
    assertEquals(d1.getLaunchCount() + d2.getLaunchCount(),
        values.getValue(root.getChildren()[0]));

    provider.setPaintCategory(Category.LAUNCH_TYPE);
    assertEquals(d1.getLaunchCount() + d2.getLaunchCount(),
        values.getValue(root.getChildren()[0]));

    provider.setPaintCategory(Category.LAUNCH);
    assertEquals(d1.getLaunchCount() + d2.getLaunchCount(),
        values.getValue(root.getChildren()[0]));

    provider.setPaintCategory(Category.LAUNCH_MODE);
    provider.setSelectedCategories(Category.LAUNCH_MODE);
    TreeNode[] nodes = root.getChildren();
    assertEquals(2, nodes.length);
    assertEquals(d1.getLaunchCount(), values.getValue(nodes[0]));
    assertEquals(d2.getLaunchCount(), values.getValue(nodes[1]));
  }

  @Test
  public void testGetLaunchCountValueProvider_shouldPaint() {
    assertShouldPaint(provider.getLaunchCountValueProvider());
  }

  @Test
  public void testGetLaunchDurationValueProvider() {
    assertNotNull(provider.getLaunchDurationValueProvider());
  }

  @Test
  public void testGetLaunchDurationValueProvider_getMaxValue() {
    /*
     * Build a tree that look like the following, then check the value of each
     * node against the data:
     * 
     * Date +-- LaunchType +-- Launch +-- RunMode | +-- DebugMode
     */

    LaunchConfigurationDescriptor runMode = new LaunchConfigurationDescriptor(
        "Name", ILaunchManager.RUN_MODE, "org.eclipse.pde.ui.RuntimeWorkbench");
    LaunchConfigurationDescriptor debugMode = new LaunchConfigurationDescriptor(
        runMode.getLaunchName(), ILaunchManager.DEBUG_MODE,
        runMode.getLaunchTypeId());

    LaunchDataDescriptor d1 = new LaunchDataDescriptor(new LocalDate(),
        runMode, 1, new Duration(10), Collections.<IPath> emptySet());
    LaunchDataDescriptor d2 = new LaunchDataDescriptor(d1.getDate(), debugMode,
        19, new Duration(1200), Collections.<IPath> emptySet());

    IValueProvider values = provider.getLaunchDurationValueProvider();
    provider.setSelectedCategories(Category.DATE, Category.LAUNCH_TYPE,
        Category.LAUNCH, Category.LAUNCH_MODE);

    provider.setPaintCategory(Category.DATE);
    assertEquals(d1.getDuration().getMillis() + d2.getDuration().getMillis(),
        values.getMaxValue());

    provider.setPaintCategory(Category.LAUNCH_TYPE);
    assertEquals(d1.getDuration().getMillis() + d2.getDuration().getMillis(),
        values.getMaxValue());

    provider.setPaintCategory(Category.LAUNCH);
    assertEquals(d1.getDuration().getMillis() + d2.getDuration().getMillis(),
        values.getMaxValue());

    provider.setPaintCategory(Category.LAUNCH_MODE);
    assertEquals(d2.getDuration().getMillis(), values.getMaxValue());
  }

  @Test
  public void testGetLaunchDurationValueProvider_getValue() {
    /*
     * Build a tree that look like the following, then check the value of each
     * node against the data:
     * 
     * Date +-- LaunchType +-- Launch +-- RunMode | +-- DebugMode
     */

    LaunchConfigurationDescriptor runMode = new LaunchConfigurationDescriptor(
        "Name", ILaunchManager.RUN_MODE, "org.eclipse.pde.ui.RuntimeWorkbench");
    LaunchConfigurationDescriptor debugMode = new LaunchConfigurationDescriptor(
        runMode.getLaunchName(), ILaunchManager.DEBUG_MODE,
        runMode.getLaunchTypeId());
    LaunchDataDescriptor d1 = new LaunchDataDescriptor(new LocalDate(),
        runMode, 1, new Duration(10), Collections.<IPath> emptySet());
    LaunchDataDescriptor d2 = new LaunchDataDescriptor(d1.getDate(), debugMode,
        19, new Duration(1200), Collections.<IPath> emptySet());

    provider.setSelectedCategories(Category.DATE, Category.LAUNCH_TYPE,
        Category.LAUNCH, Category.LAUNCH_MODE);
    provider.getViewer().setInput(Arrays.asList(d1, d2));
    IValueProvider values = provider.getLaunchDurationValueProvider();
    TreeNode root = provider.getRoot();

    provider.setPaintCategory(Category.DATE);
    assertEquals(d1.getDuration().getMillis() + d2.getDuration().getMillis(),
        values.getValue(root.getChildren()[0]));

    provider.setPaintCategory(Category.LAUNCH_TYPE);
    assertEquals(d1.getDuration().getMillis() + d2.getDuration().getMillis(),
        values.getValue(root.getChildren()[0]));

    provider.setPaintCategory(Category.LAUNCH);
    assertEquals(d1.getDuration().getMillis() + d2.getDuration().getMillis(),
        values.getValue(root.getChildren()[0]));

    provider.setPaintCategory(Category.LAUNCH_MODE);
    provider.setSelectedCategories(Category.LAUNCH_MODE);
    TreeNode[] nodes = root.getChildren();
    assertEquals(2, nodes.length);
    assertEquals(d1.getDuration().getMillis(), values.getValue(nodes[0]));
    assertEquals(d2.getDuration().getMillis(), values.getValue(nodes[1]));
  }

  @Test
  public void testGetLaunchDurationValueProvider_shouldPaint() {
    assertShouldPaint(provider.getLaunchDurationValueProvider());
  }

  @Test
  public void testGetPaintCategory_defaultValue() {
    assertSame(Category.LAUNCH, new LaunchPageContentProvider(new TreeViewer(
        shell)).getPaintCategory());
  }

  @Test
  public void testGetSelectedCategories() {
    assertNotNull(provider.getSelectedCategories());
    // Never empty, if were set to empty, defaults should be used:
    assertTrue(provider.getSelectedCategories().length > 0);

    ICategory[] categories = new ICategory[]{Category.DATE, Category.LAUNCH};
    provider.setSelectedCategories(categories);
    assertArrayEquals(categories, provider.getSelectedCategories());

    categories = new ICategory[]{Category.LAUNCH_MODE, Category.LAUNCH_TYPE};
    provider.setSelectedCategories(categories);
    assertArrayEquals(categories, provider.getSelectedCategories());
  }

  @Test
  public void testGetUnselectedCategories() {
    Set<Category> all = Sets.newHashSet(Category.DATE, Category.LAUNCH,
        Category.LAUNCH_MODE, Category.LAUNCH_TYPE);
    ICategory[] categories = all.toArray(new ICategory[all.size()]);
    provider.setSelectedCategories(categories);
    assertEquals(0, provider.getUnselectedCategories().length);

    categories = new ICategory[]{Category.DATE, Category.LAUNCH};
    provider.setSelectedCategories(categories);

    Set<Category> unselect = Sets.difference(all, Sets.newHashSet(categories));
    assertEquals(unselect.size(), provider.getUnselectedCategories().length);
    assertTrue(unselect.containsAll(Arrays.asList(provider.getUnselectedCategories())));
  }

  @Test
  public void testHasChildren() {
    LaunchConfigurationDescriptor runMode = new LaunchConfigurationDescriptor(
        "Name", ILaunchManager.RUN_MODE, "org.eclipse.pde.ui.RuntimeWorkbench");
    LaunchDataDescriptor des = new LaunchDataDescriptor(new LocalDate(),
        runMode, 1, new Duration(10), Collections.<IPath> emptySet());

    provider.getViewer().setInput(Arrays.asList(des));

    TreeNode root = provider.getRoot();
    provider.setSelectedCategories(Category.DATE);
    assertFalse(provider.hasChildren(root.getChildren()[0]));

    provider.setSelectedCategories(Category.DATE, Category.LAUNCH);
    assertTrue(provider.hasChildren(root.getChildren()[0]));
    assertFalse(provider.hasChildren(root.getChildren()[0].getChildren()[0]));
  }

  @Test
  public void testInputChanged_clearsOldData() {
    provider.getRoot().setChildren(new TreeNode[]{new TreeNode("1")});
    assertTrue(provider.getElements(null).length > 0);
    TreeViewer viewer = provider.getViewer();
    provider.inputChanged(viewer, viewer.getInput(), null);
    assertEquals(0, provider.getElements(null).length);
  }

  @Test
  public void testInputChanged_newInputNull() {
    try {
      TreeViewer viewer = provider.getViewer();
      provider.inputChanged(viewer, viewer.getInput(), null);
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
  public void testSetPaintCategory() {
    ICategory cat = Category.DATE;
    provider.setPaintCategory(cat);
    assertEquals(cat, provider.getPaintCategory());

    cat = Category.LAUNCH;
    provider.setPaintCategory(cat);
    assertEquals(cat, provider.getPaintCategory());
  }

  public void testSetPaintCategory_argumentNullToReset() {
    provider.setPaintCategory(null);
    assertSame(Category.LAUNCH, provider.getPaintCategory());
  }

  @Test
  public void testShouldFilter() {
    TreeNode dateNode = new TreeNode(new LocalDate());
    TreeNode launchNode = new TreeNode(Pair.create("", ""));
    TreeNode typeNode = new TreeNode(new UndefinedLaunchConfigurationType(""));
    TreeNode modeNode = new TreeNode(new UndefinedLaunchMode(""));

    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    TreeNode projectNode = new TreeNode(root.getProject("p"));
    TreeNode folderNode = new TreeNode(root.getProject("p").getFolder("f"));
    TreeNode fileNode = new TreeNode(root.getProject("p").getFile("f.txt"));

    provider.setSelectedCategories(Category.DATE);
    assertFalse(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(launchNode));
    assertTrue(provider.shouldFilter(typeNode));
    assertTrue(provider.shouldFilter(modeNode));
    assertFalse(provider.shouldFilter(projectNode));
    assertFalse(provider.shouldFilter(folderNode));
    assertFalse(provider.shouldFilter(fileNode));

    provider.setSelectedCategories(Category.LAUNCH);
    assertTrue(provider.shouldFilter(dateNode));
    assertFalse(provider.shouldFilter(launchNode));
    assertTrue(provider.shouldFilter(typeNode));
    assertTrue(provider.shouldFilter(modeNode));
    assertFalse(provider.shouldFilter(projectNode));
    assertFalse(provider.shouldFilter(folderNode));
    assertFalse(provider.shouldFilter(fileNode));

    provider.setSelectedCategories(Category.LAUNCH_MODE);
    assertTrue(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(launchNode));
    assertTrue(provider.shouldFilter(typeNode));
    assertFalse(provider.shouldFilter(modeNode));
    assertFalse(provider.shouldFilter(projectNode));
    assertFalse(provider.shouldFilter(folderNode));
    assertFalse(provider.shouldFilter(fileNode));

    provider.setSelectedCategories(Category.LAUNCH_TYPE);
    assertTrue(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(launchNode));
    assertFalse(provider.shouldFilter(typeNode));
    assertTrue(provider.shouldFilter(modeNode));
    assertFalse(provider.shouldFilter(projectNode));
    assertFalse(provider.shouldFilter(folderNode));
    assertFalse(provider.shouldFilter(fileNode));

    // Test with multiple categories:
    provider.setSelectedCategories(Category.LAUNCH_MODE, Category.DATE);
    assertFalse(provider.shouldFilter(dateNode));
    assertTrue(provider.shouldFilter(launchNode));
    assertTrue(provider.shouldFilter(typeNode));
    assertFalse(provider.shouldFilter(modeNode));
    assertFalse(provider.shouldFilter(projectNode));
    assertFalse(provider.shouldFilter(folderNode));
    assertFalse(provider.shouldFilter(fileNode));
  }

  private void assertShouldPaint(IValueProvider values) {
    TreeNode dateNode = new TreeNode(new LocalDate());
    TreeNode launchNode = new TreeNode(Pair.create("", ""));
    TreeNode typeNode = new TreeNode(new UndefinedLaunchConfigurationType(""));
    TreeNode modeNode = new TreeNode(new UndefinedLaunchMode(""));

    IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
    TreeNode projectNode = new TreeNode(root.getProject("p"));
    TreeNode folderNode = new TreeNode(root.getProject("p").getFolder("f"));
    TreeNode fileNode = new TreeNode(root.getProject("p").getFile("f.txt"));

    provider.setPaintCategory(Category.DATE);
    assertTrue(values.shouldPaint(dateNode));
    assertFalse(values.shouldPaint(launchNode));
    assertFalse(values.shouldPaint(typeNode));
    assertFalse(values.shouldPaint(modeNode));
    assertFalse(values.shouldPaint(projectNode));
    assertFalse(values.shouldPaint(folderNode));
    assertFalse(values.shouldPaint(fileNode));

    provider.setPaintCategory(Category.LAUNCH);
    assertFalse(values.shouldPaint(dateNode));
    assertTrue(values.shouldPaint(launchNode));
    assertFalse(values.shouldPaint(typeNode));
    assertFalse(values.shouldPaint(modeNode));
    assertFalse(values.shouldPaint(projectNode));
    assertFalse(values.shouldPaint(folderNode));
    assertFalse(values.shouldPaint(fileNode));

    provider.setPaintCategory(Category.LAUNCH_MODE);
    assertFalse(values.shouldPaint(dateNode));
    assertFalse(values.shouldPaint(launchNode));
    assertFalse(values.shouldPaint(typeNode));
    assertTrue(values.shouldPaint(modeNode));
    assertFalse(values.shouldPaint(projectNode));
    assertFalse(values.shouldPaint(folderNode));
    assertFalse(values.shouldPaint(fileNode));

    provider.setPaintCategory(Category.LAUNCH_TYPE);
    assertFalse(values.shouldPaint(dateNode));
    assertFalse(values.shouldPaint(launchNode));
    assertTrue(values.shouldPaint(typeNode));
    assertFalse(values.shouldPaint(modeNode));
    assertFalse(values.shouldPaint(projectNode));
    assertFalse(values.shouldPaint(folderNode));
    assertFalse(values.shouldPaint(fileNode));
  }
}
