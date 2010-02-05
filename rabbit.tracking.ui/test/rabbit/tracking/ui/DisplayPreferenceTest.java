package rabbit.tracking.ui;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Observable;
import java.util.Observer;

import org.junit.Before;
import org.junit.Test;

/**
 * Test for {@link DisplayPreference}
 */
public class DisplayPreferenceTest {

	/** Helper class for testing observer. */
	private class DisplayPreferenceObserver implements Observer {

		private boolean isUpdated = false;

		@Override public void update(Observable o, Object arg) {
			isUpdated = true;
		}
	}

	private DisplayPreference preference; // Test subject.
	private DisplayPreferenceObserver observer; // For testing observer feature.

	@Before public void setUp() {
		preference = new DisplayPreference();
		observer = new DisplayPreferenceObserver();
	}

	/** Test the start is before or equal to end time. */
	@Test public void testContructor() {
		assertTrue(preference.getStartDate().compareTo(preference.getEndDate()) <= 0);
	}

	/** Test the get method returns the value set before. */
	@Test public void testGetEndDate() {
		Calendar calendar = new GregorianCalendar(1123, 1, 17);
		preference.setEndDate(calendar);
		assertEquals(calendar, preference.getEndDate());
		calendar = Calendar.getInstance();
		preference.setEndDate(calendar);
		assertEquals(calendar, preference.getEndDate());
	}

	/** Test the get method returns a copy, not the real thing. */
	@Test public void testGetEndDate_returnsClone() {
		Calendar cal = preference.getEndDate();
		assertNotNull(cal);
		assertNotSame(cal, preference.getEndDate());
	}

	/** Test the get method returns the value set before. */
	@Test public void testGetStartDate() {
		Calendar calendar = new GregorianCalendar(23, 9, 7);
		preference.setStartDate(calendar);
		assertEquals(calendar, preference.getStartDate());
		calendar = Calendar.getInstance();
		preference.setStartDate(calendar);
		assertEquals(calendar, preference.getStartDate());
	}

	/** Test the get method returns a copy, not the real thing. */
	@Test public void testGetStartDate_returnsClone() {
		Calendar cal = preference.getStartDate();
		assertNotNull(cal);
		assertNotSame(cal, preference.getStartDate());
	}

	/** Test the observer is notified when setting to a different date. */
	@Test public void testSetEndDate_withDifferentDate() {
		preference.addObserver(observer);
		assertFalse(observer.isUpdated);
		Calendar calendar = new GregorianCalendar(10, 9, 0);
		preference.setEndDate(calendar);
		assertTrue(observer.isUpdated);
		assertTrue(calendar.equals(preference.getEndDate()));
	}

	/** Test a {@link NullPointerException} is thrown when setting to null. */
	@Test(expected = NullPointerException.class) public void testSetEndDate_withNull() {
		preference.setEndDate(null);
	}

	/** Test the observer is not notified when setting to a same date. */
	@Test public void testSetEndDate_withSameDate() {
		// Test the observer is not notified when setting to a same date.
		preference.addObserver(observer);
		assertFalse(observer.isUpdated);
		Calendar calendar = preference.getEndDate();
		preference.setEndDate(calendar);
		assertFalse(observer.isUpdated);
	}

	/** Test the observer is notified when setting to a different date. */
	@Test public void testSetStartDate_withDifferentDate() {
		preference.addObserver(observer);
		assertFalse(observer.isUpdated);
		Calendar calendar = new GregorianCalendar(0, 0, 0);
		preference.setStartDate(calendar);
		assertTrue(observer.isUpdated);
		assertTrue(calendar.equals(preference.getStartDate()));
	}

	/** Test a {@link NullPointerException} is thrown when setting to null. */
	@Test(expected = NullPointerException.class) public void testSetStartDate_withNull() {
		preference.setStartDate(null);
	}

	/** Test the observer isn't notified when setting to a date with same value. */
	@Test public void testSetStartDate_withSameDate() {
		preference.addObserver(observer);
		assertFalse(observer.isUpdated);
		Calendar calendar = (Calendar) preference.getStartDate().clone();
		preference.setStartDate(calendar);
		assertFalse(observer.isUpdated);
	}
}
