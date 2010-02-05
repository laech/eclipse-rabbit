package rabbit.tracking.storage.xml;

import static org.junit.Assert.assertEquals;
import static rabbit.tracking.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

import java.util.Calendar;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

/**
 * Test for {@link DatatypeConverter}
 */
public class DatatypeConverterTest {

	@Test public void testToXMLGregorianCalendarDate() {

		Calendar cal = Calendar.getInstance();
		XMLGregorianCalendar xmlCal = toXMLGregorianCalendarDate(cal);

		assertEquals(cal.get(Calendar.YEAR), xmlCal.getYear());
		// Calendar.MONTH is zero based.
		assertEquals(cal.get(Calendar.MONTH) + 1, xmlCal.getMonth());
		assertEquals(cal.get(Calendar.DAY_OF_MONTH), xmlCal.getDay());
	}
}
