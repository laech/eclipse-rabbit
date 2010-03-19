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

import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventListType;
import rabbit.core.internal.storage.xml.schema.events.PartEventType;

/**
 * Gets data about part usage.
 */
public class PartDataAccessor extends AbstractIdToValueAccessor<PartEventType, PartEventListType> {

	/** Constructor. */
	public PartDataAccessor() {
		super();
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * The keys of the map returned are command IDs, and the values are usage
	 * counts.
	 * </p>
	 */
	@Override
	public Map<String, Long> getData(Calendar start, Calendar end) {
		return super.getData(start, end);
	}

	@Override
	protected Collection<PartEventListType> getCategories(EventListType doc) {
		return doc.getPartEvents();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.PART_STORE;
	}

	@Override
	protected String getId(PartEventType e) {
		return e.getPartId();
	}

	@Override
	protected long getUsage(PartEventType e) {
		return e.getDuration();
	}

	@Override
	protected Collection<PartEventType> getXmlTypes(PartEventListType list) {
		return list.getPartEvent();
	}
}
