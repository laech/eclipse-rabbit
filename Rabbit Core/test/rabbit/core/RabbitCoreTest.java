package rabbit.core;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

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

	@Test
	public void testTrackerExtensionPointId() {
		Assert.assertTrue(Platform.getExtensionRegistry().getConfigurationElementsFor(RabbitCore.TRACKER_EXTENSION_ID).length > 0);
	}

	@Test
	public void testIdleDetectionField() {
		Assert.assertNotNull(RabbitCore.IDLE_DETECTOR_ENABLE);
		Assert.assertTrue(RabbitCore.IDLE_DETECTOR_ENABLE.length() > 0);
	}

	@Test
	public void testPluginId() {
		Assert.assertEquals(RabbitCore.PLUGIN_ID, plugin.getBundle().getSymbolicName());
	}

	@Test
	public void testStorageLocationField() {
		Assert.assertNotNull(RabbitCore.STORAGE_LOCATION);
		Assert.assertTrue(RabbitCore.STORAGE_LOCATION.length() > 0);
	}

	@Test
	public void testStart() throws Exception {
		// It's already started by now.
		Assert.assertTrue(plugin.isIdleDetectionEnabled() == plugin.getIdleDetector().isRunning());
		for (TrackerObject t : getTrackers(plugin)) {
			Assert.assertTrue(t.getTracker().isEnabled());
		}
		// Errors or may have loaded the wrong extension point:
		Assert.assertFalse(getTrackers(plugin).isEmpty());
	}

	@Test
	public void testPreShutdown() {
		Assert.assertTrue(plugin.preShutdown(null, false));
		for (TrackerObject o : getTrackers(plugin)) {
			Assert.assertFalse(o.getTracker().isEnabled());
		}
	}

	@Test
	public void testSetEnableTrackers() throws Exception {
		Method m = RabbitCore.class.getDeclaredMethod("setEnableTrackers", Collection.class, boolean.class);
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
	public void testIsIdleDetectionEnabled() {
		Assert.assertTrue(plugin.getIdleDetector().isRunning() == plugin.isIdleDetectionEnabled());
	}

	@Test
	public void testGetIdleDetector() {
		Assert.assertNotNull(plugin.getIdleDetector());
	}

	@Test
	public void testIdleDetectorState() {
		Assert.assertTrue(plugin.getIdleDetector().getIdleInterval() == 10000);
		Assert.assertTrue(plugin.getIdleDetector().getRunDelay() == 1000);
	}

	@Test
	public void testSetIdleDetectionEnabled() throws Exception {
		IPreferenceStore store = plugin.getPreferenceStore();
		IdleDetector detector = plugin.getIdleDetector();
		detector.setRunning(true);

		plugin.setIdleDetectionEnabled(false);
		Assert.assertFalse(store.getBoolean(RabbitCore.IDLE_DETECTOR_ENABLE));
		Assert.assertFalse(plugin.isIdleDetectionEnabled());
		Assert.assertFalse(detector.isRunning());

		plugin.setIdleDetectionEnabled(true);
		Assert.assertTrue(store.getBoolean(RabbitCore.IDLE_DETECTOR_ENABLE));
		Assert.assertTrue(plugin.isIdleDetectionEnabled());
		Assert.assertTrue(detector.isRunning());

		// Test this preference is permanently saved:

		RabbitCore rc = new RabbitCore();
		rc.start(plugin.getBundle().getBundleContext());
		Assert.assertTrue(rc.isIdleDetectionEnabled());
		rc.stop(rc.getBundle().getBundleContext());

		rc.setIdleDetectionEnabled(false);
		rc = new RabbitCore();
		rc.start(plugin.getBundle().getBundleContext());
		Assert.assertFalse(rc.isIdleDetectionEnabled());
		rc.stop(rc.getBundle().getBundleContext());
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
	@SuppressWarnings("unchecked")
	public void testCreateTrackers() throws Exception {
		Method createTrackers = RabbitCore.class.getDeclaredMethod("createTrackers", IConfigurationElement[].class);
		createTrackers.setAccessible(true);

		IConfigurationElement[] elements = Platform.getExtensionRegistry().getConfigurationElementsFor(RabbitCore.TRACKER_EXTENSION_ID);
		Collection<TrackerObject> trackers = (Collection<TrackerObject>) createTrackers.invoke(plugin, (Object) elements);
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
}
