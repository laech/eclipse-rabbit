/*
 * Copyright 2010 The Rabbit Eclipse Plug-in Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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

import org.junit.Before;
import org.junit.Test;

import rabbit.core.events.DiscreteEvent;
import rabbit.core.internal.storage.xml.schema.events.EventGroupType;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;

/**
 * Test for {@link AbstractDiscreteEventStorer}
 */
public abstract class AbstractDiscreteEventStorerTest<E extends DiscreteEvent, T, S extends EventGroupType>
		extends AbstractStorerTest<E, T, S> {

	protected AbstractDiscreteEventStorer<E, T, S> storer = create();

	protected File dataFile = storer.getDataStore().getDataFile(Calendar.getInstance());

	@Before
	public void setUp() throws Exception {
		if (dataFile.exists()) {
			if (!dataFile.delete()) {
				System.err.println("File is not deleted.");
			}
		}
		getDataField(storer).clear();
	}

	@Test
	public void testAbstractXmlStorer() {
		assertNotNull(storer);
	}

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
			if (!f.createNewFile() && !f.exists()) {
				throw new RuntimeException();
			}
		}

		List<File> returnedFiles = storer.getDataStore().getDataFiles(lowerBound, upperBound);
		assertEquals(files.size(), returnedFiles.size());
		for (File f : returnedFiles) {
			assertTrue(files.contains(f));
		}

		assertEquals(0, storer.getDataStore().getDataFiles(upperBound, lowerBound).size());

		for (File f : files) {
			if (!f.delete()) {
				System.err.println("File is not deleted.");
			}
		}
		assertEquals(0, storer.getDataStore().getDataFiles(lowerBound, upperBound).size());
	}

	@Test
	public abstract void testHasSameId_typeAndEvent();

	@Test
	public abstract void testHasSameId_typeAndType();

	@Test
	public abstract void testMerge_typeAndEvent();

	@Test
	public abstract void testMerge_typeAndType();

	@Test
	public abstract void testNewXmlTypeHolderXMLGregorianCalendar();

	@Test
	public abstract void testNewXmlTypeT();

	@Test
	public void testRead() throws IOException {

		Calendar cal = new GregorianCalendar(1, 1, 1);
		File f = storer.getDataStore().getDataFile(cal);

		if (f.exists()) {
			assertNotNull(storer.getDataStore().read(f));

		} else {
			if (!f.createNewFile()) {
				throw new RuntimeException();
			}
			assertNotNull(storer.getDataStore().read(f));
			if (!f.delete()) {
				System.err.println("File is not deleted.");
			}
		}
	}

	@Test
	public void testWrite() {

		File f = new File(System.getProperty("user.home") + File.separator + "tmpTestFile.xml");
		assertFalse(f.exists());

		storer.getDataStore().write(new ObjectFactory().createEventListType(), f);
		assertTrue(f.exists());
		if (!f.delete()) {
			System.err.println("File is not deleted.");
		}
	}
	
	@Override
	protected abstract AbstractDiscreteEventStorer<E, T, S> create();

	/** Creates an event (with all values filled) for testing. */
	protected abstract E createEvent();

	/**
	 * Creates an event (with all values filled) that is different to
	 * {@link #createEvent()}.
	 */
	protected abstract E createEvent2();
}
