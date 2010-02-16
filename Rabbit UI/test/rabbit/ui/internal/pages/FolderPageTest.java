package rabbit.ui.internal.pages;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.junit.Test;

import rabbit.ui.internal.util.FileElement;
import rabbit.ui.internal.util.FolderElement;
import rabbit.ui.internal.util.ProjectElement;
import rabbit.ui.internal.util.ResourceElement;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Test for {@link FolderPage}
 */
public class FolderPageTest extends AbstractGraphTablePageTest {

	@Override
	protected AbstractGraphTreePage createPage() {
		return new FolderPage();
	}

	@Test
	public void testContentProvider() throws Exception {
		ITreeContentProvider provider = page.createContentProvider();

		ResourceElement resource = new ProjectElement(Path.fromPortableString("/project"));
		assertFalse(provider.hasChildren(resource));
		resource.insert(new FileElement(Path.fromPortableString("project/folder/file.txt"), 10));
		assertTrue(provider.hasChildren(resource));

		resource = new FolderElement(Path.fromPortableString("/project/folder"));
		assertFalse(provider.hasChildren(resource));

		resource = new FileElement(Path.fromPortableString("project/folder/file.txt"), 10);
		assertFalse(provider.hasChildren(resource));
	}
}
