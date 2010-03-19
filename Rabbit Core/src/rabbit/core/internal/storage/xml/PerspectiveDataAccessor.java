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

import java.util.Collection;

import rabbit.core.internal.storage.xml.schema.events.EventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventListType;
import rabbit.core.internal.storage.xml.schema.events.PerspectiveEventType;

/**
 * Gets the data about how much time is spent using different perspectives.
 * <p>
 * The data returned by {@link #getData(java.util.Calendar, java.util.Calendar)}
 * is a map, where the keys are perspective IDs, and the values are durations in
 * milliseconds.
 * </p>
 */
public class PerspectiveDataAccessor extends
		AbstractIdToValueAccessor<PerspectiveEventType, PerspectiveEventListType> {

	@Override
	protected Collection<PerspectiveEventListType> getCategories(EventListType doc) {
		return doc.getPerspectiveEvents();
	}

	@Override
	protected IDataStore getDataStore() {
		return DataStore.PERSPECTIVE_STORE;
	}

	@Override
	protected String getId(PerspectiveEventType e) {
		return e.getPerspectiveId();
	}

	@Override
	protected long getUsage(PerspectiveEventType e) {
		return e.getDuration();
	}

	@Override
	protected Collection<PerspectiveEventType> getXmlTypes(PerspectiveEventListType list) {
		return list.getPerspectiveEvent();
	}

}
