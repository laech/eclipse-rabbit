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

	@Test
	public void testDefaultDisplayDatePeriod() {
		assertTrue(RabbitUI.getDefault().getDefaultDisplayDatePeriod() >= 0);
		RabbitUI.getDefault().setDefaultDisplayDatePeriod(10);
		assertEquals(10, RabbitUI.getDefault().getDefaultDisplayDatePeriod());
	}

}
