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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.core.events.CommandEvent;
import rabbit.core.events.FileEvent;
import rabbit.core.events.PartEvent;
import rabbit.core.events.PerspectiveEvent;
import rabbit.core.internal.TrackerObject;
import rabbit.core.internal.storage.xml.CommandEventStorer;
import rabbit.core.internal.storage.xml.FileEventStorer;
import rabbit.core.internal.storage.xml.PartEventStorer;
import rabbit.core.internal.storage.xml.PerspectiveEventStorer;

/**
 * Test for {@link RabbitCore}
 */
public class RabbitCoreTest {

	private static RabbitCore plugin = RabbitCore.getDefault();

	@BeforeClass
	public static void setUpBeforeClass() {
		TestUtil.setUpPathForTesting();
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		plugin.stop(plugin.getBundle().getBundleContext());
	}

	/**
	 * Gets the trackers from the private field.
	 * 
	 * @param target
	 *            The target to get the field from.
	 * @return The trackers.
	 */
	@SuppressWarnings("unchecked")
	private static Collection<TrackerObject> getTrackers(RabbitCore target) {
		try {
			Field f = RabbitCore.class.getDeclaredField("trackerList");
			f.setAccessible(true);
			return (Collection<TrackerObject>) f.get(target);

		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail();
			return null;
		}
	}

	/**
	 * Crates a tracker object for testing.
	 * 
	 * @return A tracker object for testing.
	 */
	private static TrackerObject newTrackerObject() {
		return newTrackerObject(TestUtil.<Object> newTracker());
	}

