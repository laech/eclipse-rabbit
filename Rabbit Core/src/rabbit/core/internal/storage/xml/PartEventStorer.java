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

import rabbit.core.events.PartEvent;
import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventType;

public class PartEventStorer
		extends AbstractStorer<PartEvent, PartEventType, PartEventListType> {

	public PartEventStorer() {
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.PART_STORE;
	}

	@Override
	protected List<PartEventListType> getXmlTypeCategories(EventListType events) {
		return events.getPartEvents();
	}

	@Override
	protected boolean hasSameId(PartEventType x, PartEvent e) {

		boolean result = false;
		if (e.getWorkbenchPart() != null) {
			result = e.getWorkbenchPart().getSite().getId().equals(x.getPartId());
		}
		return result;
	}

	@Override
	protected boolean hasSameId(PartEventType x1, PartEventType x2) {
		return x1.getPartId().equals(x2.getPartId());
	}

	@Override
	protected void merge(PartEventListType main, PartEvent e) {
		merge(main.getPartEvent(), e);
	}

	@Override
	protected void merge(PartEventListType main, PartEventListType data) {
		merge(main.getPartEvent(), data.getPartEvent());
	}

	@Override
	protected void merge(PartEventType main, PartEvent e) {
		main.setDuration(main.getDuration() + e.getDuration());
	}

	@Override
	protected void merge(PartEventType main, PartEventType x2) {
		main.setDuration(main.getDuration() + x2.getDuration());
	}

	@Override
	protected PartEventType newXmlType(PartEvent e) {

		PartEventType type = OBJECT_FACTORY.createPartEventType();
		type.setDuration(e.getDuration());
		type.setPartId(e.getWorkbenchPart().getSite().getId());

		return type;
	}

	@Override
	protected PartEventListType newXmlTypeHolder(XMLGregorianCalendar date) {

		PartEventListType type = OBJECT_FACTORY.createPartEventListType();
		type.setDate(date);

		return type;
	}
}
