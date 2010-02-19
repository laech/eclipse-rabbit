package rabbit.ui.internal.util;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
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

	@Test
	public void testToDefaultString() {
		long millis = 1000;
		System.out.println(MillisConverter.toDefaultString(millis));
		assertEquals("1 s", MillisConverter.toDefaultString(millis));

		millis = 60000;
		System.out.println(MillisConverter.toDefaultString(millis));
		assertEquals("1 min 00 s", MillisConverter.toDefaultString(millis));

		millis = 3600000;
		System.out.println(MillisConverter.toDefaultString(millis));
		assertEquals("1 hr 00 min 00 s", MillisConverter.toDefaultString(millis));

		millis = 36061000;
		System.out.println(MillisConverter.toDefaultString(millis));
		assertEquals("10 hr 01 min 01 s", MillisConverter.toDefaultString(millis));
	}

}
