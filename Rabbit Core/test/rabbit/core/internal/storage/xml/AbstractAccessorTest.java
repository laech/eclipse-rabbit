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

import static org.junit.Assert.assertNotNull;
import static rabbit.core.internal.storage.xml.DatatypeUtil.toXMLGregorianCalendarDate;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.core.runtime.IPath;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.core.internal.RabbitCorePlugin;
import rabbit.core.internal.storage.xml.schema.events.EventGroupType;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;

/**
 * Test for {@link AbstractAccessor}
 */
public abstract class AbstractAccessorTest<T, E, S extends EventGroupType> {

	private AbstractAccessor<T, E, S> accessor = create();

	protected ObjectFactory objectFactory = new ObjectFactory();

	private static final IPath originalRoot = RabbitCorePlugin.getDefault().getStoragePathRoot();

	@BeforeClass
	public static void beforeClass() {
		// Set the directory root to a new folder so that we don't mess
		// with existing data:
		File dir = originalRoot.append(System.currentTimeMillis() + "").toFile();
		if (!dir.exists() && !dir.mkdirs()) {
			Assert.fail();
		}
		RabbitCorePlugin.getDefault().setStoragePathRoot(dir);
	}

	@AfterClass
	public static void afterClass() {
		RabbitCorePlugin.getDefault().setStoragePathRoot(originalRoot.toFile());
	}

	@Test
	public void testGetCategories() {
		EventListType eventList = objectFactory.createEventListType();
		S list = createListType();
		accessor.getCategories(eventList).add(list);
		Assert.assertEquals(1, accessor.getCategories(eventList).size());
	}

	@Test
	public void testGetData_listsWithDifferentDates() {
		String id = "qfnnvkfde877thfg";

		// 1:

		int count1 = 298;
		E type1 = createXmlType();
		setId(type1, id);
		setUsage(type1, count1);

		S list1 = createListType();
		Calendar tmp = Calendar.getInstance();
		tmp.set(Calendar.DAY_OF_MONTH, 1);
		XMLGregorianCalendar start = toXMLGregorianCalendarDate(tmp);
		list1.setDate(start);
		getXmlTypes(list1).add(type1);

		// 2:

		int count2 = 22817;
		E type2 = createXmlType();
		setId(type2, id);
		setUsage(type2, count2);

		S list2 = createListType();
		tmp.set(Calendar.DAY_OF_MONTH, 3);
		XMLGregorianCalendar end = toXMLGregorianCalendarDate(tmp);
		list2.setDate(end);
		getXmlTypes(list2).add(type2);

		EventListType events = objectFactory.createEventListType();
		accessor.getCategories(events).add(list1);
		accessor.getCategories(events).add(list2);

		File f = accessor.getDataStore().getDataFile(tmp);
		accessor.getDataStore().write(events, f);

		assertValues(accessor.getData(
				start.toGregorianCalendar(), end.toGregorianCalendar()), events);
	}

	/**
	 * Tests that two lists with the same date are stored, then getting the data
	 * out should return the combined data. Note that although two lists with
	 * the same date should not have happened.
	 */
	@Test
	public void testGetData_listsWithSameDate() {
		Calendar date = Calendar.getInstance();
		String id = "qfnnvkfde877thfg";

		// 1:

		int count1 = 1232948;
		E type1 = createXmlType();
		setId(type1, id);
		setUsage(type1, count1);

		S list1 = createListType();
		XMLGregorianCalendar start = toXMLGregorianCalendarDate(date);
		list1.setDate(start);
		getXmlTypes(list1).add(type1);

		// 2:

		int count2 = 2342817;
		E type2 = createXmlType();
		setId(type2, id);
		setUsage(type2, count2);

		S list2 = createListType();
		XMLGregorianCalendar end = toXMLGregorianCalendarDate(date);
		list2.setDate(end);
		getXmlTypes(list2).add(type2);

		EventListType events = objectFactory.createEventListType();
		accessor.getCategories(events).add(list1);
		accessor.getCategories(events).add(list2);

		File f = accessor.getDataStore().getDataFile(date);
		accessor.getDataStore().write(events, f);

		assertValues(accessor.getData(
				start.toGregorianCalendar(), end.toGregorianCalendar()), events);
	}

	/**
	 * Gets the list of XML event types.
	 */
	protected abstract List<E> getXmlTypes(S list);

	@Test
	public void testGetDataStore() {
		assertNotNull(accessor.getDataStore());
	}

	/**
	 * Checks the values are OK against the two parameters, one is returned by
	 * the IAccessor, one is the raw data.
	 */
	protected abstract void assertValues(T data, EventListType events);

	/** Creates a subject for testing. */
	protected abstract AbstractAccessor<T, E, S> create();

	/**
	 * Creates a new list type.
	 * 
	 * @return A new list type.
	 */
	protected abstract S createListType();

	/**
	 * Creates a new XML type with fields filled.
	 * 
	 * @return A new XML type.
	 */
	protected abstract E createXmlType();

	/**
	 * Sets the id of the type.
	 * 
	 * @param type
	 *            The type.
	 * @param id
	 *            The id.
	 */
	protected abstract void setId(E type, String id);

	/**
	 * Sets the usage of the type.
	 * 
	 * @param type
	 *            The type.
	 * @param usage
	 *            The usage.
	 */
	protected abstract void setUsage(E type, long usage);
}
