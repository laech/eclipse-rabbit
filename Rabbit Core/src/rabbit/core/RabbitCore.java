package rabbit.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchListener;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import rabbit.core.events.CommandEvent;
import rabbit.core.events.FileEvent;
import rabbit.core.events.PartEvent;
import rabbit.core.events.PerspectiveEvent;
import rabbit.core.internal.IdleDetector;
import rabbit.core.internal.TrackerObject;
import rabbit.core.internal.storage.xml.CommandEventStorer;
import rabbit.core.internal.storage.xml.FileEventStorer;
import rabbit.core.internal.storage.xml.PartEventStorer;
import rabbit.core.internal.storage.xml.PerspectiveEventStorer;
import rabbit.core.internal.storage.xml.XmlResourceManager;
import rabbit.core.storage.IResourceManager;
import rabbit.core.storage.IStorer;

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

	/**
	 * Identifier for getting/setting whether idleness detection should be
	 * enabled.
	 */
	public static final String IDLE_DETECTOR_ENABLE = "enableIdleDetector";

	/* Map<EventType, EventStorerType> */
	private static final Map<Class<?>, IStorer<?>> storers;

	/** The shared instance. */
	private static RabbitCore plugin;

	static {
		Map<Class<?>, IStorer<?>> map = new HashMap<Class<?>, IStorer<?>>();
		map.put(PerspectiveEvent.class, new PerspectiveEventStorer());
		map.put(CommandEvent.class, new CommandEventStorer());
		map.put(FileEvent.class, new FileEventStorer());
		map.put(PartEvent.class, new PartEventStorer());
		storers = Collections.unmodifiableMap(map);
	}

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RabbitCore getDefault() {
		return plugin;
	}

	/**
	 * Gets a storer that stores the objects of the given type.
	 * <p>
	 * The following object types are supported:
	 * <ul>
	 * <li>{@link CommandEvent}</li>
	 * <li>{@link FileEvent}</li>
	 * <li>{@link PartEvent}</li>
	 * <li>{@link PerspectiveEvent}</li>
	 * </ul>
	 * </p>
	 * 
	 * @param <T>
	 *            The type of the objects that the storer can store.
	 * @param objectClass
	 *            The class of the type.
	 * @return A storer that stores the objects of the given type, or null.
	 * @throws NullPointerException
	 *             If null is passed in.
	 */
	@SuppressWarnings("unchecked")
	public static <T> IStorer<T> getStorer(Class<T> objectClass) {
		if (null == objectClass) {
			throw new NullPointerException();
		}
		Object storer = storers.get(objectClass);
		return (null == storer) ? null : (IStorer<T>) storer;
	}

	/** List of trackers loaded. */
	private List<TrackerObject> trackerList;

	private IdleDetector idleDetector;

	/**
	 * The constructor.
	 */
	public RabbitCore() {
		idleDetector = new IdleDetector(getWorkbench().getDisplay(), 60000, 1000);
		trackerList = Collections.emptyList();
	}

	/**
	 * Creates trackers from the extension point.
	 * 
	 * @return A list of tracker objects.
	 */
	private List<TrackerObject> createTrackers(IConfigurationElement[] elements) {
		final List<TrackerObject> result = new ArrayList<TrackerObject>(elements.length);
		for (final IConfigurationElement e : elements) {

			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable e) {
					System.err.println(e.getMessage());
				}

				@Override
				public void run() throws Exception {
					Object o = e.createExecutableExtension("class");
					if (o instanceof ITracker<?>) {
						String id = e.getAttribute("id");
						String name = e.getAttribute("name");
						String description = e.getAttribute("description");
						ITracker<?> s = (ITracker<?>) o;
						result.add(new TrackerObject(id, name, description, s));
					} else {
						System.err.println("Object is not a tracker: " + e.getName());
					}
				}

			});
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
	 * All data will be saved and flushed from the trackers.
	 */
	public void saveCurrentData() {
		for (TrackerObject t : trackerList) {
			t.getTracker().saveData();
			t.getTracker().flushData();
		}
		XmlResourceManager.INSTANCE.write();
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

		getWorkbench().addWorkbenchListener(this);
		trackerList = createTrackers(Platform.getExtensionRegistry().getConfigurationElementsFor(TRACKER_EXTENSION_ID));
		setEnableTrackers(trackerList, true);

		// if (isIdleDetectionEnabled()) {
		idleDetector.setRunning(true);
		// } else {
		// idleDetector.setRunning(false);
		// }
	}

	/**
	 * Gets the global idleness detector in use. Clients may attach themselves
	 * as observers to the detector but must not change the detector's state
	 * (like calling {@link IdleDetector#setRunning(boolean)}).
	 * 
	 * @return The idleness detector.
	 */
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

	// /**
	// * Checks to see whether idleness detection is enabled.
	// *
	// * @return True if idleness detection is enabled, false otherwise.
	// */
	// public boolean isIdleDetectionEnabled() {
	// return getPreferenceStore().getBoolean(IDLE_DETECTOR_ENABLE);
	// }
	//
	// /**
	// * Sets whether idleness detection should be enabled. This change is saved
	// * permanently into the preference store.
	// *
	// * @param enable
	// * True to enable idleness detection, false to disable it. The
	// * change is applied immediately.
	// */
	// public void setIdleDetectionEnabled(boolean enable) {
	// getPreferenceStore().setValue(IDLE_DETECTOR_ENABLE, enable);
	// idleDetector.setRunning(enable);
	// }
}
