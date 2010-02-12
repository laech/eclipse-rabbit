package rabbit.ui.internal.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import rabbit.ui.internal.util.ResourceElement.ResourceType;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/**
 * Test for {@link FileElement}
 */
public class FileElementTest extends ResourceElementTest {

	private double value = 10834;

	@Override
	protected FileElement createResource() {
		return new FileElement(Path.fromPortableString("/project/file.ex"), value);
	}

	@Override
	public void testExists() {
		IPath notExists = Path.fromPortableString("/project/" + System.currentTimeMillis() + ".txt");
		FileElement e = new FileElement(notExists, 10);
		assertFalse(e.exists());

		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(System.currentTimeMillis() + "");
			if (!project.exists()) {
				project.create(null);
			}
			if (!project.isOpen()) {
				project.open(null);
			}

			IFile file = project.getFile(System.currentTimeMillis() + ".txt");
			if (!file.exists()) {
				file.create(null, true, null);
			}

			assertTrue(file.exists());
		} catch (CoreException e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Override
	public void testGetName() {
		String name = "hello.txt";
		IPath path = Path.fromPortableString("/project/" + name);
		FileElement file = new FileElement(path, 2);
		assertEquals(name, file.getName());
	}

	@Override
	public void testGetPath() {
		IPath path = Path.fromPortableString("/project/sdff.txt");
		FileElement file = new FileElement(path, 2);
		assertEquals(path, file.getPath());
	}

	@Override
	public void testGetType() {
		FileElement file = new FileElement(Path.fromPortableString("/p/d.txt"), 1);
		assertSame(ResourceType.FILE, file.getType());
	}

	@Override
	public void testGetValue() {
		FileElement file = new FileElement(Path.fromPortableString("/p/f.txt"), 10);
		assertTrue(Double.compare(10, file.getValue()) == 0);
	}

	@Override
	public void testInsert() {
		FileElement file1 = createResource();
		FileElement file2 = createResource();
		assertNull(file1.insert(file2));
	}

	@Override
	public void testSetPath() {
		IPath path = Path.fromPortableString("/nchhf/238749/2398fbncj.txt");
		FileElement file = createResource();
		file.setPath(path);
		assertEquals(path, file.getPath());
	}

	@Test
	public void testSetValue() {
		double value = Math.random();
		FileElement file = createResource();
		file.setValue(value);
		assertTrue(Double.compare(value, file.getValue()) == 0);

		value = 0;
		file.setValue(value);
		assertTrue(Double.compare(value, file.getValue()) == 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetPath_invalidPath() {
		IPath path = Path.fromPortableString("/2398fbncj.txt");
		FileElement file = createResource();
		file.setPath(path);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor_nullPath() {
		new FileElement(null, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_invalidPath() {
		IPath path = Path.fromPortableString("/2398fbncj.txt");
		new FileElement(path, 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_invalidValue() {
		IPath path = Path.fromPortableString("/p/2398fbncj.txt");
		new FileElement(path, -1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetValue_invalidValue() {
		FileElement file = createResource();
		file.setValue(-1);
	}
}
