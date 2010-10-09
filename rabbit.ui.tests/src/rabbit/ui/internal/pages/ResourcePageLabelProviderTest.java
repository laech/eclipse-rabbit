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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.joda.time.LocalDate;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @see ResourcePageTableLabelProvider
 */
public class ResourcePageLabelProviderTest {

  private ResourcePageLabelProvider provider;
  private DateLabelProvider dateLabels;
  private final IProject project = 
      ResourcesPlugin.getWorkspace().getRoot().getProject("p");
  private final IFolder folder = project.getFolder("f");
  private final IFile file = project.getFile("a");
  
  @Before
  public void before() {
    provider = new ResourcePageLabelProvider();
    dateLabels = new DateLabelProvider();
  }
  
  @After
  public void after() {
    provider.dispose();
    dateLabels.dispose();
  }
  
  @Test
  public void testGetBackground_argIsFile() {
    assertNull(provider.getBackground(file));
  }
  
  @Test
  public void testGetBackground_argIsFileInTreeNode() {
    assertNull(provider.getBackground(new TreeNode(file)));
  }
  
  @Test
  public void testGetBackground_argIsFolder() {
    assertNull(provider.getBackground(folder));
  }
  
  @Test
  public void testGetBackground_argIsFolderInTreeNode() {
    assertNull(provider.getBackground(new TreeNode(folder)));
  }
  
  @Test
  public void testGetBackground_argIsLocalDate() {
    assertNull(provider.getBackground(new LocalDate()));
  }
  
  @Test
  public void testGetBackground_argIsLocalDateInTreeNode() {
    assertNull(provider.getBackground(new TreeNode(new LocalDate())));
  }
  
  @Test
  public void testGetBackground_argIsNull() {
    assertNull(provider.getBackground(null));
  }
  
  @Test
  public void testGetBackground_argIsProject() {
    assertNull(provider.getBackground(project));
  }
  
  @Test
  public void testGetBackground_argIsProjectInTreeNode() {
    assertNull(provider.getBackground(new TreeNode(project)));
  }

  @Test
  public void testGetForeground_argIsResourceExists() throws Exception {
    IProject project = newProject(true);
    assertNull(provider.getForeground(project));
  }
  
  @Test
  public void testGetForeground_argIsResourceExistsInTreeNode() throws Exception {
    IProject project = newProject(true);
    assertNull(provider.getForeground(new TreeNode(project)));
  }
  
  @Test
  public void testGetForeground_argIsResourceNotExists() throws Exception {
    IProject project = newProject(false);
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        provider.getForeground(project));
  }

  @Test
  public void testGetForeground_argIsResourceNotExistsInTreeNode() throws Exception {
    IProject project = newProject(false);
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        provider.getForeground(new TreeNode(project)));
  }
  
  @Test
  public void testGetImage_argIsFile() {
    assertNotNull(provider.getImage(file));
  }
  
  @Test
  public void testGetImage_argIsFileInTreeNode() {
    assertNotNull(provider.getImage(new TreeNode(file)));
  }
  
  @Test
  public void testGetImage_argIsFolder() {
    assertNotNull(provider.getImage(folder));
  }
  
  @Test
  public void testGetImage_argIsFolderInTreeNode() {
    assertNotNull(provider.getImage(new TreeNode(folder)));
  }
  
  @Test
  public void testGetImage_argIsLocalDate() {
    assertNotNull(provider.getImage(new LocalDate()));
  }
  
  @Test
  public void testGetImage_argIsLocalDateInTreeNode() {
    assertNotNull(provider.getImage(new TreeNode(new LocalDate())));
  }
  
  @Test
  public void testGetImage_argIsProject() {
    assertNotNull(provider.getImage(project));
  }
  
  @Test
  public void testGetImage_argIsProjectInTreeNode() {
    assertNotNull(provider.getImage(new TreeNode(project)));
  }
  
  @Test
  public void testGetText_argIsFile() {
    assertEquals(file.getName(), provider.getText(file));
  }

  @Test
  public void testGetText_argIsFileInTreeNode() {
    assertEquals(file.getName(), provider.getText(new TreeNode(file)));
  }
  
  @Test
  public void testGetText_argIsFolder() {
    assertEquals(folder.getName(), provider.getText(folder));
  }
  
  @Test
  public void testGetText_argIsFolderInTreeNode() {
    assertEquals(folder.getName(), provider.getText(new TreeNode(folder)));
  }
  
  @Test
  public void testGetText_argIsLocalDate_someDay() {
    LocalDate date = new LocalDate().minusDays(10);
    assertEquals(dateLabels.getText(date), provider.getText(date));
  }
  
  @Test
  public void testGetText_argIsLocalDate_today() {
    LocalDate today = new LocalDate();
    assertEquals(dateLabels.getText(today), provider.getText(today));
  }
  
  @Test
  public void testGetText_argIsLocalDate_yesterday() {
    LocalDate yesterday = new LocalDate().minusDays(1);
    assertEquals(dateLabels.getText(yesterday), provider.getText(yesterday));
  }
  
  @Test
  public void testGetText_argIsLocalDateInTreeNode_someDay() {
    LocalDate date = new LocalDate().minusDays(10);
    assertEquals(dateLabels.getText(date), provider.getText(new TreeNode(date)));
  }
  
  @Test
  public void testGetText_argIsLocalDateInTreeNode_today() {
    LocalDate today = new LocalDate();
    assertEquals(
        dateLabels.getText(today), provider.getText(new TreeNode(today)));
  }
  
  @Test
  public void testGetText_argIsLocalDateInTreeNode_yesterday() {
    LocalDate yesterday = new LocalDate().minusDays(1);
    assertEquals(dateLabels.getText(yesterday), 
        provider.getText(new TreeNode(yesterday)));
  }
  @Test
  public void testGetText_argIsProject() {
    assertEquals(project.getName(), provider.getText(project));
  }
  
  @Test
  public void testGetText_argIsProjectInTreeNode() {
    assertEquals(project.getName(), provider.getText(new TreeNode(project)));
  }
  
  /**
   * Creates a new project.
   * @param create True to create the underlying project, false to create a 
   *      handler only.
   */
  private IProject newProject(boolean create) throws Exception {
    IProject project = ResourcesPlugin.getWorkspace().getRoot()
        .getProject(System.currentTimeMillis() + "");
    if (create) {
      project.create(null);
      project.open(null);
    }
    return project;
  }
}
