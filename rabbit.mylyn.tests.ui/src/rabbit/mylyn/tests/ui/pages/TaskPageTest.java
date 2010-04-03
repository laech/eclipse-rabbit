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

import rabbit.data.IFileMapper;
import rabbit.data.access.IAccessor;
import rabbit.data.handler.DataHandler;
import rabbit.mylyn.TaskCore;
import rabbit.mylyn.TaskId;
import rabbit.mylyn.internal.ui.pages.TaskPage;
import rabbit.mylyn.internal.ui.pages.TaskResource;
import rabbit.mylyn.internal.ui.pages.TaskPage.ShowMode;
import rabbit.mylyn.internal.ui.util.MissingTask;
import rabbit.mylyn.internal.ui.util.MissingTaskCategory;
import rabbit.ui.DisplayPreference;
import rabbit.ui.tests.pages.AbstractTreeViewerPageTest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryModel;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

/**
 * @see TaskPage
 */
@SuppressWarnings("restriction")
public class TaskPageTest extends AbstractTreeViewerPageTest {

  private static final Shell shell = new Shell(PlatformUI.getWorkbench()
      .getDisplay());

  @AfterClass
  public static void afterClass() {
    shell.dispose();
  }

  /**
   * Gets the private field "fileToValue".
   */
  @SuppressWarnings("unchecked")
  static Map<TaskResource, Long> getFileToValueField(TaskPage page)
      throws Exception {
    Field field = TaskPage.class.getDeclaredField("fileToValue");
    field.setAccessible(true);
    return (Map<TaskResource, Long>) field.get(page);
  }

  /**
   * Gets the private field "folderToFiles".
   */
  @SuppressWarnings("unchecked")
  static Map<TaskResource, Set<TaskResource>> getFolderToFilesField(
      TaskPage page) throws Exception {
    Field field = TaskPage.class.getDeclaredField("folderToFiles");
    field.setAccessible(true);
    return (Map<TaskResource, Set<TaskResource>>) field.get(page);
  }

  /**
   * Gets the private field "projectToResources".
   */
  @SuppressWarnings("unchecked")
  static Map<TaskResource, Set<TaskResource>> getProjectToResourcesField(
      TaskPage page) throws Exception {
    Field field = TaskPage.class.getDeclaredField("projectToResources");
    field.setAccessible(true);
    return (Map<TaskResource, Set<TaskResource>>) field.get(page);
  }

  /**
   * Gets the private field "taskToProjects".
   */
  @SuppressWarnings("unchecked")
  static Map<ITask, Set<TaskResource>> getTaskToProjectsField(TaskPage page)
      throws Exception {
    Field field = TaskPage.class.getDeclaredField("taskToProjects");
    field.setAccessible(true);
    return (Map<ITask, Set<TaskResource>>) field.get(page);
  }

  private static void doUpdate(TaskPage page,
      Map<TaskId, Map<String, Long>> data) throws Exception {
    Method method = TaskPage.class.getDeclaredMethod("doUpdate", Map.class);
    method.setAccessible(true);
    method.invoke(page, data);
  }

  private final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

  private TaskPage page;

  @Before
  public void before() {
    page = createPage();
    page.createContents(shell);
  }

  @Test
  public void shouldPaint() {
    ITaskContainer container = MissingTaskCategory.getCategory();
    ITask task = new LocalTask("id", "summary");
    TaskResource project = new TaskResource(task, root.getProject("p"));
    TaskResource folder = new TaskResource(task, root.getProject("pp")
        .getFolder("f"));
    TaskResource file = new TaskResource(task, root.getProject("ppp").getFile(
        "ff"));

    page.setShowMode(ShowMode.FILE);
    assertFalse(page.shouldPaint(container));
    assertFalse(page.shouldPaint(task));
    assertFalse(page.shouldPaint(project));
    assertFalse(page.shouldPaint(folder));
    assertTrue(page.shouldPaint(file));

    page.setShowMode(ShowMode.FOLDER);
    assertFalse(page.shouldPaint(container));
    assertFalse(page.shouldPaint(task));
    assertFalse(page.shouldPaint(project));
    assertTrue(page.shouldPaint(folder));
    assertFalse(page.shouldPaint(file));

    page.setShowMode(ShowMode.PROJECT);
    assertFalse(page.shouldPaint(container));
    assertFalse(page.shouldPaint(task));
    assertTrue(page.shouldPaint(project));
    assertFalse(page.shouldPaint(folder));
    assertFalse(page.shouldPaint(file));

    page.setShowMode(ShowMode.TASK);
    assertFalse(page.shouldPaint(container));
    assertTrue(page.shouldPaint(task));
    assertFalse(page.shouldPaint(project));
    assertFalse(page.shouldPaint(folder));
    assertFalse(page.shouldPaint(file));

    page.setShowMode(ShowMode.TASK_CATEGORY);
    assertTrue(page.shouldPaint(container));
    assertFalse(page.shouldPaint(task));
    assertFalse(page.shouldPaint(project));
    assertFalse(page.shouldPaint(folder));
    assertFalse(page.shouldPaint(file));
  }

