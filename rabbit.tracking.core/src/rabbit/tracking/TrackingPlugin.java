package rabbit.tracking;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rabbit.tracking.internal.TrackerObject;
import rabbit.tracking.trackers.ITracker;

/**
 * The activator class controls the plug-in life cycle
 */
public class TrackingPlugin extends AbstractUIPlugin implements IWorkbenchListener {

	/** The plug-in ID. **/
	public static final String PLUGIN_ID = "Rabbit";

	/** 
	 * Identifier for getting the storage location from the preference store.
	 * @see #getStoragePath() 
	 */
	public static final String STORAGE_LOCATION = "storageLocation";

	/** ID of the tracker extension point. */
	public static final String TRACKER_EXTENSION_ID = "rabbit.tracking.tracker";

	/** The shared instance. */
	private static TrackingPlugin plugin;

	/** List of trackers loaded. */
	private List<TrackerObject> trackerList;

	/**
	 * The constructor.
	 */
	public TrackingPlugin() {
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		PlatformUI.getWorkbench().addWorkbenchListener(this);
		trackerList = createSensorsFromExtension();
		for (TrackerObject t : trackerList) {
			t.getTracker().setEnabled(true);
		}
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		PlatformUI.getWorkbench().removeWorkbenchListener(this);
		// Disables the trackers to let them finalize (save data etc.):
		for (TrackerObject t : trackerList) {
			t.getTracker().setEnabled(false);
		}
		plugin = null;
		super.stop(context);
	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		for (TrackerObject t : trackerList) {
			t.getTracker().setEnabled(false);
		}
		return true;
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static TrackingPlugin getDefault() {
		return plugin;
	}

	/**
	 * Gets the full path to the storage location of this plug-in.
	 * 
	 * @return The full path to the storage location folder.
	 */
	public IPath getStoragePath() {
		return new Path(getPreferenceStore().getString(STORAGE_LOCATION));
	}

	/**
	 * Creates sensors from the extension point.
	 * 
	 * @return A list of sensors.
	 */
	private List<TrackerObject> createSensorsFromExtension() {
		List<TrackerObject> result = new ArrayList<TrackerObject>();
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(TRACKER_EXTENSION_ID);
		for (IConfigurationElement e : elements) {
			try {
				Object o = e.createExecutableExtension("class");
				if (o instanceof ITracker) {
					String id = e.getAttribute("id");
					String name = e.getAttribute("name");
					String description = e.getAttribute("description");
					ITracker s = (ITracker) o;
					result.add(new TrackerObject(id, name, description, s));
				}
			} catch (Exception ex) {
				// Ignore and continue with the loop.
				ex.printStackTrace();
			}
		}
		return result;
	}

	@Override
	public void postShutdown(IWorkbench workbench) {
	}
}
