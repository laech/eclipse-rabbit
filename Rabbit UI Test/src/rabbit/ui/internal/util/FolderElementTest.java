package rabbit.ui.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import rabbit.ui.internal.util.ResourceElement.ResourceType;

/**
 * Test for {@link FolderElement}
 */
public class FolderElementTest extends ResourceElementTest {

	@Override
	protected FolderElement createResource() {
		return new FolderElement(Path.fromPortableString("/p/f.txt"));
	}

	@Override
	public void testExists() {
		IPath notExists = Path.fromPortableString("/project/" + System.currentTimeMillis());
		FolderElement e = new FolderElement(notExists);
		assertFalse(e.exists());

		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(System.currentTimeMillis() + "");
			if (!project.exists()) {
				project.create(null);
			}
			if (!project.isOpen()) {
				project.open(null);
			}

			IFolder folder = project.getFolder("aFolder");
			if (!folder.exists()) {
				folder.create(true, true, null);
			}

			assertTrue(folder.exists());

		} catch (CoreException e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Override
	public void testGetName() {
		IPath path = Path.fromPortableString("/project/a/b/c/d/e");
		FolderElement folder = new FolderElement(path);
		assertEquals("a.b.c.d.e", folder.getName());
	}

	@Override
	public void testGetPath() {
		IPath path = Path.fromPortableString("/project/sdff/abc/fdf");
		FolderElement folder = new FolderElement(path);
		assertEquals(path, folder.getPath());
	}

	@Override
	public void testGetType() {
		FolderElement e = createResource();
		assertSame(ResourceType.FOLDER, e.getType());
	}

	@Override
	public void testGetValue() {
		FolderElement folder = createResource();
		assertTrue(Double.compare(0, folder.getValue()) == 0);

		FileElement file1 = new FileElement(Path.fromPortableString("/p/d/a.txt"), 10);
		FileElement file2 = new FileElement(Path.fromPortableString("/p/d/b.txt"), 10);
		folder.insert(file1);
		folder.insert(file2);
		assertEquals(20, folder.getValue());
	}

	@Override
	public void testInsert() {
		FolderElement folder = createResource();
		assertEquals(0, folder.getChildren().size());

		FileElement file1 = new FileElement(Path.fromPortableString("/p/d/a.txt"), 10);
		FileElement file2 = new FileElement(Path.fromPortableString("/p/d/b.txt"), 10);
		folder.insert(file1);
		folder.insert(file2);
		assertEquals(2, folder.getChildren().size());
		assertTrue(folder.getChildren().contains(file1));
		assertTrue(folder.getChildren().contains(file2));
	}

	@Override
	public void testSetPath() {
		IPath path = Path.fromPortableString("/nchhf/238749/2398fbncj/asdf");
		FolderElement folder = createResource();
		folder.setPath(path);
		assertEquals(path, folder.getPath());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetPath_invalidPath() {
		IPath path = Path.fromPortableString("/2398fbncj");
		FolderElement folder = createResource();
		folder.setPath(path);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor_nullPath() {
		new FolderElement(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_invalidPath() {
		IPath path = Path.fromPortableString("/2398fbncj");
		new FolderElement(path);
	}
}
