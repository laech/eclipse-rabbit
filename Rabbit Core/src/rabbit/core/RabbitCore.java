/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
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
import rabbit.core.internal.storage.xml.CommandDataAccessor;
import rabbit.core.internal.storage.xml.CommandEventStorer;
import rabbit.core.internal.storage.xml.FileDataAccessor;
import rabbit.core.internal.storage.xml.FileEventStorer;
import rabbit.core.internal.storage.xml.PartDataAccessor;
import rabbit.core.internal.storage.xml.PartEventStorer;
import rabbit.core.internal.storage.xml.PerspectiveDataAccessor;
import rabbit.core.internal.storage.xml.PerspectiveEventStorer;
import rabbit.core.internal.storage.xml.SessionDataAccessor;
import rabbit.core.internal.storage.xml.XmlResourceManager;
import rabbit.core.storage.IAccessor;
import rabbit.core.storage.IResourceMapper;
import rabbit.core.storage.IStorer;

/**
 * The activator class controls the plug-in life cycle
 */
public class RabbitCore extends AbstractUIPlugin implements IWorkbenchListener {

	public static enum AccessorType {
		PERSPECTIVE, PART, SESSION, COMMAND, FILE,
	}

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

	/** Map<T, IStorer<T> */
	private static final Map<Class<?>, IStorer<?>> storers;

	private static final Map<AccessorType, IAccessor<Map<String, Long>>> accessors;

	/** The shared instance. */
	private static RabbitCore plugin;

	static {
		Map<Class<?>, IStorer<?>> map = new HashMap<Class<?>, IStorer<?>>();
		map.put(PerspectiveEvent.class, PerspectiveEventStorer.getInstance());
		map.put(CommandEvent.class, CommandEventStorer.getInstance());
		map.put(FileEvent.class, FileEventStorer.getInstance());
		map.put(PartEvent.class, PartEventStorer.getInstance());
		storers = Collections.unmodifiableMap(map);

		Map<AccessorType, IAccessor<Map<String, Long>>> accessorMap =
				new HashMap<AccessorType, IAccessor<Map<String, Long>>>();
		accessorMap.put(AccessorType.PERSPECTIVE, new PerspectiveDataAccessor());
		accessorMap.put(AccessorType.COMMAND, new CommandDataAccessor());
		accessorMap.put(AccessorType.FILE, new FileDataAccessor());
		accessorMap.put(AccessorType.PART, new PartDataAccessor());
		accessorMap.put(AccessorType.SESSION, new SessionDataAccessor());
		accessors = Collections.unmodifiableMap(accessorMap);
	}

	/**
	 * Gets an accessor that gets the data from the database.
	 * 
	 * @param type
	 *            The type of accessor.
	 * @return An accessor that can get the data.
	 * @throws NullPointerException
	 *             If null is passed in.
	 */
	public static IAccessor<Map<String, Long>> getAccessor(AccessorType type) {
		if (null == type) {
			throw new NullPointerException();
		}
		IAccessor<Map<String, Long>> accessor = accessors.get(type);
		return (accessor == null) ? null : accessor;
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
		long oneSecond = TimeUnit.SECONDS.toMillis(1);
		long oneMinuite = TimeUnit.MINUTES.toMillis(1);
		idleDetector = new IdleDetector(getWorkbench().getDisplay(), oneMinuite, oneSecond);
		trackerList = Collections.emptyList();
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

	/**
	 * Gets the resource manager.
	 * 
	 * @return The resource manager.
	 */
	public IResourceMapper getResourceManager() {
		return XmlResourceManager.INSTANCE;
	}

	/**
	 * Gets the full path to the storage location of this workspace.
	 * 
	 * @return The full path to the storage location folder.
	 */
	public IPath getStoragePath() {
		String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		workspace = workspace.replace(File.separatorChar, '.');
		workspace = workspace.replace(":", "");
		return new Path(getPreferenceStore().getString(STORAGE_LOCATION)).append(workspace);
	}

	/**
	 * Gets the paths to all the workspace storage locations for this plug-in.
	 * Includes {@link #getStoragePath()}.
	 * 
	 * @return The paths to all the workspace storage locations
	 */
	public IPath[] getStoragePaths() {
		IPath root = new Path(getPreferenceStore().getString(STORAGE_LOCATION));
		File rootFile = new File(root.toOSString());
		File[] files = rootFile.listFiles();
		List<IPath> paths = new ArrayList<IPath>(files.length);
		for (File file : files) {
			if (file.isDirectory()) {
				paths.add(Path.fromOSString(file.getAbsolutePath()));
			}
		}

		return paths.toArray(new IPath[paths.size()]);
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
			t.getTracker().setEnabled(false);
			t.getTracker().flushData();
			t.getTracker().setEnabled(true);
		}
		if (!XmlResourceManager.INSTANCE.write(true)) {
			getLog().log(new Status(IStatus.ERROR, PLUGIN_ID,
					"Unable to save resource mappings."));
		}
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		getWorkbench().addWorkbenchListener(this);
		trackerList = createTrackers(Platform.getExtensionRegistry()
				.getConfigurationElementsFor(TRACKER_EXTENSION_ID));
		setEnableTrackers(trackerList, true);

		idleDetector.setRunning(true);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		idleDetector.setRunning(false);
		getWorkbench().removeWorkbenchListener(this);
		setEnableTrackers(trackerList, false);

		plugin = null;
		super.stop(context);
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
						System.err.println("Object is not a tracker: " + o);
					}
				}

			});
		}
		return result;
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
}
