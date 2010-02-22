package rabbit.ui.internal.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

import rabbit.ui.internal.util.ResourceElement.ResourceType;

/**
 * Test for {@link ProjectElement}
 */
public class ProjectElementTest extends ResourceElementTest {

	@Override
	protected ProjectElement createResource() {
		return new ProjectElement(Path.fromPortableString("/project"));
	}

	@Override
	public void testExists() {
		IPath notExists = Path.fromPortableString("/project");
		ProjectElement e = new ProjectElement(notExists);
		assertFalse(e.exists());

		try {
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(System.currentTimeMillis() + "");
			if (!project.exists()) {
				project.create(null);
			}
			if (!project.isOpen()) {
				project.open(null);
			}

			assertTrue(project.exists());

		} catch (CoreException e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Override
	public void testGetName() {
		String name = "asdnchjo";
		ProjectElement project = new ProjectElement(new Path("/" + name));
		assertEquals(name, project.getName());
	}

	@Override
	public void testGetPath() {
		IPath path = Path.fromPortableString("/projectabc");
		ProjectElement project = new ProjectElement(path);
		assertEquals(path, project.getPath());
	}

	@Override
	public void testGetType() {
		ProjectElement project = createResource();
		assertSame(ResourceType.PROJECT, project.getType());
	}

	@Override
	public void testGetValue() {
		ProjectElement project = createResource();
		assertTrue(Double.compare(0, project.getValue()) == 0);

		FileElement file1 = new FileElement(Path.fromPortableString("/p/d/a.txt"), 10);
		FileElement file2 = new FileElement(Path.fromPortableString("/p/d/b.txt"), 10);
		project.insert(file1);
		project.insert(file2);
		assertEquals(20, project.getValue());
	}

	@Override
	public void testInsert() {
		ProjectElement project = createResource();
		assertEquals(0, project.getChildren().size());

		FileElement file1 = new FileElement(Path.fromPortableString("/p/d/a.txt"), 10);
		FileElement file2 = new FileElement(Path.fromPortableString("/p/d/b.txt"), 10);
		project.insert(file1);
		project.insert(file2);
		assertEquals(2, project.getChildren().size());
		assertTrue(project.getChildren().contains(file1));
		assertTrue(project.getChildren().contains(file2));
	}

	@Override
	public void testSetPath() {
		IPath path = Path.fromPortableString("/nchhf");
		ProjectElement folder = createResource();
		folder.setPath(path);
		assertEquals(path, folder.getPath());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetPath_invalidPath() {
		IPath path = Path.fromPortableString("/2398fbncj/a");
		ProjectElement project = createResource();
		project.setPath(path);
	}

	@Test(expected = NullPointerException.class)
	public void testConstructor_nullPath() {
		new ProjectElement(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_invalidPath() {
		IPath path = Path.fromPortableString("/2398fbncj/1");
		new ProjectElement(path);
	}

}
