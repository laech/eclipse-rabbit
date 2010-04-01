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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
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

import rabbit.core.events.LaunchEvent;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventListType;
import rabbit.core.internal.storage.xml.schema.events.LaunchEventType;

/**
 * @see LaunchEventStorer
 */
@SuppressWarnings("restriction")
public class LaunchEventStorerTest extends
		AbstractDiscreteEventStorerTest<LaunchEvent, LaunchEventType, LaunchEventListType> {

	// Empty class for testing.
	private static class ConfigurationElementForTest extends ConfigurationElementHandle {

		public ConfigurationElementForTest() {
			super(null, 0);
		}
	
		@Override
		public String getAttribute(String propertyName) {
			return null;
		}
		
		@Override
		protected ConfigurationElement getConfigurationElement() {
			return null;
		}
	}

	// Empty class for testing.
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

	// Empty class for testing.
	private static class LaunchConfigurationTypeForTest extends LaunchConfigurationType {

		public LaunchConfigurationTypeForTest() {
			super(new ConfigurationElementForTest());
		}

		@Override
		public String getName() {
			return "MyType";
		}
		
		@Override
		public String getIdentifier() {
			return "MyTypeIdentifier";
		}
	}

	protected LaunchEventStorer storer = create();

	@Before
	public void before() throws Exception {
		getDataField(storer).clear();
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
		assertEquals(duration, type.getTotalDuration());
		assertEquals(1, type.getCount());
		assertEquals(config.getType().getIdentifier(), type.getLaunchTypeId());
		assertEquals(ILaunchManager.RUN_MODE, type.getLaunchModeId());
		assertEquals(config.getName(), type.getName());
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
		assertEquals(duration, type.getTotalDuration());
		assertEquals(1, type.getCount());
		assertEquals(config.getType().getIdentifier(), type.getLaunchTypeId());
		assertEquals(ILaunchManager.RUN_MODE, type.getLaunchModeId());
		assertEquals(config.getName(), type.getName());
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
		assertEquals(duration, type.getTotalDuration());
		assertEquals(1, type.getCount());
		assertEquals(config.getType().getIdentifier(), type.getLaunchTypeId());
		assertEquals(ILaunchManager.RUN_MODE, type.getLaunchModeId());
		assertEquals(config.getName(), type.getName());
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

		// Insert 100 identical events, then the all of them should be merged
		// after inserting into the storer:
		int numEvents = 100;
		Set<LaunchEvent> events = new HashSet<LaunchEvent>();
		for (int i = 0; i < numEvents; i++)
			events.add(new LaunchEvent(time, duration, launch, config, fileIds));

		storer.insert(events);
		Collection<LaunchEventListType> data = getDataField(storer);

		// One list for this month:
		assertEquals(1, data.size());

		// Same date:
		LaunchEventListType list = data.iterator().next();
		assertEquals(DatatypeUtil.toXMLGregorianCalendarDate(time), list.getDate());

		List<LaunchEventType> types = list.getLaunchEvent();
		assertEquals(1, types.size());
		
		LaunchEventType type = types.get(0);
		assertEquals(numEvents, type.getCount());
		assertEquals(duration * numEvents, type.getTotalDuration());
		assertEquals(config.getType().getIdentifier(), type.getLaunchTypeId());
		assertEquals(ILaunchManager.RUN_MODE, type.getLaunchModeId());
		assertEquals(config.getName(), type.getName());
		assertEquals(fileIds.size(), type.getFileId().size());
		for (String str : type.getFileId()) {
			assertTrue(fileIds.contains(str));
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
		assertEquals(duration, type.getTotalDuration());
		assertEquals(config.getType().getIdentifier(), type.getLaunchTypeId());
		assertEquals(ILaunchManager.RUN_MODE, type.getLaunchModeId());
		assertEquals(config.getName(), type.getName());
		assertEquals(fileIds.size(), type.getFileId().size());
		for (String str : type.getFileId()) {
			assertTrue(fileIds.contains(str));
		}
		
		// Repeat, now the event should be merged with the existing type:
		storer.merge(list, event);
		assertEquals(1, list.size()); //
		type = list.get(0); //
		assertEquals(duration * 2, type.getTotalDuration());
		assertEquals(config.getType().getIdentifier(), type.getLaunchTypeId());
		assertEquals(ILaunchManager.RUN_MODE, type.getLaunchModeId());
		assertEquals(config.getName(), type.getName());
		assertEquals(fileIds.size(), type.getFileId().size());
		for (String str : type.getFileId()) {
			assertTrue(fileIds.contains(str));
		}
	}

	@Override
	public void testMerge_listOfXmlTypesAndListOfXmlTypes() {
		List<LaunchEventType> list1 = new ArrayList<LaunchEventType>();
		List<LaunchEventType> list2 = new ArrayList<LaunchEventType>();
		long duration = 192;
		int count = 2;
		String name = "Name";
		String typeId = "Type";
		String modeId = "run";
		
		Set<String> fileIds1 = new HashSet<String>(Arrays.asList("1"));
		Set<String> fileIds2 = new HashSet<String>(Arrays.asList("1", "2"));
		
		// Object 1:
		LaunchEventType type1 = objectFactory.createLaunchEventType();
		type1.setTotalDuration(duration);
		type1.setLaunchModeId(modeId);
		type1.setName(name);
		type1.setLaunchTypeId(typeId);
		type1.setCount(count);
		type1.getFileId().addAll(fileIds1);
		list2.add(type1);
		
		// Check:
		storer.merge(list1, list2);
		assertEquals(1, list1.size());
		assertEquals(type1, list1.get(0));
		
		list2.clear();
		// Object 2:
		LaunchEventType type2 = objectFactory.createLaunchEventType();
		type2.setTotalDuration(duration);
		type2.setLaunchModeId(modeId);
		type2.setName(name);
		type2.setLaunchTypeId(typeId);
		type2.setCount(count);
		type2.getFileId().addAll(fileIds2);
		list2.add(type2);
		
		// Repeat, two objects should be merged into one:
		storer.merge(list1, list2);
		assertEquals(1, list1.size());
		LaunchEventType type = list1.get(0);
		assertEquals(name, type.getName());
		assertEquals(modeId, type.getLaunchModeId());
		assertEquals(typeId, type.getLaunchTypeId());
		assertEquals(count * 2, type.getCount());
		assertEquals(duration * 2, type.getTotalDuration());
		fileIds1.addAll(fileIds2);
		assertEquals(fileIds1.size(), type.getFileId().size());
		assertTrue(type.getFileId().containsAll(fileIds1));
		assertTrue(type.getFileId().containsAll(fileIds2));
	}

	@Override
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

		assertEquals(1, type.getCount());
		assertEquals(duration, type.getTotalDuration());
		assertEquals(config.getType().getIdentifier(), type.getLaunchTypeId());
		assertEquals(ILaunchManager.DEBUG_MODE, type.getLaunchModeId());
		assertEquals(config.getName(), type.getName());
		assertEquals(fileIds.size(), type.getFileId().size());
		fileIds.removeAll(type.getFileId());
		assertTrue(fileIds.isEmpty());
	}

	@Override
	protected LaunchEventStorer create() {
		return LaunchEventStorer.getInstance();
	}

	@Override
	protected LaunchEvent createEvent() {
		Calendar time = new GregorianCalendar();
		long duration = 19823;
		ILaunchConfiguration config = new LaunchConfigurationForTest();
		ILaunch launch = new Launch(config, ILaunchManager.DEBUG_MODE, null);
		Set<String> fileIds = new HashSet<String>();
		fileIds.add("ab1c");
		fileIds.add("d1ef");
		
		return new LaunchEvent(time, duration, launch, config, fileIds);
	}

	@Override
	protected LaunchEvent createEvent2() {
		Calendar time = new GregorianCalendar(1999, 1, 1);
		long duration = 119823;
		ILaunchConfiguration config = new LaunchConfigurationForTest();
		ILaunch launch = new Launch(config, ILaunchManager.PROFILE_MODE, null);
		Set<String> fileIds = new HashSet<String>();
		fileIds.add("1ab1c");
		
		return new LaunchEvent(time, duration, launch, config, fileIds);
	}

	@Override
	public void testHasSameId_typeAndEvent() {
		LaunchEvent event = createEvent();
		LaunchEventType type = storer.newXmlType(event);
		assertTrue(storer.hasSameId(type, event));
		
		type.setCount(type.getCount() + 1);
		assertTrue("Count is not part of ID",
				storer.hasSameId(type, event));
		
		type.setTotalDuration(type.getTotalDuration() + 1);
		assertTrue("Total duration is not part of ID", 
				storer.hasSameId(type, event));
		
		type.getFileId().add(System.currentTimeMillis() + "");
		type.getFileId().add(System.nanoTime() + "");
		assertTrue("FileIds are not part of ID", 
				storer.hasSameId(type, event));
		
		String modeId = type.getLaunchModeId();
		type.setLaunchModeId(modeId + "abc");
		assertFalse("LaunchModeId is part of ID", 
				storer.hasSameId(type, event));
		
		// Restore:
		type.setLaunchModeId(modeId);
		assertTrue(storer.hasSameId(type, event));
		
		String typeId = type.getLaunchTypeId();
		type.setLaunchTypeId(typeId + "abc");
		assertFalse("LaunchTypeId is part of ID",
				storer.hasSameId(type, event));
		
		// Restore:
		type.setLaunchTypeId(typeId);
		assertTrue(storer.hasSameId(type, event));
		
		String name = type.getName();
		type.setName(name + "abc");
		assertFalse("Name is part of ID", storer.hasSameId(type, event));
	}

	@Override
	public void testHasSameId_typeAndType() {
		LaunchEvent e = createEvent();
		LaunchEventType type1 = storer.newXmlType(e);
		LaunchEventType type2 = storer.newXmlType(e);
		assertTrue(storer.hasSameId(type1, type2));
		
		type1.setCount(type1.getCount() + 1);
		assertTrue("Count is not part of ID",
				storer.hasSameId(type1, type2));
		
		type1.setTotalDuration(type1.getTotalDuration() + 1);
		assertTrue("Total duration is not part of ID", 
				storer.hasSameId(type1, type2));
		
		type1.getFileId().add(System.currentTimeMillis() + "");
		type1.getFileId().add(System.nanoTime() + "");
		assertTrue("FileIds are not part of ID", 
				storer.hasSameId(type1, type2));
		
		String modeId = type1.getLaunchModeId();
		type1.setLaunchModeId(modeId + "abc");
		assertFalse("LaunchModeId is part of ID", 
				storer.hasSameId(type1, type2));
		
		// Restore:
		type1.setLaunchModeId(modeId);
		assertTrue(storer.hasSameId(type1, type2));
		
		String typeId = type1.getLaunchTypeId();
		type1.setLaunchTypeId(typeId + "abc");
		assertFalse("LaunchTypeId is part of ID",
				storer.hasSameId(type1, type2));
		
		// Restore:
		type1.setLaunchTypeId(typeId);
		assertTrue(storer.hasSameId(type1, type2));
		
		String name = type1.getName();
		type1.setName(name + "abc");
		assertFalse("Name is part of ID", storer.hasSameId(type1, type2));
	}

	@Override
	public void testMerge_typeAndEvent() throws Exception {
		LaunchEvent event1 = createEvent();
		LaunchEventType type = storer.newXmlType(event1);
		
		LaunchEvent event2 = createEvent2();
		storer.merge(type, event2);
		assertEquals(2, type.getCount());
		assertEquals(event1.getDuration() + event2.getDuration(), type.getTotalDuration());
		assertEquals(event1.getLaunchConfiguration().getName(), type.getName());
		assertEquals(event1.getLaunchConfiguration().getType().getIdentifier(), type.getLaunchTypeId());
		assertEquals(event1.getLaunch().getLaunchMode(), type.getLaunchModeId());
		assertEquals(event1.getFileIds().size() + event2.getFileIds().size(), type.getFileId().size());
		assertTrue(type.getFileId().containsAll(event1.getFileIds()));
		assertTrue(type.getFileId().containsAll(event2.getFileIds()));
	}

	@Override
	public void testMerge_typeAndType() throws Exception {
		LaunchEvent event1 = createEvent();
		LaunchEvent event2 = createEvent2();
		LaunchEventType type1 = storer.newXmlType(event1);
		LaunchEventType type2 = storer.newXmlType(event2);
		
		storer.merge(type1, type2);
		assertEquals(2, type1.getCount());
		assertEquals(event1.getDuration() + event2.getDuration(), type1.getTotalDuration());
		assertEquals(event1.getLaunchConfiguration().getName(), type1.getName());
		assertEquals(event1.getLaunchConfiguration().getType().getIdentifier(), type1.getLaunchTypeId());
		assertEquals(event1.getLaunch().getLaunchMode(), type1.getLaunchModeId());
		assertEquals(event1.getFileIds().size() + event2.getFileIds().size(), type1.getFileId().size());
		assertTrue(type1.getFileId().containsAll(event1.getFileIds()));
		assertTrue(type1.getFileId().containsAll(event2.getFileIds()));
	}
}
