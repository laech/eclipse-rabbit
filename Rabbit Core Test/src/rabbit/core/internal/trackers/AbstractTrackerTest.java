package rabbit.core.internal.trackers;

import org.junit.*;

import rabbit.core.internal.trackers.AbstractTracker;
import static org.junit.Assert.*;

/**
 * Test for {@link AbstractTracker}
 */
public abstract class AbstractTrackerTest<T> {

	private AbstractTracker<T> tracker = createTracker();

	/** Creates a tracker for testing. */
	protected abstract AbstractTracker<T> createTracker();

	/** Creates an event for testing. */
	protected abstract T createEvent();

	@Test
	public void testTracker() {
		assertNotNull(tracker);
	}

	@Test
	public void testIsEnabled() {
		assertFalse(tracker.isEnabled());
	}

	@Test
	public void testSetEnabled() {
		if (tracker.isEnabled())
			tracker.setEnabled(false);

		tracker.setEnabled(true);
		assertTrue(tracker.isEnabled());

		tracker.setEnabled(false);
		assertFalse(tracker.isEnabled());

		tracker.setEnabled(true);
		assertTrue(tracker.isEnabled());
	}

	@Test
	public void testGetData() {
		assertNotNull(tracker.getData());
		assertTrue(tracker.getData().isEmpty());
	}

	@Test
	public void testAddData() {
		tracker.addData(createEvent());
		assertEquals(1, tracker.getData().size());
	}

	@Test
	public void testCreateDataStorer() {
		assertNotNull(tracker.createDataStorer());
	}

	@Test
	public void testFlushData() {
		tracker.addData(createEvent());
		assertFalse(tracker.getData().isEmpty());
		tracker.flushData();
		assertTrue(tracker.getData().isEmpty());
	}

	@Test
	public void testSaveData() {
		tracker.addData(createEvent());
		assertFalse(tracker.getData().isEmpty());
		tracker.saveData();
		assertFalse(tracker.getData().isEmpty()); // Only empty when re-enabled.
		tracker.setEnabled(false);
		tracker.setEnabled(true);
		assertTrue(tracker.getData().isEmpty());
	}
}
