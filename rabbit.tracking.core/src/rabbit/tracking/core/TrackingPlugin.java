package rabbit.tracking.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rabbit.tracking.core.internal.TrackerObject;

/**
 * The activator class controls the plug-in life cycle
 */
public class TrackingPlugin extends AbstractUIPlugin implements IWorkbenchListener {

	/** The plug-in ID. **/
	public static final String PLUGIN_ID = "rabbit.tracking.core";

	/** ID of the tracker extension point. */
	public static final String TRACKER_EXTENSION_ID = "rabbit.tracking.tracker";

	/** The shared instance. */
	private static TrackingPlugin plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static TrackingPlugin getDefault() {
		return plugin;
	}

	/** List of trackers loaded. */
	private List<TrackerObject> trackerList;

	/**
	 * The constructor.
	 */
	public TrackingPlugin() {
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

	@Override
	public void postShutdown(IWorkbench workbench) {
		// Ignore.
	}

	@Override
	public boolean preShutdown(IWorkbench workbench, boolean forced) {
		for (TrackerObject t : trackerList) {
			t.getTracker().setEnabled(false);
		}
		return true;
	}

	/**
	 * Enables or disables the trackers.
	 * @param trackers The trackers to perform actions on.
	 * @param enable True to enable, false to disable.
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
		PlatformUI.getWorkbench().addWorkbenchListener(this);
		
		if (trackerList != null) { // May be we didn't stop correctly?
			setEnableTrackers(trackerList, false);
			trackerList.clear();
		}

		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(TRACKER_EXTENSION_ID);
		trackerList = createSensorsFromExtension(elements);
		
		setEnableTrackers(trackerList, true);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		PlatformUI.getWorkbench().removeWorkbenchListener(this);
		setEnableTrackers(trackerList, false);
		plugin = null;
		super.stop(context);
	}
}
