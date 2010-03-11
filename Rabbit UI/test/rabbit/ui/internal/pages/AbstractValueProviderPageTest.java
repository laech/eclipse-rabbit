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

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public abstract class AbstractValueProviderPageTest {

	@Test
	public void testGetSetMaxValue() {
		AbstractValueProviderPage page = createPage();
		page.setMaxValue(10);
		assertEquals(10, page.getMaxValue());
		page.setMaxValue(101);
		assertEquals(101, page.getMaxValue());
	}

	protected abstract AbstractValueProviderPage createPage();
}
