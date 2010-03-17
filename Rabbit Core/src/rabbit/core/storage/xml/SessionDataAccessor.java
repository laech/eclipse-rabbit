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

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;

/**
 * Gets data about how much time is spent using Eclipse everyday.
 * <p>
 * Data returned {@link #getData(java.util.Calendar, java.util.Calendar)} is a
 * map, where the keys are formated date strings using {@link #DATE_FORMAT}, and
 * the values are durations in milliseconds.
 * </p>
 */
public class SessionDataAccessor extends PerspectiveDataAccessor {

	/**
	 * The format used to format the dates with a SimpleDateFormat.
	 * 
	 * @see SimpleDateFormat
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd EEEE";

	private final Format formatter;

	/** Constructor. */
	public SessionDataAccessor() {
		formatter = new SimpleDateFormat(DATE_FORMAT);
	}

	@Override
	protected Map<String, Long> filter(List<PerspectiveEventListType> data) {
		Map<String, Long> result = new LinkedHashMap<String, Long>();
		for (PerspectiveEventListType list : data) {

			String dateStr = formatter.format(
					list.getDate().toGregorianCalendar().getTime());
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
		return result;
	}
}
