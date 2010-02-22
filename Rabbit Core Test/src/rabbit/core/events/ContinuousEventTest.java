package rabbit.core.events;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

/**
 * Test for {@link ContinuousEvent}
 */
public class ContinuousEventTest extends DiscreteEventTest {

	private static long duration = 348723;

	private ContinuousEvent event;

	public ContinuousEventTest() {
		event = createEvent(Calendar.getInstance());
	}

	@Override
	protected ContinuousEvent createEvent(Calendar time) {
		return createEvent(time, duration);
	}

	protected ContinuousEvent createEvent(Calendar time, long duration) {
		return new ContinuousEvent(time, duration);
	}

	@Test
	public void testGetDuration() {
		assertEquals(duration, event.getDuration());
	}

	@Test
	public void testSetDuration() {

		long newDura = 349850;
		event.setDuration(newDura);
		assertEquals(newDura, event.getDuration());

		newDura = 0;
		event.setDuration(newDura);
		assertEquals(newDura, event.getDuration());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetDuration_withNegativeDuration() {
		event.setDuration(-1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructor_withNegativeDuration() {
		new ContinuousEvent(Calendar.getInstance(), -1);
	}
}
