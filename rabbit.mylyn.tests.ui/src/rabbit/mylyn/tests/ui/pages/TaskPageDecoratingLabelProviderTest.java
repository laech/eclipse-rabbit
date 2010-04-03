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
package rabbit.mylyn.tests.ui.pages;

import rabbit.mylyn.TaskId;
import rabbit.mylyn.internal.ui.pages.TaskPage;
import rabbit.mylyn.internal.ui.pages.TaskPageDecoratingLabelProvider;
import rabbit.mylyn.internal.ui.pages.TaskPageLabelProvider;
import rabbit.mylyn.internal.ui.pages.TaskResource;
import rabbit.mylyn.internal.ui.pages.TaskPage.ShowMode;
import rabbit.mylyn.internal.ui.util.MissingTask;
import rabbit.ui.internal.util.MillisConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import static rabbit.mylyn.tests.ui.pages.TaskPageTest.*;

/**
 * @see TaskPageDecoratingLabelProvider
 */
@SuppressWarnings("restriction")
public class TaskPageDecoratingLabelProviderTest {

  private static TaskPageDecoratingLabelProvider provider;
  private static TaskPageLabelProvider labeler;
  private static ILabelDecorator decorator;
  private static TaskPage page;
  private static Shell shell;

  private static ITaskContainer category = TasksUiPlugin.getTaskList()
      .getRootElements().iterator().next();
  private static ITask task = new MissingTask(new TaskId("id", new Date()));
  private static TaskResource file = new TaskResource(task, ResourcesPlugin
      .getWorkspace().getRoot().getProject("p").getFolder("f").getFile("ff"));
  private static TaskResource folder = new TaskResource(task, file.resource
      .getParent());
  private static TaskResource project = new TaskResource(task, folder.resource
      .getParent());
  private static long value = 98982361;

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  @BeforeClass
  public static void beforeClass() throws Exception {
    shell = new Shell(PlatformUI.getWorkbench().getDisplay());
    page = new TaskPage();
    page.createContents(shell);

    labeler = new TaskPageLabelProvider();
    decorator = PlatformUI.getWorkbench().getDecoratorManager()
        .getLabelDecorator();
    provider = new TaskPageDecoratingLabelProvider(page, labeler, decorator);

    getFileToValueField(page).put(file, value);
    getFolderToFilesField(page).put(folder,
        new HashSet<TaskResource>(Arrays.asList(file)));
    getProjectToResourcesField(page).put(project,
        new HashSet<TaskResource>(Arrays.asList(folder)));
    getTaskToProjectsField(page).put(task,
        new HashSet<TaskResource>(Arrays.asList(project)));
  }

  @Test
  public void testGetBackgroun() {
    assertNull(provider.getBackground(category));
    assertNull(provider.getBackground(task));
    assertNull(provider.getBackground(project));
    assertNull(provider.getBackground(folder));
    assertNull(provider.getBackground(file));
  }

  @Test
  public void testGetColumnImage() {
    assertNotNull(provider.getColumnImage(category, 0));
    assertNotNull(provider.getColumnImage(task, 0));
    assertNotNull(provider.getColumnImage(project, 0));
    assertNotNull(provider.getColumnImage(folder, 0));
    assertNotNull(provider.getColumnImage(file, 0));

    assertNull(provider.getColumnImage(category, 1));
    assertNull(provider.getColumnImage(task, 1));
    assertNull(provider.getColumnImage(project, 1));
    assertNull(provider.getColumnImage(folder, 1));
    assertNull(provider.getColumnImage(file, 1));
  }

