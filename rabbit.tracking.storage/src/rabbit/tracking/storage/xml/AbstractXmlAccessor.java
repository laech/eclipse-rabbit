package rabbit.tracking.storage.xml;

import static rabbit.tracking.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.tracking.storage.xml.schema.EventGroupType;
import rabbit.tracking.storage.xml.schema.EventListType;

/**
 * Abstract class provides default behaviors, this class is designed
 * specifically for the schema.
 * 
 * @param <T> The XML type.
 * @param <S> The XML category type.
 */
public abstract class AbstractXmlAccessor<T, S extends EventGroupType> implements IAccessor {

	private final IDataStore dataStore;

	/** Constructor. */
	public AbstractXmlAccessor() {
		dataStore = getDataStore();
	}

	/**
	 * Gets the data store.
	 * 
	 * @return The data store.
	 */
	protected abstract IDataStore getDataStore();

	@Override public Map<String, Long> getData(Calendar start, Calendar end) {
		Map<String, Long> result = new LinkedHashMap<String, Long>();
		XMLGregorianCalendar startXmlCal = toXMLGregorianCalendarDate(start);
		XMLGregorianCalendar endXmlCal = toXMLGregorianCalendarDate(end);

		List<File> files = dataStore.getDataFiles(start, end);
		for (File f : files) {
			for (S list : getCategories(dataStore.read(f))) {
				if (list.getDate().compare(startXmlCal) >= 0 && list.getDate().compare(endXmlCal) <= 0) {

					for (T e : getXmlTypes(list)) {

						Long usage = result.get(getId(e));
						if (usage == null) {
							result.put(getId(e), getUsage(e));
						} else {
							result.put(getId(e), getUsage(e) + usage);
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * Gets a collection of types from the given category.
	 * 
	 * @param list The category.
	 * @return A collection of objects.
	 */
	protected abstract Collection<T> getXmlTypes(S list);

	/**
	 * Gets the collection of categories from the given parameter.
	 * 
	 * @param doc The root of a document type.
	 * @return A collection of categories.
	 */
	protected abstract Collection<S> getCategories(EventListType doc);

	/**
	 * Gets the usage info from the given type.
	 * 
	 * @param e The type to get info from.
	 * @return The usage info.
	 */
	protected abstract long getUsage(T e);

	/**
	 * Gets the id of the given type.
	 * 
	 * @param e The type.
	 * @return The id.
	 */
	protected abstract String getId(T e);

}
