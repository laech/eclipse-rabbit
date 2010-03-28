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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.internal.registry.ConfigurationElement;
import org.eclipse.core.internal.registry.ConfigurationElementHandle;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.Launch;
import org.eclipse.debug.internal.core.LaunchConfiguration;
import org.eclipse.debug.internal.core.LaunchConfigurationType;
import org.junit.Before;
import org.junit.Test;

import rabbit.core.events.LaunchEvent;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventListType;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventType;
import rabbit.core.internal.storage.xml.schema.events.LaunchMode;

/**
 * @see LaunchEventStorer
 */
@SuppressWarnings("restriction")
public class LaunchEventStorerTest extends
		AbstractStorerTest<LaunchEvent, LaunchEventType, LaunchEventListType> {

	protected LaunchEventStorer storer = create();

	@Before
	public void before() throws Exception {
		getDataField(storer).clear();
	}

	@Override
	protected LaunchEventStorer create() {
		return LaunchEventStorer.getInstance();
	}

	private static class LaunchConfigurationForTest extends LaunchConfiguration {

		private ILaunchConfigurationType type = new LaunchConfigurationTypeForTest();

		protected LaunchConfigurationForTest() {
			super("Abc", null);
		}

		@Override
		public ILaunchConfigurationType getType() throws CoreException {
			return type;
		}
	}

	private static class LaunchConfigurationTypeForTest extends LaunchConfigurationType {

		public LaunchConfigurationTypeForTest() {
			super(new ConfigurationElementForTest());
		}

		@Override
		public String getName() {
			return "MyType";
		}
	}
	
	private static class ConfigurationElementForTest extends ConfigurationElementHandle {

		public ConfigurationElementForTest() {
			super(null, 0);
		}
	
		@Override
		protected ConfigurationElement getConfigurationElement() {
			return null;
		}
		
		@Override
		public String getAttribute(String propertyName) {
			return null;
		}
	}

	@Test
	public void testNewXmlType() throws CoreException {
		Calendar time = new GregorianCalendar();
		long duration = 9823;
		ILaunchConfiguration config = new LaunchConfigurationForTest();
		ILaunch launch = new Launch(config, ILaunchManager.DEBUG_MODE, null);
		Set<String> fileIds = new HashSet<String>();
		fileIds.add("abc");
		fileIds.add("def");

		LaunchEvent event = new LaunchEvent(time, duration, launch, config, fileIds);
		LaunchEventType type = storer.newXmlType(event);

		assertEquals(time.getTime(), type.getLaunchTime().toGregorianCalendar().getTime());
		assertEquals(duration, type.getDuration());
		assertEquals(config.getType().getName(), type.getLaunchType());
		assertEquals(LaunchMode.DEBUG, type.getLaunchMode());
		assertEquals(config.getName(), type.getLaunchName());
		assertEquals(fileIds.size(), type.getFileId().size());
		fileIds.removeAll(type.getFileId());
		assertTrue(fileIds.isEmpty());
	}

	@Override
	public void testCommit() throws Exception {
		IDataStore dataStore = storer.getDataStore();
		
		Calendar time1 = new GregorianCalendar();
		time1.add(Calendar.YEAR, -1);
		File file1 = dataStore.getDataFile(time1);
		if (file1.exists() && !file1.delete()) {
			fail("File is not deleted");
		}
		
		Calendar time2 = new GregorianCalendar();
		time2.add(Calendar.YEAR, 1);
		File file2 = dataStore.getDataFile(time2);
		if (file2.exists() && !file2.delete()) {
			fail("File is not deleted");
		}
		
		long duration = 198123;
		ILaunchConfiguration config = new LaunchConfigurationForTest();
		ILaunch launch = new Launch(config, ILaunchManager.RUN_MODE, null);
		Set<String> fileIds = new HashSet<String>();
		fileIds.add("123111");
		fileIds.add("456111");

		LaunchEvent event1 = new LaunchEvent(time1, duration, launch, config, fileIds);
		LaunchEvent event2 = new LaunchEvent(time2, duration, launch, config, fileIds);
		storer.insert(event1);
		storer.insert(event2);

		storer.commit();

		// Empty after commit.
		assertTrue(getDataField(storer).isEmpty());

		// Test event1:
		List<LaunchEventListType> lists = dataStore.read(file1)
				.getLaunchEvents();
		assertEquals(1, lists.size());

		LaunchEventListType list = lists.iterator().next();
		assertEquals(DatatypeUtil.toXMLGregorianCalendarDate(time1), list.getDate());
		assertEquals(1, list.getLaunchEvent().size());

		LaunchEventType type = list.getLaunchEvent().iterator().next();
		assertEquals(time1.getTime(), type.getLaunchTime().toGregorianCalendar().getTime());
		assertEquals(duration, type.getDuration());
		assertEquals(config.getType().getName(), type.getLaunchType());
		assertEquals(LaunchMode.RUN, type.getLaunchMode());
		assertEquals(config.getName(), type.getLaunchName());
		assertEquals(fileIds.size(), type.getFileId().size());
		for (String str : type.getFileId()) {
			assertTrue(fileIds.contains(str));
		}

		// Test event2:
		lists = dataStore.read(file2).getLaunchEvents();
		assertEquals(1, lists.size());

		list = lists.iterator().next();
		assertEquals(DatatypeUtil.toXMLGregorianCalendarDate(time2), list.getDate());
		assertEquals(1, list.getLaunchEvent().size());

		type = list.getLaunchEvent().iterator().next();
		assertEquals(time2.getTime(), type.getLaunchTime().toGregorianCalendar().getTime());
		assertEquals(duration, type.getDuration());
		assertEquals(config.getType().getName(), type.getLaunchType());
		assertEquals(LaunchMode.RUN, type.getLaunchMode());
		assertEquals(config.getName(), type.getLaunchName());
		assertEquals(fileIds.size(), type.getFileId().size());
		for (String str : type.getFileId()) {
			assertTrue(fileIds.contains(str));
		}
	}

	@Override
	public void testInsert() throws Exception {
		Calendar time = new GregorianCalendar();
		long duration = 198123;
		ILaunchConfiguration config = new LaunchConfigurationForTest();
		ILaunch launch = new Launch(config, ILaunchManager.RUN_MODE, null);
		Set<String> fileIds = new HashSet<String>();
		fileIds.add("123111");
		fileIds.add("456111");

		LaunchEvent event = new LaunchEvent(time, duration, launch, config, fileIds);
		storer.insert(event);
		Collection<LaunchEventListType> data = getDataField(storer);

		// One list for this month:
		assertEquals(1, data.size());

		// Same date:
		LaunchEventListType list = data.iterator().next();
		assertEquals(DatatypeUtil.toXMLGregorianCalendarDate(time), list.getDate());

		List<LaunchEventType> types = list.getLaunchEvent();
		assertEquals(1, types.size());

		LaunchEventType type = types.iterator().next();
		assertEquals(time.getTime(), type.getLaunchTime().toGregorianCalendar().getTime());
		assertEquals(duration, type.getDuration());
		assertEquals(config.getType().getName(), type.getLaunchType());
		assertEquals(LaunchMode.RUN, type.getLaunchMode());
		assertEquals(config.getName(), type.getLaunchName());
		assertEquals(fileIds.size(), type.getFileId().size());
		fileIds.removeAll(type.getFileId());
		assertTrue(fileIds.isEmpty());
	}

	@Override
	public void testInsertCollection() throws Exception {
		Calendar time = new GregorianCalendar();
		long duration = 19823;
		ILaunchConfiguration config = new LaunchConfigurationForTest();
		ILaunch launch = new Launch(config, ILaunchManager.RUN_MODE, null);
		Set<String> fileIds = new HashSet<String>();
		fileIds.add("123");
		fileIds.add("456");

		Set<LaunchEvent> events = new HashSet<LaunchEvent>();
		for (int i = 0; i < 100; i++)
			events.add(new LaunchEvent(time, duration, launch, config, fileIds));

		storer.insert(events);
		Collection<LaunchEventListType> data = getDataField(storer);

		// One list for this month:
		assertEquals(1, data.size());

		// Same date:
		LaunchEventListType list = data.iterator().next();
		assertEquals(DatatypeUtil.toXMLGregorianCalendarDate(time), list.getDate());

		List<LaunchEventType> types = list.getLaunchEvent();
		assertEquals(events.size(), types.size());
		for (LaunchEventType type : types) {
			assertEquals(time.getTime(), type.getLaunchTime().toGregorianCalendar().getTime());
			assertEquals(duration, type.getDuration());
			assertEquals(config.getType().getName(), type.getLaunchType());
			assertEquals(LaunchMode.RUN, type.getLaunchMode());
			assertEquals(config.getName(), type.getLaunchName());
			assertEquals(fileIds.size(), type.getFileId().size());
			for (String str : type.getFileId()) {
				assertTrue(fileIds.contains(str));
			}
		}
	}

	@Override
	public void testMerge_listOfXmlTypesAndEvent() throws Exception {
		Calendar time = new GregorianCalendar();
		long duration = 19823;
		ILaunchConfiguration config = new LaunchConfigurationForTest();
		ILaunch launch = new Launch(config, ILaunchManager.RUN_MODE, null);
		Set<String> fileIds = new HashSet<String>();
		fileIds.add("12311");
		fileIds.add("456111111");
		
		LaunchEvent event = new LaunchEvent(time, duration, launch, config, fileIds);
		List<LaunchEventType> list = new ArrayList<LaunchEventType>();
		
		storer.merge(list, event);
		assertEquals(1, list.size());
		LaunchEventType type = list.get(0);
		assertEquals(time.getTime(), type.getLaunchTime().toGregorianCalendar().getTime());
		assertEquals(duration, type.getDuration());
		assertEquals(config.getType().getName(), type.getLaunchType());
		assertEquals(LaunchMode.RUN, type.getLaunchMode());
		assertEquals(config.getName(), type.getLaunchName());
		assertEquals(fileIds.size(), type.getFileId().size());
		for (String str : type.getFileId()) {
			assertTrue(fileIds.contains(str));
		}
		
		// Repeat, now the list should contain 2 objects.
		storer.merge(list, event);
		assertEquals(2, list.size()); //
		type = list.get(1); //
		assertEquals(time.getTime(), type.getLaunchTime().toGregorianCalendar().getTime());
		assertEquals(duration, type.getDuration());
		assertEquals(config.getType().getName(), type.getLaunchType());
		assertEquals(LaunchMode.RUN, type.getLaunchMode());
		assertEquals(config.getName(), type.getLaunchName());
		assertEquals(fileIds.size(), type.getFileId().size());
		for (String str : type.getFileId()) {
			assertTrue(fileIds.contains(str));
		}
	}

	@Override
	public void testMerge_listOfXmlTypesAndListOfXmlTypes() {
		List<LaunchEventType> list1 = new ArrayList<LaunchEventType>();
		List<LaunchEventType> list2 = new ArrayList<LaunchEventType>();
		
		LaunchEventType type = objectFactory.createLaunchEventType();
		type.setDuration(101);
		type.setLaunchMode(LaunchMode.PROFILE);
		type.setLaunchName("Name");
		type.setLaunchTime(DatatypeUtil.toXMLGregorianCalendarDateTime(new GregorianCalendar()));
		type.setLaunchType("Type");
		list2.add(type);
		
		storer.merge(list1, list2);
		assertEquals(1, list1.size());
		assertEquals(type, list1.get(0));
		
		// Repeat, now list1 should have two objects:
		storer.merge(list1, list2);
		assertEquals(2, list1.size());
		assertEquals(type, list1.get(0));
		assertEquals(type, list1.get(1));
	}
}
