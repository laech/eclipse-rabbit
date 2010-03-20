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
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.core.TaskCategory;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import rabbit.tasks.ui.internal.pages.TaskPage.ShowMode;

/**
 * @see TaskPageContentProvider
 */
@SuppressWarnings("restriction")
public class TaskPageContentProviderTest {

	private static final Shell shell;
	private static final IWorkspaceRoot root;

	static {
		shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		root = ResourcesPlugin.getWorkspace().getRoot();
	}

	@AfterClass
	public static void afterClass() {
		shell.dispose();
	}

	private TaskPage page;
	private TaskPageContentProvider provider;

	@Before
	public void before() {
		page = new TaskPage();
		page.createContents(shell);
		provider = new TaskPageContentProvider(page);
	}

	@Test
	public void testHasChildren() {

		// Create a task:
		LocalTask task = new LocalTask("id", "summary");
		task.setCreationDate(new Date());

		// Add task to Mylyn:
		AbstractTaskContainer category = TasksUiPlugin.getTaskList().getRootElements().iterator().next();
		category.internalAddChild(task);

		// Link a file with value:
		TaskResource taskFile = new TaskResource(task, root.getProject("p").getFolder("f").getFile("ff"));
		page.fileToValue.put(taskFile, 1000L);

		// Link a folder with the file:
		TaskResource taskFolder = new TaskResource(task, taskFile.resource.getParent());
		Set<TaskResource> resources = new HashSet<TaskResource>();
		resources.add(taskFile);
		page.folderToFiles.put(taskFolder, resources);

		// Link a project with the folder:
		TaskResource taskProject = new TaskResource(task, taskFolder.resource.getParent());
		resources.clear();
		resources.add(taskFolder);
		page.projectToResources.put(taskProject, resources);

		// Link the task with the project:
		resources.clear();
		resources.add(taskProject);
		page.taskToProjects.put(task, resources);

		// Test with FILE mode
		page.setShowMode(ShowMode.FILE);
		assertTrue(provider.hasChildren(category));
		assertTrue(provider.hasChildren(task));
		assertTrue(provider.hasChildren(taskProject));
		assertTrue(provider.hasChildren(taskFolder));
		assertFalse(provider.hasChildren(taskFile));

		// Test with FOLDER mode
		page.setShowMode(ShowMode.FOLDER);
		assertTrue(provider.hasChildren(category));
		assertTrue(provider.hasChildren(task));
		assertTrue(provider.hasChildren(taskProject));
		assertFalse(provider.hasChildren(taskFolder));
		assertFalse(provider.hasChildren(taskFile));

		// Test with PROJECT mode
		page.setShowMode(ShowMode.PROJECT);
		assertTrue(provider.hasChildren(category));
		assertTrue(provider.hasChildren(task));
		assertFalse(provider.hasChildren(taskProject));
		assertFalse(provider.hasChildren(taskFolder));
		assertFalse(provider.hasChildren(taskFile));

		// Test with TASK mode
		page.setShowMode(ShowMode.TASK);
		assertTrue(provider.hasChildren(category));
		assertFalse(provider.hasChildren(task));
		assertFalse(provider.hasChildren(taskProject));
		assertFalse(provider.hasChildren(taskFolder));
		assertFalse(provider.hasChildren(taskFile));
		{
			// Add a sub task with value into the task.
			// Now has children should return true:
			LocalTask subTask = new LocalTask("abc", "nulla");
			subTask.setCreationDate(new Date());
			task.internalAddChild(subTask);

			// Link some value with sub task:
			taskFile = new TaskResource(subTask, taskFile.resource);
			page.fileToValue.put(taskFile, 10L);

			taskFolder = new TaskResource(subTask, taskFolder.resource);
			resources.clear();
			resources.add(taskFile);
			page.folderToFiles.put(taskFolder, resources);

			taskProject = new TaskResource(subTask, taskProject.resource);
			resources.clear();
			resources.add(taskFolder);
			page.projectToResources.put(taskProject, resources);

			resources.clear();
			resources.add(taskProject);
			page.taskToProjects.put(subTask, resources);

			// Test parent task:
			assertTrue(provider.hasChildren(task));
		}

		// Test with TASK_CATEGORY mode
		page.setShowMode(ShowMode.TASK_CATEGORY);
		assertFalse(provider.hasChildren(category));
		assertFalse(provider.hasChildren(task));
		assertFalse(provider.hasChildren(taskProject));
		assertFalse(provider.hasChildren(taskFolder));
		assertFalse(provider.hasChildren(taskFile));
	}

	@Test
	public void testGetChildren() {

		// Create a task:
		LocalTask task = new LocalTask("id", "summary");
		task.setCreationDate(new Date());

		// Add task to Mylyn:
		TaskCategory category = new TaskCategory("someCategory");
		TasksUiPlugin.getTaskList().addCategory(category);
		category.internalAddChild(task);

		// Link a file with value:
		TaskResource taskFile = new TaskResource(task, root.getProject("p").getFolder("f").getFile("ff"));
		page.fileToValue.put(taskFile, 1000L);

		// Link a folder with the file:
		TaskResource taskFolder = new TaskResource(task, taskFile.resource.getParent());
		Set<TaskResource> resources = new HashSet<TaskResource>();
		resources.add(taskFile);
		page.folderToFiles.put(taskFolder, resources);

		// Link a project with the folder:
		TaskResource taskProject = new TaskResource(task, taskFolder.resource.getParent());
		resources = new HashSet<TaskResource>();
		resources.add(taskFolder);
		page.projectToResources.put(taskProject, resources);

		// Link the task with the project:
		resources = new HashSet<TaskResource>();
		resources.add(taskProject);
		page.taskToProjects.put(task, resources);

		// Add a sub task with value into the task.
		LocalTask subTask = new LocalTask("abc", "nulla");
		subTask.setCreationDate(new Date());
		task.internalAddChild(subTask);

		// Link some value with sub task:
		TaskResource subtaskFile = new TaskResource(subTask, taskFile.resource);
		page.fileToValue.put(subtaskFile, 10L);

		TaskResource subtaskFolder = new TaskResource(subTask, taskFolder.resource);
		resources = new HashSet<TaskResource>();
		resources.add(subtaskFile);
		page.folderToFiles.put(subtaskFolder, resources);

		TaskResource subtaskProject = new TaskResource(subTask, taskProject.resource);
		resources = new HashSet<TaskResource>();
		resources.add(subtaskFolder);
		page.projectToResources.put(subtaskProject, resources);

		resources = new HashSet<TaskResource>();
		resources.add(subtaskProject);
		page.taskToProjects.put(subTask, resources);

		assertSame(task, provider.getChildren(category)[0]);
		// The project and sub task should be returned:
		assertEquals(2, provider.getChildren(task).length);
		assertTrue(provider.getChildren(task)[0] == subTask 
				|| provider.getChildren(task)[1] == subTask);
		assertTrue(provider.getChildren(task)[0] == taskProject 
				|| provider.getChildren(task)[1] == taskProject);
		assertSame(taskFolder, provider.getChildren(taskProject)[0]);
		assertSame(taskFile, provider.getChildren(taskFolder)[0]);
		assertSame(subtaskProject, provider.getChildren(subTask)[0]);
		assertSame(subtaskFolder, provider.getChildren(subtaskProject)[0]);
		assertSame(subtaskFile, provider.getChildren(subtaskFolder)[0]);
	}
}
