package rabbit.core.internal.storage.xml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.BeforeClass;
import org.junit.Test;

import rabbit.core.TestUtil;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;

public class DataStoreTest {

	private DataStore store = DataStore.COMMAND_STORE;

	@BeforeClass
	public static void setUp() {
		TestUtil.setUpPathForTesting();
	}

	@Test
	public void testGetStorageLocation() {
		assertNotNull(store.getStorageLocation());
	}

	@Test
	public void testGetDataFile() {
		assertNotNull(store.getDataFile(Calendar.getInstance()));
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
		files.add(store.getDataFile(lowerBound));
		files.add(store.getDataFile(upperBound));
		files.add(store.getDataFile(insideLowerBound));
		files.add(store.getDataFile(insideUpperBound));
		for (File f : files) {
			if (!f.exists() && !f.createNewFile()) {
				throw new RuntimeException();
			}
		}

		List<File> returnedFiles = store.getDataFiles(lowerBound, upperBound);
		assertEquals(files.size(), returnedFiles.size());
		for (File f : returnedFiles) {
			assertTrue(files.contains(f));
		}

		assertEquals(0, store.getDataFiles(upperBound, lowerBound).size());

		for (File f : files) {
			if (!f.delete()) {
				System.out.println("File is not deleted.");
			}
		}
		assertEquals(0, store.getDataFiles(lowerBound, upperBound).size());
	}

	@Test
	public void testRead() throws IOException {

		Calendar cal = new GregorianCalendar(1, 1, 1);
		File f = store.getDataFile(cal);

		if (f.exists()) {
			assertNotNull(store.read(f));

		} else {
			if (!f.createNewFile())
				throw new RuntimeException();

			assertNotNull(store.read(f));
			if (!f.delete())
				System.err.println("File is not deleted.");
		}
	}

	@Test
	public void testWrite() {

		File f = new File(System.getProperty("user.home") + File.separator
				+ "tmpTestFile.xml");
		assertFalse(f.exists());

		store.write(new ObjectFactory().createEventListType(), f);
		assertTrue(f.exists());
		if (!f.delete())
			System.err.println("File is not deleted.");
	}
}
