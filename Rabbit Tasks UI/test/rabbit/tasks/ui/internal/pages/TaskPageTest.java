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
package rabbit.tasks.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
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

import rabbit.core.RabbitCore;
import rabbit.core.storage.IAccessor;
import rabbit.core.storage.IFileMapper;
import rabbit.tasks.core.TaskCore;
import rabbit.tasks.core.TaskId;
import rabbit.tasks.ui.internal.pages.TaskPage.ShowMode;
import rabbit.tasks.ui.internal.util.MissingTask;
import rabbit.tasks.ui.internal.util.MissingTaskCategory;
import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.pages.AbstractTreeViewerPageTest;

/**
 * @see TaskPage
 */
@SuppressWarnings("restriction")
public class TaskPageTest extends AbstractTreeViewerPageTest {

	private static final Shell shell = new Shell(PlatformUI.getWorkbench().getDisplay());

	@AfterClass
	public static void afterClass() {
		shell.dispose();
	}

	private static void doUpdate(TaskPage page, Map<TaskId, Map<String, Long>> data) throws Exception {
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
		TaskResource folder = new TaskResource(task, root.getProject("pp").getFolder("f"));
		TaskResource file = new TaskResource(task, root.getProject("ppp").getFile("ff"));

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
		IFileMapper manager = RabbitCore.getFileMapper();

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
		fileData1.put(manager.insert(root.getProject("p").getFile("f").getFullPath().toString()), value1);
		fileData1.put(manager.insert(root.getProject("p").getFile("ff").getFullPath().toString()), value2);
		data.put(new TaskId(activeTask.getHandleIdentifier(), activeTask.getCreationDate()), fileData1);

		long value3 = 92374;
		Map<String, Long> fileData2 = new HashMap<String, Long>();
		fileData2.put(manager.insert(root.getProject("p").getFile("f12").getFullPath().toString()), value3);
		data.put(new TaskId(missingTask.getHandleIdentifier(), missingTask.getCreationDate()), fileData2);

		doUpdate(page, data);

		assertEquals(2, page.taskToProjects.size()); // Two tasks.
		assertTrue(page.taskToProjects.keySet().contains(activeTask));
		assertTrue(page.taskToProjects.keySet().contains(missingTask));
		assertEquals(value1 + value2, page.getValue(activeTask));
		assertEquals(value3, page.getValue(missingTask));
	}

	@Test
	public void testDoUpdate_twoFileIdPointToOneFile() throws Exception {
		// Test two id pointing to same file, getting the value of the file must
		// return the sum.

		IFileMapper manager = RabbitCore.getFileMapper();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("tmp");
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
			FileInputStream stream = new FileInputStream(File.createTempFile("tmp1", "txt"));
			file1.create(stream, true, null);
			stream.close();
		}
		IFile file2 = folder.getFile("Hello2.txt");
		if (!file2.exists()) {
			FileInputStream stream = new FileInputStream(File.createTempFile("tmp2", "txt"));
			file2.create(stream, true, null);
			stream.close();
		}

		// Insert file1 into the database, then delete it from workspace:
		String file1Id = manager.insert(file1.getFullPath().toString());
		file1.delete(true, null);

		// Insert file2 into the database,
		// then rename file2 to become the deleted file1:
		String file2Id = manager.insert(file2.getFullPath().toString());
		file2.move(file1.getFullPath(), true, null);

		// Now the two IDs should point to the same path:
		assertEquals(manager.getPath(file1Id), manager.getPath(file2Id));
		assertEquals(file1.getFullPath().toString(), manager.getPath(file1Id));

		long value1 = 19084;
		long value2 = 28450;

		LocalTask task = new LocalTask("taskId", "summary");
		task.setCreationDate(new Date());
		TasksUiPlugin.getTaskList().addTask(task);

		Map<TaskId, Map<String, Long>> data = new HashMap<TaskId, Map<String, Long>>();
		Map<String, Long> fileData = new HashMap<String, Long>();
		fileData.put(file1Id, value1);
		fileData.put(file2Id, value2);
		data.put(new TaskId(task.getHandleIdentifier(), task.getCreationDate()), fileData);
		doUpdate(page, data);

