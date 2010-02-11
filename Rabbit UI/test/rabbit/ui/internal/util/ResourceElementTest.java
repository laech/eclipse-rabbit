package rabbit.ui.internal.util;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Test for {@link ResourceElement}
 */
public abstract class ResourceElementTest {

	private ResourceElement resource;

	protected abstract ResourceElement createResource();

	@Before
	public void setUp() {
		resource = createResource();
	}

	@Test
	public void testGetChildren() {
		assertNotNull(resource.getChildren());
		assertEquals(0, resource.getChildren().size());
	}

	@Test(expected = NullPointerException.class)
	public void testSetPath_nullPath() {
		ResourceElement file = createResource();
		file.setPath(null);
	}

	@Test
	public abstract void testInsert();

	@Test
	public abstract void testExists();

	@Test
	public abstract void testGetName();

	@Test
	public abstract void testGetPath();

	@Test
	public abstract void testGetType();

	@Test
	public abstract void testGetValue();

	@Test
	public abstract void testSetPath();

}
