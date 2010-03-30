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
package rabbit.core.internal;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
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

import rabbit.core.ITracker;
import rabbit.core.internal.storage.xml.XmlFileMapper;
import rabbit.core.internal.util.IdleDetector;

/**
 * The activator class controls the plug-in life cycle
 */
public class RabbitCorePlugin extends AbstractUIPlugin implements IWorkbenchListener {

	/** The plug-in ID. **/
	public static final String PLUGIN_ID = "rabbit.core";

	/** ID of the tracker extension point. */
	public static final String TRACKER_EXTENSION_ID = "rabbit.core.trackers";

	/** The shared instance. */
	private static RabbitCorePlugin plugin;

	/**
	 * Returns the shared instance
	 * 
	 * @return the shared instance
	 */
	public static RabbitCorePlugin getDefault() {
		return plugin;
	}

	/** An set of trackers. */
	private Set<ITracker<?>> trackers;

	private IdleDetector idleDetector;

	/**
	 * The constructor.
	 */
	public RabbitCorePlugin() {
		long oneSecond = TimeUnit.SECONDS.toMillis(1);
		long oneMinuite = TimeUnit.MINUTES.toMillis(1);
		idleDetector = new IdleDetector(getWorkbench().getDisplay(), oneMinuite, oneSecond);
		trackers = new HashSet<ITracker<?>>();
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
	 * Gets the full path to the storage location of this workspace. The
	 * returned path should not be cached because it is changeable.
	 * 
	 * @return The full path to the storage location folder.
	 */
	public IPath getStoragePath() {
		String workspace = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
		workspace = workspace.replace(File.separatorChar, '.');
		workspace = workspace.replace(":", "");
		return getStoragePathRoot().append(workspace);
	}

	/**
	 * Gets the root of the storage location. The returned path should not be
	 * cached because it's changeable.
	 * 
	 * @return The path to the root of the storage location.
	 */
	public IPath getStoragePathRoot() {
		FileInputStream stream = null;
		try {
			stream = new FileInputStream(getPropertiesFile());
			Properties prop = new Properties();
			prop.load(stream);
			return Path.fromOSString(prop.getProperty(PROP_STORAGE_ROOT, DEFAULT_STORAGE_ROOT
					.toOSString()));

		} catch (FileNotFoundException e) {
			return resetStoragePathRoot();

		} catch (IOException e) {
			return resetStoragePathRoot();

		} catch (IllegalArgumentException e) {
			return resetStoragePathRoot();

		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
	}

	/**
	 * The default location of the storage root.
	 */
	private static final IPath DEFAULT_STORAGE_ROOT = Path.fromOSString(
			System.getProperty("user.home")).append("Rabbit");

	/**
	 * Constant string to use with a java.util.Properties to get/set the storage
	 * root.
	 */
	private static final String PROP_STORAGE_ROOT = "storage.root";

	/**
	 * Resets the storage root.
	 * 
	 * @return The default storage root.
	 */
	private IPath resetStoragePathRoot() {
		setStoragePathRoot(DEFAULT_STORAGE_ROOT.toFile());
		return DEFAULT_STORAGE_ROOT;
	}

	/**
	 * Sets the storage root.
	 * 
	 * @param directory
	 *            The new storage root.
	 * @return true if the setting is applied; false if any of the followings is
	 *         true:
	 *         <ul>
	 *         <li>The directory does not exist.</li>
	 *         <li>The directory cannot be read from.</li>
	 *         <li>The directory cannot be written to.</li>
	 *         <li>If error occurs while saving the setting.</li>
	 *         </ul>
	 * @throws NullPointerException
	 *             If parameter is null.
	 */
	public boolean setStoragePathRoot(File directory) {
		if (directory == null) {
			throw new IllegalArgumentException();
		}

		if (!directory.exists() || !directory.canRead() || !directory.canWrite()) {
			return false;
		}

		FileOutputStream stream = null;
		try {
			stream = new FileOutputStream(getPropertiesFile());
			Properties prop = new Properties();
			prop.setProperty(PROP_STORAGE_ROOT, directory.getAbsolutePath());
			prop.store(stream, "This file contains configurations for the Rabbit Eclipse plugin." +
					"\nPlease do not delete.");
			return true;

		} catch (FileNotFoundException e) {
			return false;

		} catch (IOException e) {
			return false;

		} finally {
			if (stream != null) {
				try {
					stream.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Gets the properties file for saving the storage root property.
	 */
	private File getPropertiesFile() {
		String str = System.getProperty("user.home") + File.separator + ".rabbit.properties";
		return new File(str);
	}

	/**
	 * Gets the paths to all the workspace storage locations for this plug-in.
	 * Includes {@link #getStoragePath()}. The returned paths should not be
	 * cached because they are changeable.
	 * 
	 * @return The paths to all the workspace storage locations
	 */
	public IPath[] getStoragePaths() {
		IPath root = getStoragePathRoot();
		File rootFile = root.toFile();
		File[] files = rootFile.listFiles();
		if (files == null) {
			return new IPath[0];
		}

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
		for (ITracker<?> tracker : trackers) {
			tracker.setEnabled(false);
		}
		return true;
	}

	/**
	 * Call this method to saves all current data collected by the trackers now.
	 * All data will be saved and flushed from the trackers.
	 */
	public void saveCurrentData() {
		for (ITracker<?> tracker : trackers) {
			tracker.setEnabled(false);
			tracker.flushData();
			tracker.setEnabled(true);
		}
		XmlFileMapper.INSTANCE.write(true);
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		getWorkbench().addWorkbenchListener(this);
		trackers = new HashSet<ITracker<?>>(createTrackers());
		setEnableTrackers(trackers, true);

		idleDetector.setRunning(true);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		idleDetector.setRunning(false);
		getWorkbench().removeWorkbenchListener(this);
		setEnableTrackers(trackers, false);

		plugin = null;
		super.stop(context);
	}

	/**
	 * Creates trackers from the extension point.
	 * 
	 * @return A list of tracker objects.
	 */
	private Set<ITracker<?>> createTrackers() {

		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(TRACKER_EXTENSION_ID);

		final Set<ITracker<?>> result = new HashSet<ITracker<?>>();
		for (final IConfigurationElement e : elements) {

			SafeRunner.run(new ISafeRunnable() {
				@Override
				public void handleException(Throwable e) {
					getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
				}

				@Override
				public void run() throws Exception {
					Object o = e.createExecutableExtension("class");
					if (o instanceof ITracker<?>) {
						result.add((ITracker<?>) o);
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
	private void setEnableTrackers(Collection<ITracker<?>> trackers, boolean enable) {
		for (ITracker<?> tracker : trackers) {
			tracker.setEnabled(enable);
		}
	}
}