		TaskResource taskFile1 = new TaskResource(task, file1);
		TaskResource taskFolder = new TaskResource(task, folder);
		TaskResource taskProject = new TaskResource(task, project);

		assertEquals(value1 + value2, page.getValue(taskFile1));
		assertEquals(value1 + value2, page.getValue(taskFolder));
		assertEquals(value1 + value2, page.getValue(taskProject));
	}

	@Test
	public void testGetMaxCategoryValue() {
		final long maxValue = 1010101010;

		// Get root elements will not be empty, there are default categories in
		// Mylyn.
		Set<AbstractTaskContainer> categories = TasksUiPlugin.getTaskList().getRootElements();
		int i = 0;
		for (AbstractTaskContainer cat : categories) {
			AbstractTask task = new LocalTask(System.nanoTime() + "", "summary");
			cat.internalAddChild(task);

			TaskResource project = new TaskResource(task, root.getProject("proj1"));
			Set<TaskResource> resources = new HashSet<TaskResource>();
			resources.add(project);
			page.taskToProjects.put(task, resources);

			TaskResource file = new TaskResource(task, ((IProject) project.resource).getFile("file"));
			resources = new HashSet<TaskResource>();
			resources.add(file);
			page.projectToResources.put(project, resources);

			page.fileToValue.put(file, maxValue - i++);
		}

		assertEquals(maxValue, page.getMaxCategoryValue());
	}

	@Test
	public void testGetMaxFileValue() {
		final long maxValue = 1847291049;
		for (int i = 0; i < 10; i++) {
			IResource file = root.getProject("" + i).getFile("" + i);
			ITask task = new LocalTask(i + "", i + "");
			TaskResource resource = new TaskResource(task, file);
			page.fileToValue.put(resource, maxValue - i);
		}
		assertEquals(maxValue, page.getMaxFileValue());
	}

	@Test
	public void testGetMaxFolderValue() {
		long maxValue = 1000001;
		for (int i = 0; i < 10; i++) {
			ITask task = new LocalTask("id", "summary");

			IFolder folder = root.getProject("proj").getFolder(i + "");
			TaskResource taskFolder = new TaskResource(task, folder);

			Set<TaskResource> files = new HashSet<TaskResource>();
			IFile file = folder.getFile(i + "");
			TaskResource taskFile = new TaskResource(task, file);
			files.add(taskFile);

			page.fileToValue.put(taskFile, maxValue - i);
			page.folderToFiles.put(taskFolder, files);
		}
		assertEquals(maxValue, page.getMaxFolderValue());
	}

	@Test
	public void testGetMaxProjectValue() {
		long maxValue = 109838721;
		for (int i = 0; i < 10; i++) {
			ITask task = new LocalTask(i + "", "summary");

			IProject project = root.getProject("proj");
			TaskResource taskProject = new TaskResource(task, project);

			Set<TaskResource> files = new HashSet<TaskResource>();
			IFile file = project.getFile(i + "");
			TaskResource taskFile = new TaskResource(task, file);
			files.add(taskFile);

			page.fileToValue.put(taskFile, maxValue - i);
			page.projectToResources.put(taskProject, files);

			Set<TaskResource> projects = new HashSet<TaskResource>();
			projects.add(taskProject);
			page.taskToProjects.put(task, projects);
		}
		assertEquals(maxValue, page.getMaxProjectValue());
	}

	@Test
	public void testGetMaxTaskValue() {
		long maxValue = 1098387211;
		for (int i = 0; i < 10; i++) {
			ITask task = new LocalTask(i + "", "summary");

			IProject project = root.getProject("proj");
			TaskResource taskProject = new TaskResource(task, project);

			Set<TaskResource> files = new HashSet<TaskResource>();
			IFile file = project.getFile(i + "");
			TaskResource taskFile = new TaskResource(task, file);
			files.add(taskFile);

			page.fileToValue.put(taskFile, maxValue - i);
			page.projectToResources.put(taskProject, files);

			Set<TaskResource> projects = new HashSet<TaskResource>();
			projects.add(taskProject);
			page.taskToProjects.put(task, projects);
		}
		assertEquals(maxValue, page.getMaxTaskValue());
	}

	@Test
	public void testGetResources_task() {
		ITask task = new MissingTask(new TaskId("id", new Date()));
		Set<TaskResource> projects = new HashSet<TaskResource>();
		projects.add(new TaskResource(task, root.getProject("proj1")));
		projects.add(new TaskResource(task, root.getProject("proj2")));
		projects.add(new TaskResource(task, root.getProject("proj3")));

		page.taskToProjects.put(task, projects);
		assertEquals(projects.size(), page.getTaskResources(task).size());
		for (TaskResource res : page.getTaskResources(task)) {
			assertTrue(projects.contains(res));
		}
	}

	@Test
	public void testGetResources_taskElement() {
		ITask task = new MissingTask(new TaskId("Id", new Date()));
		IProject project = root.getProject("Proj1");
		Set<TaskResource> resources = new HashSet<TaskResource>();
		resources.add(new TaskResource(task, project.getFolder("folder1").getFile("file1")));
		resources.add(new TaskResource(task, project.getFolder("folder3")));
		resources.add(new TaskResource(task, project.getFile("file2")));
		resources.add(new TaskResource(task, project.getFile("file3")));
		page.projectToResources.put(new TaskResource(task, project), resources);

		assertEquals(resources.size(), page.getProjectResources(new TaskResource(task, project)).size());
		for (TaskResource res : page.getProjectResources(new TaskResource(task, project))) {
			assertTrue(resources.contains(res));
		}
	}

	@Test
	public void testGetShowMode() {
		assertSame(ShowMode.TASK, page.getShowMode());
	}

	@Test
	public void testGetValueOfCategory_andGetValue() {
		// Get root elements will not be empty, there are default categories in
		// Mylyn.
		Set<AbstractTaskContainer> categories = TasksUiPlugin.getTaskList().getRootElements();
		for (AbstractTaskContainer cat : categories) {
			AbstractTask task = new LocalTask(System.nanoTime() + "", "summary");
			cat.internalAddChild(task);

			TaskResource project = new TaskResource(task, root.getProject("proj1"));
			Set<TaskResource> resources = new HashSet<TaskResource>();
			resources.add(project);
			page.taskToProjects.put(task, resources);

			TaskResource file = new TaskResource(task, ((IProject) project.resource).getFile("file"));
			resources = new HashSet<TaskResource>();
			resources.add(file);
			page.projectToResources.put(project, resources);

			long value = System.nanoTime();
			page.fileToValue.put(file, value);

			assertEquals(value, page.getValueOfCategory(cat));
			assertEquals(value, page.getValue(cat));
		}
	}

	@Test
	public void testGetValueOfResource_file_andGetValue() {
		long value = 1010101;
		TaskResource file = new TaskResource(new LocalTask("b", "a"), root.getProject("p").getFile("f"));
		page.fileToValue.put(file, value);
		assertEquals(value, page.getValueOfFile(file));
		assertEquals(value, page.getValue(file));
	}

	@Test
	public void testGetValueOfResource_folder_andGetValue() {
		long value1 = 12312;
		long value2 = 98372;
		TaskResource taskFolder = new TaskResource(new LocalTask("b", "a"), root.getProject("p").getFolder("F"));
		Set<TaskResource> files = new HashSet<TaskResource>();

		TaskResource taskFile = new TaskResource(taskFolder.task, ((IFolder) taskFolder.resource).getFile("f"));
		files.add(taskFile);
		page.fileToValue.put(taskFile, value1);

		taskFile = new TaskResource(taskFolder.task, ((IFolder) taskFolder.resource).getFile("ffff"));
		files.add(taskFile);
		page.fileToValue.put(taskFile, value2);

		page.folderToFiles.put(taskFolder, files);

		assertEquals(value1 + value2, page.getValueOfFolder(taskFolder));
		assertEquals(value1 + value2, page.getValue(taskFolder));
	}

	@Test
	public void testGetValueOfResource_project_andGetValue() {
		long value1 = 12312;
		long value2 = 98372;
		TaskResource taskProject = new TaskResource(new LocalTask("b", "a"), root.getProject("p"));
		Set<TaskResource> files = new HashSet<TaskResource>();

		TaskResource taskFile = new TaskResource(taskProject.task, ((IProject) taskProject.resource).getFile("f"));
		files.add(taskFile);
		page.fileToValue.put(taskFile, value1);

		taskFile = new TaskResource(taskProject.task, ((IProject) taskProject.resource).getFile("ffff"));
		files.add(taskFile);
		page.fileToValue.put(taskFile, value2);

		page.projectToResources.put(taskProject, files);

		assertEquals(value1 + value2, page.getValueOfProject(taskProject));
		assertEquals(value1 + value2, page.getValue(taskProject));
	}

	@Test
	public void testGetValueOfTask_andGetValue() {
		long value1 = 12312;
		long value2 = 98372;
		ITask task = new LocalTask("b", "a");
		TaskResource taskProject = new TaskResource(task, root.getProject("p"));
		Set<TaskResource> files = new HashSet<TaskResource>();

		TaskResource taskFile = new TaskResource(taskProject.task, ((IProject) taskProject.resource).getFile("f"));
		files.add(taskFile);
		page.fileToValue.put(taskFile, value1);

		taskFile = new TaskResource(taskProject.task, ((IProject) taskProject.resource).getFile("ffff"));
		files.add(taskFile);
		page.fileToValue.put(taskFile, value2);

		page.projectToResources.put(taskProject, files);

		Set<TaskResource> projects = new HashSet<TaskResource>();
		projects.add(taskProject);
		page.taskToProjects.put(task, projects);

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
	public void testUpdate() {
		Map<ITask, Set<TaskResource>> taskToProjects = new HashMap<ITask, Set<TaskResource>>();
		Map<TaskResource, Set<TaskResource>> projectToResources = new HashMap<TaskResource, Set<TaskResource>>();
		Map<TaskResource, Set<TaskResource>> folderToFiles = new HashMap<TaskResource, Set<TaskResource>>();
		Map<TaskResource, Long> fileToValue = new HashMap<TaskResource, Long>();

		DisplayPreference preference = new DisplayPreference();
		Calendar end = preference.getEndDate();
		preference.getStartDate().setTime(end.getTime());
		preference.getStartDate().add(Calendar.MONTH, -3);

		IAccessor<Map<TaskId, Map<String, Long>>> accessor = TaskCore.getTaskDataAccessor();
		Map<TaskId, Map<String, Long>> data = accessor.getData(
				preference.getStartDate(), preference.getEndDate());

		IFileMapper resourceMapper = RabbitCore.getFileMapper();
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
				String pathStr = resourceMapper.getPath(fileEn.getKey());
				if (pathStr == null) {
					pathStr = resourceMapper.getExternalPath(fileEn.getKey());
				}
				if (pathStr == null) {
					continue;
				}

				IFile file = root.getFile(Path.fromPortableString(pathStr));
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

		assertEquals(taskToProjects.size(), page.taskToProjects.size());
		for (Entry<ITask, Set<TaskResource>> entry : taskToProjects.entrySet()) {

			Set<TaskResource> resources = page.taskToProjects.get(entry.getKey());
			assertNotNull(entry.getKey().toString(), resources);
			assertEquals(entry.getValue().size(), resources.size());

			for (TaskResource res : entry.getValue()) {
				assertTrue(resources.contains(res));
			}
		}

		assertEquals(projectToResources.size(), page.projectToResources.size());
		for (Entry<TaskResource, Set<TaskResource>> entry : projectToResources.entrySet()) {

			Set<TaskResource> resources = page.projectToResources.get(entry.getKey());
			assertNotNull(resources);
			assertEquals(entry.getValue().size(), resources.size());

			for (TaskResource res : entry.getValue()) {
				assertTrue(resources.contains(res));
			}
		}
		
		assertEquals(folderToFiles.size(), page.folderToFiles.size());
		for (Entry<TaskResource, Set<TaskResource>> entry : folderToFiles.entrySet()) {

			Set<TaskResource> resources = page.folderToFiles.get(entry.getKey());
			assertNotNull(resources);
			assertEquals(entry.getValue().size(), resources.size());

			for (TaskResource res : entry.getValue()) {
				assertTrue(resources.contains(res));
			}
		}

		assertEquals(fileToValue.size(), page.fileToValue.size());
		for (Entry<TaskResource, Long> entry : fileToValue.entrySet()) {
			assertEquals(entry.getValue(), page.fileToValue.get(entry.getKey()));
		}
	}

	@Override
	protected TaskPage createPage() {
		return new TaskPage();
	}
}
