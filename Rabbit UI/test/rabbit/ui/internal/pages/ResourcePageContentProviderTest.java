package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.runtime.Path;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.ui.internal.util.FileElement;
import rabbit.ui.internal.util.FolderElement;
import rabbit.ui.internal.util.ProjectElement;
import rabbit.ui.internal.util.ResourceElement;

/**
 * Test for {@link ResourcePageContentProvider}
 */
public class ResourcePageContentProviderTest {

	private static ResourcePageContentProvider provider;
	private static Collection<ResourceElement> resources;
	private static ResourceElement project;
	private static ResourceElement folder;
	private static ResourceElement file;
	private static long value;

	@BeforeClass
	public static void beforeClass() {
		value = 1098;
		file = new FileElement(Path.fromPortableString("/p/f/f.txt"), value);
		folder = new FolderElement(Path.fromPortableString("/p/f"));
		project = new ProjectElement(Path.fromPortableString("/p"));
		provider = new ResourcePageContentProvider();
		resources = new ArrayList<ResourceElement>();

		folder.insert(file);
		project.insert(folder);
		resources.add(project);
	}

	@Test
	public void testGetChildren() {
		assertEquals(resources.size(), provider.getChildren(resources).length);
		assertEquals(project.getChildren().size(), provider.getChildren(project).length);
		assertEquals(folder.getChildren().size(), provider.getChildren(folder).length);
		assertEquals(0, provider.getChildren(file).length);

		assertSame(project, provider.getChildren(resources)[0]);
		assertSame(folder, provider.getChildren(project)[0]);
		assertSame(file, provider.getChildren(folder)[0]);
	}

	@Test
	public void hasChildren() {
		assertTrue(provider.hasChildren(project));
		assertTrue(provider.hasChildren(folder));
		assertFalse(provider.hasChildren(file));

		// Empty elements:
		assertFalse(provider.hasChildren(new FolderElement(Path.fromPortableString("/p/f"))));
		assertFalse(provider.hasChildren(new ProjectElement(Path.fromPortableString("/p"))));
	}
}
