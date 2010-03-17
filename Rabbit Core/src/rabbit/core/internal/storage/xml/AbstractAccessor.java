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

import static rabbit.core.internal.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.core.internal.storage.xml.schema.events.EventGroupType;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.storage.IAccessor;

/**
 * Abstract class provides default behaviors, this class is designed
 * specifically for the schema.
 * 
 * @param <E>
 *            The XML type.
 * @param <S>
 *            The XML category type.
 */
public abstract class AbstractAccessor<T, E, S extends EventGroupType> implements IAccessor<T> {

	private final IDataStore dataStore;

	/** Constructor. */
	public AbstractAccessor() {
		dataStore = getDataStore();
	}

	@Override
	public T getData(Calendar start, Calendar end) {
		return filter(getXmlData(start, end));
	}

	/**
	 * Filters the given data.
	 * 
	 * @param data
	 *            The raw data between the two dates of
	 *            {@link #getData(Calendar, Calendar)}.
	 * @return The filtered data.
	 */
	protected abstract T filter(List<S> data);

	/**
	 * Gets the collection of categories from the given parameter.
	 * 
	 * @param doc
	 *            The root of a document type.
	 * @return A collection of categories.
	 */
	protected abstract Collection<S> getCategories(EventListType doc);

	/**
	 * Gets the data store.
	 * 
	 * @return The data store.
	 */
	protected abstract IDataStore getDataStore();

	/**
	 * Gets the id of the given type.
	 * 
	 * @param e
	 *            The type.
	 * @return The id.
	 */
	protected abstract String getId(E e);

	/**
	 * Gets the usage info from the given type.
	 * 
	 * @param e
	 *            The type to get info from.
	 * @return The usage info.
	 */
	protected abstract long getUsage(E e);

	/**
	 * Gets the data from the XML files.
	 * 
	 * @param start
	 *            The start date of the data.
	 * @param end
	 *            The end date of the data.
	 * @return The data between the dates, inclusive.
	 */
	protected List<S> getXmlData(Calendar start, Calendar end) {
		List<S> data = new LinkedList<S>();
		XMLGregorianCalendar startXmlCal = toXMLGregorianCalendarDate(start);
		XMLGregorianCalendar endXmlCal = toXMLGregorianCalendarDate(end);

		List<File> files = dataStore.getDataFiles(start, end);
		for (File f : files) {

			for (S list : getCategories(dataStore.read(f))) {
				if (list.getDate().compare(startXmlCal) >= 0
						&& list.getDate().compare(endXmlCal) <= 0) {

					data.add(list);
				}
			}
		}
		return data;
	}

	/**
	 * Gets a collection of types from the given category.
	 * 
	 * @param list
	 *            The category.
	 * @return A collection of objects.
	 */
	protected abstract Collection<E> getXmlTypes(S list);

}
