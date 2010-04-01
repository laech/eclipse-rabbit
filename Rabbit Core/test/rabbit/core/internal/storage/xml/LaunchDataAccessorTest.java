package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static rabbit.core.internal.util.StringUtil.getString;

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

				boolean done = false;

				for (LaunchDescriptor des : myData) {
					if (getString(type.getName()).equals(des.getLaunchName())
							&& getString(type.getLaunchModeId()).equals(des.getLaunchModeId())
							&& getString(type.getLaunchTypeId()).equals(des.getLaunchTypeId())) {

						des.setCount(des.getCount() + type.getCount());
						des.setTotalDuration(des.getTotalDuration() + type.getTotalDuration());
						des.getFileIds().addAll(type.getFileId());
						
						done = true;
						break;
					}
				}

				if (!done) {
					LaunchDescriptor des = new LaunchDescriptor();
					des.setCount(type.getCount());
					des.setTotalDuration(type.getTotalDuration());
					des.getFileIds().addAll(type.getFileId());
					des.setLaunchName(type.getName());
					des.setLaunchTypeId(type.getLaunchTypeId());
					des.setLaunchModeId(type.getLaunchModeId());

					myData.add(des);
				}
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
		type.setTotalDuration(10);
		type.setLaunchModeId(ILaunchManager.RUN_MODE);
		type.setName("name");
		type.setLaunchTypeId("type");
		type.setCount(1);
		return type;
	}

	@Override
	protected List<LaunchEventType> getXmlTypes(LaunchEventListType list) {
		return list.getLaunchEvent();
	}

	@Override
	protected void setId(LaunchEventType type, String id) {
		type.setLaunchModeId(id);
		type.setLaunchTypeId(id);
		type.setName(id);
	}

	@Override
	protected void setUsage(LaunchEventType type, long usage) {
		type.setTotalDuration(usage);
	}
}