  /*
   * Test one local task is deleted and it's id is reused by another task,
   * should result in two different tasks.
   */
  @Test
  public void testDoUpdate_sameTaskHanldeId() throws Exception {
    IFileMapper manager = DataHandler.getFileMapper();

    String handleId = "13iu4ey";
    String summary = "nvheuihf";

    Date creationDate1 = new Date();
    LocalTask activeTask = new LocalTask(handleId, summary);
    activeTask.setCreationDate(creationDate1);

    // The deleted task:
    Date creationDate2 = new GregorianCalendar(1999, 1, 1).getTime();
    ITask missingTask = new MissingTask(new TaskId(handleId, creationDate2));
    missingTask.setCreationDate(creationDate2);

    /*
     * Note: only put the active task into Mylyn's task list.
     */
    TasksUiPlugin.getTaskList().addTask(activeTask);

    Map<TaskId, Map<String, Long>> data = new HashMap<TaskId, Map<String, Long>>();

    long value1 = 19873;
    long value2 = 18734;
    Map<String, Long> fileData1 = new HashMap<String, Long>();
    fileData1.put(manager.insert(root.getProject("p").getFile("f")), value1);
    fileData1.put(manager.insert(root.getProject("p").getFile("ff")), value2);
    data.put(new TaskId(activeTask.getHandleIdentifier(), activeTask
        .getCreationDate()), fileData1);

    long value3 = 92374;
    Map<String, Long> fileData2 = new HashMap<String, Long>();
    fileData2.put(manager.insert(root.getProject("p").getFile("f12")), value3);
    data.put(new TaskId(missingTask.getHandleIdentifier(), missingTask
        .getCreationDate()), fileData2);

    doUpdate(page, data);

    assertEquals(2, getTaskToProjectsField(page).size()); // Two tasks.
    assertTrue(getTaskToProjectsField(page).keySet().contains(activeTask));
    assertTrue(getTaskToProjectsField(page).keySet().contains(missingTask));
    assertEquals(value1 + value2, page.getValue(activeTask));
    assertEquals(value3, page.getValue(missingTask));
  }

  @Test
  public void testDoUpdate_twoFileIdPointToOneFile() throws Exception {
    // Test two id pointing to same file, getting the value of the file must
    // return the sum.

    IFileMapper manager = DataHandler.getFileMapper();
    IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
        "tmp");
    if (!project.exists()) {
      project.create(null);
    }
    if (!project.isOpen()) {
      project.open(null);
    }
    IFolder folder = project.getFolder("folder");
    if (!folder.exists()) {
      folder.create(true, true, null);
    }

    IFile file1 = folder.getFile("Hello1.txt");
    if (!file1.exists()) {
      FileInputStream stream = new FileInputStream(File.createTempFile("tmp1",
          "txt"));
      file1.create(stream, true, null);
      stream.close();
    }
    IFile file2 = folder.getFile("Hello2.txt");
    if (!file2.exists()) {
      FileInputStream stream = new FileInputStream(File.createTempFile("tmp2",
          "txt"));
      file2.create(stream, true, null);
      stream.close();
    }

    // Insert file1 into the database, then delete it from workspace:
    String file1Id = manager.insert(file1);
    file1.delete(true, null);

