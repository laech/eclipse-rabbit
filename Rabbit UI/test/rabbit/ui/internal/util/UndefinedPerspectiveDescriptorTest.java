package rabbit.ui.internal.util;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test for {@link UndefinedPerspectiveDescriptor}
 */
public class UndefinedPerspectiveDescriptorTest {

	String id = "sansijiuqehnsdfjh22wiur";
	private UndefinedPerspectiveDescriptor per = new UndefinedPerspectiveDescriptor(id);

	@Test
	public void testGetId() {
		assertEquals(id, per.getId());
	}

	@Test
	public void testGetLabel() {
		assertEquals(id, per.getId());
	}

}
