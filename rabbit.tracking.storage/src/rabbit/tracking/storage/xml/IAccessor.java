package rabbit.tracking.storage.xml;

import java.util.Calendar;
import java.util.Map;

public interface IAccessor {

	public Map<String, Long> getData(Calendar start, Calendar end);
}
