package rabbit.core.internal.storage.xml;

import java.util.Calendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * A utility class.
 */
public class DatatypeConverter {

	/**
	 * An data type factory for creating XML data types.
	 */
	public static DatatypeFactory datatypeFactory;

	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {

			// Try again?
			try {
				datatypeFactory = DatatypeFactory.newInstance();
			} catch (DatatypeConfigurationException ex) {
				e.printStackTrace(); // OK, I give up...
			}
		}
	}

	/**
	 * Converts a {@link Calendar} to {@link XMLGregorianCalendar}. The
	 * converted calendar is a date, which means only the year, month and day of
	 * month fields are set.
	 * 
	 * @param cal
	 *            The calendar to convert from.
	 * @return The converted calendar.
	 */
	public static XMLGregorianCalendar toXMLGregorianCalendarDate(Calendar cal) {
		return datatypeFactory.newXMLGregorianCalendarDate(
				cal.get(Calendar.YEAR),
				cal.get(Calendar.MONTH) + 1,
				cal.get(Calendar.DAY_OF_MONTH),
				DatatypeConstants.FIELD_UNDEFINED);
	}
}
