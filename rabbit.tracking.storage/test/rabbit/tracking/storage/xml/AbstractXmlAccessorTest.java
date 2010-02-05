package rabbit.tracking.storage.xml;

import static org.junit.Assert.assertNotNull;
import static rabbit.tracking.storage.xml.DatatypeConverter.toXMLGregorianCalendarDate;

import java.io.File;
import java.util.Calendar;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.tracking.storage.xml.AbstractXmlAccessor;
import rabbit.tracking.storage.xml.internal.StoragePlugin;
import rabbit.tracking.storage.xml.schema.EventGroupType;
import rabbit.tracking.storage.xml.schema.EventListType;
import rabbit.tracking.storage.xml.schema.ObjectFactory;

/**
 * Test for {@link AbstractXmlAccessor}
 */
public abstract class AbstractXmlAccessorTest<T, S extends EventGroupType> {

	protected AbstractXmlAccessor<T, S> accessor = create();
	protected ObjectFactory objectFactory = new ObjectFactory();

	@BeforeClass public static void setUpBeforeClass() throws Exception {
		String path = StoragePlugin.getDefault().getStoragePath().toOSString();
		path += File.separator;
		path += "TestFiles2";
		IPreferenceStore pre = StoragePlugin.getDefault().getPreferenceStore();
		pre.setValue(StoragePlugin.STORAGE_LOCATION, path);
	}

	@Test public void testGetDataStore() {
		assertNotNull(accessor.getDataStore());
	}

	@Test public void testGetData() {
		String id = "qfnnvkfde877thfg";

		// 1:

		int count1 = 298;
		T type1 = createXmlType();
		setId(type1, id);
		setUsage(type1, count1);

		S list1 = createListType();
		Calendar tmp = Calendar.getInstance();
		tmp.set(Calendar.DAY_OF_MONTH, 1);
		XMLGregorianCalendar start = toXMLGregorianCalendarDate(tmp);
		list1.setDate(start);
		accessor.getXmlTypes(list1).add(type1);

		// 2:

		int count2 = 22817;
		T type2 = createXmlType();
		setId(type2, id);
		setUsage(type2, count2);

		S list2 = createListType();
		tmp.set(Calendar.DAY_OF_MONTH, 3);
		XMLGregorianCalendar end = toXMLGregorianCalendarDate(tmp);
		list2.setDate(end);
		accessor.getXmlTypes(list2).add(type2);

		EventListType events = objectFactory.createEventListType();
		accessor.getCategories(events).add(list1);
		accessor.getCategories(events).add(list2);

		File f = accessor.getDataStore().getDataFile(tmp);
		accessor.getDataStore().write(events, f);
		Map<String, Long> data = accessor.getData(start.toGregorianCalendar(), end.toGregorianCalendar());
		Assert.assertEquals(1, data.size());
		Assert.assertEquals(id, data.entrySet().iterator().next().getKey());
		Assert.assertEquals(count1 + count2, data.entrySet().iterator().next().getValue().longValue());
	}

	@Test public void testGetXmlTypes() {
		int size = 5;
		S list = createListType();
		for (int i = 0; i < size; i++) {
			accessor.getXmlTypes(list).add(createXmlType());
		}
		Assert.assertEquals(size, accessor.getXmlTypes(list).size());
	}

	@Test public void testGetCategories() {
		EventListType eventList = objectFactory.createEventListType();
		S list = createListType();
		accessor.getCategories(eventList).add(list);
		accessor.getXmlTypes(list).add(createXmlType());
		Assert.assertEquals(1, accessor.getCategories(eventList).size());
	}

	@Test public void testGetUsage() {
		long usage = 100193;
		T type = createXmlType();
		setUsage(type, usage);
		Assert.assertEquals(usage, accessor.getUsage(type));
	}

	@Test public void testGetId() {
		String id = "2983jncjdkf";
		T type = createXmlType();
		setId(type, id);
		Assert.assertEquals(id, accessor.getId(type));
	}

	/** Creates a subject for testing. */
	protected abstract AbstractXmlAccessor<T, S> create();

	/**
	 * Creates a new list type.
	 * 
	 * @return A new list type.
	 */
	protected abstract S createListType();

	/**
	 * Sets the usage of the type.
	 * 
	 * @param type The type.
	 * @param usage The usage.
	 */
	protected abstract void setUsage(T type, long usage);

	/**
	 * Sets the id of the type.
	 * 
	 * @param type The type.
	 * @param id The id.
	 */
	protected abstract void setId(T type, String id);

	/**
	 * Creates a new XML type.
	 * 
	 * @return A new XML type.
	 */
	protected abstract T createXmlType();
}
