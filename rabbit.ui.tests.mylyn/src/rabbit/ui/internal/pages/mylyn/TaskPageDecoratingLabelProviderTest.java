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
package rabbit.ui.internal.pages.mylyn;

import static rabbit.ui.internal.util.DurationFormat.format;

import rabbit.data.common.TaskId;
import rabbit.ui.internal.pages.mylyn.MylynCategory;
import rabbit.ui.internal.pages.mylyn.TaskPageContentProvider;
import rabbit.ui.internal.pages.mylyn.TaskPageDecoratingLabelProvider;
import rabbit.ui.internal.pages.mylyn.TaskPageLabelProvider;
import rabbit.ui.internal.util.UnrecognizedTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Date;

/**
 * @see TaskPageDecoratingLabelProvider
 */
@SuppressWarnings("restriction")
public class TaskPageDecoratingLabelProviderTest {
  
  private static Shell shell;

  private static TaskPageDecoratingLabelProvider labelProvider;
  private static TaskPageLabelProvider labelHelper;
  private static TaskPageContentProvider contentProvider;

  private TreeNode normalTaskNode = new TreeNode(new LocalTask("id", "summary"));
  private TreeNode missingTaskNode = new TreeNode(new UnrecognizedTask(new TaskId("1", new Date())));
  private TreeNode projectNode = new TreeNode(ResourcesPlugin.getWorkspace().getRoot().getProject("p"));
  private TreeNode folderNode = new TreeNode(((IProject) projectNode.getValue()).getFolder("folder"));
  private TreeNode fileNode = new TreeNode(((IFolder) folderNode.getValue()).getFile("file"));
  private TreeNode dateNode = new TreeNode(new LocalDate());

  @AfterClass
  public static void afterClass() {
    labelProvider.dispose();
    labelHelper.dispose();
    shell.dispose();
  }
  
  @BeforeClass
  public static void beforeClass() {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    TreeViewer viewer = new TreeViewer(shell);
    contentProvider = new TaskPageContentProvider(viewer);
    labelProvider = new TaskPageDecoratingLabelProvider(contentProvider);
    labelHelper = new TaskPageLabelProvider();
    viewer.setContentProvider(contentProvider);
  }

  @Test
  public void testGetBackground() {
    assertNull(labelProvider.getBackground(normalTaskNode));
    assertNull(labelProvider.getBackground(missingTaskNode));
    assertNull(labelProvider.getBackground(projectNode));
    assertNull(labelProvider.getBackground(folderNode));
    assertNull(labelProvider.getBackground(fileNode));
    assertNull(labelProvider.getBackground(dateNode));
  }

  @Test
  public void testGetColumnImage() {
    assertNotNull(labelProvider.getColumnImage(normalTaskNode, 0));
    assertNotNull(labelProvider.getColumnImage(missingTaskNode, 0));
    assertNotNull(labelProvider.getColumnImage(projectNode, 0));
    assertNotNull(labelProvider.getColumnImage(folderNode, 0));
    assertNotNull(labelProvider.getColumnImage(fileNode, 0));
    assertNotNull(labelProvider.getColumnImage(dateNode, 0));

    assertNull(labelProvider.getColumnImage(normalTaskNode, 1));
    assertNull(labelProvider.getColumnImage(missingTaskNode, 1));
    assertNull(labelProvider.getColumnImage(projectNode, 1));
    assertNull(labelProvider.getColumnImage(folderNode, 1));
    assertNull(labelProvider.getColumnImage(fileNode, 1));
    assertNull(labelProvider.getColumnImage(dateNode, 1));
  }

