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

import rabbit.data.access.model.FileDataDescriptor;
import rabbit.ui.internal.pages.Category;
import rabbit.ui.internal.pages.ResourcePage;
import rabbit.ui.internal.pages.ResourcePageContentProvider;
import rabbit.ui.internal.pages.ResourcePageLabelProvider;
import rabbit.ui.internal.pages.ResourcePageTableLabelProvider;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.joda.time.Duration;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;

/**
 * @see ResourcePageTableLabelProvider
 */
public class ResourcePageTableLabelProviderTest {

  private static ResourcePageTableLabelProvider provider;
  private static ResourcePageContentProvider contents;
  private static ResourcePageLabelProvider resourceLabels;
  private static TreeViewer viewer;
  private static Shell shell;

  private static TreeNode projectNode;
  private static TreeNode folderNode;
  private static TreeNode fileNode;
  private static TreeNode dateNode;

  @AfterClass
  public static void afterClass() {
    provider.dispose();
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
  }

  public ResourcePageTableLabelProviderTest() {
    ResourcePage page = new ResourcePage();
    page.createContents(shell);
    resourceLabels = new ResourcePageLabelProvider();
    viewer = page.getViewer();
    contents = new ResourcePageContentProvider(page.getViewer());
    provider = new ResourcePageTableLabelProvider(contents);
    viewer.setLabelProvider(provider);
    viewer.setContentProvider(contents);

    projectNode = new TreeNode(
        ResourcesPlugin.getWorkspace().getRoot().getProject("p"));
    folderNode = new TreeNode(
        ((IProject) projectNode.getValue()).getFolder("f"));
    fileNode = new TreeNode(((IFolder) folderNode.getValue()).getFile("a"));
    dateNode = new TreeNode(new LocalDate());
  }

  @Test(expected = NullPointerException.class)
  public void testConstructor_contentProviderNull() {
    new ResourcePageTableLabelProvider(null);
  }

  @Test
  public void testGetBackground() {
    assertNull(provider.getBackground(projectNode));
    assertNull(provider.getBackground(folderNode));
    assertNull(provider.getBackground(fileNode));
    assertNull(provider.getBackground(dateNode));
  }

  @Test
  public void testGetColumnImage() {
    assertNotNull(provider.getColumnImage(projectNode, 0));
    assertNotNull(provider.getColumnImage(folderNode, 0));
    assertNotNull(provider.getColumnImage(fileNode, 0));
    assertNotNull(provider.getColumnImage(dateNode, 0));

    assertNull(provider.getColumnImage(projectNode, 1));
    assertNull(provider.getColumnImage(folderNode, 1));
    assertNull(provider.getColumnImage(fileNode, 1));
    assertNull(provider.getColumnImage(dateNode, 1));
  }

  @Test
  public void testGetColumnText_0() {
    String projectStr = resourceLabels.getText(projectNode);
    String folderStr = resourceLabels.getText(folderNode);
    String fileStr = resourceLabels.getText(fileNode);
    String dateStr = resourceLabels.getText(dateNode);

    assertEquals(projectStr, provider.getColumnText(projectNode, 0));
    assertEquals(folderStr, provider.getColumnText(folderNode, 0));
    assertEquals(fileStr, provider.getColumnText(fileNode, 0));
    assertEquals(dateStr, provider.getColumnText(dateNode, 0));
  }

  @Test
  public void testGetColumnText_1() throws Exception {
    FileDataDescriptor des1 = new FileDataDescriptor(
        (LocalDate) dateNode.getValue(), new Duration(12),
        ((IFile) fileNode.getValue()).getFullPath());
    FileDataDescriptor des2 = new FileDataDescriptor(des1.getDate(),
        new Duration(12873), des1.getFilePath());

    /*
     * Sets the categories, so that the data will be structured as Date ->
     * Project -> Folder -> File.
     */
    contents.setSelectedCategories(Category.DATE, Category.PROJECT,
        Category.FOLDER, Category.FILE);
    viewer.setInput(Arrays.asList(des1, des2));

    TreeNode root = contents.getRoot();
    TreeNode dateNode = root.getChildren()[0];
    TreeNode projectNode = dateNode.getChildren()[0];
    TreeNode folderNode = projectNode.getChildren()[0];
    TreeNode fileNode = folderNode.getChildren()[0];

    // Show project:
    contents.setPaintCategory(Category.PROJECT);
    assertEquals(format(des1.getDuration().getMillis()
        + des2.getDuration().getMillis()), provider.getColumnText(projectNode,
        1));
    assertNull(provider.getColumnText(folderNode, 1));
    assertNull(provider.getColumnText(fileNode, 1));
    assertNull(provider.getColumnText(dateNode, 1));

    // Show folder:
    contents.setPaintCategory(Category.FOLDER);
    assertNull(provider.getColumnText(projectNode, 1));
    assertEquals(format(des1.getDuration().getMillis()
        + des2.getDuration().getMillis()),
        provider.getColumnText(folderNode, 1));
    assertNull(provider.getColumnText(fileNode, 1));
    assertNull(provider.getColumnText(dateNode, 1));

    // Show file:
    contents.setPaintCategory(Category.FILE);
    assertNull(provider.getColumnText(projectNode, 1));
    assertNull(provider.getColumnText(folderNode, 1));
    assertEquals(format(des1.getDuration().getMillis()
        + des2.getDuration().getMillis()), provider.getColumnText(fileNode, 1));
    assertNull(provider.getColumnText(dateNode, 1));

    // Show date:
    contents.setPaintCategory(Category.DATE);
    assertNull(provider.getColumnText(projectNode, 1));
    assertNull(provider.getColumnText(folderNode, 1));
    assertNull(provider.getColumnText(fileNode, 1));
    assertEquals(format(des1.getDuration().getMillis()
        + des2.getDuration().getMillis()), provider.getColumnText(dateNode, 1));
  }

  @Test
  public void testGetForeground() throws Exception {
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
        System.currentTimeMillis() + "");
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        provider.getForeground(new TreeNode(project)));

    project.create(null);
    project.open(null);
    assertNull(provider.getForeground(new TreeNode(project)));
  }
}
