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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Calendar;
import java.util.List;

import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.PlatformUI;

import rabbit.core.events.PerspectiveEvent;
import rabbit.core.internal.storage.xml.schema.events.ObjectFactory;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;

public class PerspectiveEventStorerTest
		extends
		AbstractStorerTest2<PerspectiveEvent, PerspectiveEventType, PerspectiveEventListType> {

	private PerspectiveEventStorer storer = create();

	@Override
	public void testHasSameId_typeAndEvent() {
		PerspectiveEventType x1 = new ObjectFactory().createPerspectiveEventType();
		x1.setPerspectiveId("paId");

		PerspectiveEventType x2 = new ObjectFactory().createPerspectiveEventType();
		x2.setPerspectiveId(x1.getPerspectiveId());

		assertTrue(storer.hasSameId(x1, x2));

		x2.setPerspectiveId("another");
		assertFalse(storer.hasSameId(x1, x2));
	}

	@Override
	public void testHasSameId_typeAndType() {
		PerspectiveEvent e = createEvent();

		PerspectiveEventType x = new ObjectFactory().createPerspectiveEventType();
		x.setPerspectiveId(e.getPerspective().getId());

		assertTrue(storer.hasSameId(x, e));

		x.setPerspectiveId("");
		assertFalse(storer.hasSameId(x, e));
	}

	@Override
	protected PerspectiveEventStorer create() {
		return PerspectiveEventStorer.getInstance();
	}

	@Override
	protected PerspectiveEvent createEvent() {
		IPerspectiveDescriptor p = PlatformUI.getWorkbench().getPerspectiveRegistry()
				.getPerspectives()[0];
		return new PerspectiveEvent(Calendar.getInstance(), 194, p);
	}

	@Override
	protected PerspectiveEvent createEvent2() {
		IPerspectiveDescriptor p = PlatformUI.getWorkbench().getPerspectiveRegistry()
				.getPerspectives()[1];
		return new PerspectiveEvent(Calendar.getInstance(), 11094, p);
	}

	@Override
	protected List<PerspectiveEventType> getEventTypes(PerspectiveEventListType type) {
		return type.getPerspectiveEvent();
	}

	@Override
	protected long getValue(PerspectiveEvent event) {
		return event.getDuration();
	}

	@Override
	protected long getValue(PerspectiveEventType type) {
		return type.getDuration();
	}

	@Override
	protected boolean isEqual(PerspectiveEventType type, PerspectiveEvent event) {
		boolean isEqual = false;
		isEqual = type.getPerspectiveId().equals(event.getPerspective().getId());
		if (isEqual) {
			isEqual = (type.getDuration() == event.getDuration());
		}
		return isEqual;
	}

	@Override
	protected PerspectiveEvent mergeValue(PerspectiveEvent main, PerspectiveEvent tmp) {
		return new PerspectiveEvent(main.getTime(), main.getDuration() + tmp.getDuration(), main
				.getPerspective());
	}

	@Override
	protected PerspectiveEvent createEvent(Calendar eventTime) {
		IPerspectiveDescriptor p = PlatformUI.getWorkbench().getPerspectiveRegistry()
				.getPerspectives()[0];
		return new PerspectiveEvent(eventTime, 1924, p);
	}

}
