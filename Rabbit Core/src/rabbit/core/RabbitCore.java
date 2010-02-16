package rabbit.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rabbit.core.internal.IdleDetector;
import rabbit.core.internal.TrackerObject;
import rabbit.core.internal.storage.xml.XmlResourceManager;
import rabbit.core.storage.IResourceManager;

/**
 * The activator class controls the plug-in life cycle
 */
public class RabbitCore extends AbstractUIPlugin implements IWorkbenchListener {

	/** The plug-in ID. **/
	public static final String PLUGIN_ID = "rabbit.core";

	/** ID of the tracker extension point. */
	public static final String TRACKER_EXTENSION_ID = "rabbit.core.trackers";

	/**
	 * Identifier for getting the storage location from the preference store.
	 * 
	 * @see #getStoragePath()
	 */
	public static final String STORAGE_LOCATION = "storageLocation";

	/** The shared instance. */
	private static RabbitCore plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RabbitCore getDefault() {
		return plugin;
	}

	/** List of trackers loaded. */
	private List<TrackerObject> trackerList;

	private IdleDetector idleDetector;

	/**
	 * The constructor.
	 */
	public RabbitCore() {
	}

	/**
	 * Creates sensors from the extension point.
	 * 
	 * @return A list of sensors.
	 */
	private List<TrackerObject> createSensorsFromExtension(IConfigurationElement[] elements) {
		List<TrackerObject> result = new ArrayList<TrackerObject>();
		for (IConfigurationElement e : elements) {
			try {
				Object o = e.createExecutableExtension("class");
				if (o instanceof ITracker<?>) {
					String id = e.getAttribute("id");
					String name = e.getAttribute("name");
					String description = e.getAttribute("description");
					ITracker<?> s = (ITracker<?>) o;
					result.add(new TrackerObject(id, name, description, s));
				}
			} catch (Exception ex) {
				// Ignore and continue with the loop.
				ex.printStackTrace();
				continue;
			}
		}
		return result;
	}

	/**
	 * Gets the resource manager.
	 * 
	 * @return The resource manager.
	 */
	public IResourceManager getResourceManager() {
		return XmlResourceManager.INSTANCE;
	}

	/**
	 * Gets the full path to the storage location of this plug-in.
	 * 
	 * @return The full path to the storage location folder.
	 */
	public IPath getStoragePath() {
		return new Path(getPreferenceStore().getString(STORAGE_LOCATION));
	}

	@Override
	public void postShutdown(IWorkbench workbench) {
		// Important not to do anything here, let XmlResourcemanager do its
		// final work.
	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		for (TrackerObject t : trackerList) {
			t.getTracker().setEnabled(false);
		}
		return true;
	}

	/**
	 * Call this method to saves all current data collected by the trackers now.
	 */
	public void saveTrackerData() {
		for (TrackerObject t : trackerList) {
			t.getTracker().saveData();
			t.getTracker().flushData();
		}
	}

	/**
	 * Enables or disables the trackers.
	 * 
	 * @param trackers
	 *            The trackers to perform actions on.
	 * @param enable
	 *            True to enable, false to disable.
	 */
	private void setEnableTrackers(Collection<TrackerObject> trackers, boolean enable) {
		for (TrackerObject o : trackers) {
			o.getTracker().setEnabled(enable);
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		idleDetector = new IdleDetector(getWorkbench().getDisplay(), 10000);
		getWorkbench().addWorkbenchListener(this);

		if (trackerList != null) { // May be we didn't stop correctly?
			setEnableTrackers(trackerList, false);
			trackerList.clear();
		}
		if (idleDetector != null) {
			idleDetector.setRunning(false);
		}

		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(TRACKER_EXTENSION_ID);
		trackerList = createSensorsFromExtension(elements);

		setEnableTrackers(trackerList, true);

		idleDetector.setRunning(true);
	}

	public IdleDetector getIdleDetector() {
		return idleDetector;
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		idleDetector.setRunning(false);
		getWorkbench().removeWorkbenchListener(this);
		setEnableTrackers(trackerList, false);

		plugin = null;
		super.stop(context);
	}
}
