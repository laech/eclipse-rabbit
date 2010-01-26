package rabbit.tracking;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

/**
 * Initialize the plug-in preferences.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

	public PreferenceInitializer() {
	}

	@Override
	public void initializeDefaultPreferences() {
		// TODO
		IPreferenceStore store = TrackingPlugin.getDefault().getPreferenceStore();
		store.setDefault(TrackingPlugin.STORAGE_LOCATION, 
				"C:\\Users\\o-o\\Desktop\\Rabbit");
	}

}
