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

import org.eclipse.ui.IPerspectiveDescriptor;
import org.junit.Test;

import rabbit.core.RabbitCore;
import rabbit.core.storage.IAccessor;
import rabbit.ui.DisplayPreference;
import rabbit.ui.internal.util.UndefinedPerspectiveDescriptor;

/**
 * Test for {@link PerspectivePage}
 */
public class PerspectivePageTest extends AbstractTableViewerPageTest {
	@SuppressWarnings("unchecked")
	static Map<IPerspectiveDescriptor, Long> getData(PerspectivePage page) throws Exception {
		Field field = PerspectivePage.class.getDeclaredField("dataMapping");
		field.setAccessible(true);
		return (Map<IPerspectiveDescriptor, Long>) field.get(page);
	}

	@Test
	public void testGetValue() throws Exception {
		long value = 9823;
		IPerspectiveDescriptor perspective = new UndefinedPerspectiveDescriptor("abc");
		Map<IPerspectiveDescriptor, Long> data = getData((PerspectivePage) page);
		data.put(perspective, value);

		assertEquals(value, page.getValue(perspective));
		assertEquals(0, page.getValue(new Object()));
	}

	@Test
	public void testUpdate() throws Exception {
		long max = 0;
		IAccessor<Map<String, Long>> accessor = RabbitCore.getPerspectiveDataAccessor();

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
		return new PerspectivePage();
	}
}
