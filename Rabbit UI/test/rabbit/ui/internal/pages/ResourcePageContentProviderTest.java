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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.core.RabbitCore;
import rabbit.core.storage.IResourceMapper;

/**
 * Test for {@link ResourcePageContentProvider}
 */
public class ResourcePageContentProviderTest {

	private static Shell shell;
	private static ResourcePage page;
	private static IWorkspaceRoot root;
	private static IResourceMapper mapper;
	private static ResourcePageContentProvider provider;

	private static IProject project;
	private static IFolder folder;
	private static IFile file1;
	private static IFile file2;

	@AfterClass
	public static void afterClass() {
		shell.dispose();
	}

	@BeforeClass
	public static void beforeClass() {
		shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		page = new ResourcePage();
		page.createContents(shell);
		provider = new ResourcePageContentProvider(page);
		root = ResourcesPlugin.getWorkspace().getRoot();
		mapper = RabbitCore.getResourceManager();

		project = root.getProject("p");
		folder = project.getFolder("f");
		file1 = folder.getFile("a");
		file2 = folder.getFile("b");
	}

	@Test
	public void hasChildren() {
		assertTrue(provider.hasChildren(project));
		assertTrue(provider.hasChildren(folder));
		assertFalse(provider.hasChildren(file1));
	}

	@Test
	public void testGetChildren() throws Exception {
		Map<String, Long> data = new HashMap<String, Long>();
		data.put(mapper.insert(file1.getFullPath().toString()), 1L);
		data.put(mapper.insert(file2.getFullPath().toString()), 2L);
		ResourcePageTest.doUpdate(page, data);

		assertEquals(1, provider.getChildren(project).length);
		assertEquals(folder, provider.getChildren(project)[0]);

		Set<IFile> files = new HashSet<IFile>(2, 1);
		files.add(file1);
		files.add(file2);
		assertEquals(2, provider.getChildren(folder).length);
		assertTrue(files.contains(provider.getChildren(folder)[0]));
		assertTrue(files.contains(provider.getChildren(folder)[1]));
	}
}