  @Test
  public void testGetColumnText() {
    String normalTaskStr = labelHelper.getText(normalTaskNode);
    String missingTaskStr = labelHelper.getText(missingTaskNode);
    String projectStr = labelHelper.getText(projectNode);
    String folderStr = labelHelper.getText(folderNode);
    String fileStr = labelHelper.getText(fileNode);
    String dateStr = labelHelper.getText(dateNode);

    // Show task only:
    contentProvider.setPaintCategory(MylynCategory.TASK);
    assertEquals(normalTaskStr, labelProvider.getColumnText(normalTaskNode, 0));
    assertEquals(missingTaskStr, labelProvider.getColumnText(missingTaskNode, 0));
    assertEquals(projectStr, labelProvider.getColumnText(projectNode, 0));
    assertEquals(folderStr, labelProvider.getColumnText(folderNode, 0));
    assertEquals(fileStr, labelProvider.getColumnText(fileNode, 0));
    assertEquals(dateStr, labelProvider.getColumnText(dateNode, 0));

    assertEquals(format(contentProvider.getValue(normalTaskNode)), labelProvider.getColumnText(normalTaskNode, 1));
    assertEquals(format(contentProvider.getValue(missingTaskNode)), labelProvider.getColumnText(missingTaskNode, 1));
    assertNull(labelProvider.getColumnText(projectNode, 1));
    assertNull(labelProvider.getColumnText(folderNode, 1));
    assertNull(labelProvider.getColumnText(fileNode, 1));
    assertNull(labelProvider.getColumnText(dateNode, 1));

    // Show project only:
    contentProvider.setPaintCategory(MylynCategory.PROJECT);
    assertEquals(normalTaskStr, labelProvider.getColumnText(normalTaskNode, 0));
    assertEquals(missingTaskStr, labelProvider.getColumnText(missingTaskNode, 0));
    assertEquals(projectStr, labelProvider.getColumnText(projectNode, 0));
    assertEquals(folderStr, labelProvider.getColumnText(folderNode, 0));
    assertEquals(fileStr, labelProvider.getColumnText(fileNode, 0));
    assertEquals(dateStr, labelProvider.getColumnText(dateNode, 0));

    assertNull(labelProvider.getColumnText(normalTaskNode, 1));
    assertNull(labelProvider.getColumnText(missingTaskNode, 1));
    assertEquals(format(contentProvider.getValue(projectNode)), labelProvider.getColumnText(projectNode, 1));
    assertNull(labelProvider.getColumnText(folderNode, 1));
    assertNull(labelProvider.getColumnText(fileNode, 1));
    assertNull(labelProvider.getColumnText(dateNode, 1));

    // Show folder only:
    contentProvider.setPaintCategory(MylynCategory.FOLDER);
    assertEquals(normalTaskStr, labelProvider.getColumnText(normalTaskNode, 0));
    assertEquals(missingTaskStr, labelProvider.getColumnText(missingTaskNode, 0));
    assertEquals(projectStr, labelProvider.getColumnText(projectNode, 0));
    assertEquals(folderStr, labelProvider.getColumnText(folderNode, 0));
    assertEquals(fileStr, labelProvider.getColumnText(fileNode, 0));
    assertEquals(dateStr, labelProvider.getColumnText(dateNode, 0));

    assertNull(labelProvider.getColumnText(normalTaskNode, 1));
    assertNull(labelProvider.getColumnText(missingTaskNode, 1));
    assertNull(labelProvider.getColumnText(projectNode, 1));
    assertEquals(format(contentProvider.getValue(folderNode)), labelProvider.getColumnText(folderNode, 1));
    assertNull(labelProvider.getColumnText(fileNode, 1));
    assertNull(labelProvider.getColumnText(dateNode, 1));

    // Show file:
    contentProvider.setPaintCategory(MylynCategory.FILE);
    assertEquals(normalTaskStr, labelProvider.getColumnText(normalTaskNode, 0));
    assertEquals(missingTaskStr, labelProvider.getColumnText(missingTaskNode, 0));
    assertEquals(projectStr, labelProvider.getColumnText(projectNode, 0));
    assertEquals(folderStr, labelProvider.getColumnText(folderNode, 0));
    assertEquals(fileStr, labelProvider.getColumnText(fileNode, 0));
    assertEquals(dateStr, labelProvider.getColumnText(dateNode, 0));

    assertNull(labelProvider.getColumnText(normalTaskNode, 1));
    assertNull(labelProvider.getColumnText(missingTaskNode, 1));
    assertNull(labelProvider.getColumnText(projectNode, 1));
    assertNull(labelProvider.getColumnText(folderNode, 1));
    assertEquals(format(contentProvider.getValue(fileNode)), labelProvider.getColumnText(fileNode, 1));
    assertNull(labelProvider.getColumnText(dateNode, 1));
    

    // Show date:
    contentProvider.setPaintCategory(MylynCategory.DATE);
    assertEquals(normalTaskStr, labelProvider.getColumnText(normalTaskNode, 0));
    assertEquals(missingTaskStr, labelProvider.getColumnText(missingTaskNode, 0));
    assertEquals(projectStr, labelProvider.getColumnText(projectNode, 0));
    assertEquals(folderStr, labelProvider.getColumnText(folderNode, 0));
    assertEquals(fileStr, labelProvider.getColumnText(fileNode, 0));
    assertEquals(dateStr, labelProvider.getColumnText(dateNode, 0));

    assertNull(labelProvider.getColumnText(normalTaskNode, 1));
    assertNull(labelProvider.getColumnText(missingTaskNode, 1));
    assertNull(labelProvider.getColumnText(projectNode, 1));
    assertNull(labelProvider.getColumnText(folderNode, 1));
    assertNull(labelProvider.getColumnText(fileNode, 1));
    assertEquals(format(contentProvider.getValue(dateNode)), labelProvider.getColumnText(dateNode, 1));
  }

  @Test
  public void testGetForeground_missingResource() throws Exception {
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(System.nanoTime() + "");
    assertFalse(project.exists());
    assertNotNull(labelProvider.getForeground(new TreeNode(project)));
    
    project.create(null);
    project.open(null);
    assertNull(labelProvider.getForeground(new TreeNode(project)));
  }
  
  @Test
  public void testGetForeground_missingTask() {
    assertNotNull(labelProvider.getForeground(missingTaskNode));
  }
  
  @Test
  public void testGetForeground_overDueTask() {
    ITask task = new LocalTask("1", "a");
    task.setDueDate(new Date(System.currentTimeMillis() - 1000));
    assertNotNull(labelProvider.getForeground(new TreeNode(task)));
  }
  
  @Test
  public void testGetForeground_completedTaskToday() {
    ITask task = new LocalTask("1", "a");
    task.setCompletionDate(new Date());
    assertNotNull(labelProvider.getForeground(new TreeNode(task)));
  }
  
  @Test
  public void testGetFont_completedTask() {
    ITask task = new LocalTask("1", "a");
    task.setCompletionDate(new Date());
    assertNotNull(labelProvider.getFont(new TreeNode(task)));
  }
}
