/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package rabbit.core.internal.storage.xml;

import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

/**
 * A utility class.
 */
public class DatatypeUtil {

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
	 * Checks whether the two calendars are representing the same year, month,
	 * and day of month.
	 * 
	 * @param cal
	 *            The first calendar.
	 * @param xmlCal
	 *            The second calendar.
	 * @return true if the two calendars are representing the same date in time,
	 *         false otherwise.
	 */
	public static boolean isSameDate(Calendar cal, XMLGregorianCalendar xmlCal) {

		return (xmlCal.getYear() == cal.get(Calendar.YEAR))
				&& (xmlCal.getMonth() == cal.get(Calendar.MONTH) + 1)
				&& (xmlCal.getDay() == cal.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Checks whether the two calendars are representing the same month in time.
	 * 
	 * @param cal1
	 *            The first calendar.
	 * @param cal2
	 *            The second calendar.
	 * @return true if the two calendars are representing the same month in
	 *         time, false otherwise.
	 */
	public static boolean isSameMonthInYear(Calendar cal1, Calendar cal2) {
		return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
				&& (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
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

	// TODO test
	public static XMLGregorianCalendar toXMLGregorianCalendarDateTime(GregorianCalendar cal) {
		return datatypeFactory.newXMLGregorianCalendar(cal);
	}
}
