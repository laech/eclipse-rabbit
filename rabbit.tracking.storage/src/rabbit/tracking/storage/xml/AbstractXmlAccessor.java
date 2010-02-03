package rabbit.tracking.storage.xml;

import static rabbit.tracking.storage.xml.AbstractXmlStorer.toXMLGregorianCalendarDate;

import java.io.File;
import java.util.Calendar;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.tracking.storage.xml.schema.EventGroupType;
import rabbit.tracking.storage.xml.schema.EventListType;


public abstract class AbstractXmlAccessor<T, S extends EventGroupType> implements IAccessor {

	private final IDataStore dataStore;
	
	public AbstractXmlAccessor() {
		dataStore = getDataStore();
	}

	protected abstract IDataStore getDataStore();

	@Override
	public Map<String, Long> getData(Calendar start, Calendar end) {
		Map<String, Long> result = new LinkedHashMap<String, Long>();
		XMLGregorianCalendar startXmlCal = toXMLGregorianCalendarDate(start);
		XMLGregorianCalendar endXmlCal = toXMLGregorianCalendarDate(end);

		List<File> files = dataStore.getDataFiles(start, end);
		for (File f : files) {
			for (S list : getCategories(dataStore.read(f))) {
				if (list.getDate().compare(startXmlCal) >= 0
						&& list.getDate().compare(endXmlCal) <= 0) {
					
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

	protected abstract Collection<T> getXmlTypes(S list);
	
	protected abstract Collection<S> getCategories(EventListType doc);

	protected abstract long getUsage(T e);

	protected abstract String getId(T e);
	
}
