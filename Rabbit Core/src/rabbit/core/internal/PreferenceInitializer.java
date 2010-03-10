package rabbit.core.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
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
		IPath path = Path.fromOSString(System.getProperty("user.home")).append("Rabbit");

		IPreferenceStore store = RabbitCore.getDefault().getPreferenceStore();
		store.setDefault(RabbitCore.STORAGE_LOCATION, path.toOSString());
		store.setDefault(RabbitCore.IDLE_DETECTOR_ENABLE, false);
	}

}
