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
package rabbit.mylyn.tests.ui.pages;

import rabbit.mylyn.TaskId;
import rabbit.mylyn.internal.ui.pages.TaskResource;
import rabbit.mylyn.internal.ui.util.MissingTask;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.mylyn.tasks.core.ITask;
import org.junit.Test;

import java.util.Date;

/**
 * @see TaskResource
 */
public class TaskResourceTest {

	@Test(expected = NullPointerException.class)
	public void testConstructor_taskNull() {
		new TaskResource(null, ResourcesPlugin.getWorkspace().getRoot());
	}
	
	@Test(expected = NullPointerException.class)
	public void testConstructor_resourceNull() {
		new TaskResource(new MissingTask(new TaskId("id", new Date())), null);
	}

	@Test
	public void testHashCode() {
		ITask task = new MissingTask(new TaskId("abc", new Date()));
		IResource resource = ResourcesPlugin.getWorkspace().getRoot();
		int hashCode = (task.getHandleIdentifier() + resource.getFullPath().toString()).hashCode();
		TaskResource taskResource = new TaskResource(task, resource);
		assertEquals(hashCode, taskResource.hashCode());
	}
	
	@Test
	public void testEquals() {
		ITask task = new MissingTask(new TaskId("abc", new Date()));
		IWorkspaceRoot resource = ResourcesPlugin.getWorkspace().getRoot();
		
		TaskResource taskResource1 = new TaskResource(task, resource);
		TaskResource taskResource2 = new TaskResource(task, resource);
		assertTrue(taskResource1.equals(taskResource2));
		
		taskResource2 = new TaskResource(task, resource.getProject("proj"));
		assertFalse(taskResource1.equals(taskResource2));
		
		taskResource2 = new TaskResource(new MissingTask(new TaskId("ddd", new Date())), resource);
		assertFalse(taskResource1.equals(taskResource2));
	}
}
