package rabbit.tracking.storage.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static rabbit.tracking.storage.xml.AbstractXmlStorer.toXMLGregorianCalendarDate;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.eclipse.jface.preference.IPreferenceStore;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.tracking.event.DiscreteEvent;
import rabbit.tracking.storage.xml.schema.EventGroupType;

public abstract class AbstractXmlStorerTest<E extends DiscreteEvent, T, S extends EventGroupType> {
	
	private AbstractXmlStorer<E, T, S> storer = create();

	protected abstract AbstractXmlStorer<E, T, S> create();
	
	protected File dataFile = storer.getDataStore().getDataFile(Calendar.getInstance());
	
	@SuppressWarnings("unchecked")
	protected Collection<S> getDataField(AbstractXmlStorer<E, T, S> s) throws Exception {
		Field f = AbstractXmlStorer.class.getDeclaredField("data");
		f.setAccessible(true);
		return (Collection<S>) f.get(s);
	}
	
	@BeforeClass
	public static void setUp() {
		String path = StoragePlugin.getDefault().getStoragePath().toOSString();
		path += File.separator;
		path += "TestFiles";
		IPreferenceStore pre = StoragePlugin.getDefault().getPreferenceStore();
		pre.setValue(StoragePlugin.STORAGE_LOCATION, path);
	}
	
	@Before
	public void cleanup() {
		if (dataFile.exists()) {
			dataFile.delete();
		}
	}
	
	@Test
	public void testAbstractXmlStorer() {
		assertNotNull(storer);
	}

	@Test
	public abstract void testCommit();

	@Test
	public void testGetDataFile() {
		assertNotNull(storer.getDataStore().getDataFile(Calendar.getInstance()));
	}

	@Test
	public void testGetDataFiles() throws IOException {
		
		Calendar lowerBound = new GregorianCalendar(1, 1, 1);
		Calendar upperBound = new GregorianCalendar(3, 1, 1);
		
		Calendar insideLowerBound = (Calendar) lowerBound.clone();
		insideLowerBound.add(Calendar.MONTH, 1);
		
		Calendar insideUpperBound = (Calendar) upperBound.clone();
		insideUpperBound.add(Calendar.MONTH, -1);
		
		Set<File> files = new HashSet<File>();
		files.add(storer.getDataStore().getDataFile(lowerBound));
		files.add(storer.getDataStore().getDataFile(upperBound));
		files.add(storer.getDataStore().getDataFile(insideLowerBound));
		files.add(storer.getDataStore().getDataFile(insideUpperBound));
		for (File f : files) {
			f.createNewFile();
		}
		
		List<File> returnedFiles = storer.getDataStore().getDataFiles(lowerBound, upperBound);
		assertEquals(files.size(), returnedFiles.size());
		for (File f : returnedFiles) {
			assertTrue(files.contains(f));
		}
		
		assertEquals(0, storer.getDataStore().getDataFiles(upperBound, lowerBound).size());
		
		for (File f : files) {
			f.delete();
		}
		assertEquals(0, storer.getDataStore().getDataFiles(lowerBound, upperBound).size());
	}

	@Test
	public void testGetXmlTypeCategories() {
		
		assertNotNull(storer.getXmlTypeCategories(
				AbstractXmlStorer.OBJECT_FACTORY.createEventListType()));
	}

	@Test
	public abstract void testInsertCollection();

	@Test
	public abstract void testInsert();

	@Test
	public void testIsSameDate() {
		
		try {
			Calendar cal = Calendar.getInstance();

			XMLGregorianCalendar xmlCal = DatatypeFactory.newInstance()
			.newXMLGregorianCalendarDate(1, 1, 1, 1);
			assertFalse(AbstractXmlStorer.isSameDate(cal, xmlCal));

			xmlCal = AbstractXmlStorer.toXMLGregorianCalendarDate(cal);
			assertTrue(AbstractXmlStorer.isSameDate(cal, xmlCal));
		
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testIsSameMonthInYear() throws DatatypeConfigurationException {

		Calendar cal1 = Calendar.getInstance();
		Calendar cal2 = Calendar.getInstance();
		assertTrue(AbstractXmlStorer.isSameMonthInYear(cal1, cal2));
		
		cal2.add(Calendar.MONTH, 1);
		assertFalse(AbstractXmlStorer.isSameMonthInYear(cal1, cal2));
	}

	@Test
	public void testNewXmlTypeHolder() {
		
		assertNotNull(storer.newXmlTypeHolder(AbstractXmlStorer
				.toXMLGregorianCalendarDate(Calendar.getInstance())));
	}

	@Test
	public void testRead() throws IOException {
		
		Calendar cal = new GregorianCalendar(1, 1, 1);
		File f = storer.getDataStore().getDataFile(cal);
		
		if (f.exists()) {
			assertNotNull(storer.getDataStore().read(f));
			
		} else {
			f.createNewFile();
			assertNotNull(storer.getDataStore().read(f));
			f.delete();
		}
	}

	@Test
	public void testToXMLGregorianCalendarDate() {
		
		Calendar cal = Calendar.getInstance();
		XMLGregorianCalendar xmlCal = toXMLGregorianCalendarDate(cal);
		
		assertEquals(cal.get(Calendar.YEAR), xmlCal.getYear());
		// Calendar.MONTH is zero based.
		assertEquals(cal.get(Calendar.MONTH) + 1, xmlCal.getMonth());
		assertEquals(cal.get(Calendar.DAY_OF_MONTH), xmlCal.getDay());
	}

	@Test
	public void testWrite() {
		
		File f = new File(System.getProperty("user.home") + File.separator + "tmpTestFile.xml");
		assertFalse(f.exists());
		
		storer.getDataStore().write(AbstractXmlStorer.OBJECT_FACTORY.createEventListType(), f);
		assertTrue(f.exists());
		f.delete();
	}
	
	@Test
	public abstract void testHasSameId_workbenchEventTypeAndEvent();
	@Test
	public abstract void testHasSameId_workbenchEventTypeAndWorkbenchEventType();
	@Test
	public abstract void testMerge_xmlListTypeAndEvent();
	@Test
	public abstract void testMerge_xmlListTypeAndxmlListType();
	@Test
	public abstract void testMerge_xmlTypeAndEvent();
	@Test
	public abstract void testMerge_xmlTypeAndXmlType();
	@Test
	public abstract void testNewXmlTypeHolderXMLGregorianCalendar();
	@Test
	public abstract void testNewXmlTypeT();
	
	protected abstract E createEvent();

}
