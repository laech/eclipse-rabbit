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
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.PerspectiveLabelProvider;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.ui.internal.util.MillisConverter;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

public class PerspectiveLabelProviderTest {

	private static Shell shell;
	private static PerspectivePage page;
	private static PerspectivePageLabelProvider provider;
	private static IPerspectiveDescriptor definedPerspective;
	private static IPerspectiveDescriptor undefinedPerspective;

	@BeforeClass
	public static void beforeClass() {
		shell = new Shell(PlatformUI.getWorkbench().getDisplay());
		page = new PerspectivePage();
		page.createContents(shell);
		provider = new PerspectivePageLabelProvider(page);
		definedPerspective = PlatformUI.getWorkbench().getPerspectiveRegistry().getPerspectives()[0];
		undefinedPerspective = new UndefinedPerspectiveDescriptor("abc.def.g");
	}

	@AfterClass
	public static void afterClass() {
		shell.dispose();
	}

	@Test
	public void testGetColumnText() throws Exception {
		Map<IPerspectiveDescriptor, Long> data = PerspectivePageTest.getData(page);

		long definedValue = 18340;
		data.put(definedPerspective, definedValue);
		assertEquals(definedPerspective.getLabel(), provider.getColumnText(definedPerspective, 0));
		assertEquals(MillisConverter.toDefaultString(definedValue), provider.getColumnText(definedPerspective, 1));

		long undefinedValue = 18736392l;
		data.put(undefinedPerspective, undefinedValue);
		assertEquals(undefinedPerspective.getLabel(), provider.getColumnText(undefinedPerspective, 0));
		assertEquals(MillisConverter.toDefaultString(undefinedValue), provider.getColumnText(undefinedPerspective, 1));
	}

	@Test
	public void testGetColumnImage() {
		assertNotNull(provider.getColumnImage(definedPerspective, 0));
		assertNotNull(provider.getColumnImage(undefinedPerspective, 0));

		assertNull(provider.getColumnImage(definedPerspective, 1));
		assertNull(provider.getColumnImage(undefinedPerspective, 1));
	}

	@Test
	public void testGetForeground() {
		assertNull(provider.getForeground(definedPerspective));
		assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), provider.getForeground(undefinedPerspective));
	}

	@Test
	public void testGetBackground() {
		assertNull(provider.getBackground(definedPerspective));
		assertNull(provider.getBackground(undefinedPerspective));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDispose() throws Exception {
		Field internalProviderField = PerspectivePageLabelProvider.class.getDeclaredField("provider");
		internalProviderField.setAccessible(true);

		PerspectiveLabelProvider internalProvider = (PerspectiveLabelProvider) internalProviderField.get(provider);
		Field imageField = internalProvider.getClass().getDeclaredField("imageCache");
		imageField.setAccessible(true);
		Map images = (Map) imageField.get(internalProvider);
		assertFalse(images.isEmpty());
		for (Object img : images.values()) {
			assertFalse(((Image) img).isDisposed());
		}

		provider.dispose();
		for (Object img : images.values()) {
			assertTrue(((Image) img).isDisposed());
		}
	}
}
