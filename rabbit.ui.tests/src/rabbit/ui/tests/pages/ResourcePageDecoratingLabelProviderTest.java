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
package rabbit.ui.tests.pages;

import rabbit.ui.internal.pages.ResourcePage;
import rabbit.ui.internal.pages.ResourcePageDecoratingLabelProvider;
import rabbit.ui.internal.pages.ResourcePageLabelProvider;
import rabbit.ui.internal.pages.ResourcePage.ShowMode;
import rabbit.ui.internal.util.MillisConverter;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;

/**
 * {@link ResourcePageDecoratingLabelProvider}
 */
public class ResourcePageDecoratingLabelProviderTest {

	private static ResourcePageDecoratingLabelProvider provider;
	private static ResourcePageLabelProvider labeler;
	private static ILabelDecorator decorator;
	private static ResourcePage page;
	private static Shell shell;

	private static IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject("p");
	private static IFolder folder = project.getFolder("f");
	private static IFile file = project.getFile("a");
	private static long value = 98982361;

	@AfterClass
	public static void afterClass() {
		provider.dispose();
		shell.dispose();
	}

	@BeforeClass
	public static void beforeClass() throws Exception {
		shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		page = new ResourcePage();
		page.createContents(shell);

		labeler = new ResourcePageLabelProvider();
		decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		provider = new ResourcePageDecoratingLabelProvider(page, labeler, decorator);

		ResourcePageTest.getFieldFileValues(page).put(file, value);
		ResourcePageTest.getFieldFolderFiles(page).put(folder,
				new HashSet<IFile>(Arrays.asList(file)));
		ResourcePageTest.getFieldProjectResources(page).put(project,
				new HashSet<IResource>(Arrays.asList(folder)));
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
		String projectStr = decorator.decorateText(labeler.getText(project), project);
		String folderStr = decorator.decorateText(labeler.getText(folder), folder);
		String fileStr = file.getName();

		// Show project only:
		page.setShowMode(ShowMode.PROJECT);
		assertEquals(projectStr, provider.getColumnText(project, 0));
		assertEquals(folderStr, provider.getColumnText(folder, 0));
		assertEquals(fileStr, provider.getColumnText(file, 0));
		assertEquals(MillisConverter.toDefaultString(page.getValueOfProject(project)), provider
				.getColumnText(project, 1));
		assertNull(provider.getColumnText(folder, 1));
		assertNull(provider.getColumnText(file, 1));

		// Show folder only:
		page.setShowMode(ShowMode.FOLDER);
		assertEquals(projectStr, provider.getColumnText(project, 0));
		assertEquals(folderStr, provider.getColumnText(folder, 0));
		assertEquals(fileStr, provider.getColumnText(file, 0));
		assertNull(provider.getColumnText(project, 1));
		assertEquals(MillisConverter.toDefaultString(page.getValueOfFolder(folder)),
				provider.getColumnText(folder, 1));
		assertNull(provider.getColumnText(file, 1));

		// Show file:
		page.setShowMode(ShowMode.FILE);
		assertEquals(projectStr, provider.getColumnText(project, 0));
		assertEquals(folderStr, provider.getColumnText(folder, 0));
		assertEquals(fileStr, provider.getColumnText(file, 0));
		assertNull(provider.getColumnText(project, 1));
		assertNull(provider.getColumnText(folder, 1));
		assertEquals(MillisConverter.toDefaultString(page.getValueOfFile(file)),
				provider.getColumnText(file, 1));
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