  @Test
  public void testGetColumnText() {
    String categoryStr = decorator.decorateText(labeler.getText(category),
        category);
    String taskStr = decorator.decorateText(labeler.getText(task), task);
    String projectStr = decorator.decorateText(labeler.getText(project),
        project);
    String folderStr = decorator.decorateText(labeler.getText(folder), folder);
    String fileStr = file.resource.getName();

    // Show category only:
    page.setShowMode(ShowMode.TASK_CATEGORY);
    assertEquals(categoryStr, provider.getColumnText(categoryStr, 0));
    assertEquals(taskStr, provider.getColumnText(taskStr, 0));
    assertEquals(projectStr, provider.getColumnText(project, 0));
    assertEquals(folderStr, provider.getColumnText(folder, 0));
    assertEquals(fileStr, provider.getColumnText(file, 0));

    assertEquals(MillisConverter.toDefaultString(page
        .getValueOfCategory(category)), provider.getColumnText(category, 1));
    assertNull(provider.getColumnText(task, 1));
    assertNull(provider.getColumnText(project, 1));
    assertNull(provider.getColumnText(folder, 1));
    assertNull(provider.getColumnText(file, 1));

    // Show task only:
    page.setShowMode(ShowMode.TASK);
    assertEquals(categoryStr, provider.getColumnText(categoryStr, 0));
    assertEquals(taskStr, provider.getColumnText(taskStr, 0));
    assertEquals(projectStr, provider.getColumnText(project, 0));
    assertEquals(folderStr, provider.getColumnText(folder, 0));
    assertEquals(fileStr, provider.getColumnText(file, 0));

    assertNull(provider.getColumnText(category, 1));
    assertEquals(MillisConverter.toDefaultString(page.getValueOfTask(task)),
        provider.getColumnText(task, 1));
    assertNull(provider.getColumnText(project, 1));
    assertNull(provider.getColumnText(folder, 1));
    assertNull(provider.getColumnText(file, 1));

    // Show project only:
    page.setShowMode(ShowMode.PROJECT);
    assertEquals(categoryStr, provider.getColumnText(categoryStr, 0));
    assertEquals(taskStr, provider.getColumnText(taskStr, 0));
    assertEquals(projectStr, provider.getColumnText(project, 0));
    assertEquals(folderStr, provider.getColumnText(folder, 0));
    assertEquals(fileStr, provider.getColumnText(file, 0));

    assertNull(provider.getColumnText(category, 1));
    assertNull(provider.getColumnText(task, 1));
    assertEquals(MillisConverter.toDefaultString(page
        .getValueOfProject(project)), provider.getColumnText(project, 1));
    assertNull(provider.getColumnText(folder, 1));
    assertNull(provider.getColumnText(file, 1));

    // Show folder only:
    page.setShowMode(ShowMode.FOLDER);
    assertEquals(categoryStr, provider.getColumnText(categoryStr, 0));
    assertEquals(taskStr, provider.getColumnText(taskStr, 0));
    assertEquals(projectStr, provider.getColumnText(project, 0));
    assertEquals(folderStr, provider.getColumnText(folder, 0));
    assertEquals(fileStr, provider.getColumnText(file, 0));

    assertNull(provider.getColumnText(category, 1));
    assertNull(provider.getColumnText(task, 1));
    assertNull(provider.getColumnText(project, 1));
    assertEquals(
        MillisConverter.toDefaultString(page.getValueOfFolder(folder)),
        provider.getColumnText(folder, 1));
    assertNull(provider.getColumnText(file, 1));

    // Show file:
    page.setShowMode(ShowMode.FILE);
    assertEquals(categoryStr, provider.getColumnText(categoryStr, 0));
    assertEquals(taskStr, provider.getColumnText(taskStr, 0));
    assertEquals(projectStr, provider.getColumnText(project, 0));
    assertEquals(folderStr, provider.getColumnText(folder, 0));
    assertEquals(fileStr, provider.getColumnText(file, 0));

    assertNull(provider.getColumnText(category, 1));
    assertNull(provider.getColumnText(task, 1));
    assertNull(provider.getColumnText(project, 1));
    assertNull(provider.getColumnText(folder, 1));
    assertEquals(MillisConverter.toDefaultString(page.getValueOfFile(file)),
        provider.getColumnText(file, 1));
  }

  @Test
  public void testGetForeground() throws Exception {
    TaskResource project = new TaskResource(task, ResourcesPlugin
        .getWorkspace().getRoot().getProject(System.currentTimeMillis() + ""));
    assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
        provider.getForeground(project));

    ((IProject) project.resource).create(null);
    ((IProject) project.resource).open(null);
    assertNull(provider.getForeground(project));
  }
}
