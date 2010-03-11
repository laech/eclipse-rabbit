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
package rabbit.core.storage.xml;

import static rabbit.core.internal.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.IDataStore;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;
import rabbit.core.storage.IAccessor;

/**
 * Gets data about how much time is spent using Eclipse everyday.
 */
public class SessionDataAccessor implements IAccessor {

	/**
	 * The format used to format the dates.
	 * 
	 * @see SimpleDateFormat
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd EEEE";

	private final Format formatter;
	private IDataStore dataStore;

	/** Constructor. */
	public SessionDataAccessor() {
		dataStore = DataStore.PERSPECTIVE_STORE;
		formatter = new SimpleDateFormat(DATE_FORMAT);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The keys of the returned Map are dates formatted in the form
	 * "yyyy-MM-dd", the values are the durations, in milliseconds.
	 * </p>
	 */
	@Override
	public Map<String, Long> getData(Calendar start, Calendar end) {
		Map<String, Long> result = new LinkedHashMap<String, Long>();
		XMLGregorianCalendar startXmlCal = toXMLGregorianCalendarDate(start);
		XMLGregorianCalendar endXmlCal = toXMLGregorianCalendarDate(end);

		List<File> files = dataStore.getDataFiles(start, end);
		for (File f : files) {
			for (PerspectiveEventListType list : dataStore.read(f).getPerspectiveEvents()) {

				XMLGregorianCalendar date = list.getDate();
				if (date.compare(startXmlCal) >= 0 && date.compare(endXmlCal) <= 0) {

					String dateStr = formatter.format(date.toGregorianCalendar().getTime());
					long value = 0;
					for (PerspectiveEventType e : list.getPerspectiveEvent()) {
						value += e.getDuration();
					}

					Long oldValue = result.get(dateStr);
					if (oldValue != null) {
						value += oldValue.longValue();
					}
					result.put(dateStr, value);
				}
			}
		}
		return result;
	}
}
