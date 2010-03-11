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
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.ui.internal.pages.ResourcePage.ShowMode;
import rabbit.ui.internal.util.MillisConverter;

/**
 * {@link ResourcePageLabelProvider}
 */
public class ResourcePageLabelProviderTest {

	private static ResourcePageLabelProvider provider;
	private static ResourcePage page;
	private static Shell shell;

	private static IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("p");
	private static IFolder folder = project.getFolder("f");
	private static IFile file = project.getFile("a");
	private static long value = 98982361;

	@AfterClass
	public static void afterClass() {
		shell.dispose();
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		page = new ResourcePage() {

			@Override
			public long getValue(Object o) {
				if (o instanceof IProject) {
					return getValueOfProject((IProject) o);
				} else if (o instanceof IFolder) {
					return getValueOfFolder((IFolder) o);
				} else if (o instanceof IFile) {
					return getValueOfFile((IFile) o);
				} else {
					return 0;
				}
			}
		};
		page.createContents(shell);

		provider = new ResourcePageLabelProvider(page);

		ResourcePageTest.getFieldFileValues(page).put(file, value);
		ResourcePageTest.getFieldFolderFiles(page).put(folder,
				new HashSet<IFile>(Arrays.asList(file)));
		ResourcePageTest.getFieldProjectResources(page).put(project,
				new HashSet<IResource>(Arrays.asList(folder)));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDispose() throws Exception {
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

		Field resourceMapField = ResourcePageLabelProvider.class.getDeclaredField("resourceMap");
		resourceMapField.setAccessible(true);
		Map<String, Image> resourceMap = (Map<String, Image>) resourceMapField.get(provider);
		resourceMap.put("abc", sharedImages.getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT)
				.createImage());

		Field editorMapField = ResourcePageLabelProvider.class.getDeclaredField("editorMap");
		editorMapField.setAccessible(true);
		Map<String, Image> editorMap = (Map<String, Image>) editorMapField.get(provider);
		editorMap.put("cde", sharedImages.getImageDescriptor(ISharedImages.IMG_OBJ_FILE)
				.createImage());

		provider.dispose();
		for (Image img : resourceMap.values()) {
			if (img != null) {
				assertTrue(img.isDisposed());
			}
		}
		for (Image img : editorMap.values()) {
			if (img != null) {
				assertTrue(img.isDisposed());
			}
		}
	}

	@Test
	public void testGetBackgroun() {
		assertNull(provider.getBackground(project));
		assertNull(provider.getBackground(folder));
		assertNull(provider.getBackground(file));
	}

	@Test
	public void testGetColumnImage() {
		assertNotNull(provider.getColumnImage(project, 0));
		assertNotNull(provider.getColumnImage(folder, 0));
		assertNotNull(provider.getColumnImage(file, 0));
		assertNull(provider.getColumnImage(project, 1));
		assertNull(provider.getColumnImage(folder, 1));
		assertNull(provider.getColumnImage(file, 1));
	}

	@Test
	public void testGetColumnText() {
		// Show project only:
		page.setShowMode(ShowMode.PROJECT);
		assertEquals(project.getName(), provider.getColumnText(project, 0));
		assertEquals(folder.getName(), provider.getColumnText(folder, 0));
		assertEquals(file.getName(), provider.getColumnText(file, 0));
		assertEquals(MillisConverter.toDefaultString(page.getValueOfProject(project)), provider
				.getColumnText(project, 1));
		assertNull(provider.getColumnText(folder, 1));
		assertNull(provider.getColumnText(file, 1));

		// Show folder only:
		page.setShowMode(ShowMode.FOLDER);
		assertEquals(project.getName(), provider.getColumnText(project, 0));
		assertEquals(folder.getName(), provider.getColumnText(folder, 0));
		assertEquals(file.getName(), provider.getColumnText(file, 0));
		assertNull(provider.getColumnText(project, 1));
		assertEquals(MillisConverter.toDefaultString(page.getValueOfFolder(folder)), provider
				.getColumnText(folder, 1));
		assertNull(provider.getColumnText(file, 1));

		// Show file:
		page.setShowMode(ShowMode.FILE);
		assertEquals(project.getName(), provider.getColumnText(project, 0));
		assertEquals(folder.getName(), provider.getColumnText(folder, 0));
		assertEquals(file.getName(), provider.getColumnText(file, 0));
		assertNull(provider.getColumnText(project, 1));
		assertNull(provider.getColumnText(folder, 1));
		assertEquals(MillisConverter.toDefaultString(page.getValueOfFile(file)), provider
				.getColumnText(file, 1));
	}

	@Test
	public void testGetForeground() throws Exception {
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(
				System.currentTimeMillis() + "");
		assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), provider
				.getForeground(project));

		project.create(null);
		project.open(null);
		assertNull(provider.getForeground(project));
	}
}
