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

import org.eclipse.ui.IWorkbenchPartDescriptor;
import org.junit.Test;

import rabbit.core.storage.IAccessor;
import rabbit.core.storage.xml.PartDataAccessor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.util.UndefinedWorkbenchPartDescriptor;

/**
 * Test for {@link PartPage}
 */
public class PartPageTest extends AbstractTableViewerPageTest {

	@SuppressWarnings("unchecked")
	static Map<IWorkbenchPartDescriptor, Long> getData(PartPage page) throws Exception {
		Field field = PartPage.class.getDeclaredField("dataMapping");
		field.setAccessible(true);
		return (Map<IWorkbenchPartDescriptor, Long>) field.get(page);
	}

	@Test
	public void testGetValue() throws Exception {
		long value = 9823;
		IWorkbenchPartDescriptor part = new UndefinedWorkbenchPartDescriptor("abc");
		Map<IWorkbenchPartDescriptor, Long> data = getData((PartPage) page);
		data.put(part, value);

		assertEquals(value, page.getValue(part));
		assertEquals(0, page.getValue(new Object()));
	}

	@Test
	public void testUpdate() throws Exception {
		long max = 0;
		IAccessor<Map<String, Long>> accessor = new PartDataAccessor();

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
		return new PartPage();
	}
}
