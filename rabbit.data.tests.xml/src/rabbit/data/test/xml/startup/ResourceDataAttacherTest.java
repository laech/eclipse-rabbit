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
package rabbit.data.test.xml.startup;

import rabbit.data.xml.XmlFileMapper;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.ui.internal.Workbench;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Field;

@SuppressWarnings("restriction")
public class ResourceDataAttacherTest {

	@Test
	public void testResourceListenerIsAttached() throws Exception {
		// Simple test to test the listener is attached, does not test the
		// accuracy of the results.

		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("tmp");
		if (!project.exists()) {
			project.create(null);
		}
		if (!project.isOpen()) {
			project.open(null);
		}
		
		IFile oldfile = project.getFile("f.txt");
		if (!oldfile.exists()) {
			FileInputStream stream = new FileInputStream(File.createTempFile("abc", "def"));
			oldfile.create(stream, true, null);
			stream.close();
		}
		XmlFileMapper.INSTANCE.insert(oldfile);

		IFile newfile = project.getFile("f2.txt");
		IPath newPath = newfile.getFullPath();
		
		assertNull(XmlFileMapper.INSTANCE.getId(newfile));
		oldfile.move(newPath, true, null);

		assertNull(XmlFileMapper.INSTANCE.getId(oldfile));
		assertNotNull(XmlFileMapper.INSTANCE.getId(newfile));
	}

	@Test
	public void testWorkbenchListenerIsAttached() throws Exception {
		Field field = Workbench.class.getDeclaredField("workbenchListeners");
		field.setAccessible(true);
		ListenerList listeners = (ListenerList) field.get(Workbench.getInstance());
		for (Object listener : listeners.getListeners()) {
			if (listener == XmlFileMapper.INSTANCE) {
				return;
			}
		}
		Assert.fail();
	}
}
