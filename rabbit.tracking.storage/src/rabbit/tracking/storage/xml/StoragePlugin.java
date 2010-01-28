package rabbit.tracking.storage.xml;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class StoragePlugin extends AbstractUIPlugin {


	/** The plug-in ID. **/
	public static final String PLUGIN_ID = "rabbit.tracking.storage";

	/** 
	 * Identifier for getting the storage location from the preference store.
	 * @see #getStoragePath() 
	 */
	public static final String STORAGE_LOCATION = "storageLocation";

	/** The shared instance. */
	private static StoragePlugin plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static StoragePlugin getDefault() {
		return plugin;
	}

	/**
	 * The constructor.
	 */
	public StoragePlugin() {
	}

	/**
	 * Gets the full path to the storage location of this plug-in.
	 * 
	 * @return The full path to the storage location folder.
	 */
	public IPath getStoragePath() {
		// TODO
		return new Path(getPreferenceStore().getString(STORAGE_LOCATION));
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

}
