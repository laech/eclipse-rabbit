package rabbit.tracking.storage.xml;

import static rabbit.tracking.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.tracking.storage.xml.internal.DataStore;
import rabbit.tracking.storage.xml.schema.PartEventListType;
import rabbit.tracking.storage.xml.schema.PartEventType;

/**
 * Gets data about how much time is spent using Eclipse everyday.
 */
public class SessionDataAccessor implements IAccessor {

	private static final Format formatter = new SimpleDateFormat("yyyy-MM-dd");
	private IDataStore dataStore;

	/** Constructor. */
	public SessionDataAccessor() {
		dataStore = DataStore.PART_STORE;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The keys of the returned Map are dates formatted in the form
	 * "yyyy-MM-dd", the values are the durations, in milliseconds.
	 * </p>
	 */
	@Override public Map<String, Long> getData(Calendar start, Calendar end) {
		Map<String, Long> result = new LinkedHashMap<String, Long>();
		XMLGregorianCalendar startXmlCal = toXMLGregorianCalendarDate(start);
		XMLGregorianCalendar endXmlCal = toXMLGregorianCalendarDate(end);

		List<File> files = dataStore.getDataFiles(start, end);
		for (File f : files) {
			for (PartEventListType list : dataStore.read(f).getPartEvents()) {

				XMLGregorianCalendar date = list.getDate();
				if (date.compare(startXmlCal) >= 0 && date.compare(endXmlCal) <= 0) {

					String dateStr = formatter.format(date.toGregorianCalendar().getTime());
					long value = 0;
					for (PartEventType e : list.getPartEvent()) {
						value += e.getDuration();
						System.out.println(e.getDuration());
					}
					result.put(dateStr, value);
				}
			}
		}
		return result;
	}
}
