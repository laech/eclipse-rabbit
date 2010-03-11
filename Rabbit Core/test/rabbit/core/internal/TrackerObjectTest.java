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
package rabbit.core.internal;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;

import org.junit.Test;

import rabbit.core.ITracker;
import rabbit.core.TestUtil;

public class TrackerObjectTest {

	private String id = "idA";
	private String name = "nameA";
	private String description = "descA";
	private ITracker<?> tracker = TestUtil.newTracker();

	private TrackerObject tObject = new TrackerObject(id, name, description, tracker);

	@Test
	public void testGetDescription() {
		assertEquals(description, tObject.getDescription());
	}

	@Test
	public void testGetId() {
		assertEquals(id, tObject.getId());
	}

	@Test
	public void testGetName() {
		assertEquals(name, tObject.getName());
	}

	@Test
	public void testGetTracker() {
		assertSame(tracker, tObject.getTracker());
	}

	@Test
	public void testSetDescription() {

		String newDes = "HelloWorld...!";
		tObject.setDescription(newDes);
		assertEquals(newDes, tObject.getDescription());
	}

	@Test
	public void testSetId() {

		String newId = "asdfkljsdlfj";
		tObject.setId(newId);
		assertEquals(newId, tObject.getId());
	}

	@Test
	public void testSetName() {

		String newName = "`12434nvd";
		tObject.setName(newName);
		assertEquals(newName, tObject.getName());
	}

	@Test
	public void testSetTracker() {

		ITracker<?> newTracker = TestUtil.newTracker();
		tObject.setTracker(newTracker);
		assertSame(newTracker, tObject.getTracker());
	}

	@Test
	public void testTrackerObject() {
		assertNotNull(tObject);
	}

}
