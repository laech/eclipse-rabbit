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
package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.junit.AfterClass;
import org.junit.Test;

/**
 * {@link ResourcePageDecoratingLabelProvider}
 */
public class ResourcePageLabelProviderTest {

	private static ResourcePageLabelProvider provider = new ResourcePageLabelProvider();

	private static IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("p");
	private static IFolder folder = project.getFolder("f");
	private static IFile file = project.getFile("a");
	
	@AfterClass
	public static void afterClass() {
		provider.dispose();
	}

	@Test
	public void testGetBackground() {
		assertNull(provider.getBackground(project));
		assertNull(provider.getBackground(folder));
		assertNull(provider.getBackground(file));
	}

	@Test
	public void testGetImage() {
		assertNotNull(provider.getImage(project));
		assertNotNull(provider.getImage(folder));
		assertNotNull(provider.getImage(file));
	}

	@Test
	public void testGetText() {
		assertEquals(project.getName(), provider.getText(project));
		assertEquals(folder.getName(), provider.getText(folder));
		assertEquals(file.getName(), provider.getText(file));
	}

	@Test
	public void testGetForeground() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot()
				.getProject(System.currentTimeMillis() + "");
		assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY),
				provider.getForeground(project));

		project.create(null);
		project.open(null);
		assertNull(provider.getForeground(project));
	}
}
