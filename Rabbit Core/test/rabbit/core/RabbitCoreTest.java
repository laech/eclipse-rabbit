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
package rabbit.core;

import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Assert;
import org.junit.Test;

import rabbit.core.RabbitCore.AccessorType;
import rabbit.core.events.CommandEvent;
import rabbit.core.events.FileEvent;
import rabbit.core.events.PartEvent;
import rabbit.core.events.PerspectiveEvent;
import rabbit.core.internal.storage.xml.CommandDataAccessor;
import rabbit.core.internal.storage.xml.CommandEventStorer;
import rabbit.core.internal.storage.xml.FileDataAccessor;
import rabbit.core.internal.storage.xml.FileEventStorer;
import rabbit.core.internal.storage.xml.PartDataAccessor;
import rabbit.core.internal.storage.xml.PartEventStorer;
import rabbit.core.internal.storage.xml.PerspectiveDataAccessor;
import rabbit.core.internal.storage.xml.PerspectiveEventStorer;
import rabbit.core.internal.storage.xml.SessionDataAccessor;

/**
 * Test for {@link RabbitCore}
 */
public class RabbitCoreTest {

	@Test
	public void testGetResourceManager() {
		Assert.assertNotNull(RabbitCore.getFileMapper());
	}

	@Test
	public void testGetStorer() {
		assertTrue(RabbitCore.getStorer(PerspectiveEvent.class) instanceof PerspectiveEventStorer);
		assertTrue(RabbitCore.getStorer(CommandEvent.class) instanceof CommandEventStorer);
		assertTrue(RabbitCore.getStorer(FileEvent.class) instanceof FileEventStorer);
		assertTrue(RabbitCore.getStorer(PartEvent.class) instanceof PartEventStorer);
		assertNull(RabbitCore.getStorer(String.class));
	}
	
	@Test
	public void testGetAccessor() {
		assertTrue(RabbitCore.getAccessor(AccessorType.PERSPECTIVE) instanceof PerspectiveDataAccessor);
		assertTrue(RabbitCore.getAccessor(AccessorType.COMMAND) instanceof CommandDataAccessor);
		assertTrue(RabbitCore.getAccessor(AccessorType.FILE) instanceof FileDataAccessor);
		assertTrue(RabbitCore.getAccessor(AccessorType.PART) instanceof PartDataAccessor);
		assertTrue(RabbitCore.getAccessor(AccessorType.SESSION) instanceof SessionDataAccessor);
	}

	@Test(expected = NullPointerException.class)
	public void testGetStorer_withNull() {
		RabbitCore.getStorer(null);
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetAccessor_withNull() {
		RabbitCore.getAccessor(null);
	}
}
