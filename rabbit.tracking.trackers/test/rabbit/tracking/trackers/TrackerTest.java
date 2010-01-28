package rabbit.tracking.trackers;

import org.junit.*;
import static org.junit.Assert.*;

public abstract class TrackerTest {
	
	private Tracker tracker = createTracker();
	
	protected abstract Tracker createTracker();

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
		
		tracker.setEnabled(true);
		assertTrue(tracker.isEnabled());
		
		tracker.setEnabled(false);
		assertFalse(tracker.isEnabled());
		
		tracker.setEnabled(true);
		assertTrue(tracker.isEnabled());
	}

}
