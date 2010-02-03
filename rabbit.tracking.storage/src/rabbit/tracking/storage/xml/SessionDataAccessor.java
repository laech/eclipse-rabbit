package rabbit.tracking.storage.xml;

import static rabbit.tracking.storage.xml.AbstractXmlStorer.toXMLGregorianCalendarDate;

import java.io.File;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.tracking.storage.xml.schema.PartEventListType;
import rabbit.tracking.storage.xml.schema.PartEventType;

public class SessionDataAccessor implements IAccessor {

	private static final Format formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	private IDataStore dataStore;
	
	public SessionDataAccessor() {
		dataStore = DataStore.PART_STORE;
	}

	@Override
	public Map<String, Long> getData(Calendar start, Calendar end) {
		Map<String, Long> result = new LinkedHashMap<String, Long>();
		XMLGregorianCalendar startXmlCal = toXMLGregorianCalendarDate(start);
		XMLGregorianCalendar endXmlCal = toXMLGregorianCalendarDate(end);

		List<File> files = dataStore.getDataFiles(start, end);
		for (File f : files) {
			for (PartEventListType list : dataStore.read(f).getPartEvents()) {
				
				XMLGregorianCalendar date = list.getDate();
				if (date.compare(startXmlCal) >= 0 
						&& date.compare(endXmlCal) <= 0) {
					
					String dateStr = formatter.format(date.toGregorianCalendar().getTime());
					long value = 0;
					for (PartEventType e : list.getPartEvent()) {
						value += e.getDuration();
					}
					result.put(dateStr, value);
				}
			}
		}
		return result;
	}
}
