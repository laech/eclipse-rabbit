package rabbit.tracking.core;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Collection;

import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import rabbit.tracking.core.internal.TrackerObject;

public class TrackingPluginTest {
	
	private static TrackingPlugin plugin = TrackingPlugin.getDefault();
	private static Collection<TrackerObject> trackerList = getTrackers();
	
	@Before
	public void setUp() {
		trackerList.clear();
		
		TrackerObject trackerObject = newTrackerObject();
		trackerList.add(trackerObject);
		for (TrackerObject o : trackerList) {
			o.getTracker().setEnabled(true); // Enable trackers before testing.
			Assert.assertTrue(o.getTracker().isEnabled());
		}
	}
	
	// This is also a test, but has to be ran as the last, because it will
	// cause the variable "plugin" to be null.
	@AfterClass
	public static void testStop() {
		try {
			plugin.stop(null);
			
		} catch (Exception e) {
			// Ignore, because we passed in null, but the trackers should still
			// be disabled.
		}
		
		for (TrackerObject o : trackerList) {
			Assert.assertFalse(o.getTracker().isEnabled());
		}
	}

	@Test
	public void testPreShutdown() {

		// Make sure all trackers are disabled before workbench shutdown:
		plugin.preShutdown(null, false);
		for (TrackerObject o : trackerList) {
			Assert.assertFalse(o.getTracker().isEnabled());
		}
	}

	@Test
	public void testSetEnableTrackers() {
		
		try {
			Method m = TrackingPlugin.class.getDeclaredMethod(
					"setEnableTrackers", Collection.class, boolean.class);
			m.setAccessible(true);

			// Test all trackers are disable.
			m.invoke(plugin, trackerList, false);
			for (TrackerObject o : trackerList) {
				Assert.assertFalse(o.getTracker().isEnabled());
			}
			
			// Test all trackers are enabled.
			m.invoke(plugin, trackerList, true);
			for (TrackerObject o : trackerList) {
				Assert.assertTrue(o.getTracker().isEnabled());
			}
			
			// Test all trackers are disable, again.
			m.invoke(plugin, trackerList, false);
			for (TrackerObject o : trackerList) {
				Assert.assertFalse(o.getTracker().isEnabled());
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Exception occurred, test needs rewritting.");
		}
	}
	
	/**
	 * Gets the trackers from the private field.
	 * @return The trackers.
	 */
	@SuppressWarnings("unchecked")
	private static Collection<TrackerObject> getTrackers() {
		
		try {
			Field f = TrackingPlugin.class.getDeclaredField("trackerList");
			f.setAccessible(true);
			return (Collection<TrackerObject>) f.get(plugin);
			
		} catch (SecurityException e) {
			Assert.fail();
		} catch (NoSuchFieldException e) {
			Assert.fail("Filed not found, test needs rewritten.");
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail("Field properties might have been changed, test needs rewritten.");
		}
		Assert.fail();
		return null;
	}
	
	/**
	 * Creates a new tracker object for testing.
	 * @return A new tracker object.
	 */
	private TrackerObject newTrackerObject() {
		return new TrackerObject("", "", "", TestUtil.newTracker());
	}
}