	/**
	 * Crates a tracker object for testing.
	 * 
	 * @param t
	 *            The tracker for creation.
	 * @return A tracker object for testing.
	 */
	private static TrackerObject newTrackerObject(ITracker<?> t) {
		return new TrackerObject("", "", "", t);
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateTrackers() throws Exception {
		Method createTrackers = RabbitCore.class.getDeclaredMethod("createTrackers",
				IConfigurationElement[].class);
		createTrackers.setAccessible(true);

		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(RabbitCore.TRACKER_EXTENSION_ID);
		Collection<TrackerObject> trackers = (Collection<TrackerObject>) createTrackers.invoke(
				plugin, (Object) elements);
		Assert.assertNotNull(trackers);
		// This may fail if one of the trackers (mainly our trackers) are failed
		// to load:
		Assert.assertEquals(elements.length, trackers.size());
		// if (elements.length != trackers.size()) {
		// System.err.println("WARNING: numElements=" + elements.length +
		// " numTrackers=" + trackers.size());
		// }
	}

	@Test
	public void testGetIdleDetector() {
		Assert.assertNotNull(plugin.getIdleDetector());
	}

	@Test
	public void testGetResourceManager() {
		Assert.assertNotNull(plugin.getResourceManager());
	}

	@Test
	public void testGetStoragePath() {
		Assert.assertNotNull(plugin.getStoragePath());
		Assert.assertTrue(plugin.getStoragePath().toFile().exists());
		Assert.assertTrue(plugin.getStoragePath().toFile().isDirectory());
	}

	@Test
	public void testGetStorer() {
		assertTrue(RabbitCore.getStorer(PerspectiveEvent.class) instanceof PerspectiveEventStorer);
		assertTrue(RabbitCore.getStorer(CommandEvent.class) instanceof CommandEventStorer);
		assertTrue(RabbitCore.getStorer(FileEvent.class) instanceof FileEventStorer);
		assertTrue(RabbitCore.getStorer(PartEvent.class) instanceof PartEventStorer);
		assertNull(RabbitCore.getStorer(String.class));
	}

	@Test(expected = NullPointerException.class)
	public void testGetStorer_withNull() {
		RabbitCore.getStorer(null);
	}

	@Test
	public void testIdleDetectorState() {
		Assert.assertTrue(plugin.getIdleDetector().getIdleInterval() == 60000);
		Assert.assertTrue(plugin.getIdleDetector().getRunDelay() == 1000);
	}

	@Test
	public void testPluginId() {
		Assert.assertEquals(RabbitCore.PLUGIN_ID, plugin.getBundle().getSymbolicName());
	}

	@Test
	public void testPreShutdown() {
		Assert.assertTrue(plugin.preShutdown(null, false));
		for (TrackerObject o : getTrackers(plugin)) {
			Assert.assertFalse(o.getTracker().isEnabled());
		}
	}

	@Test
	public void testSaveCurrentData() throws Exception {
		ITracker<Object> tracker = TestUtil.newTracker();
		tracker.getData().add(new Object());
		tracker.getData().add(new Object());
		Assert.assertFalse(tracker.getData().isEmpty());

		RabbitCore rc = new RabbitCore();
		rc.start(plugin.getBundle().getBundleContext());
		getTrackers(rc).clear();
		getTrackers(rc).add(newTrackerObject(tracker));
		rc.saveCurrentData();
		Assert.assertTrue(tracker.getData().isEmpty());
		rc.stop(rc.getBundle().getBundleContext());
	}

	@Test
	public void testSetEnableTrackers() throws Exception {
		Method m = RabbitCore.class.getDeclaredMethod("setEnableTrackers", Collection.class,
				boolean.class);
		m.setAccessible(true);

		Set<TrackerObject> trackers = new HashSet<TrackerObject>();
		trackers.add(newTrackerObject());
		trackers.add(newTrackerObject());
		trackers.add(newTrackerObject());

		// Test all trackers are disable.
		m.invoke(plugin, trackers, false);
		for (TrackerObject o : trackers) {
			Assert.assertFalse(o.getTracker().isEnabled());
		}

		// Test all trackers are enabled.
		m.invoke(plugin, trackers, true);
		for (TrackerObject o : trackers) {
			Assert.assertTrue(o.getTracker().isEnabled());
		}

		// Test all trackers are disable, again.
		m.invoke(plugin, trackers, false);
		for (TrackerObject o : trackers) {
			Assert.assertFalse(o.getTracker().isEnabled());
		}
	}

	@Test
	public void testStart() throws Exception {
		// It's already started by now.
		// Assert.assertTrue(plugin.isIdleDetectionEnabled() ==
		// plugin.getIdleDetector().isRunning());
		Assert.assertTrue(plugin.getIdleDetector().isRunning());
		for (TrackerObject t : getTrackers(plugin)) {
			Assert.assertTrue(t.getTracker().isEnabled());
		}
		// Errors or may have loaded the wrong extension point:
		Assert.assertFalse(getTrackers(plugin).isEmpty());
	}

	/**
	 * Place this test at end of all tests.
	 */
	@Test
	public void testStop() throws Exception {
		RabbitCore rc = new RabbitCore();
		rc.start(plugin.getBundle().getBundleContext());
		for (TrackerObject o : getTrackers(rc)) {
			Assert.assertTrue(o.getTracker().isEnabled());
		}

		rc.stop(rc.getBundle().getBundleContext());
		for (TrackerObject o : getTrackers(rc)) {
			Assert.assertFalse(o.getTracker().isEnabled());
		}
		Assert.assertFalse(rc.getIdleDetector().isRunning());
	}

	@Test
	public void testStorageLocationField() {
		Assert.assertNotNull(RabbitCore.STORAGE_LOCATION);
		Assert.assertTrue(RabbitCore.STORAGE_LOCATION.length() > 0);
	}

	@Test
	public void testStoragePaths() {
		Set<IPath> paths = new HashSet<IPath>();
		for (IPath path : plugin.getStoragePaths()) {
			paths.add(path);
		}
		boolean hasDefaultPath = false;
		for (IPath path : paths) {
			if (path.toString().equals(plugin.getStoragePath().toString())) {
				hasDefaultPath = true;
				break;
			}
		}
		if (!hasDefaultPath) {
			fail();
		}

		IPath root = Path.fromPortableString(plugin.getPreferenceStore().getString(
				RabbitCore.STORAGE_LOCATION));
		File rootFile = new File(root.toOSString());
		File[] files = rootFile.listFiles();
		paths = new HashSet<IPath>(files.length);
		for (File file : files) {
			if (file.isDirectory()) {
				paths.add(Path.fromOSString(file.getAbsolutePath()));
			}

			System.out.println(file.getName());
		}
		assertEquals(paths.size(), plugin.getStoragePaths().length);
		for (IPath path : plugin.getStoragePaths()) {
			assertTrue(paths.contains(path));
		}
	}

	@Test
	public void testTrackerExtensionPointId() {
		Assert.assertTrue(Platform.getExtensionRegistry().getConfigurationElementsFor(
				RabbitCore.TRACKER_EXTENSION_ID).length > 0);
	}
}
