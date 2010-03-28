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
import static org.junit.Assert.assertNotNull;
import static rabbit.core.internal.storage.xml.DatatypeUtil.toXMLGregorianCalendarDate;

import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collection;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import rabbit.core.events.DiscreteEvent;
import rabbit.core.internal.storage.xml.schema.events.EventGroupType;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;

/**
 * @see AbstractStorer
 */
public abstract class AbstractStorerTest<E extends DiscreteEvent, T, S extends EventGroupType> {

	private AbstractStorer<E, T, S> storer = create();

	protected ObjectFactory objectFactory = new ObjectFactory();

	@Test
	public abstract void testCommit() throws Exception;

	@Test
	public void testGetDataStore() {
		assertNotNull(storer.getDataStore());
	}

	@Test
	public void testGetXmlTypeCategories() {
		assertNotNull(storer.getXmlTypeCategories(new ObjectFactory().createEventListType()));
	}

	@Test
	public abstract void testInsert() throws Exception;

	@Test
	public abstract void testInsertCollection() throws Exception;
	
	/**
	 * @see AbstractStorer#merge(java.util.List, DiscreteEvent)
	 */
	@Test
	public abstract void testMerge_listOfXmlTypesAndEvent() throws Exception;
	
	/**
	 * @see AbstractStorer#merge(java.util.List, java.util.List)
	 */
	@Test
	public abstract void testMerge_listOfXmlTypesAndListOfXmlTypes() throws Exception;

	@Test
	public void testNewXmlTypeHolder() {
		XMLGregorianCalendar cal = toXMLGregorianCalendarDate(Calendar.getInstance());
		S s = storer.newXmlTypeHolder(cal);
		assertNotNull(s);
		assertEquals(cal, s.getDate());
	}

	protected abstract AbstractStorer<E, T, S> create();

	@SuppressWarnings("unchecked")
	protected Collection<S> getDataField(AbstractStorer<E, T, S> s) throws Exception {
		Field f = AbstractStorer.class.getDeclaredField("data");
		f.setAccessible(true);
		return (Collection<S>) f.get(s);
	}
}
