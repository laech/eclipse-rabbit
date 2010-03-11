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
package rabbit.core.events;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Test;

/**
 * Test for {@link DiscreteEvent}
 */
public class DiscreteEventTest {

	private Calendar time = Calendar.getInstance();

	private DiscreteEvent event = createEvent(time);

	@Test(expected = NullPointerException.class)
	public void testConstructor_withTimeNull() {
		new DiscreteEvent(null);
	}

	@Test
	public void testEvent() {
		assertNotNull(event);
	}

	@Test
	public void testGetTime() {
		assertEquals(time, event.getTime());
	}

	@Test
	public void testSetTime() {

		Calendar newTime = new GregorianCalendar(10, Calendar.JANUARY, 20);
		event.setTime(newTime);
		assertEquals(newTime, event.getTime());
	}

	@Test(expected = NullPointerException.class)
	public void testSetTimeNull() {
		event.setTime(null);
	}

	/** Creates an event for testing. */
	protected DiscreteEvent createEvent(Calendar time) {
		return new DiscreteEvent(time);
	}
}
