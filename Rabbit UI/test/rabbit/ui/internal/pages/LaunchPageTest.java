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
package rabbit.ui.internal.pages;

import static org.junit.Assert.*;

import org.junit.Test;

import rabbit.core.storage.LaunchDescriptor;

public class LaunchPageTest extends AbstractTreeViewerPageTest {

	@Override
	protected LaunchPage createPage() {
		return new LaunchPage();
	}

	@Test
	public void testGetValue() {
		LaunchPage page = createPage();
		assertEquals(0, page.getValue(new Object()));
		assertEquals(0, page.getValue(null));

		LaunchDescriptor des = new LaunchDescriptor();
		des.setDuration(19834);
		assertEquals(des.getDuration(), page.getValue(des));
	}
}
