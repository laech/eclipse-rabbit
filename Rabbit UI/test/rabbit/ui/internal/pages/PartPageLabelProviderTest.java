package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.eclipse.ui.PlatformUI;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.ui.internal.util.MillisConverter;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

/**
 * Test for {@link PartPageLabelProvider}
 */
public class PartPageLabelProviderTest {

	private static Shell shell;
	private static PartPage page;
	private static PartPageLabelProvider provider;
	private static IWorkbenchPartDescriptor definedPart;
	private static IWorkbenchPartDescriptor undefinedPart;

	@BeforeClass
	public static void beforeClass() {
		shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		page = new PartPage();
		page.createContents(shell);
		provider = new PartPageLabelProvider(page);
		definedPart = PlatformUI.getWorkbench().getViewRegistry().getViews()[0];
		undefinedPart = new UndefinedWorkbenchPartDescriptor("abc.def.g");
	}

	@AfterClass
	public static void afterClass() {
		shell.dispose();
	}

	@Test
	public void testGetColumnText() throws Exception {
		Map<IWorkbenchPartDescriptor, Long> data = PartPageTest.getData(page);

		long definedValue = 18340;
		data.put(definedPart, definedValue);
		assertEquals(definedPart.getLabel(), provider.getColumnText(definedPart, 0));
		assertEquals(MillisConverter.toDefaultString(definedValue), provider.getColumnText(definedPart, 1));

		long undefinedValue = 18736392l;
		data.put(undefinedPart, undefinedValue);
		assertEquals(undefinedPart.getLabel(), provider.getColumnText(undefinedPart, 0));
		assertEquals(MillisConverter.toDefaultString(undefinedValue), provider.getColumnText(undefinedPart, 1));
	}

	@Test
	public void testGetColumnImage() {
		assertNotNull(provider.getColumnImage(definedPart, 0));
		assertNotNull(provider.getColumnImage(undefinedPart, 0));

		assertNull(provider.getColumnImage(definedPart, 1));
		assertNull(provider.getColumnImage(undefinedPart, 1));
	}

	@Test
	public void testGetForeground() {
		assertNull(provider.getForeground(definedPart));
		assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), provider.getForeground(undefinedPart));
	}

	@Test
	public void testGetBackground() {
		assertNull(provider.getBackground(definedPart));
		assertNull(provider.getBackground(undefinedPart));
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

}
