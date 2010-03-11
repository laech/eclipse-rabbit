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

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.core.events.DiscreteEvent;
import rabbit.core.internal.storage.xml.schema.events.EventGroupType;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;
import rabbit.core.storage.IStorer;

/**
 * This abstract class is designed specifically for the XML schema.
 * 
 * @param <E>
 *            The event type. Such as {@link rabbit.core.events.CommandEvent} .
 * @param <T>
 *            The corresponding XML object type of the event type.
 * @param <S>
 *            A {@link EventGroupType} that separates the XML object types
 *            according to event date.
 */
public abstract class AbstractStorer<E extends DiscreteEvent, T, S extends EventGroupType>
		implements IStorer<E> {

	protected static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

	/**
	 * Checks whether the two calendars are representing the same date in time.
	 * 
	 * @param cal
	 *            The first calendar.
	 * @param xmlCal
	 *            The second calendar.
	 * @return true if the two calendars are representing the same date in time,
	 *         false otherwise.
	 */
	protected static boolean isSameDate(Calendar cal, XMLGregorianCalendar xmlCal) {

		return (xmlCal.getYear() == cal.get(Calendar.YEAR))
				&& (xmlCal.getMonth() == cal.get(Calendar.MONTH) + 1)
				&& (xmlCal.getDay() == cal.get(Calendar.DAY_OF_MONTH));
	}

	/**
	 * Checks whether the two calendars are representing the same month in time.
	 * 
	 * @param cal1
	 *            The first calendar.
	 * @param cal2
	 *            The second calendar.
	 * @return true if the two calendars are representing the same month in
	 *         time, false otherwise.
	 */
	protected static boolean isSameMonthInYear(Calendar cal1, Calendar cal2) {
		return (cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR))
				&& (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH));
	}

	private Set<S> data;

	protected Calendar currentMonth;

	/**
	 * Sole constructor.
	 */
	public AbstractStorer() {
		data = new LinkedHashSet<S>();
		currentMonth = Calendar.getInstance();
	}

	/**
	 * Writes the data to disk.
	 */
	public void commit() {

		if (data.isEmpty()) {
			return;
		}

		File f = getDataStore().getDataFile(currentMonth);
		EventListType events = getDataStore().read(f);
		List<S> mainList = getXmlTypeCategories(events);

		for (S newList : data) {

			boolean done = false;
			for (S oldList : mainList) {
				if (newList.getDate().equals(oldList.getDate())) {
					merge(oldList, newList);
					done = true;
					break;
				}
			}

			if (!done) {
				mainList.add(newList);
			}
		}

		getDataStore().write(events, f);
		data.clear();
	}

	/**
	 * Inserts a collection of event data to be stored.
	 * 
	 * @param col
	 *            The collection of events.
	 */
	@Override
	public void insert(Collection<? extends E> col) {

		for (E e : col) {
			insert(e);
		}
	}

	/**
	 * Inserts an event to be stored.
	 * 
	 * @param e
	 *            The event.
	 */
	@Override
	public void insert(E e) {

		if (!isSameMonthInYear(e.getTime(), currentMonth)) {
			commit();
			// :
			currentMonth = e.getTime();
		}

		boolean done = false;

		for (S list : data) {
			if (isSameDate(e.getTime(), list.getDate())) {
				merge(list, e);
				done = true;
				break;
			}
		}

		if (!done) {
			S holder = newXmlTypeHolder(DatatypeConverter.toXMLGregorianCalendarDate(e.getTime()));
			merge(holder, e);
			data.add(holder);
		}
	}

	/**
	 * Gets the data store.
	 * 
	 * @return The data store.
	 */
	protected abstract IDataStore getDataStore();

	/**
	 * Gets the XML nodes for grouping the event objects by date in a
	 * {@link EventListType}.
	 * 
	 * @param <U>
	 *            The class type.
	 * @param events
	 *            The root element.
	 * @return A list of groups.
	 */
	protected abstract <U extends S> List<U> getXmlTypeCategories(EventListType events);

	/**
	 * Checks whether the given objects has the same id.
	 * <p>
	 * Other properties are ignored. In other words, checks to see whether the
	 * two objects can be merged without loosing its unique identity.
	 * </p>
	 * 
	 * @param x
	 *            A java object representing an XML element.
	 * @param e
	 *            A event object.
	 * @return true If the two object has the same id.
	 */
	protected abstract boolean hasSameId(T x, E e);

	/**
	 * Checks whether the given objects has the same id.
	 * <p>
	 * Other properties are ignored. In other words, checks to see whether the
	 * two objects can be merged without loosing its unique identity.
	 * </p>
	 * 
	 * @param x1
	 *            A java object representing an XML element.
	 * @param x2
	 *            A java object representing an XML element.
	 * @return true If the two object has the same id.
	 */
	protected abstract boolean hasSameId(T x1, T x2);

	/**
	 * Merges the event into the list.
	 * 
	 * @param xList
	 *            The list for merging the event into.
	 * @param event
	 *            The event for merging.
	 */
	protected void merge(List<T> xList, E event) {

		boolean done = false;
		for (T xmlType : xList) {
			if (hasSameId(xmlType, event)) {
				merge(xmlType, event);
				done = true;
				break;
			}
		}

		if (!done) {
			T xItem = newXmlType(event);
			xList.add(xItem);
		}
	}

	/**
	 * Merges the second list into the first list.
	 * 
	 * @param mainList
	 *            The list for merging into.
	 * @param newList
	 *            The list for getting data from.
	 */
	protected void merge(List<T> mainList, List<T> newList) {

		for (T newType : newList) {
			boolean done = false;

			for (T mainType : mainList) {
				if (hasSameId(mainType, newType)) {
					merge(mainType, newType);
					done = true;
					break;
				}
			}

			if (!done) {
				mainList.add(newType);
			}
		}
	}

	/**
	 * Merges the data of the second parameter into the first parameter.
	 * 
	 * @param main
	 *            The XML group to merge into.
	 * @param e
	 *            The object to merge from.
	 */
	protected abstract void merge(S main, E e);

	/**
	 * Merges the two group objects together, use the first parameter as the
	 * final result.
	 * 
	 * @param main
	 *            The XML group to merge into.
	 * @param data
	 *            The XML group to merge from.
	 */
	protected abstract void merge(S main, S data);

	/**
	 * Merges the data of the second parameter into the first parameter.
	 * 
	 * @param main
	 *            The object to merge into.
	 * @param e
	 *            The object to merge from.
	 */
	protected abstract void merge(T main, E e);

	/**
	 * Merges the data of the second parameter into the first parameter.
	 * 
	 * @param main
	 *            The object to merge into.
	 * @param x
	 *            The object to merge from.
	 */
	protected abstract void merge(T main, T x);

	/**
	 * Creates a new XML object type from the given event.
	 * 
	 * @param e
	 *            The event.
	 * @return A new XML object type.
	 */
	protected abstract T newXmlType(E e);

	/**
	 * Creates a new XML object group type from the given date.
	 * 
	 * @param date
	 *            The date.
	 * @return A new XML object group type configured with the date.
	 */
	protected abstract S newXmlTypeHolder(XMLGregorianCalendar date);
}
