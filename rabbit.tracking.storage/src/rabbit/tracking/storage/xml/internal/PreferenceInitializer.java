package rabbit.tracking.storage.xml.internal;

import java.io.File;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Initialize the plug-in preferences.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {}

	@Override public void initializeDefaultPreferences() {
		// TODO
		String path = System.getProperty("user.home") + File.separator
				+ "Desktop" + File.separator + "Rabbit" + File.separator
				+ "XmlDb";
		IPreferenceStore store = StoragePlugin.getDefault()
				.getPreferenceStore();
		store.setDefault(StoragePlugin.STORAGE_LOCATION, path);
	}

}
