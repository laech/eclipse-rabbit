package rabbit.ui.internal.pages;

import java.util.ArrayList;
import java.util.Collection;

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
 * Test for {@link ProjectPage}
 */
public class ProjectPageTest extends AbstractGraphTablePageTest {

	@Override
	protected ProjectPage createPage() {
		return new ProjectPage();
	}

	@Test
	public void testContentProvider() throws Exception {
		ITreeContentProvider provider = page.createContentProvider();

		Collection<Object> objects = new ArrayList<Object>();
		objects.add(new Object());
		assertTrue(provider.hasChildren(objects));

		ResourceElement resource = new ProjectElement(Path.fromPortableString("/project"));
		assertFalse(provider.hasChildren(resource));

		resource = new FolderElement(Path.fromPortableString("/project/folder"));
		assertFalse(provider.hasChildren(resource));

		resource = new FileElement(Path.fromPortableString("project/folder/file.txt"), 10);
		assertFalse(provider.hasChildren(resource));
	}
}
