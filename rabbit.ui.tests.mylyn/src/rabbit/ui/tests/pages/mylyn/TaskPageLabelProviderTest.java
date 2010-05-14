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
package rabbit.ui.tests.pages.mylyn;

import rabbit.data.common.TaskId;
import rabbit.ui.internal.pages.mylyn.TaskPageLabelProvider;
import rabbit.ui.internal.util.UnrecognizedTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.tasks.core.ITask;
import org.joda.time.LocalDate;
import org.junit.AfterClass;
import org.junit.Test;

import java.util.Date;

/**
 * @see TaskPageLabelProvider
 */
@SuppressWarnings("restriction")
public class TaskPageLabelProviderTest {

  private static TaskPageLabelProvider provider = new TaskPageLabelProvider();
  
  private TreeNode normalTaskNode = new TreeNode(new LocalTask("id", "summary"));
  private TreeNode missingTaskNode = new TreeNode(new UnrecognizedTask(new TaskId("1", new Date())));
  private TreeNode projectNode = new TreeNode(ResourcesPlugin.getWorkspace().getRoot().getProject("p"));
  private TreeNode folderNode = new TreeNode(((IProject) projectNode.getValue()).getFolder("folder"));
  private TreeNode fileNode = new TreeNode(((IFolder) folderNode.getValue()).getFile("file"));
  private TreeNode dateNode = new TreeNode(new LocalDate());
  
  @AfterClass
  public static void afterClass() {
    provider.dispose();
  }

  @Test
  public void testGetBackground() {
    assertNull(provider.getBackground(normalTaskNode));
    assertNull(provider.getBackground(missingTaskNode));
    assertNull(provider.getBackground(projectNode));
    assertNull(provider.getBackground(folderNode));
    assertNull(provider.getBackground(fileNode));
    assertNull(provider.getBackground(dateNode));
  }

  @Test
  public void testGetForeground_missingResource() throws Exception {
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(System.nanoTime() + "");
    assertFalse(project.exists());
    assertNotNull(provider.getForeground(new TreeNode(project)));
    
    project.create(null);
    project.open(null);
    assertNull(provider.getForeground(new TreeNode(project)));
  }
  
  @Test
  public void testGetForeground_missingTask() {
    assertNotNull(provider.getForeground(missingTaskNode));
  }
  
  @Test
  public void testGetForeground_overDueTask() {
    ITask task = new LocalTask("1", "a");
    task.setDueDate(new Date(System.currentTimeMillis() - 1000));
    assertNotNull(provider.getForeground(new TreeNode(task)));
  }
  
  @Test
  public void testGetForeground_completedTaskToday() {
    ITask task = new LocalTask("1", "a");
    task.setCompletionDate(new Date());
    assertNotNull(provider.getForeground(new TreeNode(task)));
  }
  
  @Test
  public void testGetFont_completedTask() {
    ITask task = new LocalTask("1", "a");
    task.setCompletionDate(new Date());
    assertNotNull(provider.getFont(new TreeNode(task)));
  }

  @Test
  public void testGetImage() {
    assertNotNull(provider.getImage(normalTaskNode));
    assertNotNull(provider.getImage(missingTaskNode));
    assertNotNull(provider.getImage(projectNode));
    assertNotNull(provider.getImage(folderNode));
    assertNotNull(provider.getImage(fileNode));
    assertNotNull(provider.getImage(dateNode));
  }

  @Test
  public void testGetText() {
    assertEquals(((ITask) normalTaskNode.getValue()).getSummary(), provider.getText(normalTaskNode));
    assertEquals(((ITask) missingTaskNode.getValue()).getSummary(), provider.getText(missingTaskNode));
    assertEquals(((IProject) projectNode.getValue()).getName(), provider.getText(projectNode));
    assertEquals(((IFolder) folderNode.getValue()).getProjectRelativePath().toString(), provider.getText(folderNode));
    assertEquals(((IFile) fileNode.getValue()).getName(), provider.getText(fileNode));
  }
}
