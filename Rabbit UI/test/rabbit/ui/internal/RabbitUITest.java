package rabbit.ui.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.Platform;
import org.junit.Test;

public class RabbitUITest {

	@Test
	public void testPluginId() {
		assertEquals(RabbitUI.getDefault().getBundle().getSymbolicName(), RabbitUI.PLUGIN_ID);
	}

	@Test
	public void testExtensionId() {
		assertTrue(Platform.getExtensionRegistry().getConfigurationElementsFor(RabbitUI.UI_PAGE_EXTENSION_ID).length > 0);
	}

	@Test
	public void testGetPages() {
		assertNotNull(RabbitUI.getDefault().getPages());
		assertFalse(RabbitUI.getDefault().getPages().isEmpty());
	}

}
