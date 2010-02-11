package rabbit.core.internal;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import rabbit.core.RabbitCore;

/**
 * Initialize the plug-in preferences.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		// TODO
		String path = System.getProperty("user.home") + File.separator
						// + "Desktop" + File.separator
				+ "Rabbit" + File.separator
				+ "XmlDb";
		IPreferenceStore store = RabbitCore.getDefault().getPreferenceStore();
		store.setDefault(RabbitCore.STORAGE_LOCATION, path);
	}

}