    // Insert file2 into the database,
    // then rename file2 to become the deleted file1:
    String file2Id = manager.insert(file2);
    file2.move(file1.getFullPath(), true, null);

    // Now the two IDs should point to the same path:
    assertEquals(manager.getFile(file1Id), manager.getFile(file2Id));
    assertEquals(file1, manager.getFile(file1Id));

    long value1 = 19084;
    long value2 = 28450;

    LocalTask task = new LocalTask("taskId", "summary");
    task.setCreationDate(new Date());
    TasksUiPlugin.getTaskList().addTask(task);

    Map<TaskId, Map<String, Long>> data = new HashMap<TaskId, Map<String, Long>>();
    Map<String, Long> fileData = new HashMap<String, Long>();
    fileData.put(file1Id, value1);
    fileData.put(file2Id, value2);
    data.put(new TaskId(task.getHandleIdentifier(), task.getCreationDate()),
        fileData);
    doUpdate(page, data);

    TaskResource taskFile1 = new TaskResource(task, file1);
    TaskResource taskFolder = new TaskResource(task, folder);
    TaskResource taskProject = new TaskResource(task, project);

    assertEquals(value1 + value2, page.getValue(taskFile1));
    assertEquals(value1 + value2, page.getValue(taskFolder));
    assertEquals(value1 + value2, page.getValue(taskProject));
  }

  @Test
  public void testGetMaxCategoryValue() throws Exception {
    final long maxValue = 1010101010;

    // Get root elements will not be empty, there are default categories in
    // Mylyn.
    Set<AbstractTaskContainer> categories = TasksUiPlugin.getTaskList()
        .getRootElements();
    int i = 0;
    for (AbstractTaskContainer cat : categories) {
      AbstractTask task = new LocalTask(System.nanoTime() + "", "summary");
      cat.internalAddChild(task);

      TaskResource project = new TaskResource(task, root.getProject("proj1"));
      Set<TaskResource> resources = new HashSet<TaskResource>();
      resources.add(project);
      getTaskToProjectsField(page).put(task, resources);

      TaskResource file = new TaskResource(task, ((IProject) project.resource)
          .getFile("file"));
      resources = new HashSet<TaskResource>();
      resources.add(file);
      getProjectToResourcesField(page).put(project, resources);

      getFileToValueField(page).put(file, maxValue - i++);
    }

    assertEquals(maxValue, page.getMaxCategoryValue());
  }

  @Test
  public void testGetMaxFileValue() throws Exception {
    final long maxValue = 1847291049;
    for (int i = 0; i < 10; i++) {
      IResource file = root.getProject("" + i).getFile("" + i);
      ITask task = new LocalTask(i + "", i + "");
      TaskResource resource = new TaskResource(task, file);
      getFileToValueField(page).put(resource, maxValue - i);
    }
    assertEquals(maxValue, page.getMaxFileValue());
  }

  @Test
  public void testGetMaxFolderValue() throws Exception {
    long maxValue = 1000001;
    for (int i = 0; i < 10; i++) {
      ITask task = new LocalTask("id", "summary");

      IFolder folder = root.getProject("proj").getFolder(i + "");
      TaskResource taskFolder = new TaskResource(task, folder);

      Set<TaskResource> files = new HashSet<TaskResource>();
      IFile file = folder.getFile(i + "");
      TaskResource taskFile = new TaskResource(task, file);
      files.add(taskFile);

      getFileToValueField(page).put(taskFile, maxValue - i);
      getFolderToFilesField(page).put(taskFolder, files);
    }
    assertEquals(maxValue, page.getMaxFolderValue());
  }

  @Test
  public void testGetMaxProjectValue() throws Exception {
    long maxValue = 109838721;
    for (int i = 0; i < 10; i++) {
      ITask task = new LocalTask(i + "", "summary");

      IProject project = root.getProject("proj");
      TaskResource taskProject = new TaskResource(task, project);

      Set<TaskResource> files = new HashSet<TaskResource>();
      IFile file = project.getFile(i + "");
      TaskResource taskFile = new TaskResource(task, file);
      files.add(taskFile);

      getFileToValueField(page).put(taskFile, maxValue - i);
      getProjectToResourcesField(page).put(taskProject, files);

      Set<TaskResource> projects = new HashSet<TaskResource>();
      projects.add(taskProject);
      getTaskToProjectsField(page).put(task, projects);
    }
    assertEquals(maxValue, page.getMaxProjectValue());
  }

  @Test
  public void testGetMaxTaskValue() throws Exception {
    long maxValue = 1098387211;
    for (int i = 0; i < 10; i++) {
      ITask task = new LocalTask(i + "", "summary");

      IProject project = root.getProject("proj");
      TaskResource taskProject = new TaskResource(task, project);

      Set<TaskResource> files = new HashSet<TaskResource>();
      IFile file = project.getFile(i + "");
      TaskResource taskFile = new TaskResource(task, file);
      files.add(taskFile);

      getFileToValueField(page).put(taskFile, maxValue - i);
      getProjectToResourcesField(page).put(taskProject, files);

      Set<TaskResource> projects = new HashSet<TaskResource>();
      projects.add(taskProject);
      getTaskToProjectsField(page).put(task, projects);
    }
    assertEquals(maxValue, page.getMaxTaskValue());
  }

  @Test
  public void testGetResources_task() throws Exception {
    ITask task = new MissingTask(new TaskId("id", new Date()));
    Set<TaskResource> projects = new HashSet<TaskResource>();
    projects.add(new TaskResource(task, root.getProject("proj1")));
    projects.add(new TaskResource(task, root.getProject("proj2")));
    projects.add(new TaskResource(task, root.getProject("proj3")));

    getTaskToProjectsField(page).put(task, projects);
    assertEquals(projects.size(), page.getTaskResources(task).size());
    for (TaskResource res : page.getTaskResources(task)) {
      assertTrue(projects.contains(res));
    }
  }

  @Test
  public void testGetResources_taskElement() throws Exception {
    ITask task = new MissingTask(new TaskId("Id", new Date()));
    IProject project = root.getProject("Proj1");
    Set<TaskResource> resources = new HashSet<TaskResource>();
    resources.add(new TaskResource(task, project.getFolder("folder1").getFile(
        "file1")));
    resources.add(new TaskResource(task, project.getFolder("folder3")));
    resources.add(new TaskResource(task, project.getFile("file2")));
    resources.add(new TaskResource(task, project.getFile("file3")));
    getProjectToResourcesField(page).put(new TaskResource(task, project),
        resources);

    assertEquals(resources.size(), page.getProjectResources(
        new TaskResource(task, project)).size());
    for (TaskResource res : page.getProjectResources(new TaskResource(task,
        project))) {
      assertTrue(resources.contains(res));
    }
  }

  @Test
  public void testGetShowMode() {
    assertSame(ShowMode.TASK, page.getShowMode());
  }

  @Test
  public void testGetValueOfCategory_andGetValue() throws Exception {
    // Get root elements will not be empty, there are default categories in
    // Mylyn.
    Set<AbstractTaskContainer> categories = TasksUiPlugin.getTaskList()
        .getRootElements();
    for (AbstractTaskContainer cat : categories) {
      AbstractTask task = new LocalTask(System.nanoTime() + "", "summary");
      cat.internalAddChild(task);

      TaskResource project = new TaskResource(task, root.getProject("proj1"));
      Set<TaskResource> resources = new HashSet<TaskResource>();
      resources.add(project);
      getTaskToProjectsField(page).put(task, resources);

      TaskResource file = new TaskResource(task, ((IProject) project.resource)
          .getFile("file"));
      resources = new HashSet<TaskResource>();
      resources.add(file);
      getProjectToResourcesField(page).put(project, resources);

      long value = System.nanoTime();
      getFileToValueField(page).put(file, value);

      assertEquals(value, page.getValueOfCategory(cat));
      assertEquals(value, page.getValue(cat));
    }
  }

  @Test
  public void testGetValueOfResource_file_andGetValue() throws Exception {
    long value = 1010101;
    TaskResource file = new TaskResource(new LocalTask("b", "a"), root
        .getProject("p").getFile("f"));
    getFileToValueField(page).put(file, value);
    assertEquals(value, page.getValueOfFile(file));
    assertEquals(value, page.getValue(file));
  }

  @Test
  public void testGetValueOfResource_folder_andGetValue() throws Exception {
    long value1 = 12312;
    long value2 = 98372;
    TaskResource taskFolder = new TaskResource(new LocalTask("b", "a"), root
        .getProject("p").getFolder("F"));
    Set<TaskResource> files = new HashSet<TaskResource>();

    TaskResource taskFile = new TaskResource(taskFolder.task,
        ((IFolder) taskFolder.resource).getFile("f"));
    files.add(taskFile);
    getFileToValueField(page).put(taskFile, value1);

    taskFile = new TaskResource(taskFolder.task,
        ((IFolder) taskFolder.resource).getFile("ffff"));
    files.add(taskFile);
    getFileToValueField(page).put(taskFile, value2);

    getFolderToFilesField(page).put(taskFolder, files);

    assertEquals(value1 + value2, page.getValueOfFolder(taskFolder));
    assertEquals(value1 + value2, page.getValue(taskFolder));
  }

  @Test
  public void testGetValueOfResource_project_andGetValue() throws Exception {
    long value1 = 12312;
    long value2 = 98372;
    TaskResource taskProject = new TaskResource(new LocalTask("b", "a"), root
        .getProject("p"));
    Set<TaskResource> files = new HashSet<TaskResource>();

    TaskResource taskFile = new TaskResource(taskProject.task,
        ((IProject) taskProject.resource).getFile("f"));
    files.add(taskFile);
    getFileToValueField(page).put(taskFile, value1);

    taskFile = new TaskResource(taskProject.task,
        ((IProject) taskProject.resource).getFile("ffff"));
    files.add(taskFile);
    getFileToValueField(page).put(taskFile, value2);

    getProjectToResourcesField(page).put(taskProject, files);

    assertEquals(value1 + value2, page.getValueOfProject(taskProject));
    assertEquals(value1 + value2, page.getValue(taskProject));
  }

  @Test
  public void testGetValueOfTask_andGetValue() throws Exception {
    long value1 = 12312;
    long value2 = 98372;
    ITask task = new LocalTask("b", "a");
    TaskResource taskProject = new TaskResource(task, root.getProject("p"));
    Set<TaskResource> files = new HashSet<TaskResource>();

    TaskResource taskFile = new TaskResource(taskProject.task,
        ((IProject) taskProject.resource).getFile("f"));
    files.add(taskFile);
    getFileToValueField(page).put(taskFile, value1);

    taskFile = new TaskResource(taskProject.task,
        ((IProject) taskProject.resource).getFile("ffff"));
    files.add(taskFile);
    getFileToValueField(page).put(taskFile, value2);

    getProjectToResourcesField(page).put(taskProject, files);

    Set<TaskResource> projects = new HashSet<TaskResource>();
    projects.add(taskProject);
    getTaskToProjectsField(page).put(task, projects);

    assertEquals(value1 + value2, page.getValueOfTask(task));
    assertEquals(value1 + value2, page.getValue(task));
  }

  @Test
  public void testSetShowMode() {
    page.setShowMode(ShowMode.FILE);
    assertSame(ShowMode.FILE, page.getShowMode());

    page.setShowMode(ShowMode.FOLDER);
    assertSame(ShowMode.FOLDER, page.getShowMode());
  }

  /*
   * This test will only work if there are actual data.
   * 
   * @see testDoUpdate
   */
  @Test
  public void testUpdate() throws Exception {
    Map<ITask, Set<TaskResource>> taskToProjects = new HashMap<ITask, Set<TaskResource>>();
    Map<TaskResource, Set<TaskResource>> projectToResources = new HashMap<TaskResource, Set<TaskResource>>();
    Map<TaskResource, Set<TaskResource>> folderToFiles = new HashMap<TaskResource, Set<TaskResource>>();
    Map<TaskResource, Long> fileToValue = new HashMap<TaskResource, Long>();

    DisplayPreference preference = new DisplayPreference();
    Calendar end = preference.getEndDate();
    preference.getStartDate().setTime(end.getTime());
    preference.getStartDate().add(Calendar.MONTH, -3);

    IAccessor<Map<TaskId, Map<String, Long>>> accessor = TaskCore
        .getTaskDataAccessor();
    Map<TaskId, Map<String, Long>> data = accessor.getData(preference
        .getStartDate(), preference.getEndDate());

    IFileMapper resourceMapper = DataHandler.getFileMapper();
    IRepositoryModel repo = TasksUi.getRepositoryModel();

    for (Entry<TaskId, Map<String, Long>> taskEn : data.entrySet()) {

      TaskId id = taskEn.getKey();
      ITask task = repo.getTask(id.getHandleIdentifier());

      // Same handle id but different creation date, which means different
      // tasks.
      if (task != null && !task.getCreationDate().equals(id.getCreationDate())) {
        task = null;
      }
      if (task == null) {
        task = new MissingTask(id);
      }

      for (Entry<String, Long> fileEn : taskEn.getValue().entrySet()) {
        IFile file = resourceMapper.getFile(fileEn.getKey());
        if (file == null) {
          file = resourceMapper.getExternalFile(fileEn.getKey());
        }
        if (file == null) {
          continue;
        }

        TaskResource fileElement = new TaskResource(task, file);
        Long oldValue = fileToValue.get(fileElement);
        if (oldValue == null) {
          oldValue = Long.valueOf(0);
        }
        fileToValue.put(fileElement, fileEn.getValue() + oldValue);

        IProject project = file.getProject();
        TaskResource projectElement = new TaskResource(task, project);
        Set<TaskResource> projectSet = taskToProjects.get(task);
        if (projectSet == null) {
          projectSet = new HashSet<TaskResource>();
          taskToProjects.put(task, projectSet);
        }
        projectSet.add(projectElement);

        IContainer folder = file.getParent();
        TaskResource folderElement = new TaskResource(task, folder);

        Set<TaskResource> resources = projectToResources.get(projectElement);
        if (resources == null) {
          resources = new HashSet<TaskResource>();
          projectToResources.put(projectElement, resources);
        }

        if (project == folder) {
          resources.add(fileElement);
        } else {
          resources.add(folderElement);
          Set<TaskResource> fileset = folderToFiles.get(folderElement);
          if (fileset == null) {
            fileset = new HashSet<TaskResource>();
            folderToFiles.put(folderElement, fileset);
          }
          fileset.add(fileElement);
        }
      }
    }

    page.update(preference);

    assertEquals(taskToProjects.size(), getTaskToProjectsField(page).size());
    for (Entry<ITask, Set<TaskResource>> entry : taskToProjects.entrySet()) {

      Set<TaskResource> resources = getTaskToProjectsField(page).get(
          entry.getKey());
      assertNotNull(entry.getKey().toString(), resources);
      assertEquals(entry.getValue().size(), resources.size());

      for (TaskResource res : entry.getValue()) {
        assertTrue(resources.contains(res));
      }
    }

    assertEquals(projectToResources.size(), getProjectToResourcesField(page)
        .size());
    for (Entry<TaskResource, Set<TaskResource>> entry : projectToResources
        .entrySet()) {

      Set<TaskResource> resources = getProjectToResourcesField(page).get(
          entry.getKey());
      assertNotNull(resources);
      assertEquals(entry.getValue().size(), resources.size());

      for (TaskResource res : entry.getValue()) {
        assertTrue(resources.contains(res));
      }
    }

    assertEquals(folderToFiles.size(), getFolderToFilesField(page).size());
    for (Entry<TaskResource, Set<TaskResource>> entry : folderToFiles
        .entrySet()) {

      Set<TaskResource> resources = getFolderToFilesField(page).get(
          entry.getKey());
      assertNotNull(resources);
      assertEquals(entry.getValue().size(), resources.size());

      for (TaskResource res : entry.getValue()) {
        assertTrue(resources.contains(res));
      }
    }

    assertEquals(fileToValue.size(), getFileToValueField(page).size());
    for (Entry<TaskResource, Long> entry : fileToValue.entrySet()) {
      assertEquals(entry.getValue(), getFileToValueField(page).get(
          entry.getKey()));
    }
  }

  @Override
  protected TaskPage createPage() {
    return new TaskPage();
  }
}
