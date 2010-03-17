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
package rabbit.ui.internal.pages;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Map;

import org.junit.Test;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.SessionDataAccessor;
import rabbit.ui.DisplayPreference;

/**
 * Test for {@link SessionPage}
 */
public class SessionPageTest extends AbstractTableViewerPageTest {

	@SuppressWarnings("unchecked")
	static Map<String, Long> getData(SessionPage page) throws Exception {
		Field field = SessionPage.class.getDeclaredField("model");
		field.setAccessible(true);
		return (Map<String, Long>) field.get(page);
	}

	@Test
	public void testGetValue() throws Exception {
		long value = 9823;
		String date = "abc";
		Map<String, Long> data = getData((SessionPage) page);
		data.put(date, value);

		assertEquals(value, page.getValue(date));
		assertEquals(0, page.getValue(new Object()));
	}

	@Test
	public void testUpdate() throws Exception {
		long max = 0;
		IAccessor<Map<String, Long>> accessor = new SessionDataAccessor();

		DisplayPreference pref = new DisplayPreference();
		Map<String, Long> data = accessor.getData(pref.getStartDate(), pref.getEndDate());
		for (long value : data.values()) {
			if (value > max) {
				max = value;
			}
		}
		page.update(pref);
		assertEquals(max, page.getMaxValue());

		pref.getStartDate().add(Calendar.MONTH, -1);
		pref.getEndDate().add(Calendar.DAY_OF_MONTH, -5);
		data = accessor.getData(pref.getStartDate(), pref.getEndDate());
		max = 0;
		for (long value : data.values()) {
			if (value > max) {
				max = value;
			}
		}
		page.update(pref);
		assertEquals(max, page.getMaxValue());
	}

	@Override
	protected AbstractTableViewerPage createPage() {
		return new SessionPage();
	}
}
