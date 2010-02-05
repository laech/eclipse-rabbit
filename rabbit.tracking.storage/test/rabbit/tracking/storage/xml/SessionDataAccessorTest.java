package rabbit.tracking.storage.xml;

import static rabbit.tracking.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Assert;
import org.junit.Test;

import rabbit.tracking.storage.xml.SessionDataAccessor;
import rabbit.tracking.storage.xml.internal.DataStore;
import rabbit.tracking.storage.xml.schema.EventListType;
import rabbit.tracking.storage.xml.schema.ObjectFactory;
import rabbit.tracking.storage.xml.schema.PartEventListType;
import rabbit.tracking.storage.xml.schema.PartEventType;

/**
 * Test for {@link SessionDataAccessor}
 */
public class SessionDataAccessorTest {

	private ObjectFactory objectFactory = new ObjectFactory();
	private SessionDataAccessor accessor = new SessionDataAccessor();

	@Test public void testGetData() {
		// Test two part events with different id but same date, should return
		// only one combined entry in the map

		// 1:

		String id1 = "qfnnvkfde877thfg";
		int count1 = 298;
		PartEventType type1 = objectFactory.createPartEventType();
		type1.setPartId(id1);
		type1.setDuration(count1);

		// 2:

		String id2 = "23jfno084";
		int count2 = 22817;
		PartEventType type2 = objectFactory.createPartEventType();
		type2.setPartId(id2);
		type2.setDuration(count2);

		PartEventListType list = objectFactory.createPartEventListType();
		Calendar tmp = Calendar.getInstance();
		tmp.set(Calendar.DAY_OF_MONTH, 1);
		XMLGregorianCalendar start = toXMLGregorianCalendarDate(tmp);
		list.setDate(start);
		list.getPartEvent().add(type1);
		list.getPartEvent().add(type2);

		EventListType events = objectFactory.createEventListType();
		events.getPartEvents().add(list);

		File f = DataStore.PART_STORE.getDataFile(tmp);
		DataStore.PART_STORE.write(events, f);
		Map<String, Long> data = accessor.getData(tmp, tmp);
		Assert.assertEquals(1, data.size());
		Assert.assertEquals(new SimpleDateFormat("yyyy-MM-dd").format(tmp.getTime()), data.entrySet().iterator().next().getKey());
		Assert.assertEquals(count1 + count2, data.entrySet().iterator().next().getValue().longValue());
	}

}
