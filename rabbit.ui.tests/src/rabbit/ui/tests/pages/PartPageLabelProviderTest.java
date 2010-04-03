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

import rabbit.ui.internal.pages.PartPage;
import rabbit.ui.internal.pages.PartPageLabelProvider;
import rabbit.ui.internal.util.MillisConverter;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.lang.reflect.Field;
import java.util.Map;

/**
 * Test for {@link PartPageLabelProvider}
 */
public class PartPageLabelProviderTest {

	private static Shell shell;
	private static PartPage page;
	private static PartPageLabelProvider provider;
	private static IWorkbenchPartDescriptor definedPart;
	private static IWorkbenchPartDescriptor undefinedPart;

	@AfterClass
	public static void afterClass() {
		provider.dispose();
		shell.dispose();
	}

	@BeforeClass
	public static void beforeClass() {
		shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		page = new PartPage();
		page.createContents(shell);
		provider = new PartPageLabelProvider(page);
		definedPart = PlatformUI.getWorkbench().getViewRegistry().getViews()[0];
		undefinedPart = new UndefinedWorkbenchPartDescriptor("abc.def.g");
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDispose() throws Exception {
		Field field = PartPageLabelProvider.class.getDeclaredField("images");
		field.setAccessible(true);
		Map<String, Image> images = (Map<String, Image>) field.get(provider);
		assertFalse(images.isEmpty());
		for (Image img : images.values()) {
			if (img != null) {
				assertFalse(img.isDisposed());
			}
		}

		provider.dispose();
		for (Image img : images.values()) {
			if (img != null) {
				assertTrue(img.isDisposed());
			}
		}
	}

	@Test
	public void testGetBackground() {
		assertNull(provider.getBackground(definedPart));
		assertNull(provider.getBackground(undefinedPart));
	}

	@Test
	public void testGetColumnImage() {
		assertNotNull(provider.getColumnImage(definedPart, 0));
		assertNotNull(provider.getColumnImage(undefinedPart, 0));

		assertNull(provider.getColumnImage(definedPart, 1));
		assertNull(provider.getColumnImage(undefinedPart, 1));
	}

	@Test
	public void testGetColumnText() throws Exception {
		Map<IWorkbenchPartDescriptor, Long> data = PartPageTest.getData(page);

		long definedValue = 18340;
		data.put(definedPart, definedValue);
		assertEquals(definedPart.getLabel(), provider.getColumnText(definedPart, 0));
		assertEquals(MillisConverter.toDefaultString(definedValue), provider.getColumnText(
				definedPart, 1));

		long undefinedValue = 18736392l;
		data.put(undefinedPart, undefinedValue);
		assertEquals(undefinedPart.getLabel(), provider.getColumnText(undefinedPart, 0));
		assertEquals(MillisConverter.toDefaultString(undefinedValue), provider.getColumnText(
				undefinedPart, 1));
	}

	@Test
	public void testGetForeground() {
		assertNull(provider.getForeground(definedPart));
		assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), provider
				.getForeground(undefinedPart));
	}

}
