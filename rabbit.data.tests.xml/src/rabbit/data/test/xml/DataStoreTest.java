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
package rabbit.data.test.xml;

import rabbit.data.internal.xml.DataStore;
import rabbit.data.internal.xml.XmlPlugin;
import rabbit.data.internal.xml.schema.events.ObjectFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.IPath;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class DataStoreTest {

	private DataStore store = DataStore.PART_STORE;

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

		// Temporary test for testing files across multiple workspaces:
		Calendar end = Calendar.getInstance();
		Calendar start = (Calendar) end.clone();
		start.add(Calendar.YEAR, -1);

		List<File> result = new ArrayList<File>();
		IPath[] storagePaths = XmlPlugin.getDefault().getStoragePaths();
		Calendar date = (Calendar) start.clone();
		while (date.compareTo(end) <= 0) {

			for (IPath path : storagePaths) {
				File f = store.getDataFile(date, path);
				if (f.exists()) {
					result.add(f);
				}
			}
			date.add(Calendar.MONTH, 1);
		}
		assertEquals(result.size(), store.getDataFiles(start, end).size());
	}

	@Test
	public void testGetStorageLocation() {
		assertNotNull(store.getStorageLocation());
	}

	@Test
	public void testRead() throws IOException {

		Calendar cal = new GregorianCalendar(1, 1, 1);
		File f = store.getDataFile(cal);

		if (f.exists()) {
			assertNotNull(store.read(f));

		} else {
			if (!f.createNewFile()) {
				throw new RuntimeException();
			}

			assertNotNull(store.read(f));
			if (!f.delete()) {
				System.err.println("File is not deleted.");
			}
		}
	}

	@Test
	public void testWrite() {

		File f = new File(System.getProperty("user.home") + File.separator
				+ "tmpTestFile.xml");
		assertFalse(f.exists());

		store.write(new ObjectFactory().createEventListType(), f);
		assertTrue(f.exists());
		if (!f.delete()) {
			System.err.println("File is not deleted.");
		}
	}

	@Test(expected = NullPointerException.class)
	public void testWrite_fileNull() {
		store.write(new ObjectFactory().createEventListType(), null);
	}

	@Test(expected = NullPointerException.class)
	public void testWrite_dataNull() {
		store.write(null, new File("/"));
	}
}
