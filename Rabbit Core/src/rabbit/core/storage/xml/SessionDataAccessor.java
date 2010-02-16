package rabbit.core.storage.xml;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.IDataStore;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;
import rabbit.core.storage.IAccessor;
import static rabbit.core.internal.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

/**
 * Gets data about how much time is spent using Eclipse everyday.
 */
public class SessionDataAccessor implements IAccessor {

	/**
	 * The format used to format the dates.
	 * 
	 * @see SimpleDateFormat
	 */
	public static final String DATE_FORMAT = "yyyy-MM-dd E";

	private final Format formatter;
	private IDataStore dataStore;

	/** Constructor. */
	public SessionDataAccessor() {
		dataStore = DataStore.PERSPECTIVE_STORE;
		formatter = new SimpleDateFormat(DATE_FORMAT);
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The keys of the returned Map are dates formatted in the form
	 * "yyyy-MM-dd", the values are the durations, in milliseconds.
	 * </p>
	 */
	@Override
	public Map<String, Long> getData(Calendar start, Calendar end) {
		Map<String, Long> result = new LinkedHashMap<String, Long>();
		XMLGregorianCalendar startXmlCal = toXMLGregorianCalendarDate(start);
		XMLGregorianCalendar endXmlCal = toXMLGregorianCalendarDate(end);

		List<File> files = dataStore.getDataFiles(start, end);
		for (File f : files) {
			for (PerspectiveEventListType list : dataStore.read(f).getPerspectiveEvents()) {

				XMLGregorianCalendar date = list.getDate();
				if (date.compare(startXmlCal) >= 0 && date.compare(endXmlCal) <= 0) {

					String dateStr = formatter.format(date.toGregorianCalendar().getTime());
					long value = 0;
					for (PerspectiveEventType e : list.getPerspectiveEvent()) {
						value += e.getDuration();
					}

					Long oldValue = result.get(dateStr);
					if (oldValue != null) {
						value += oldValue.longValue();
					}
					result.put(dateStr, value);
				}
			}
		}
		return result;
	}
}
