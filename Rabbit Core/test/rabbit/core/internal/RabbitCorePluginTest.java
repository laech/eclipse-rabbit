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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.junit.Test;

import rabbit.core.ITracker;
import rabbit.core.TestUtil;

/**
 * Test for {@link RabbitCorePlugin}
 */
public class RabbitCorePluginTest {

	private static RabbitCorePlugin plugin = RabbitCorePlugin.getDefault();

	/**
	 * Gets the trackers from the private field.
	 * 
	 * @param target
	 *            The target to get the field from.
	 * @return The trackers.
	 */
	@SuppressWarnings("unchecked")
	private static Collection<ITracker<?>> getTrackers(RabbitCorePlugin target) throws Exception {
		Field f = RabbitCorePlugin.class.getDeclaredField("trackers");
		f.setAccessible(true);
		return (Collection<ITracker<?>>) f.get(target);
	}

	@Test
	public void testGetStoragePathRoot() {
		assertNotNull(plugin.getStoragePathRoot());
	}

	@Test
	public void testSetStoragePathRoot() throws IOException {
		IPath oldPath = plugin.getStoragePathRoot();
		IPath path = oldPath.append(System.currentTimeMillis() + "");

		File file = path.toFile();
		
		// File not exist, should return false:
		assertFalse(file.exists());
		assertFalse(plugin.setStoragePathRoot(file));
		
		// File exists, readable, writable, should return true:
		assertTrue(file.mkdirs());
		assertTrue(file.setReadable(true));
		assertTrue(file.setWritable(true));
		assertTrue(plugin.setStoragePathRoot(file));
		
		plugin.setStoragePathRoot(oldPath.toFile());
	}

	@Test
	@SuppressWarnings("unchecked")
	public void testCreateTrackers() throws Exception {
		Method createTrackers = RabbitCorePlugin.class.getDeclaredMethod("createTrackers");
		createTrackers.setAccessible(true);

		Collection<ITracker<?>> trackers = (Collection<ITracker<?>>) createTrackers.invoke(plugin);
		assertNotNull(trackers);
		// This may fail if one or more of the trackers (mainly our trackers)
		// are failed
		// to load:
		IConfigurationElement[] elements = Platform.getExtensionRegistry()
				.getConfigurationElementsFor(RabbitCorePlugin.TRACKER_EXTENSION_ID);
		assertEquals(elements.length, trackers.size());
		// if (elements.length != trackers.size()) {
		// System.err.println("WARNING: numElements=" + elements.length +
		// " numTrackers=" + trackers.size());
		// }
	}

	@Test
	public void testGetIdleDetector() {
		assertNotNull(plugin.getIdleDetector());
	}

	@Test
	public void testGetStoragePath() {
		assertNotNull(plugin.getStoragePath());
		assertTrue(plugin.getStoragePath().toFile().exists());
		assertTrue(plugin.getStoragePath().toFile().isDirectory());
	}

	@Test
	public void testIdleDetectorState() {
		assertTrue(plugin.getIdleDetector().getIdleInterval() == 60000);
		assertTrue(plugin.getIdleDetector().getRunDelay() == 1000);
	}

	@Test
	public void testPluginId() {
		assertEquals(RabbitCorePlugin.PLUGIN_ID, plugin.getBundle().getSymbolicName());
	}

	@Test
	public void testPreShutdown() throws Exception {
		assertTrue(plugin.preShutdown(null, false));
		for (ITracker<?> tracker : getTrackers(plugin)) {
			assertFalse(tracker.isEnabled());
		}
	}

	@Test
	public void testSaveCurrentData() throws Exception {
		ITracker<Object> tracker = TestUtil.newTracker();
		tracker.getData().add(new Object());
		tracker.getData().add(new Object());
		assertFalse(tracker.getData().isEmpty());

		RabbitCorePlugin rc = new RabbitCorePlugin();
		rc.start(plugin.getBundle().getBundleContext());
		getTrackers(rc).clear();
		getTrackers(rc).add(tracker);
		rc.saveCurrentData();
		assertTrue(tracker.getData().isEmpty());
		rc.stop(rc.getBundle().getBundleContext());
	}

	@Test
	public void testSetEnableTrackers() throws Exception {
		Method m = RabbitCorePlugin.class.getDeclaredMethod("setEnableTrackers", Collection.class,
				boolean.class);
		m.setAccessible(true);

		Set<ITracker<?>> trackers = new HashSet<ITracker<?>>();
		trackers.add(TestUtil.newTracker());
		trackers.add(TestUtil.newTracker());
		trackers.add(TestUtil.newTracker());

		// Test all trackers are disable.
		m.invoke(plugin, trackers, false);
		for (ITracker<?> tracker : trackers) {
			assertFalse(tracker.isEnabled());
		}

		// Test all trackers are enabled.
		m.invoke(plugin, trackers, true);
		for (ITracker<?> tracker : trackers) {
			assertTrue(tracker.isEnabled());
		}

		// Test all trackers are disable, again.
		m.invoke(plugin, trackers, false);
		for (ITracker<?> tracker : trackers) {
			assertFalse(tracker.isEnabled());
		}
	}

	@Test
	public void testStart() throws Exception {
		RabbitCorePlugin rc = new RabbitCorePlugin();
		rc.start(plugin.getBundle().getBundleContext());
		// It's already started by now.
		assertTrue(rc.getIdleDetector().isRunning());
		// Errors or may have loaded the wrong extension point:
		assertFalse(getTrackers(rc).isEmpty());

		for (ITracker<?> t : getTrackers(rc)) {
			assertTrue(t.toString(), t.isEnabled());
		}
		rc.stop(rc.getBundle().getBundleContext());
	}

	/**
	 * Place this test at end of all tests.
	 */
	@Test
	public void testStop() throws Exception {
		RabbitCorePlugin rc = new RabbitCorePlugin();
		rc.start(plugin.getBundle().getBundleContext());
		for (ITracker<?> o : getTrackers(rc)) {
			assertTrue(o.isEnabled());
		}

		rc.stop(rc.getBundle().getBundleContext());
		for (ITracker<?> o : getTrackers(rc)) {
			assertFalse(o.isEnabled());
		}
		assertFalse(rc.getIdleDetector().isRunning());
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

		IPath root = plugin.getStoragePathRoot();
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
		assertTrue(Platform.getExtensionRegistry().getConfigurationElementsFor(
				RabbitCorePlugin.TRACKER_EXTENSION_ID).length > 0);
	}
}
