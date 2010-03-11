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

import static org.junit.Assert.assertEquals;
import static rabbit.core.internal.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

import java.util.Calendar;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

/**
 * Test for {@link DatatypeConverter}
 */
public class DatatypeConverterTest {

	@Test
	public void testToXMLGregorianCalendarDate() {

		Calendar cal = Calendar.getInstance();
		XMLGregorianCalendar xmlCal = toXMLGregorianCalendarDate(cal);

		assertEquals(cal.get(Calendar.YEAR), xmlCal.getYear());
		// Calendar.MONTH is zero based.
		assertEquals(cal.get(Calendar.MONTH) + 1, xmlCal.getMonth());
		assertEquals(cal.get(Calendar.DAY_OF_MONTH), xmlCal.getDay());
	}
}
