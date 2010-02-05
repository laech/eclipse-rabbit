package rabbit.tracking.storage.xml;

import java.util.Calendar;
import java.util.Map;

/**
 * Represents a simple data accessor to get data out of a data store. Check the
 * implementer's documentation for more information about the returned data.
 */
public interface IAccessor {

	/**
	 * Gets the data between the dates, inclusive.
	 * 
	 * @param start The start date.
	 * @param end The end Date.
	 * @return A map of data.
	 */
	public Map<String, Long> getData(Calendar start, Calendar end);
}
