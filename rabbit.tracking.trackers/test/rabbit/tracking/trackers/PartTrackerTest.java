package rabbit.tracking.trackers;

import static org.junit.Assert.*;

import org.junit.Test;

public class PartTrackerTest extends TrackerTest {

	@Test
	public void testDoEnable() {
		fail("Not yet implemented");
	}

	@Test
	public void testDoDisable() {
		fail("Not yet implemented");
	}

	@Override
	protected Tracker createTracker() {
		return new PartTracker();
	}

}
