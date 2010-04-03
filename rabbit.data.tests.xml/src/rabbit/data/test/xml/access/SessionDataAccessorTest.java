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
package rabbit.data.test.xml.access;

import rabbit.data.internal.xml.schema.events.EventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventListType;
import rabbit.data.internal.xml.schema.events.PerspectiveEventType;
import rabbit.data.xml.access.SessionDataAccessor;

import static org.junit.Assert.assertEquals;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Test for {@link SessionDataAccessor}
 */
public class SessionDataAccessorTest extends PerspectiveDataAccessorTest {
	
	@Override
	protected SessionDataAccessor create() {
		return new SessionDataAccessor();
	}
	
	@Override
	protected void assertValues(Map<String, Long> data, EventListType events) {
		Format format = new SimpleDateFormat(SessionDataAccessor.DATE_FORMAT);
		
		Map<String, Long> map = new HashMap<String, Long>();
		for (PerspectiveEventListType list : events.getPerspectiveEvents()) {
			String dateStr = format.format(list.getDate().toGregorianCalendar().getTime());
			
			long value = 0;
			for (PerspectiveEventType type : list.getPerspectiveEvent()) {
				value += type.getDuration();
			}
			
			Long oldValue = map.get(dateStr);
			if (oldValue == null) {
				oldValue = Long.valueOf(0);
			}
			map.put(dateStr, value + oldValue);
		}
		
		assertEquals(map.size(), data.size());
		for (Entry<String, Long > entry : map.entrySet()) {
			assertEquals(entry.getValue(), data.get(entry.getKey()));
		}
	}
}
