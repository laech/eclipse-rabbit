package rabbit.core.storage.xml;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.core.TestUtil;
import rabbit.core.internal.storage.xml.DataStore;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;
import static rabbit.core.internal.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

/**
 * Test for {@link SessionDataAccessor}
 */
public class SessionDataAccessorTest {

	private ObjectFactory objectFactory = new ObjectFactory();
	private SessionDataAccessor accessor = new SessionDataAccessor();

	@BeforeClass
	public static void setUpBeforeClass() {
		TestUtil.setUpPathForTesting();
	}

	@Test
	public void testGetData() {
		// Test two part events with different id but in same list, should
		// return
		// only one combined entry in the map

		// 1:

		String id1 = "qfnnvkfde877thfg";
		int count1 = 298;
		PerspectiveEventType type1 = objectFactory.createPerspectiveEventType();
		type1.setPerspectiveId(id1);
		type1.setDuration(count1);

		// 2:

		String id2 = "23jfno084";
		int count2 = 22817;
		PerspectiveEventType type2 = objectFactory.createPerspectiveEventType();
		type2.setPerspectiveId(id2);
		type2.setDuration(count2);

		PerspectiveEventListType list = objectFactory.createPerspectiveEventListType();
		Calendar tmp = Calendar.getInstance();
		tmp.set(Calendar.DAY_OF_MONTH, 1);
		XMLGregorianCalendar start = toXMLGregorianCalendarDate(tmp);
		list.setDate(start);
		list.getPerspectiveEvent().add(type1);
		list.getPerspectiveEvent().add(type2);

		PerspectiveEventListType list2 = objectFactory.createPerspectiveEventListType();
		list2.setDate(toXMLGregorianCalendarDate(tmp));
		list2.getPerspectiveEvent().add(type2);

		EventListType events = objectFactory.createEventListType();
		events.getPerspectiveEvents().add(list);

		File f = DataStore.PERSPECTIVE_STORE.getDataFile(tmp);
		DataStore.PERSPECTIVE_STORE.write(events, f);
		Map<String, Long> data = accessor.getData(tmp, tmp);
		Assert.assertEquals(1, data.size());
		Assert.assertEquals(new SimpleDateFormat(SessionDataAccessor.DATE_FORMAT).format(tmp.getTime()), data.entrySet().iterator().next().getKey());
		Assert.assertEquals(count1 + count2, data.entrySet().iterator().next().getValue().longValue());
	}

	/**
	 * Tests that two lists with the same date are stored, then getting the data
	 * out should return the combined data. Note that although two lists with
	 * the same date should not have happened.
	 */
	@Test
	public void testGetData_twoListsWithSameDate() {

		Calendar date = Calendar.getInstance();

		// An event
		String id1 = "qfnnvkfde877thfg";
		int count1 = 23498;
		PerspectiveEventType type1 = objectFactory.createPerspectiveEventType();
		type1.setPerspectiveId(id1);
		type1.setDuration(count1);

		// Another event
		String id2 = "23jfno084";
		int count2 = 222817;
		PerspectiveEventType type2 = objectFactory.createPerspectiveEventType();
		type2.setPerspectiveId(id2);
		type2.setDuration(count2);

		// List 1
		PerspectiveEventListType list = objectFactory.createPerspectiveEventListType();
		XMLGregorianCalendar start = toXMLGregorianCalendarDate(date);
		list.setDate(start);
		list.getPerspectiveEvent().add(type1);

		// List 2
		PerspectiveEventListType list2 = objectFactory.createPerspectiveEventListType();
		list2.setDate(toXMLGregorianCalendarDate(date));
		list2.getPerspectiveEvent().add(type2);

		EventListType events = objectFactory.createEventListType();
		events.getPerspectiveEvents().add(list);
		events.getPerspectiveEvents().add(list2);

		File f = DataStore.PERSPECTIVE_STORE.getDataFile(date);
		DataStore.PERSPECTIVE_STORE.write(events, f);
		Map<String, Long> data = accessor.getData(date, date);
		Assert.assertEquals(1, data.size());
		Assert.assertEquals(new SimpleDateFormat(SessionDataAccessor.DATE_FORMAT).format(date.getTime()), data.entrySet().iterator().next().getKey());
		Assert.assertEquals(count1 + count2, data.entrySet().iterator().next().getValue().longValue());
	}
}
