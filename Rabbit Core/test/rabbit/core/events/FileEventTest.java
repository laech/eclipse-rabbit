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

import java.util.Calendar;

import org.junit.Test;

/**
 * @see FileEvent
 */
public class FileEventTest extends ContinuousEventTest {

	@Test(expected = NullPointerException.class)
	public void testContructor_fileIdNull() {
		new FileEvent(Calendar.getInstance(), 10, null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testContructor_fileIdEmpty() {
		new FileEvent(Calendar.getInstance(), 10, "");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testContructor_fileIdWhitespaceOnly() {
		new FileEvent(Calendar.getInstance(), 10, " \t");
	}

	@Test
	public void testGetFileId() {
		String fileId = System.currentTimeMillis() + "";
		FileEvent event = new FileEvent(Calendar.getInstance(), 10, fileId);
		assertEquals(fileId, event.getFileId());
	}

	@Override
	protected FileEvent createEvent(Calendar time, long duration) {
		return new FileEvent(time, duration, System.currentTimeMillis() + "");
	}
}
