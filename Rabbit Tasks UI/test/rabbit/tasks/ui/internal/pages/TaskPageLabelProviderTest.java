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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.LocalTask;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.junit.Test;

/**
 * @see TaskPageLabelProvider
 */
@SuppressWarnings("restriction")
public class TaskPageLabelProviderTest {

	private static TaskPageLabelProvider provider = new TaskPageLabelProvider();

	private static AbstractTaskContainer category = TasksUiPlugin.getTaskList().getRootElements().iterator().next();
	private static ITask task = new LocalTask("id", "summary");
	private static TaskResource file = new TaskResource(task, ResourcesPlugin.getWorkspace().getRoot().getProject("p").getFolder("f").getFile("ff"));
	private static TaskResource folder = new TaskResource(task, file.resource.getParent());
	private static TaskResource project = new TaskResource(task, folder.resource.getParent());

	@Test
	public void testGetBackground() {
		assertNull(provider.getBackground(category));
		assertNull(provider.getBackground(task));
		assertNull(provider.getBackground(project));
		assertNull(provider.getBackground(folder));
		assertNull(provider.getBackground(file));
	}

	@Test
	public void testGetImage() {
		assertNotNull(provider.getImage(category));
		assertNotNull(provider.getImage(task));
		assertNotNull(provider.getImage(project));
		assertNotNull(provider.getImage(folder));
		assertNotNull(provider.getImage(file));
	}

	@Test
	public void testGetText() {
		assertEquals(category.getSummary(), provider.getText(category));
		assertEquals(task.getSummary(), provider.getText(task));
		assertEquals(project.resource.getName(), provider.getText(project));
		assertEquals(folder.resource.getName(), provider.getText(folder));
		assertEquals(file.resource.getName(), provider.getText(file));
	}

	@Test
	public void testGetForeground() throws Exception {
		TaskResource project = new TaskResource(task, ResourcesPlugin.getWorkspace().getRoot().getProject(System.currentTimeMillis() + ""));
		assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), provider.getForeground(project));

		((IProject) project.resource).create(null);
		((IProject) project.resource).open(null);
		assertNull(provider.getForeground(project));
	}
}
