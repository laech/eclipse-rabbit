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

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;
import org.junit.Test;

/**
 * Test for {@link PerspectiveEvent}
 */
public class PerspectiveEventTest extends ContinuousEventTest {

	IPerspectiveDescriptor pers = PlatformUI.getWorkbench()
			.getPerspectiveRegistry().getPerspectives()[1];
	private PerspectiveEvent event = createEvent(Calendar.getInstance(), 19);

	@Test(expected = NullPointerException.class)
	public void testConstructor_withPerspectiveNull() {
		new PerspectiveEvent(Calendar.getInstance(), 10, null);
	}

	@Test
	public void testGetPerspective() {
		assertEquals(pers, event.getPerspective());
	}

	@Override
	protected PerspectiveEvent createEvent(Calendar time, long duration) {
		return new PerspectiveEvent(time, duration, PlatformUI.getWorkbench()
				.getPerspectiveRegistry().getPerspectives()[1]);
	}
}
