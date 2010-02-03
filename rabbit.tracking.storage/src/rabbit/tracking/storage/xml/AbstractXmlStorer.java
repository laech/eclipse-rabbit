package rabbit.tracking.storage.xml;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeConstants;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.tracking.event.DiscreteEvent;
import rabbit.tracking.storage.xml.schema.EventGroupType;
import rabbit.tracking.storage.xml.schema.EventListType;
import rabbit.tracking.storage.xml.schema.ObjectFactory;

/**
 * This abstract class is designed specifically for the XML schema.
 * 
 * @param <E>
 *            The event type. Such as {@link rabbit.tracking.event.CommandEvent}.
 * @param <T>
 *            The corresponding XML object type of the event type. Such as
 *            {@link rabbit.tracking.storage.xml.schema.CommandEventType}.
 * @param <S>
 *            The group holder type that separates the XML object types
 *            according to event date. Such as
 *            {@link rabbit.tracking.storage.xml.schema.CommandEventListType}.
 */
public abstract class AbstractXmlStorer<E extends DiscreteEvent, T, S extends EventGroupType> implements IStorer<E> {

	/**
	 * Formats a date into "yyyy-MM-dd".
	 */
	public static final DateFormat DAY_FORMATTER = new SimpleDateFormat("yyyy-MM-dd");
	
	public static final ObjectFactory OBJECT_FACTORY = new ObjectFactory();

	/**
	 * An data type factory for creating XML data types.
	 */
	public static DatatypeFactory datatypeFactory;
	
	private Set<S> data;
	
	protected Calendar currentMonth;
	
	static {
		try {
			datatypeFactory = DatatypeFactory.newInstance();
		} catch (DatatypeConfigurationException e) {

			// Try again?
			try {
				datatypeFactory = DatatypeFactory.newInstance();
			} catch (DatatypeConfigurationException ex) {
 				e.printStackTrace(); // OK, I give up...
			}
		}
	}

	/**
	 * Sole constructor.
	 */
	public AbstractXmlStorer() {
		data = new LinkedHashSet<S>();
		currentMonth = Calendar.getInstance();
	}

	/**
	 * Converts a {@link Calendar} to {@link XMLGregorianCalendar}. The converted
	 * calendar is a date, which means only the year, month and day of month fields
	 * are set.
	 * 
	 * @param cal The calendar to convert from.
	 * @return The converted calendar.
	 */
	public static XMLGregorianCalendar toXMLGregorianCalendarDate(Calendar cal) {
		
		return datatypeFactory.newXMLGregorianCalendarDate(
				cal.get(Calendar.YEAR), 
				cal.get(Calendar.MONTH) + 1, 
				cal.get(Calendar.DAY_OF_MONTH), 
				DatatypeConstants.FIELD_UNDEFINED);
	}

	/**
	 * Gets the XML nodes for grouping the event objects by date in a
	 * {@link EventListType} as defined by the schema package {@link rabbit.tracking.storage.xml.schema}.
	 * 
	 * @param <U> The class type.
	 * @param events The root element.
	 * @return A list of groups.
	 */
	protected abstract <U extends S> List<U> getXmlTypeCategories(EventListType events);

	/**
	 * Checks whether the given objects has the same id.
	 * <p>
	 * Other properties are ignored. In other words, checks to see whether the 
	 * two objects can be merged without loosing its unique identity.
	 * </p>
	 * @param x A java object representing an XML element.
	 * @param e A event object.
	 * @return true If the two object has the same id.
	 */
	protected abstract boolean hasSameId(T x, E e);

	/**
	 * Checks whether the given objects has the same id.
	 * <p>
	 * Other properties are ignored. In other words, checks to see whether the 
	 * two objects can be merged without loosing its unique identity.
	 * </p>
	 * @param x1 A java object representing an XML element.
	 * @param x2 A java object representing an XML element.
	 * @return true If the two object has the same id.
	 */
	protected abstract boolean hasSameId(T x1, T x2);
	
	/**
	 * Inserts a collection of event data to be stored.
	 * 
	 * @param col The collection of events.
	 */
	@Override
	public void insert(Collection<? extends E> col) {
		
		for (E e: col) {
			insert(e);
		}
	}

	/**
	 * Inserts an event to be stored.
	 * 
	 * @param e The event.
	 */
	@Override
	public void insert(E e) {
		
		if (!isSameMonthInYear(e.getTime(), currentMonth)) {
			commit();
			//:
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
			S holder = newXmlTypeHolder(toXMLGregorianCalendarDate(e.getTime()));
			merge(holder, e);
			
			data.add(holder);
		}
	}
	
	/**
	 * Checks whether the two calendars are representing the same date in time.
	 * 
	 * @param cal The first calendar.
	 * @param xmlCal The second calendar.
	 * @return true if the two calendars are representing the same date in time,
	 * 				false otherwise.
	 */
	public static boolean isSameDate(Calendar cal, XMLGregorianCalendar xmlCal) {
		
		return ((xmlCal.getYear()  == cal.get(Calendar.YEAR))
			 && (xmlCal.getMonth() == cal.get(Calendar.MONTH) + 1)
			 && (xmlCal.getDay())  == cal.get(Calendar.DAY_OF_MONTH));
	}
	
	/**
	 * Checks whether the two calendars are representing the same month in time.
	 * 
	 * @param cal1 The first calendar.
	 * @param cal2 The second calendar.
	 * @return true if the two calendars are representing the same month in time,
	 * 				false otherwise.
	 */
	public static boolean isSameMonthInYear(Calendar cal1, Calendar cal2) {
		
		return ((cal1.get(Calendar.YEAR)  == cal2.get(Calendar.YEAR))
			 && (cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH)));
	}
	
	/**
	 * Merges the event into the list.
	 * 
	 * @param xList The list for merging the event into.
	 * @param event The event for merging.
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
	 * @param mainList The list for merging into.
	 * @param newList The list for getting data from.
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
	 * @param main The object to merge into.
	 * @param e The object to merge from.
	 */
	protected abstract void merge(T main, E e);
	
	/**
	 * Merges the data of the second parameter into the first parameter.
	 *  
	 * @param main The object to merge into.
	 * @param x The object to merge from.
	 */
	protected abstract void merge(T main, T x);
	
	/**
	 * Merges the data of the second parameter into the first parameter.
	 * 
	 * @param main The XML group to merge into.
	 * @param e The object to merge from.
	 */
	protected abstract void merge(S main, E e);
	
	/**
	 * Merges the two group objects together, use the first parameter as the
	 * final result.
	 * 
	 * @param main The XML group to merge into.
	 * @param data The XML group to merge from.
	 */
	protected abstract void merge(S main, S data);
	
	/**
	 * Creates a new XML object type from the given event.
	 * 
	 * @param e The event.
	 * @return A new XML object type.
	 */
	protected abstract T newXmlType(E e);
	
	/**
	 * Creates a new XML object group type from the given date.
	 * 
	 * @param date The date.
	 * @return A new XML object group type configured with the date.
	 */
	protected abstract S newXmlTypeHolder(XMLGregorianCalendar date);
	
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
}
