package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertEquals;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventListType;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventType;
import rabbit.core.internal.storage.xml.schema.events.LaunchMode;
import rabbit.core.storage.LaunchDescriptor;
import rabbit.core.storage.LaunchDescriptor.Mode;

/**
 * @see LaunchDataAccessor
 */
public class LaunchDataAccessorTest extends
		AbstractAccessorTest<Set<LaunchDescriptor>, LaunchEventType, LaunchEventListType> {

	@Override
	protected void assertValues(Set<LaunchDescriptor> data, EventListType events) {
		Set<LaunchDescriptor> myData = new HashSet<LaunchDescriptor>();
		for (LaunchEventListType list : events.getLaunchEvents()) {
			for (LaunchEventType type : list.getLaunchEvent()) {
				LaunchDescriptor des = new LaunchDescriptor();
				des.setDuration(type.getDuration());
				des.setFileIds(type.getFileId());
				des.setLaunchName(type.getLaunchName());
				des.getLaunchTime().setTimeInMillis(
						type.getLaunchTime().toGregorianCalendar().getTimeInMillis());
				des.setLaunchType(type.getLaunchType());

				switch (type.getLaunchMode()) {
				case DEBUG:
					des.setLaunchMode(Mode.DEBUG_MODE);
					break;
				case RUN:
					des.setLaunchMode(Mode.RUN_MODE);
					break;
				case PROFILE:
					des.setLaunchMode(Mode.PROFILE_MODE);
					break;
				default:
					des.setLaunchMode(Mode.UNKNOWN);
					break;
				}
			}
		}

		assertEquals(myData.size(), data.size());
		assertEquals(myData, data);
	}

	@Override
	protected LaunchDataAccessor create() {
		return new LaunchDataAccessor();
	}

	@Override
	protected LaunchEventListType createListType() {
		LaunchEventListType type = objectFactory.createLaunchEventListType();
		type.setDate(DatatypeUtil
				.toXMLGregorianCalendarDateTime(new GregorianCalendar()));
		return type;
	}

	@Override
	protected LaunchEventType createXmlType() {
		LaunchEventType type = objectFactory.createLaunchEventType();
		type.setDuration(10);
		type.setLaunchMode(LaunchMode.DEBUG);
		type.setLaunchName("name");
		type.setLaunchTime(DatatypeUtil
				.toXMLGregorianCalendarDateTime(new GregorianCalendar()));
		type.setLaunchType("type");
		return type;
	}

	@Override
	protected List<LaunchEventType> getXmlTypes(LaunchEventListType list) {
		return list.getLaunchEvent();
	}

	@Override
	protected void setId(LaunchEventType type, String id) {
		// Do nothing, unlike other types, all LaunchEventType are
		// treated unique, so there's no need to set an ID.
	}

	@Override
	protected void setUsage(LaunchEventType type, long usage) {
		type.setDuration(usage);
	}
}
