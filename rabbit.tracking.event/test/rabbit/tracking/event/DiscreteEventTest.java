package rabbit.tracking.event;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

public class DiscreteEventTest {

	private Calendar time = Calendar.getInstance();

	private DiscreteEvent event = createEvent(time);

	protected DiscreteEvent createEvent(Calendar time) {
		return new DiscreteEvent(time);
	}

	@Test
	public void testEvent() {
		assertNotNull(event);
	}

	@Test
	public void testGetTime() {
		assertEquals(time, event.getTime());
	}

	@Test
	public void testSetTime() {

		Calendar newTime = new GregorianCalendar(10, Calendar.JANUARY, 20);
		event.setTime(newTime);
		assertEquals(newTime, event.getTime());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetTimeNull() {
		event.setTime(null);
	}

}
