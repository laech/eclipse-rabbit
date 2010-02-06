package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import rabbit.ui.internal.pages.UndefinedWorkbenchPartDescriptor;

/**
 * Test for {@link UndefinedWorkbenchPartDescriptor}
 */
public class UndefinedWorkbenchPartDescriptorTest {

	@Test
	public void testGetId() {
		UndefinedWorkbenchPartDescriptor u = new UndefinedWorkbenchPartDescriptor("iid");
		assertEquals("iid", u.getId());
	}

	@Test
	public void testGetLabel() {
		UndefinedWorkbenchPartDescriptor u = new UndefinedWorkbenchPartDescriptor("iid");
		assertNotNull(u.getLabel());
		assertEquals(u.getId(), u.getId());
	}

	@Test
	public void testGetImage() {
		assertNotNull(new UndefinedWorkbenchPartDescriptor("id").getImageDescriptor());
	}

}
