package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.junit.Before;
import org.junit.Test;

import rabbit.ui.internal.util.FileElement;
import rabbit.ui.internal.util.FolderElement;
import rabbit.ui.internal.util.MillisConverter;
import rabbit.ui.internal.util.ProjectElement;
import rabbit.ui.internal.util.ResourceElement;

/**
 * {@link ResourcePageLabelProvider}
 */
public class ResourcePageLabelProviderTest {

	private long value = 239;
	private ResourceElement project = new ProjectElement(Path.fromPortableString("/p"));
	private ResourceElement folder = new FolderElement(project.getPath().append("f"));
	private ResourceElement file = new FileElement(folder.getPath().append("f.e"), value);

	private ResourcePageLabelProvider provider;

	@Before
	public void before() {
		provider = new ResourcePageLabelProvider(true, true, true);
	}

	@Test
	public void testGetColumnText() {
		// Show all:
		provider = new ResourcePageLabelProvider(true, true, true);
		assertEquals(project.getName(), provider.getColumnText(project, 0));
		assertEquals(folder.getName(), provider.getColumnText(folder, 0));
		assertEquals(file.getName(), provider.getColumnText(file, 0));
		assertEquals(MillisConverter.toDefaultString(project.getValue()), provider.getColumnText(project, 1));
		assertEquals(MillisConverter.toDefaultString(folder.getValue()), provider.getColumnText(folder, 1));
		assertEquals(MillisConverter.toDefaultString(file.getValue()), provider.getColumnText(file, 1));

		// Show project only:
		provider = new ResourcePageLabelProvider(true, false, false);
		assertEquals(project.getName(), provider.getColumnText(project, 0));
		assertEquals(folder.getName(), provider.getColumnText(folder, 0));
		assertEquals(file.getName(), provider.getColumnText(file, 0));
		assertEquals(MillisConverter.toDefaultString(project.getValue()), provider.getColumnText(project, 1));
		assertNull(provider.getColumnText(folder, 1));
		assertNull(provider.getColumnText(file, 1));

		// Show folder only:
		provider = new ResourcePageLabelProvider(false, true, false);
		assertEquals(project.getName(), provider.getColumnText(project, 0));
		assertEquals(folder.getName(), provider.getColumnText(folder, 0));
		assertEquals(file.getName(), provider.getColumnText(file, 0));
		assertNull(provider.getColumnText(project, 1));
		assertEquals(MillisConverter.toDefaultString(folder.getValue()), provider.getColumnText(folder, 1));
		assertNull(provider.getColumnText(file, 1));

		// Show file only:
		provider = new ResourcePageLabelProvider(false, false, true);
		assertEquals(project.getName(), provider.getColumnText(project, 0));
		assertEquals(folder.getName(), provider.getColumnText(folder, 0));
		assertEquals(file.getName(), provider.getColumnText(file, 0));
		assertNull(provider.getColumnText(project, 1));
		assertNull(provider.getColumnText(folder, 1));
		assertEquals(MillisConverter.toDefaultString(file.getValue()), provider.getColumnText(file, 1));
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
	public void testGetForeground() throws Exception {
		ResourceElement notExist = new ProjectElement(Path.fromPortableString("/" + System.currentTimeMillis()));
		assertEquals(Display.getDefault().getSystemColor(SWT.COLOR_DARK_GRAY), provider.getForeground(notExist));

		if (ResourcesPlugin.getWorkspace().getRoot().getProjects().length == 0) {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(System.currentTimeMillis() + "");
			project.create(null);
			project.open(null);
		}
		ResourceElement exist = new ProjectElement(ResourcesPlugin.getWorkspace().getRoot().getProjects()[0].getFullPath());
		assertNull(provider.getForeground(exist));
	}

	@Test
	public void testGetBackgroun() {
		assertNull(provider.getBackground(project));
		assertNull(provider.getBackground(folder));
		assertNull(provider.getBackground(file));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testDispose() throws Exception {
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();

		Field resourceMapField = ResourcePageLabelProvider.class.getDeclaredField("resourceMap");
		resourceMapField.setAccessible(true);
		Map<String, Image> resourceMap = (Map<String, Image>) resourceMapField.get(provider);
		resourceMap.put("abc", sharedImages.getImageDescriptor(ISharedImages.IMG_OBJ_ELEMENT).createImage());

		Field editorMapField = ResourcePageLabelProvider.class.getDeclaredField("editorMap");
		editorMapField.setAccessible(true);
		Map<String, Image> editorMap = (Map<String, Image>) editorMapField.get(provider);
		editorMap.put("cde", sharedImages.getImageDescriptor(ISharedImages.IMG_OBJ_FILE).createImage());

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
}
