package rabbit.ui.internal.util;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * Test for {@link MillisConverter}
 */
public class MillisConverterTest {

	@Test
	public void testToSeconds() {
		long millis = 1000098712;
		double seconds = (double) millis / 1000;
		assertTrue(Double.compare(seconds, MillisConverter.toSeconds(millis)) == 0);
	}

	@Test
	public void testToMinutes() {
		long millis = 945736236;
		double minutes = (double) millis / 1000 / 60;
		assertTrue(Double.compare(minutes, MillisConverter.toMinutes(millis)) == 0);
	}

	@Test
	public void testToHours() {
		long millis = 127468458;
		double hours = (double) millis / 1000 / 60 / 60;
		assertTrue(Double.compare(hours, MillisConverter.toHours(millis)) == 0);
	}

}
