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

import java.util.List;

import javax.xml.datatype.XMLGregorianCalendar;

import rabbit.core.events.PerspectiveEvent;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;

public final class PerspectiveEventStorer
		extends AbstractStorer<PerspectiveEvent, PerspectiveEventType, PerspectiveEventListType> {

	private static final PerspectiveEventStorer INSTANCE = new PerspectiveEventStorer();

	/**
	 * Gets the shared instance of this class.
	 * 
	 * @return The shared instanceof this class.
	 */
	public static PerspectiveEventStorer getInstance() {
		return INSTANCE;
	}

	private PerspectiveEventStorer() {
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.PERSPECTIVE_STORE;
	}

	@Override
	protected List<PerspectiveEventListType> getXmlTypeCategories(EventListType events) {
		return events.getPerspectiveEvents();
	}

	@Override
	protected boolean hasSameId(PerspectiveEventType x, PerspectiveEvent e) {
		return x.getPerspectiveId().equals(e.getPerspective().getId());
	}

	@Override
	protected boolean hasSameId(PerspectiveEventType x1, PerspectiveEventType x2) {
		return x1.getPerspectiveId().equals(x2.getPerspectiveId());
	}

	@Override
	protected void merge(PerspectiveEventListType main, PerspectiveEvent e) {
		merge(main.getPerspectiveEvent(), e);
	}

	@Override
	protected void merge(PerspectiveEventListType main, PerspectiveEventListType data) {
		merge(main.getPerspectiveEvent(), data.getPerspectiveEvent());
	}

	@Override
	protected void merge(PerspectiveEventType main, PerspectiveEvent e) {
		main.setDuration(main.getDuration() + e.getDuration());
	}

	@Override
	protected void merge(PerspectiveEventType main, PerspectiveEventType x) {
		main.setDuration(main.getDuration() + x.getDuration());
	}

	@Override
	protected PerspectiveEventType newXmlType(PerspectiveEvent e) {
		PerspectiveEventType type = objectFactory.createPerspectiveEventType();
		type.setDuration(e.getDuration());
		type.setPerspectiveId(e.getPerspective().getId());
		return type;
	}

	@Override
	protected PerspectiveEventListType newXmlTypeHolder(XMLGregorianCalendar date) {
		PerspectiveEventListType type = objectFactory.createPerspectiveEventListType();
		type.setDate(date);
		return type;
	}

}
