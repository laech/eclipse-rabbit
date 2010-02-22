package rabbit.core.internal;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Assert;
import org.junit.Test;

import rabbit.core.RabbitCore;

/**
 * Test for {@link PreferenceInitializer}
 */
public class PreferenceInitializerTest {

	private PreferenceInitializer pref = new PreferenceInitializer();

	@Test
	public void testInitializePreference() {
		IPreferenceStore store = RabbitCore.getDefault().getPreferenceStore();
		store.setDefault(RabbitCore.STORAGE_LOCATION, "");
		store.setDefault(RabbitCore.IDLE_DETECTOR_ENABLE, "");

		pref.initializeDefaultPreferences();
		Assert.assertFalse(store.getDefaultString(RabbitCore.STORAGE_LOCATION).equals(""));
		Assert.assertFalse(store.getDefaultBoolean(RabbitCore.IDLE_DETECTOR_ENABLE));
	}
}
