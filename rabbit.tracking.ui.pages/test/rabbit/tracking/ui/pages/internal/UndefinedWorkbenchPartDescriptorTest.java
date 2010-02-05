package rabbit.tracking.ui.pages.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * Test for {@link UndefinedWorkbenchPartDescriptor}
 */
public class UndefinedWorkbenchPartDescriptorTest {

	@Test public void testGetId() {
		UndefinedWorkbenchPartDescriptor u = new UndefinedWorkbenchPartDescriptor("iid");
		assertEquals("iid", u.getId());
	}

}
