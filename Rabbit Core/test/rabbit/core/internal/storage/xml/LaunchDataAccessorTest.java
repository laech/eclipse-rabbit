package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.debug.core.ILaunchManager;

import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventListType;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventType;
import rabbit.core.storage.LaunchDescriptor;

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
				des.setLaunchTypeId(type.getLaunchTypeId());
				des.setLaunchModeId(type.getLaunchModeId());

				myData.add(des);
			}
		}

		assertEquals(myData.size(), data.size());
		myData.removeAll(data);
		assertTrue(myData.isEmpty());
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
		type.setLaunchModeId(ILaunchManager.RUN_MODE);
		type.setLaunchName("name");
		type.setLaunchTime(DatatypeUtil
				.toXMLGregorianCalendarDateTime(new GregorianCalendar()));
		type.setLaunchTypeId("type");
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
